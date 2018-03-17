package com.thehoick.photolandia

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.github.piasy.biv.view.BigImageView
import com.github.piasy.biv.indicator.ProgressIndicator
import com.filippudak.ProgressPieView.ProgressPieView;
import java.util.*


class ProgressPieIndicator : ProgressIndicator {
    val TAG = ProgressPieIndicator::class.java.simpleName
    private var mProgressPieView: ProgressPieView? = null

    override fun getView(parent: BigImageView): View? {
        mProgressPieView = LayoutInflater.from(parent.context)
                .inflate(R.layout.ui_progress_pie_indecator, parent, false) as ProgressPieView
        Log.d(TAG,"getView()...")
        return mProgressPieView
    }

    override fun onStart() {
        // not interested
    }

    override fun onProgress(progress: Int) {
        if (progress < 0 || progress > 100 || mProgressPieView == null) {
            return
        }
        mProgressPieView!!.setProgress(progress)
        mProgressPieView!!.setText(String.format(Locale.getDefault(), "%d%%", progress))
    }

    override fun onFinish() {
        // not interested
    }
}