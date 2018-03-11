package com.thehoick.photolandia

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import org.w3c.dom.Text

class NeedToLoginFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.activity_main, container, false)

        val message = view.findViewById<TextView>(R.id.message)
        message.setText(getString(R.string.please_login))
        message.visibility = VISIBLE
        return view
    }
}