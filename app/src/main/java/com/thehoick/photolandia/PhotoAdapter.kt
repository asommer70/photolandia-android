package com.thehoick.photolandia

import android.app.Activity
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.widget.*
import android.widget.ImageView.ScaleType.FIT_CENTER
import com.thehoick.photolandia.database.PhotolandiaDataSource
import com.thehoick.photolandia.models.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PhotoAdapter(private val context: Activity, val photoObjs: List<Photo>?, val photos: List<String>?, val photoList: List<List<String>>?) : BaseAdapter() {
    val TAG = PhotoAdapter::class.java.simpleName
    var images: ArrayList<Photo>? = null
    var selectedPhotos = mutableListOf<List<String>>()

    init {
//        if (photos == null) {
//            images = getLocalPhotos(context)
//        } else {
            images = photos as ArrayList<Photo>
//        }
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
            data.putString("photo", photo.local_path)
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

                        // Deselect photos.
                        notifyDataSetChanged()
                    }

                }
                api.getAlbums(callback)
            }
        }

        Glide.with(context).load(images!![position].local_path).into(picturesView)

        return picturesView
    }

//    fun getLocalPhotos(activity: Activity): ArrayList<Photo> {
//        val uri: Uri
//        val cursor: Cursor?
//        val column_index_data: Int
////        val column_index_folder_name: Int
//        val column_index_date_taken: Int
//        val listOfAllImages = ArrayList<Photo>()
////        var absolutePathOfImage: String? = null
////        var imageId: String? = null
//        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//
//        val projection = arrayOf(
//                MediaStore.MediaColumns.DATA,
////                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
//                MediaStore.Images.Media.DATE_TAKEN
//        )
//
//        cursor = activity.contentResolver.query(uri, projection, null, null, null)
//
//        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
////        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
//        column_index_date_taken = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
//
//        while (cursor.moveToNext()) {
//            val absolutePathOfImage = cursor.getString(column_index_data)
//            val filename = absolutePathOfImage.split("/").last()
//            val imageId = cursor.getString(column_index_date_taken)
//            Log.d(TAG, "absolutePathOfImage: $absolutePathOfImage")
//            Log.d(TAG, "imageId: $imageId")
//            Log.d(TAG, "filename: $filename")
//
//
//            val dataSource = PhotolandiaDataSource(context)
//
//            // Check if the photo is in the database.
//            var photo: Photo? = null
//            photo = dataSource.getPhoto(absolutePathOfImage)
////            Log.d(TAG, "Found photo.local_filename: ${photo?.local_filename}")
//            if (photo == null) {
//                photo = Photo(null, null, null, null, filename,
//                        absolutePathOfImage, imageId)
//
//                // Add photo to the database.
//                dataSource.createPhoto(photo)
//                listOfAllImages.add(photo)
//            }
//
//        }
//
//        cursor.close()
//        return listOfAllImages.reversed() as ArrayList<Photo>
//    }
}