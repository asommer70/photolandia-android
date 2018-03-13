package com.thehoick.photolandia

import android.app.Activity
import android.app.Fragment
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
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
import java.text.SimpleDateFormat

class LocalPhotosFragment: Fragment() {
    val TAG = LocalPhotosFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)

        val photos = view.findViewById(R.id.photos) as GridView

        // TODO:as check if all photos have been uploaded and if so set the message and don't create a photo grid.
        val images = getLocalPhotos(activity)
        if (images!!.isNotEmpty()) {
            photos.adapter = PhotoAdapter(activity, images, false)
        } else {
            photos.visibility = INVISIBLE
            val message = view.findViewById<TextView>(R.id.message)
            message.setText(getString(R.string.all_photos_uploaded))
            message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f)
            message.visibility = VISIBLE
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
//        val column_index_folder_name: Int
        val column_index_date_taken: Int
        val listOfAllImages = ArrayList<Photo>()
//        var absolutePathOfImage: String? = null
//        var imageId: String? = null
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
                MediaStore.MediaColumns.DATA,
//                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        )

        cursor = activity.contentResolver.query(uri, projection, null, null, null)

        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
//        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
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

        }

        cursor.close()
        if (listOfAllImages.isNotEmpty()) {
            return listOfAllImages.reversed() as ArrayList<Photo>
        } else {
            return listOfAllImages
        }
    }

    private fun sync(view: View) {
//        val localPhotos = PhotoAdapter(activity, null, null).getAllShownImagesPath(activity)
//        val photoNames = localPhotos.map { listOf(it.split('/').last(), it) }
//        Log.d(TAG, "photoNames.size: ${photoNames.size}")

        // Get a list of photo names not in the database.
        val dataSource = PhotolandiaDataSource(context)
        val localPhotos = dataSource.getUnuploadedPhotos()

//        val prefs = activity.getSharedPreferences(activity.packageName + "_preferences", 0)
//        val defaultAlbumId = prefs!!.getString("default_album_id", "")
//
//        val api = Api(view.context)
//
//        val callback = object: Callback<Album> {
//            override fun onFailure(call: Call<Album>?, t: Throwable?) {
//                Log.d(TAG, "A problem occurred inside callback for getAlbum()...")
//
//            }
//
//            override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
//                val serverPhotos = response?.body()?.photo_set?.map { it.local_filename }
//                Log.d(TAG, "serverPhotos.size: ${serverPhotos!!.size}")
////                Log.d(TAG, "serverPhotos[0]: ${serverPhotos[0]}")
//
//                // Create a list of the firest 30 file names not on the server.
//                val notOnServer = mutableListOf<Photo>()
//                for (photo in localPhotos.take(5).toList()) {
//                    if (photo.local_filename !in serverPhotos) {
//                        Log.d(TAG, "it.local_filename: ${photo.local_filename}")
//                        notOnServer.add(photo)
//                    }
//                }
//
//                Log.d(TAG, "notOnServer.size: ${notOnServer.size}")
//
//                // Upload the photos.
//                for (lP in notOnServer) {
//                    upload(lP)
//                }
//
////                upload(notOnServer[0])
//            }
//
//        }
//        api.getAlbum(defaultAlbumId.toInt(), callback)
        for (photo in localPhotos) {
            upload(photo)
        }
    }

    fun upload(photo: Photo) {
        Snackbar.make(view, "Uploading $photo", Snackbar.LENGTH_SHORT).show()

        val api = Api(view.context)

        try {
            val file = File(photo.local_path)
            val fileInputStream = FileInputStream(file)

            val img: BufferedSource = Okio.buffer(Okio.source(fileInputStream))
            val image = img.readByteArray()

            val callback = object: Callback<Photo> {
                override fun onFailure(call: Call<Photo>?, t: Throwable?) {
                    Log.d(TAG, "A problem occurred uploading image...")
                }

                override fun onResponse(call: Call<Photo>?, response: Response<Photo>?) {
                    Log.d(TAG, "response?.body()?.filename: ${response?.body()?.filename}")

                    // Save server photo data into the database.
                    val dataSource = PhotolandiaDataSource(context)
                    val photo = response?.body()
                    dataSource.updatePhoto(photo!!)
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