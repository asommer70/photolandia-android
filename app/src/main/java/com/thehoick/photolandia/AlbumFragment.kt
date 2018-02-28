package com.thehoick.photolandia


import android.app.Fragment
import android.graphics.Color
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
import java.text.SimpleDateFormat

class AlbumFragment: Fragment() {
    val TAG = AlbumFragment::class.java.simpleName
    public val ALBUM_INDEX = "album_index"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.album_layout, container, false)

        val albumId = getArguments().getInt("album")
        Log.d(TAG, "albumId: $albumId")

        val api = Api(view.context)
        val albumDetailName = view.findViewById<TextView>(R.id.albumDetailName)
        val albumDetailDescription = view.findViewById<TextView>(R.id.albumDetailDescription)
        val albumDetailCreatedAt = view.findViewById<TextView>(R.id.albumDetailCreatedAt)
        val albumDetailUpdatedAt = view.findViewById<TextView>(R.id.albumDetailUpdatedAt)
        val albumDetailPhotos = view.findViewById<GridView>(R.id.albumDetailPhotos)
        val progressAlbumDetails = view.findViewById<ProgressBar>(R.id.progressAlbumDetails)

        progressAlbumDetails.visibility = View.VISIBLE

        val callback = object: Callback<Album> {
            override fun onFailure(call: Call<Album>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getAlbum()...")
                albumDetailName.setText("A problem occured fetching the Album details...")
                albumDetailName.setTextColor(Color.RED)
                progressAlbumDetails.visibility = View.INVISIBLE
            }

            override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
                val albumDetailPhotosView = view.findViewById<GridView>(R.id.albumDetailPhotos)
                Log.d(TAG, "response?.body()?.name: ${response?.body()?.name}")

                progressAlbumDetails.visibility = View.INVISIBLE

                albumDetailName.setText(response?.body()?.name)
                albumDetailDescription.setText(response?.body()?.description)
                val df = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                albumDetailCreatedAt.setText(df.format(response?.body()?.created_at))
                albumDetailUpdatedAt.setText(df.format(response?.body()?.updated_at))

                val albumPhotoAdapter = AlbumPhotoAdapter(activity, response?.body()?.photo_set!!)
                albumDetailPhotosView.setAdapter(albumPhotoAdapter)
            }

        }
        api.getAlbum(albumId, callback)

        return view
    }
}