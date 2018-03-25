package com.thehoick.photolandia

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
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
    var photosAdapter: PhotoAdapter? = null
    var photoGrid: GridView? = null
    var syncButton: FloatingActionButton? = null
    var message: TextView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)

        val progress = view.findViewById<ProgressBar>(R.id.progress)
        progress.visibility = VISIBLE
        photoGrid = view.findViewById(R.id.photos)

        val fab = activity.findViewById<FloatingActionButton>(R.id.fab)
        fab.show()
        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav.visibility = View.VISIBLE

        // Check if all photos have been uploaded and if so set the message and don't create a photo grid.
        val images = getLocalPhotos(activity)
        if (images!!.isNotEmpty()) {
            if (photosAdapter == null) {
                photosAdapter = PhotoAdapter(activity, images, false)
            }
            photoGrid?.adapter = photosAdapter
            progress.visibility = INVISIBLE
        } else {
            photoGrid?.visibility = INVISIBLE
            message = view.findViewById<TextView>(R.id.message)
            message?.setText(getString(R.string.all_photos_uploaded))
            message?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f)
            message?.visibility = VISIBLE
            progress.visibility = INVISIBLE
        }

        syncButton = activity.findViewById<FloatingActionButton>(R.id.fab)
        setSyncButtonToSync()

        return view
    }

    fun setSyncButtonToSync() {
        syncButton?.setImageDrawable(context.getDrawable(R.drawable.ic_upload_icon))
        syncButton?.setOnClickListener {
            val prefs = activity.getSharedPreferences(activity.packageName + "_preferences", 0)
            val defaultAlbumId = prefs!!.getString("default_album_id", null)
            if (defaultAlbumId.isNullOrEmpty()) {
                Toast.makeText(context, "Please configure a default Alubm ID!", Toast.LENGTH_LONG).show()
                fragmentManager.beginTransaction()
                        .addToBackStack("Settings")
                        .replace(android.R.id.content, Settings())
                        .commit()
            } else {
                // Get a list of photo names not in the database.
                val dataSource = PhotolandiaDataSource(context)
                val localPhotos = dataSource.getUnuploadedPhotos()

                Log.d(TAG, "localPhotos.size: ${localPhotos.size}")

                for (photo in localPhotos) {
                    upload(photo)
                }
            }
        }
    }

    fun getLocalPhotos(activity: Activity): List<Photo>? {
        val uri: Uri
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_date_taken: Int
        val listOfAllImages = ArrayList<Photo>()
        val dataSource = PhotolandiaDataSource(context)

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

            // Check if the photo is in the database.
            var photo: Photo? = null
            photo = dataSource.getPhoto(absolutePathOfImage)
            if (photo == null) {
                photo = Photo(null, null, null, null, filename,
                        absolutePathOfImage, imageId)

                // Add photo to the database.
                dataSource.createPhoto(photo)

                // Add the Photo to the list if it wasn't in the local database.
                listOfAllImages.add(photo)
            } else {
                // Add the Photo to the list if it's note been uploaded.
                if (photo.image == null) {
                    listOfAllImages.add(photo)
                }
            }
        }

        Log.d(TAG, "listOfAllImages.size: ${listOfAllImages.size}")

        cursor.close()
        if (listOfAllImages.isNotEmpty()) {
            // Reasonable number of unuploaded photos to put in the list.
            if (listOfAllImages.size > 20) {
                return listOfAllImages.take(20).reversed()
            } else {
                return listOfAllImages.reversed()
            }
        } else {
            // Only get un-uploaded Photos.
            return dataSource.getUnuploadedPhotos()
        }
    }

    fun upload(photo: Photo, otherContext: Context? = null) {
        val theContext: Context
        if (otherContext != null) {
            theContext = otherContext
        } else {
            theContext = context
        }

        val api = Api(theContext)

        try {
            val file = File(photo.local_path)
            val fileInputStream = FileInputStream(file)

            val img: BufferedSource = Okio.buffer(Okio.source(fileInputStream))
            val image = img.readByteArray()
            fileInputStream.close()
            img.close()

            val callback = object: Callback<Photo> {
                override fun onFailure(call: Call<Photo>?, t: Throwable?) {
                    Log.d(TAG, "A problem occurred uploading image... photo.local_path: ${photo.local_path}")
                }

                override fun onResponse(call: Call<Photo>?, response: Response<Photo>?) {

                    val uploadedPhoto = response?.body()
                    if (response?.body()?.filename != null) {
                        // Save server photo data into the database.
                        val dataSource = PhotolandiaDataSource(theContext)
                        dataSource.updatePhoto(uploadedPhoto!!)

                        photosAdapter?.images = photosAdapter?.images?.filter { it.local_path != uploadedPhoto.local_path }
                        photosAdapter?.notifyDataSetChanged()

                        Log.d(TAG, "photosAdapter?.images?.size: ${photosAdapter?.images?.size}")
                        Log.d(TAG, "uploadedPhoto.id: ${uploadedPhoto.id}")

                        message?.setText(getString(R.string.photos_uploaded_check_for_more))
                        message?.visibility = VISIBLE
                        photoGrid?.visibility = INVISIBLE

                        syncButton?.setImageDrawable(view.context.getDrawable(android.R.drawable.ic_popup_sync))
                        syncButton?.setOnClickListener {
                            getLocalPhotos(activity)
                            photosAdapter?.images = getLocalPhotos(activity)
                            photosAdapter?.notifyDataSetChanged()
                            setSyncButtonToSync()
                        }
                    } else {
                        Log.d(TAG, "Failed to upload photo.local_filename: ${photo.local_filename}")
                        Log.d(TAG, "failed photo.local_id: ${photo.local_id}")
                        Log.d(TAG, "failed photo.local_path: ${photo.local_path}")
                        Snackbar.make(view, "Problem with ${photo.local_filename}", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }

            val prefs = theContext.getSharedPreferences(theContext.packageName + "_preferences", 0)
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