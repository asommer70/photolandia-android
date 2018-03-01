package com.thehoick.photolandia

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class LocalPhotosFragment: Fragment() {
    val TAG = LocalPhotosFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)

        val photos = view.findViewById(R.id.photos) as GridView
        photos.adapter = PhotoAdapter(activity, null);


        val syncButton = activity.findViewById<FloatingActionButton>(R.id.sync)
        syncButton.setOnClickListener {
            sync(view)
        }

        return view
    }


    fun sync(view: View) {
        // Get a list of local photo filenames.
        val localPhotos = PhotoAdapter(activity, null).getAllShownImagesPath(activity)
        val photoNames = localPhotos.map { listOf(it.split('/').last(), it) }
        Log.d(TAG, "photoNames[0]: ${photoNames[0]}")

        val api = Api(view.context)

        val callback = object: Callback<PhotosResult> {
            override fun onFailure(call: Call<PhotosResult>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for sync()...")
            }

            override fun onResponse(call: Call<PhotosResult>?, response: Response<PhotosResult>?) {
                Log.d(TAG, "response?.body()?.count: ${response?.body()?.count}")

                var serverPhotos: List<String>? = null
                serverPhotos = response?.body()?.results?.map { it.filename }

                // Create a list of file names not on the server.
                val notOnServer = mutableListOf<String>()
//                var notOnServer = photoNames.map {
//                    if (it[0] !in serverPhotos!!) {
//                        Log.d(TAG, "it[1]: ${it[1]}")
//                        it[1]
//                    }
//                }

                for (photo in photoNames) {
                    if (photo[0] !in serverPhotos!!) {
                        Log.d(TAG, "it[1]: ${photo[1]}")
                        notOnServer.add(photo[1])
                    }
                }

//                photoNames.ma

                Log.d(TAG, "notOnServer[0]: ${notOnServer[0]}")
                // TODO:as do a HTTP POST to upload the Photo into the default Album.

                for (lP in notOnServer) {
                    upload(lP)
                }
            }

        }
        api.getPhotos(callback)
    }

    fun upload(photo: String) {
        Snackbar.make(view, "Uploading $photo", Snackbar.LENGTH_SHORT).show()

        val api = Api(view.context)

        try {
            val file = File(photo)
            val fileInputStream = FileInputStream(file)

            val img: BufferedSource = Okio.buffer(Okio.source(fileInputStream))
            val image = img.readByteArray()

            val callback = object: Callback<Photo> {
                override fun onFailure(call: Call<Photo>?, t: Throwable?) {
                    Log.d(TAG, "A problem occurred uploading image...")
                }

                override fun onResponse(call: Call<Photo>?, response: Response<Photo>?) {
//                    Log.d(TAG, "response?.body()?.count: ${response?.body()?.count}")
                }
            }

            api.uploadImage(RequestBody.create(MediaType.parse("image/jpeg"), image), callback)
        } catch (e: IOException) {

        }
    }
}