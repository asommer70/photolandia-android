package com.thehoick.photolandia

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.java.simpleName
    private var prefs: SharedPreferences? = null
    private var usernameInput: EditText? = null
    private var passwordInput: EditText? = null
    private var loginButton: Button? = null
    private var statusText: TextView? = null
    private val USER_ID = "user_id"
    private val USERNAME = "username"
    private val TOKEN = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        prefs = this.getSharedPreferences(this.getPackageName() + "_preferences", 0)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        statusText = findViewById(R.id.statusText)

        loginButton!!.setOnClickListener {
            val username = usernameInput!!.text.toString()
            val password = passwordInput!!.text.toString()

            val api = Api(it.context)
            val callback = object: Callback<User> {
                override fun onFailure(call: Call<User>?, t: Throwable?) {
                    Log.d(TAG, "A problem occurred logging in...")
                    // Display login error message.
                    statusText!!.setText("Bad username or password.")
                    statusText!!.visibility = View.VISIBLE
                }

                override fun onResponse(call: Call<User>?, response: Response<User>?) {
                    // Save token, id, and username to SharedPrefs and finish.
                    val editor = prefs!!.edit()
                    editor.putString(USERNAME, response?.body()?.username)
                    editor.putString(TOKEN, response?.body()?.token)
                    if (response?.body()?.id != null) {
                        editor.putInt(USER_ID, response?.body()?.id as Int)
                    }
                    editor.apply()
                    Toast.makeText(this@LoginActivity, response?.body()?.message, Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK, intent);
                    finish()
                }

            }
            api.login(username, password, callback)

        }
    }
}