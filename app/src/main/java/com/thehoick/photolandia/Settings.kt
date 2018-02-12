package com.thehoick.photolandia

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class Settings: PreferenceFragment() {

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // Set the background of the view or else it's transparent and on top of the GridView.
        view!!.setBackgroundColor(resources.getColor(android.R.color.white))
        return view
    }
}