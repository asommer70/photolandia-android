package com.thehoick.photolandia

import android.Manifest
import android.app.FragmentManager
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.GridView
import com.thehoick.photolandia.R.*
import android.content.Intent
import android.content.SharedPreferences
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private val READ_EXTERNAL_REQUEST_CODE = 101
    private var prefs: SharedPreferences? = null
    var token: String? = null
    var username: String? = null
    private val USER_ID = "user_id"
    private val USERNAME = "username"
    private val TOKEN = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        } else {
            val savedFragment = fragmentManager.findFragmentById(R.id.container)
            if (savedFragment == null) {
                localPhotosFragment()
            } else {

            }

            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        }

        prefs = this.getSharedPreferences(this.getPackageName() + "_preferences", 0)
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
        R.id.settings -> {
            // Open the Settings fragment.
            fragmentManager.beginTransaction()
                    .addToBackStack("Settings")
                    .replace(android.R.id.content, Settings())
                    .commit()
            true
        }
        R.id.login -> {
            // Open the LoginActivity.
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, 100)
            true
        }

        R.id.logout -> {
            // Clear token, user_id, and username from SharedPrefs, and finish the Activity.
            val editor = prefs!!.edit()
            editor.putString(USERNAME, null)
            editor.putString(TOKEN, null)
            editor.putInt(USER_ID, 0)
            editor.apply()
            finish()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)

        token = prefs!!.getString(TOKEN, "")
        username = prefs!!.getString(USERNAME, "")

        // Add welcome message to the menu and logout item if token and username SharedPrefs are set.
        if (!token.equals("") && !username.equals("")) {
            menu.clear()
            menu.add(0, R.id.username, Menu.NONE, "Welcome, " + username)
            menu.add(0, R.id.logout, Menu.NONE, "Logout")
            menu.add(0, R.id.settings, Menu.NONE, "Settings")
        } else {
            menu.removeItem(R.id.username)
            menu.removeItem(R.id.logout)
        }

        return super.onCreateOptionsMenu(menu)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        invalidateOptionsMenu()
    }

    fun localPhotosFragment() {
        val localPhotosFragment = LocalPhotosFragment()
        val fragmentManager = getFragmentManager()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, localPhotosFragment)
        fragmentTransaction.commit()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.local_photos -> {
                localPhotosFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.albums -> {
                Log.d(TAG, "Albums clicked...")
                val albumsFragment = AlbumsFragment()
                val fragmentManager = getFragmentManager()
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, albumsFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.photos -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
