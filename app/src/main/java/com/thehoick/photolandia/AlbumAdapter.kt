package com.thehoick.photolandia

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat

class AlbumAdapter(val context: Activity, val albums: Array<Album>): BaseAdapter() {
    val TAG = AlbumAdapter::class.java.simpleName

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView
        var holder = ViewHolder()

        if (row == null) {
            val inflater = context.layoutInflater
            row = inflater!!.inflate(R.layout.album_item, parent, false)
            holder.albumImage = row.findViewById(R.id.albumImage) as ImageView
            holder.albumName = row.findViewById(R.id.albumName) as TextView
            holder.albumCreatedAt = row.findViewById(R.id.albumCreatedAt) as TextView
            row.setTag(holder)

            // Open the AlbumFragment when the Album image is clicked.
            holder.albumImage?.setOnClickListener() {
                val albumFragment = AlbumFragment()
                val data = Bundle()
                data.putInt("album", albums[position].id)
                albumFragment.setArguments(data)
                val fragmentTransaction = this.context.fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, albumFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }

        } else {
            holder = row.getTag() as ViewHolder
        }

        val album = albums[position]

        Glide.with(context).load(album.photo_set[0].image).into(holder.albumImage!!)
        holder.albumName!!.setText(album.name)
        val df = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        holder.albumCreatedAt!!.setText(df.format(album.created_at))

        return row!!
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return albums.size
    }

    private inner class ViewHolder: View.OnContextClickListener {
        var albumImage: ImageView? = null
        var albumName: TextView? = null
        var albumCreatedAt: TextView? = null

        override fun onContextClick(v: View?): Boolean {
            Log.d(TAG, "onContextClick v: $v")
            return true
        }
    }
}
