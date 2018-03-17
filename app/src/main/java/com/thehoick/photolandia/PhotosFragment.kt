package com.thehoick.photolandia

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import com.thehoick.photolandia.models.Photo
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

    override fun onResume() {
        Log.d(TAG, "onResume()...")
        val fab = activity.findViewById<FloatingActionButton>(R.id.fab)
        fab.show()

        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav.visibility = View.VISIBLE

        super.onResume()
    }

    private fun getServerPhotos(context: View) {
        val api = Api(context.context)
        val progress = context.findViewById<ProgressBar>(R.id.progress)
        val message = context.findViewById<TextView>(R.id.message)

        progress.visibility = VISIBLE
        message.text = getString(R.string.loading_photos)
        message.visibility = VISIBLE

        val callback = object: Callback<PhotosResult> {
            override fun onFailure(call: Call<PhotosResult>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getPhotos()...")
                progress.visibility = INVISIBLE
                message.text = getString(R.string.fetching_photos_error)
                message.setTextColor(Color.RED)
                message.visibility = VISIBLE
            }

            override fun onResponse(call: Call<PhotosResult>?, response: Response<PhotosResult>?) {
                Log.d(TAG, "response?.body()?.count: ${response?.body()?.count}")

                progress.visibility = INVISIBLE
                message.visibility = INVISIBLE

                val photos = mutableListOf<Photo>()
                for (image in response?.body()?.results!!) {
                    // Create a Photo instance for each result returned as JSON.
                    val photo = Photo(
                            image.id,
                            image.image,
                            image.filename,
                            image.caption,
                            image.local_filename,
                            image.local_path,
                            image.local_id
                    )
                    photos.add(photo)
                }

                val photosView = context.findViewById(R.id.photos) as GridView
                photosView.adapter = PhotoAdapter(activity, photos, true)
            }

        }
        api.getPhotos(callback)
    }
}