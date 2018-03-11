package com.thehoick.photolandia


import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
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
import java.text.SimpleDateFormat

class AlbumFragment: Fragment() {
    val TAG = AlbumFragment::class.java.simpleName
    public val ALBUM_INDEX = "album_index"
    var albumPhotoAdapter: AlbumPhotoAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.album_layout, container, false)

        val albumId = getArguments().getInt("album")
        Log.d(TAG, "albumId: $albumId")

        val api = Api(view.context)
        val albumDetailName = view.findViewById<TextView>(R.id.albumDetailName)
        val albumDetailDescription = view.findViewById<TextView>(R.id.albumDetailDescription)
        val albumDetailId = view.findViewById<TextView>(R.id.albumDetailId)
        val albumDetailCreatedAt = view.findViewById<TextView>(R.id.albumDetailCreatedAt)
        val albumDetailUpdatedAt = view.findViewById<TextView>(R.id.albumDetailUpdatedAt)
        val progressAlbumDetails = view.findViewById<ProgressBar>(R.id.progressAlbumDetails)

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

                progressAlbumDetails.visibility = INVISIBLE

                albumDetailName.setText(response?.body()?.name)
                albumDetailDescription.setText(response?.body()?.description)
                albumDetailId.setText("Album ID Number: ${response?.body()?.id.toString()}")
                val df = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                albumDetailCreatedAt.setText(df.format(response?.body()?.created_at))
                albumDetailUpdatedAt.setText(df.format(response?.body()?.updated_at))

                albumPhotoAdapter = AlbumPhotoAdapter(activity, response?.body()?.id!!, response.body()?.photo_set!!)
                albumDetailPhotosView.adapter = albumPhotoAdapter
            }

        }
        api.getAlbum(albumId, callback)

        return view
    }
}