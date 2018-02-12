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



class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private val READ_EXTERNAL_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//        val permissionCheck = ContextCompat.checkSelfPermission(thisActivity,
//                Manifest.permission.WRITE_CALENDAR)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        } else {
            val photos = findViewById(R.id.photos) as GridView
            photos.adapter = PhotoAdapter(this);
        }
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
}
