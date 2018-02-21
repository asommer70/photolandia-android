package com.thehoick.photolandia

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView

class LocalPhotosFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)
        val photos = view.findViewById(R.id.photos) as GridView
        photos.adapter = PhotoAdapter(activity);
        return view
    }
}