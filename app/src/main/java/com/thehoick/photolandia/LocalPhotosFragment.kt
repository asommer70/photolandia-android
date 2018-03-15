package com.thehoick.photolandia

import android.app.Activity
import android.app.Fragment
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.thehoick.photolandia.database.PhotolandiaDataSource
import com.thehoick.photolandia.models.Photo
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSource
import okio.Okio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class LocalPhotosFragment: Fragment() {
    val TAG = LocalPhotosFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)

        val progress = view.findViewById<ProgressBar>(R.id.progress)
        progress.visibility = VISIBLE
        val photos = view.findViewById(R.id.photos) as GridView

        // Check if all photos have been uploaded and if so set the message and don't create a photo grid.
        val images = getLocalPhotos(activity)
        if (images!!.isNotEmpty()) {
            photos.adapter = PhotoAdapter(activity, images, false)
            progress.visibility = INVISIBLE
        } else {
            photos.visibility = INVISIBLE
            val message = view.findViewById<TextView>(R.id.message)
            message.setText(getString(R.string.all_photos_uploaded))
            message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f)
            message.visibility = VISIBLE
            progress.visibility = INVISIBLE
        }

        val syncButton = activity.findViewById<FloatingActionButton>(R.id.sync)
        syncButton.setImageDrawable(view.context.getDrawable(android.R.drawable.ic_popup_sync))
        syncButton.setOnClickListener {
            val prefs = activity.getSharedPreferences(activity.packageName + "_preferences", 0)
            val defaultAlbumId = prefs!!.getString("default_album_id", null)
            if (defaultAlbumId.isNullOrEmpty()) {
                Toast.makeText(context, "Please configure a default Alubm ID!", Toast.LENGTH_LONG).show()
                fragmentManager.beginTransaction()
                        .addToBackStack("Settings")
                        .replace(android.R.id.content, Settings())
                        .commit()
            } else {
                sync(view)
            }
        }

        return view
    }

    fun getLocalPhotos(activity: Activity): ArrayList<Photo>? {
        val uri: Uri
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_date_taken: Int
        val listOfAllImages = ArrayList<Photo>()

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DATE_TAKEN
        )


        // Get only camera photos.
        val CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera"
        val CAMERA_IMAGE_BUCKET_ID = CAMERA_IMAGE_BUCKET_NAME.toLowerCase().hashCode().toString()
        val selectionArgs = arrayOf(CAMERA_IMAGE_BUCKET_ID)

        cursor = activity.contentResolver.query(
                uri,
                projection,
                MediaStore.Images.Media.BUCKET_ID + " = ?",
                selectionArgs,
                null
        )

        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_date_taken = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

        while (cursor.moveToNext()) {
            val absolutePathOfImage = cursor.getString(column_index_data)
            val filename = absolutePathOfImage.split("/").last()
            val imageId = cursor.getString(column_index_date_taken)

            Log.d(TAG, "absolutePathOfImage: $absolutePathOfImage")
            Log.d(TAG, "imageId: $imageId")
            Log.d(TAG, "filename: $filename")

            val dataSource = PhotolandiaDataSource(context)

            // Check if the photo is in the database.
            var photo: Photo? = null
            photo = dataSource.getPhoto(absolutePathOfImage)
            if (photo == null) {
                photo = Photo(null, null, null, null, filename,
                        absolutePathOfImage, imageId)

                // Add photo to the database.
                dataSource.createPhoto(photo)
                listOfAllImages.add(photo)
            }
            Log.d(TAG, "photo.id: ${photo.image}")
            if (photo.image != null) {
                listOfAllImages.add(photo)
            }

        }

        cursor.close()
        if (listOfAllImages.isNotEmpty()) {
            // TODO:as find a reasonable number of unuploaded photos to put in the list.
            return listOfAllImages.take(20).reversed() as ArrayList<Photo>
        } else {
            return listOfAllImages
        }
    }

    private fun sync(view: View) {
        // Get a list of photo names not in the database.
        val dataSource = PhotolandiaDataSource(context)
        val localPhotos = dataSource.getUnuploadedPhotos()

        for (photo in localPhotos) {
            upload(photo)
        }
    }

    fun upload(photo: Photo) {
        val api = Api(view.context)

        try {
            val file = File(photo.local_path)
            val fileInputStream = FileInputStream(file)

            val img: BufferedSource = Okio.buffer(Okio.source(fileInputStream))
            val image = img.readByteArray()
            fileInputStream.close()
            img.close()

            val callback = object: Callback<Photo> {
                override fun onFailure(call: Call<Photo>?, t: Throwable?) {
                    Log.d(TAG, "A problem occurred uploading image...")
                }

                override fun onResponse(call: Call<Photo>?, response: Response<Photo>?) {
                    Log.d(TAG, "response?.body()?.filename: ${response?.body()?.filename}")

                    // Save server photo data into the database.
                    val dataSource = PhotolandiaDataSource(context)
                    val uploadedPhoto = response?.body()
                    if (response?.body()?.filename != null) {
                        dataSource.updatePhoto(uploadedPhoto!!)
                        Snackbar.make(view, "Uploaded ${uploadedPhoto.local_filename}", Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(view, "Problem with ${photo.local_filename}", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }

            val prefs = context.getSharedPreferences(context.packageName + "_preferences", 0)
            val albumId = prefs.getString("default_album_id", "")

            api.uploadImage(
                    RequestBody.create(MediaType.parse("text/plain"), albumId),
                    RequestBody.create(MediaType.parse("text/plain"), photo.local_filename),
                    RequestBody.create(MediaType.parse("text/plain"), photo.local_path),
                    RequestBody.create(MediaType.parse("text/plain"), photo.local_id),
                    MultipartBody.Part.createFormData(
                            "image",
                            photo.local_path,
                            RequestBody.create(MediaType.parse("image/jpeg"), image)),
                    callback
            )
        } catch (e: IOException) {

        }
    }
}