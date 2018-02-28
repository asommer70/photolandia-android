package com.thehoick.photolandia

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhotosFragment: Fragment() {
    val TAG = PhotosFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)
        getServerPhotos(view)
        return view
    }

    private fun getServerPhotos(context: View) {
        val api = Api(context.context)
        val progress = context.findViewById<ProgressBar>(R.id.progress)
        val message = context.findViewById<TextView>(R.id.message)

        progress.visibility = View.VISIBLE
        message.setText(getString(R.string.loading_photos))
        message.visibility = View.VISIBLE

        val callback = object: Callback<PhotosResult> {
            override fun onFailure(call: Call<PhotosResult>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getAlbum()...")
                progress.visibility = View.INVISIBLE
                message.setText(getString(R.string.fetching_photos_error))
                message.visibility = View.VISIBLE
            }

            override fun onResponse(call: Call<PhotosResult>?, response: Response<PhotosResult>?) {
                Log.d(TAG, "response?.body()?.count: ${response?.body()?.count}")

                progress.visibility = View.INVISIBLE
                message.visibility = View.INVISIBLE

                var photos: List<String>? = null
                photos = response?.body()?.results?.map { it.image }

                val photosView = context.findViewById(R.id.photos) as GridView
                photosView.adapter = PhotoAdapter(activity, photos);
            }

        }
        api.getPhotos(callback)
    }
}