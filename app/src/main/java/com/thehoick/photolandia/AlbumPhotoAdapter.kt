package com.thehoick.photolandia

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.os.Bundle


class AlbumPhotoAdapter(private val context: Activity, images: Array<Photo>) : BaseAdapter() {
    var images: Array<Photo>? = null

    init {
//        images = getAllShownImagesPath(context)
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
        picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER)

        picturesView.setOnClickListener {
            val photo = images!![position]

            val photoFragment = PhotoFragment()
            val data = Bundle()//create bundle instance
            data.putString("photo", photo.image)
            photoFragment.setArguments(data)
            val fragmentTransaction = this.context.fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, photoFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        Glide.with(context).load(images!!.get(position).image).into(picturesView)

        return picturesView
    }
}