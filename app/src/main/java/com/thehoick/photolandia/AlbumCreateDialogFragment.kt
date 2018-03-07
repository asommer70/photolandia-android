package com.thehoick.photolandia

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.text.InputType
import android.widget.EditText



class AlbumCreateDialogFragment : DialogFragment() {
    val TAG = AlbumCreateDialogFragment::class.java.simpleName
    var newAlbumName: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(getActivity())
        builder.setTitle(getString(R.string.create_album))

        // Set up the input
        val input = EditText(context)
//        input.setPadding(4, 4, 4, 4)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { dialog, which -> newAlbumName = input.text.toString(); createAlbum() }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        return builder.create()
    }

    fun createAlbum() {
        Log.d(TAG, "newAlbumName: $newAlbumName")

        val api = Api(this.context)
        val callback = object : Callback<Album> {
            override fun onFailure(call: Call<Album>?, t: Throwable?) {
                Log.d(TAG, "A problem occurred inside callback for getAlbums()...")
                Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
                Log.d(TAG, "response?.body: ${response?.body()?.toString()}")

            }

        }
        api.createAlbum(newAlbumName!!, callback)
    }
}
