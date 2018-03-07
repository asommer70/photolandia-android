package com.thehoick.photolandia

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.ProgressDialog
import android.support.design.widget.FloatingActionButton
import android.view.View.*
import android.widget.ProgressBar


class AlbumsFragment: Fragment() {
    val TAG = AlbumsFragment::class.java.simpleName

    fun getAlbums(view: View) {
        val api = Api(view.context)
        val message = view.findViewById<TextView>(R.id.message)
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val photoGrid = view.findViewById<GridView>(R.id.photos)
        photoGrid.visibility = INVISIBLE

        message.setText(getString(R.string.fetching_albums))
        message.visibility = VISIBLE
        message.setTextColor(Color.BLACK)
        progress.visibility = VISIBLE


        val callback = object: Callback<AlbumResult> {
            override fun onFailure(call: Call<AlbumResult>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getAlbums()...")
                photoGrid.visibility = INVISIBLE
                message.visibility = VISIBLE
                message.setTextColor(Color.RED)
                message.setText(getString(R.string.albumsError))
                progress.visibility = INVISIBLE
            }

            override fun onResponse(call: Call<AlbumResult>?, response: Response<AlbumResult>?) {
                val albumsView = view.findViewById<GridView>(R.id.photos)
                Log.d(TAG, "response?.body()?.results: ${response?.body()?.results}")

                photoGrid.visibility = VISIBLE
                message.visibility = INVISIBLE
                progress.visibility = VISIBLE
                val albumAdapter = AlbumAdapter(activity, response?.body()?.results!!)
                albumsView.setAdapter(albumAdapter)
            }

        }
        api.getAlbums(callback)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)

        val syncButton = view.findViewById<FloatingActionButton>(R.id.sync)

        getAlbums(view)
        return view
    }
}