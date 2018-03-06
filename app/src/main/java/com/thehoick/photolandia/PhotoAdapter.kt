package com.thehoick.photolandia

import android.app.Activity
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.widget.*
import android.widget.ImageView.ScaleType.FIT_CENTER
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class PhotoAdapter(private val context: Activity, val photos: List<String>?, val photoList: List<List<String>>?) : BaseAdapter() {
    val TAG = PhotoAdapter::class.java.simpleName
    var images: ArrayList<String>? = null
    var selectedPhotos = mutableListOf<List<String>>()

    init {
        if (photos == null) {
            images = getAllShownImagesPath(context)
        } else {
            images = photos as ArrayList<String>
        }
    }

    override fun getCount(): Int {
        return images!!.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val picturesView = ImageView(context)
        picturesView.scaleType = FIT_CENTER

        picturesView.setOnClickListener {
            val photo = images!![position]

            val photoFragment = PhotoFragment()
            val data = Bundle()
            data.putString("photo", photo)
            photoFragment.arguments = data
            val fragmentTransaction = this.context.fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, photoFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        if (photoList != null) {
            picturesView.setOnLongClickListener {
                val photo = photoList[position]
                Log.d(TAG, "Long click photo: $photo")
                it.setPadding(4, 2,4 , 2)
                it.setBackgroundColor(Color.BLACK)
                this.selectedPhotos.add(photo)
                true
            }

            val syncButton = context.findViewById<FloatingActionButton>(R.id.sync)
            syncButton.setImageDrawable(context.getDrawable(R.drawable.ic_add_album_icon))
            syncButton.setOnClickListener {
                val ids = selectedPhotos.map { it[0] }
                val idsString = ids.joinToString( ",")

                Log.d(TAG, "idsString: ${idsString}")

                val api = Api(this.context)
                val callback = object: Callback<AlbumResult> {
                    override fun onFailure(call: Call<AlbumResult>?, t: Throwable?) {
                        Log.d(TAG, "A problem occurred inside callback for getAlbums()...")
                        Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<AlbumResult>?, response: Response<AlbumResult>?) {
                        Log.d(TAG, "response?.body()?.results: ${response?.body()?.results}")

                        val albums = response?.body()?.results!!
                        val albumNames = albums.map { "${it.id},${it.name}"} as ArrayList<String>

                        val albumDialogFragment = AlbumDialogFragment()
                        val bundle = Bundle()
                        bundle.putStringArrayList(albumDialogFragment.albums, albumNames)
                        bundle.putString(albumDialogFragment.photoIds, idsString)
                        albumDialogFragment.arguments = bundle
                        Log.d(TAG, "context.fragmentManager: ${context.fragmentManager}")
                        albumDialogFragment.show(context.fragmentManager, "AlbumsDialog")

                        // TODO:as deselect photos.
                    }

                }
                api.getAlbums(callback)
            }
        }

        Glide.with(context).load(images!![position]).into(picturesView)

        return picturesView
    }

    fun getAllShownImagesPath(activity: Activity): ArrayList<String> {
        val uri: Uri
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String? = null
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = activity.contentResolver.query(uri, projection, null, null, null)

        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(absolutePathOfImage)
        }

        cursor.close()
        return listOfAllImages
    }

    fun addPhotosToAlbum(albumId: Int) {

    }
}