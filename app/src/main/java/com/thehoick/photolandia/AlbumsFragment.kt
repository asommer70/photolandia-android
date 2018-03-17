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
        get() = field

    override fun onResume() {
        Log.d(TAG, "onResume()...")
        val fab = activity.findViewById<FloatingActionButton>(R.id.fab)
        fab.show()

        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav.visibility = View.VISIBLE

        super.onResume()
    }

    fun getAlbums(view: View) {
        val api = Api(context)
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
                Log.d(TAG, "response?.body()?.results.size: ${response?.body()?.results!!.size}")

                photoGrid.visibility = VISIBLE
                message.visibility = INVISIBLE
                progress.visibility = INVISIBLE

                AlbumFragmentAdapter = AlbumAdapter(activity, response.body()?.results!!)
                albumsView.setAdapter(AlbumFragmentAdapter)
            }

        }
        api.getAlbums(callback)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "AlbumsFragment.onCreate()...")
        val syncButton = activity.findViewById<FloatingActionButton>(R.id.fab)
        syncButton.setImageDrawable(context.getDrawable(android.R.drawable.ic_input_add))
        syncButton.setOnClickListener {
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

    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)

        getAlbums(view)
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