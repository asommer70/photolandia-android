package com.thehoick.photolandia

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.thehoick.photolandia.models.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AlbumPhotoAdapter(private val context: Activity, val albumId: Int, images: Array<Photo>) : BaseAdapter() {
    val TAG = AlbumPhotoAdapter::class.java.simpleName
    var images: Array<Photo>? = null
    var selectedPhotos = mutableListOf<List<String?>>()
    val fab = context.findViewById<FloatingActionButton>(R.id.fab)

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
        val layoutParams = LinearLayout.LayoutParams(520, 500)
        picturesView.setLayoutParams(layoutParams)
        picturesView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))

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
            it.setPadding(4, 2,4 , 2)
            it.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSelect))
            this.selectedPhotos.add(listOf(photo.id.toString(), photo.image, position.toString()))
            fab.setImageDrawable(context.getDrawable(android.R.drawable.ic_menu_delete))
            true
        }


        fab.setOnClickListener {
            val ids = selectedPhotos.map { it[0] }
            val idsString = ids.joinToString( ",")

            val api = Api(this.context)
            val callback = object: Callback<Album> {
                override fun onFailure(call: Call<Album>?, t: Throwable?) {
                    Log.i(TAG, "A problem occurred inside callback for getAlbums()...")
                    Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
                    // Refresh Album photos.
                    val newImages = images!!.toMutableList()
                    var isZero = false
                    for (photo in selectedPhotos) {
                        // For whatever reason there's an artififact of the first image if it's selected for removal first.
                        if (photo[2]!!.toInt().equals(0)) {
                            isZero = true
                        } else {
                            newImages.removeAt(photo[2]!!.toInt())
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
                Log.i(TAG, "A problem occurred inside callback for getAlbums()...")
                Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
                Log.i(TAG, "response?.body: ${response?.body()?.toString()}")
            }

        }
        api.addToAlbum(albumId, photos, callback)
    }
}