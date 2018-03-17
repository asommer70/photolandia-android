package com.thehoick.photolandia

import android.app.Fragment
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.github.piasy.biv.view.BigImageView
import java.io.File
import java.lang.Exception


class PhotoFragment: Fragment() {
    val TAG = PhotoFragment::class.java.simpleName
    var imageView: BigImageView? = null
    var photoProgress: ProgressBar? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        BigImageViewer.initialize(GlideImageLoader.with(activity.application))

        val view = inflater!!.inflate(R.layout.photo_layout, container, false)
        imageView = view.findViewById(R.id.photo)

        photoProgress = view.findViewById<ProgressBar>(R.id.photoProgress)
        photoProgress?.visibility = VISIBLE
        imageView?.visibility = INVISIBLE
//        val photoProgress = ProgressBar(activity, null, android.R.attr.progressBarStyleSmall)


//        val photoLayout = view.findViewById<LinearLayout>(R.id.photoLayout)

//        photoLayout.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
////                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                or View.SYSTEM_UI_FLAG_IMMERSIVE)

        activity.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN == View.SYSTEM_UI_FLAG_FULLSCREEN

//        imageView!!.systemUiVisibility = (
//            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            or View.SYSTEM_UI_FLAG_IMMERSIVE
//        )

        val photo = arguments.getString("photo")
        val local = arguments.getBoolean("local")

        if (local) {
            imageView?.showImage(Uri.parse("file://$photo"))
        } else {
            imageView?.showImage(Uri.parse(photo))
        }

        val imageLoaderCallback = object: ImageLoader.Callback {
            override fun onFinish() {
            }

            override fun onSuccess(image: File?) {
                Log.d(TAG, "imageLoaderCallback onSuccess()...")
                photoProgress?.visibility = INVISIBLE
                imageView?.visibility = VISIBLE
            }

            override fun onFail(error: Exception?) {
            }

            override fun onCacheHit(image: File?) {
            }

            override fun onCacheMiss(image: File?) {
            }

            override fun onProgress(progress: Int) {
            }

            override fun onStart() {
            }
        }

//        imageView?.setProgressIndicator(ProgressPieIndicator())
        imageView?.setImageLoaderCallback(imageLoaderCallback);
        return view
    }

}