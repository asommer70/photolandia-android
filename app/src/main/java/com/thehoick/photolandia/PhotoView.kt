package com.thehoick.photolandia

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide


class PhotoView : AppCompatActivity() {
    val TAG = PhotoView::class.java.simpleName
    private var imageView: ImageView? = null
    val PHOTO = "PhotoView.PHOTO"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_layout)

        imageView = findViewById(R.id.photo)

        val photo = intent.getStringExtra(PHOTO)
        Glide.with(this).load(photo).into(imageView!!)
    }

    companion object {
        val PHOTO = "PhotoView.PHOTO"
    }
}