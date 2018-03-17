package com.thehoick.photolandia

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ImageView.ScaleType.FIT_CENTER
import android.widget.Toast
import com.bumptech.glide.Glide
import com.thehoick.photolandia.models.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.KeyEvent.KEYCODE_BACK




class PhotoAdapter(private val context: Activity, val photos: List<Photo>?, val serverPhotos: Boolean = false) : BaseAdapter() {
    val TAG = PhotoAdapter::class.java.simpleName
    var images: List<Photo>? = null
    var selectedPhotos = mutableListOf<Photo>()

    init {
        images = photos
    }

    override fun getCount(): Int {
        return images!!.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val picturesView = ImageView(context)
        picturesView.scaleType = FIT_CENTER

        picturesView.setOnClickListener {
            val photo = images!![position]

            val photoFragment = PhotoFragment()
            val data = Bundle()
            if (serverPhotos) {
                data.putString("photo", photo.image)
                data.putBoolean("local", false)
            } else {
                data.putString("photo", photo.local_path)
                data.putBoolean("local", true)
            }
            photoFragment.arguments = data

            val fragmentTransaction = this.context.fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, photoFragment, "photo_fragment")
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        if (serverPhotos) {
            picturesView.setOnLongClickListener {
                val photo = images!![position]
                it.setPadding(4, 2,4 , 2)
                it.setBackgroundColor(Color.BLACK)
                this.selectedPhotos.add(photo)
                true
            }

            val syncButton = context.findViewById<FloatingActionButton>(R.id.fab)
            syncButton.setImageDrawable(context.getDrawable(R.drawable.ic_add_album_icon))
            syncButton.setOnClickListener {
                val ids = selectedPhotos.map { it.id }
                val idsString = ids.joinToString( ",")

                Log.d(TAG, "idsString: ${idsString}")

                val api = Api(this.context)
                val callback = object: Callback<AlbumResult> {
                    override fun onFailure(call: Call<AlbumResult>?, t: Throwable?) {
                        Log.d(TAG, "A problem occurred inside callback for getAlbums()...")
                        Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<AlbumResult>?, response: Response<AlbumResult>?) {
                        Log.d(TAG, "response?.body()?.results: ${response?.body()?.results}")

                        val albums = response?.body()?.results!!
                        val albumNames = albums.map { "${it.id},${it.name}"} as ArrayList<String>

                        val albumDialogFragment = AlbumDialogFragment()
                        val bundle = Bundle()
                        bundle.putStringArrayList(albumDialogFragment.albums, albumNames)
                        bundle.putString(albumDialogFragment.photoIds, idsString)
                        albumDialogFragment.arguments = bundle
                        Log.d(TAG, "context.fragmentManager: ${context.fragmentManager}")
                        albumDialogFragment.show(context.fragmentManager, "AlbumsDialog")

                        // Deselect photos.
                        notifyDataSetChanged()
                    }

                }
                api.getAlbums(callback)
            }
        }

        if (serverPhotos) {
            Glide.with(context).load(images!![position].image).into(picturesView)
        } else {
            Glide.with(context).load(images!![position].local_path).into(picturesView)
        }

        return picturesView
    }
}