package com.thehoick.photolandia


import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.album_item.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import android.support.constraint.ConstraintSet
import android.widget.LinearLayout





class AlbumFragment: Fragment() {
    val TAG = AlbumFragment::class.java.simpleName
    public val ALBUM_INDEX = "album_index"
    var albumPhotoAdapter: AlbumPhotoAdapter? = null

    override fun onResume() {
        Log.d(TAG, "onResume()...")
        val fab = activity.findViewById<FloatingActionButton>(R.id.fab)
        fab.show()

        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav.visibility = View.VISIBLE

        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.album_layout, container, false)

        val albumId = getArguments().getInt("album")

        val api = Api(view.context)
        val albumDetailName = view.findViewById<TextView>(R.id.albumDetailName)
        val albumDetailDescription = view.findViewById<TextView>(R.id.albumDetailDescription)
        val albumDetailId = view.findViewById<TextView>(R.id.albumDetailId)
        val albumDetailCreatedAt = view.findViewById<TextView>(R.id.albumDetailCreatedAt)
        val albumDetailUpdatedAt = view.findViewById<TextView>(R.id.albumDetailUpdatedAt)
        val progressAlbumDetails = view.findViewById<ProgressBar>(R.id.progressAlbumDetails)
        val albumContainer = view?.findViewById<ConstraintLayout>(R.id.albumContainer)
        val albumHeader = view?.findViewById<ConstraintLayout>(R.id.albumHeader)

        progressAlbumDetails.visibility = View.VISIBLE

        val callback = object: Callback<Album> {
            override fun onFailure(call: Call<Album>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getAlbum()...")
                albumDetailName.setText(getString(R.string.problem_fetching_album_details))
                albumDetailName.setTextColor(Color.RED)
                progressAlbumDetails.visibility = INVISIBLE
            }

            override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
                val albumDetailPhotosView = view.findViewById<GridView>(R.id.albumDetailPhotos)
                Log.d(TAG, "response?.body()?.name: ${response?.body()?.name}")

                albumDetailPhotosView.setOnScrollListener(object: AbsListView.OnScrollListener {
                    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                        Log.d(TAG, "onScroll visibleItemCount: $visibleItemCount")
                        Log.d(TAG, "onScroll firstVisibleItem: $firstVisibleItem")
                        if (firstVisibleItem != 0) {
                            albumDetailDescription.visibility = GONE
                            albumDetailId.visibility = GONE
                            albumDetailCreatedAt.visibility = GONE
                            albumDetailUpdatedAt.visibility = GONE

                            albumDetailUpdatedAt.height = GONE
                            albumHeader?.systemUiVisibility = GONE

                            // Gets linearlayout
                            val params = albumHeader?.getLayoutParams()
                            params?.height = 0
                            params?.width = 0
                            albumHeader?.setLayoutParams(params)

//                            albumDetailPhotosView.layoutParams.height = albumDetailPhotosView.layoutParams.height + 10
//                            albumDetailPhotosView.height = 439

//                            albumDetailPhotosView
//                            albumContainer?.removeView(albumDetailCreatedAt)
//                            albumContainer?.removeView(albumDetailUpdatedAt)
//                            albumContainer?.removeView(albumDetailId)
//                            albumContainer?.removeView(albumDetailDescription)
//                            albumContainer?.removeView(albumHeader)
                        } else {
                            albumDetailDescription.visibility = VISIBLE
                            albumDetailDescription.height = 34
                            albumDetailId.visibility = VISIBLE
                            albumDetailId.height = 25
                            albumDetailCreatedAt.visibility = VISIBLE
                            albumDetailCreatedAt.height = 18
                            albumDetailUpdatedAt.visibility = VISIBLE
                            albumDetailUpdatedAt.height = 18
//                            359
//                            albumContainer?.addView(albumDetailCreatedAt)
//                            albumContainer?.addView(albumDetailUpdatedAt)
//                            albumContainer?.addView(albumDetailId)
//                            albumDetailPhotosView.layoutParams.height = albumDetailPhotosView.layoutParams.height + 10
                        }
//                        albumDetailName.visibility = INVISIBLE
                    }

                    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                    }
                })

                progressAlbumDetails.visibility = INVISIBLE

                albumDetailName.setText(response?.body()?.name)
                albumDetailDescription.setText(response?.body()?.description)
                albumDetailId.setText("Album ID Number: ${response?.body()?.id.toString()}")
                val df = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                albumDetailCreatedAt.setText("Created: " + df.format(response?.body()?.created_at))
                albumDetailUpdatedAt.setText("Updated: " + df.format(response?.body()?.updated_at))

                albumPhotoAdapter = AlbumPhotoAdapter(activity, response?.body()?.id!!, response.body()?.photo_set!!)
                albumDetailPhotosView.adapter = albumPhotoAdapter
            }

        }
        api.getAlbum(albumId, callback)

        return view
    }
}