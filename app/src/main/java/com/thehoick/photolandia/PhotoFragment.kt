package com.thehoick.photolandia

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide

class PhotoFragment: Fragment() {
    val TAG = PhotoFragment::class.java.simpleName
    var imageView: ImageView? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.photo_layout, container, false)
        imageView = view.findViewById(R.id.photo)

        val photo = getArguments().getString("photo")
        Glide.with(this).load(photo).into(imageView!!)

        return view
    }

}