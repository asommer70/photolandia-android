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


class AlbumDialogFragment : DialogFragment() {
    val TAG = AlbumDialogFragment::class.java.simpleName
    val albums = "ALBUMS"
    val photoIds = "PHOTO_IDS"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(getActivity())

        var albumNames = arguments.getStringArrayList(albums).toTypedArray()
        val albumIds = albumNames.map { it.split(",")[0] }
        albumNames = albumNames.map { it.split(",")[1] }.toTypedArray()
        val photos = arguments.getString(photoIds)

        builder.setTitle(getString(R.string.add_photos_to_album))
                .setItems(albumNames, DialogInterface.OnClickListener { dialog, which ->
                    Log.d(TAG, "which $which, albumIds[which]: ${albumIds[which]}")
                    Log.d(TAG, "/albums/api/${albumIds[which]}&photo_ids=${photos}")
                    addPhotosToAlbum(albumIds[which], photos)
                })

        return builder.create()
    }

    fun addPhotosToAlbum(albumId: String, photos: String) {
        val api = Api(this.context)
        val callback = object: Callback<Album> {
            override fun onFailure(call: Call<Album>?, t: Throwable?) {
                Log.i(TAG, "A problem occurred inside callback for getAlbums()...")
                Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Album>?, response: Response<Album>?) {
                Log.i(TAG, "response?.body: ${response?.body()?.toString()}")
            }

        }
        api.addToAlbum(albumId, photos, callback)
    }
}