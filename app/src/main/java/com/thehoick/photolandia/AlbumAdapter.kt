package com.thehoick.photolandia

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class AlbumAdapter(val context: Activity, val albums: Array<Album>): BaseAdapter() {
    val TAG = AlbumAdapter::class.java.simpleName
    var albumList: Array<Album>? = null

    init{
        albumList = albums
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView
        var holder = ViewHolder()
        val album = albumList!![position]

        if (row == null) {
            val inflater = context.layoutInflater
            row = inflater!!.inflate(R.layout.album_item, parent, false)
            holder.albumImage = row.findViewById(R.id.albumImage) as ImageView
            holder.albumName = row.findViewById(R.id.albumName) as TextView
            holder.albumCreatedAt = row.findViewById(R.id.albumCreatedAt) as TextView


            row.setTag(holder)

        } else {
            holder = row.getTag() as ViewHolder
        }

        // Set the Album image if there is at least one.
        try {
            Glide.with(context).load(album.photo_set[0].image).into(holder.albumImage!!)
        }
        catch (e: java.lang.ArrayIndexOutOfBoundsException) {
            Glide.with(context).load("https://via.placeholder.com/150x250?text=No%20Image%20Yet...").into(holder.albumImage!!)
        }
        holder.albumName!!.setText(album.name)
        val df = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        holder.albumCreatedAt!!.setText(df.format(album.created_at))

        // Open the AlbumFragment when grid item is clicked.
        row!!.setOnClickListener {
            Log.d(TAG, "Album clicked position: ${position}")
            Log.d(TAG, "Album clicked album.name: ${album.name}")
            val albumFragment = AlbumFragment()
            val data = Bundle()
            data.putInt("album", album.id)
            albumFragment.setArguments(data)
            val fragmentTransaction = this.context.fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, albumFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        return row
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return albumList!!.size
    }

    private inner class ViewHolder: View.OnContextClickListener {
        var albumImage: ImageView? = null
        var albumName: TextView? = null
        var albumCreatedAt: TextView? = null

        override fun onContextClick(v: View?): Boolean {
            Log.d(TAG, "onContextClick v: $v")
//            Log.d(TAG, "Album clicked position: ${position}")
//            Log.d(TAG, "Album clicked album.name: ${album.name}")
//            val albumFragment = AlbumFragment()
//            val data = Bundle()
//            data.putInt("album", album.id)
//            albumFragment.setArguments(data)
//            val fragmentTransaction = this.context.fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.container, albumFragment)
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()
            return true
        }
    }

    fun getAlbums(context: Context) {
        Log.d(TAG, "AlbumAdaptoer.getAlbums()...")
        val api = Api(context)
        val callback = object: Callback<AlbumResult> {
            override fun onFailure(call: Call<AlbumResult>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getAlbums()...")
            }

            override fun onResponse(call: Call<AlbumResult>?, response: Response<AlbumResult>?) {
                Log.d(TAG, "getAlbums() response?.body()?.results.size: ${response?.body()?.results!!.size}")
                this@AlbumAdapter.albumList = response.body()?.results!!
                this@AlbumAdapter.notifyDataSetInvalidated()
                this@AlbumAdapter.notifyDataSetChanged()
            }

        }
        api.getAlbums(callback)
    }
}
