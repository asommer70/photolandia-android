package com.thehoick.photolandia

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.Glide
import android.content.Intent.getIntent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView


class PhotoView : AppCompatActivity() {
    val TAG = PhotoView::class.java.simpleName
    private var imageView: ImageView? = null
    val PHOTO = "PhotoView.PHOTO"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_layout)

        imageView = findViewById(R.id.photo)

        val photo = intent.getStringExtra(PHOTO)
        Log.d(TAG, "photo: $photo")

        Glide.with(this).load(photo).into(imageView!!)
    }

    companion object {
        val PHOTO = "PhotoView.PHOTO"
    }
}