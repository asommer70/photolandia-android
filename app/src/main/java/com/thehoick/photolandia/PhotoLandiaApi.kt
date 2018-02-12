package com.thehoick.photolandia

import android.content.Context
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import com.android.volley.Request


class PhotoLandiaApi private constructor(context: Context) {
    private var mRequestQueue: RequestQueue? = null

    val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mCtx!!.getApplicationContext())
            }
            return mRequestQueue!!
        }

    init {
        mCtx = context
        mRequestQueue = requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    companion object {
        private var mInstance: PhotoLandiaApi? = null
        private var mCtx: Context? = null

        @Synchronized
        fun getInstance(context: Context): PhotoLandiaApi {
            if (mInstance == null) {
                mInstance = PhotoLandiaApi(context)
            }
            return mInstance!!
        }
    }
}