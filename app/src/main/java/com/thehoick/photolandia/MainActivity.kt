package com.thehoick.photolandia

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.GridView
import com.thehoick.photolandia.R.*
import android.content.Intent
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.MenuItem
import android.view.MenuInflater




class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private val READ_EXTERNAL_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        } else {
            val photos = findViewById(R.id.photos) as GridView
            photos.adapter = PhotoAdapter(this);
        }

        val prefs = this.getSharedPreferences(this.getPackageName() + "_preferences", 0)
        val url = prefs.getString("url", "")
        Log.d(TAG, "prefs.url: $url")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            READ_EXTERNAL_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "READ_EXTERNAL_STORAGE permission has been denied by user.")

                    // Bring up the home screen.
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    val photos = findViewById(R.id.photos) as GridView
                    photos.adapter = PhotoAdapter(this);
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            fragmentManager.beginTransaction()
                    .addToBackStack("Settings")
                    .replace(android.R.id.content, Settings())
                    .commit()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)

        return super.onCreateOptionsMenu(menu)
    }
}
