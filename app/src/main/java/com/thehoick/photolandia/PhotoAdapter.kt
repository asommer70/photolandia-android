package com.thehoick.photolandia

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.os.Bundle
import android.util.Log
import android.widget.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class PhotoAdapter(private val context: Activity, val photos: List<String>?) : BaseAdapter() {
    val TAG = PhotoAdapter::class.java.simpleName
    var images: ArrayList<String>? = null

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
        val picturesView: ImageView
        picturesView = ImageView(context)
        picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER)

        picturesView.setOnClickListener {
            val photo = images!![position]
//            val intent = Intent(context, PhotoView::class.java)
//            intent.putExtra(PhotoView.PHOTO, photo)
//            startActivity(context, intent, null)

            val photoFragment = PhotoFragment()
            val data = Bundle()//create bundle instance
            data.putString("photo", photo)
            photoFragment.setArguments(data)
            val fragmentTransaction = this.context.fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, photoFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        Glide.with(context).load(images!!.get(position)).into(picturesView)

        return picturesView
    }

    private fun getAllShownImagesPath(activity: Activity): ArrayList<String> {
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
}