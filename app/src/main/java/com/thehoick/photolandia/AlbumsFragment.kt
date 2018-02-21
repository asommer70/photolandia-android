package com.thehoick.photolandia

import android.app.Fragment
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

class AlbumsFragment: Fragment() {
    val TAG = AlbumsFragment::class.java.simpleName

    fun getAlbums(view: View) {
        val api = Api(view.context)
        val callback = object: Callback<AlbumResult> {
            override fun onFailure(call: Call<AlbumResult>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getAlbums()...")
                val photoGrid = view.findViewById<GridView>(R.id.photos)
                photoGrid.visibility = View.INVISIBLE
                val message = view.findViewById<TextView>(R.id.message)
                message.visibility = View.VISIBLE
                message.setText(getString(R.string.albumsError))
            }

            override fun onResponse(call: Call<AlbumResult>?, response: Response<AlbumResult>?) {
                val albumsView = view.findViewById<GridView>(R.id.photos)
                Log.d(TAG, "response?.body()?.results: ${response?.body()?.results}")

                val albumAdapter = AlbumAdapter(activity, response?.body()?.results!!)
                albumsView.setAdapter(albumAdapter)
            }

        }
        api.getAlbums(callback)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)

        getAlbums(view)
        return view
    }
}