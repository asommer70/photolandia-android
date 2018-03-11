package com.thehoick.photolandia

import android.app.Activity
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View.*
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.GridView
import com.bumptech.glide.request.RequestOptions


class AlbumPhotoAdapter(private val context: Activity, val albumId: Int, images: Array<Photo>) : BaseAdapter() {
    val TAG = AlbumPhotoAdapter::class.java.simpleName
    var images: Array<Photo>? = null
    var selectedPhotos = mutableListOf<List<String>>()
    val syncButton = context.findViewById<FloatingActionButton>(R.id.sync)

    init {
        this.images = images
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
        val picturesView: ImageView
        picturesView = ImageView(context)
//        picturesView.setScaleType(ImageView.ScaleType.FIT_XY)
//        picturesView.setLayoutParams(GridView.LayoutParams(imageWidth, imageWidth))
//        picturesView.setPadding(0, 10, 0, 18)

        picturesView.setOnClickListener {
            val photo = images!![position]

            val photoFragment = PhotoFragment()
            val data = Bundle()
            data.putString("photo", photo.image)
            photoFragment.setArguments(data)
            val fragmentTransaction = this.context.fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, photoFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        picturesView.setOnLongClickListener {
            val photo = images!![position]
            Log.d(TAG, "Long click photo: $photo")
            it.setPadding(4, 2,4 , 2)
            it.setBackgroundColor(Color.BLACK)
            this.selectedPhotos.add(listOf(photo.id.toString(), photo.image, position.toString()))
            syncButton.setImageDrawable(context.getDrawable(android.R.drawable.ic_menu_delete))
            true
        }


        syncButton.setOnClickListener {
            val ids = selectedPhotos.map { it[0] }
            val idsString = ids.joinToString( ",")

            Log.d(TAG, "idsString: ${idsString}")

            val api = Api(this.context)
            val callback = object: Callback<Album> {
                override fun onFailure(call: Call<Album>?, t: Throwable?) {
                    Log.d(TAG, "A problem occurred inside callback for getAlbums()...")
                    Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
                    Log.d(TAG, "response?.body()?: ${response?.body()?.toString()}")

                    // Refresh Album photos.
                    val newImages = images!!.toMutableList()
                    var isZero = false
                    for (photo in selectedPhotos) {
                        // For whatever reason there's an artififact of the first image if it's selected for removal first.
                        if (photo[2].toInt().equals(0)) {
                            isZero = true
                        } else {
                            newImages.removeAt(photo[2].toInt())
                        }
                    }
                    if (isZero) {
                        newImages.removeAt(0)
                    }
                    images = newImages.toTypedArray()
                    notifyDataSetChanged()
                }

            }
            api.removeFromAlbum(albumId.toString(), idsString, callback)
        }

        Glide.with(context).load(images!!.get(position).image).into(picturesView)

        return picturesView
    }

    fun removePhotosFromAlbum(albumId: String, photos: String) {
        val api = Api(this.context)
        val callback = object: Callback<Album> {
            override fun onFailure(call: Call<Album>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getAlbums()...")
                Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
                Log.d(TAG, "response?.body: ${response?.body()?.toString()}")

            }

        }
        api.addToAlbum(albumId, photos, callback)
    }
}