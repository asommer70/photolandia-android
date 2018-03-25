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
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumFragment: Fragment() {
    val TAG = AlbumFragment::class.java.simpleName
    var albumPhotoAdapter: AlbumPhotoAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.album_layout, container, false)

        val albumId = getArguments().getInt("album")

        val fab = activity.findViewById<FloatingActionButton>(R.id.fab)
        fab.show()

        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav.visibility = View.VISIBLE

        val api = Api(view.context)
        val albumDetailName = view.findViewById<TextView>(R.id.albumDetailName)
        val progressAlbumDetails = view.findViewById<ProgressBar>(R.id.progressAlbumDetails)

        progressAlbumDetails.visibility = View.VISIBLE

        val callback = object: Callback<Album> {
            override fun onFailure(call: Call<Album>?, t: Throwable?) {
                Log.i(TAG, "A problem occurred inside callback for getAlbum()...")
                albumDetailName.setText(getString(R.string.problem_fetching_album_details))
                albumDetailName.setTextColor(Color.RED)
                progressAlbumDetails.visibility = INVISIBLE
            }

            override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
                val albumDetailPhotosView = view.findViewById<GridView>(R.id.albumDetailPhotos)
                progressAlbumDetails.visibility = INVISIBLE
                albumDetailName.setText(response?.body()?.name)

                albumPhotoAdapter = AlbumPhotoAdapter(activity, response?.body()?.id!!, response.body()?.photo_set!!)
                albumDetailPhotosView.adapter = albumPhotoAdapter
            }

        }
        api.getAlbum(albumId, callback)

        return view
    }
}