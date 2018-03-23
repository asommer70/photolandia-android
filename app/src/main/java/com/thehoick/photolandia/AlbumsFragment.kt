package com.thehoick.photolandia

import android.app.Fragment
import android.content.DialogInterface
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
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AlbumsFragment: Fragment() {
    val TAG = AlbumsFragment::class.java.simpleName
    var AlbumFragmentAdapter: AlbumAdapter? = null
    var albumsView: GridView? = null
    var albums: Array<Album>? = null

    fun getAlbums(view: View) {
        val api = Api(context)
        val message = view.findViewById<TextView>(R.id.message)
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        albumsView?.visibility = INVISIBLE

        message.setText(getString(R.string.fetching_albums))
        message.visibility = VISIBLE
        message.setTextColor(Color.BLACK)
        progress.visibility = VISIBLE

        val callback = object: Callback<AlbumResult> {
            override fun onFailure(call: Call<AlbumResult>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getAlbums()...")
                albumsView?.visibility = INVISIBLE
                message.visibility = VISIBLE
                message.setTextColor(Color.RED)
                message.setText(getString(R.string.albumsError))
                progress.visibility = INVISIBLE
            }

            override fun onResponse(call: Call<AlbumResult>?, response: Response<AlbumResult>?) {
                Log.d(TAG, "getAlbums onResponse() response?.body()?.results.size: ${response?.body()?.results!!.size}")

                albumsView = view.findViewById<GridView>(R.id.photos)
                albumsView?.visibility = VISIBLE
                message.visibility = INVISIBLE
                progress.visibility = INVISIBLE

                albums = response.body()?.results!!

                if (AlbumFragmentAdapter == null) {
                    AlbumFragmentAdapter = AlbumAdapter(activity, albums!!)
                }

                albumsView?.adapter = AlbumFragmentAdapter
            }

        }
        api.getAlbums(callback)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)

        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav.visibility = View.VISIBLE

        if (AlbumFragmentAdapter == null) {
            getAlbums(view)
        } else {
            albumsView = view.findViewById<GridView>(R.id.photos)
            albumsView?.adapter = AlbumFragmentAdapter
        }

        val fab = activity.findViewById<FloatingActionButton>(R.id.fab)
        fab.setImageDrawable(context.getDrawable(R.drawable.ic_plus_icon))
        fab.setOnClickListener {
            // Input DialogFragment.
            Log.d(TAG, "Creating album...")
            val bundle = Bundle()
            val albumCreateDialogFragment = AlbumCreateDialogFragment()
            albumCreateDialogFragment.arguments = bundle
            albumCreateDialogFragment.show(fragmentManager, "AlbumsDialog")

            albumCreateDialogFragment.setOnDismissListener(DialogInterface.OnDismissListener {
                createAlbum(albumCreateDialogFragment.newAlbumName)
            })

        }

        return view
    }

    fun createAlbum(newAlbumName: String?) {
        Log.d(TAG, "newAlbumName: $newAlbumName")

        val api = Api(this.context)
        val callback = object : Callback<Album> {
            override fun onFailure(call: Call<Album>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getAlbums()...")
                Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
                Log.d(TAG, "createAlbum() response?.body: ${response?.body()?.toString()}")
                AlbumFragmentAdapter!!.getAlbums(context)
            }

        }
        if (!newAlbumName.equals(null)) {
            api.createAlbum(newAlbumName!!, callback)
        }
    }
}