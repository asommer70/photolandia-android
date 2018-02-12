package com.thehoick.photolandia

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide


class PhotoAdapter(private val context: Activity) : BaseAdapter() {
    var images: ArrayList<String>? = null

    init {
        images = getAllShownImagesPath(context)
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
//        if (convertView == null) {
        picturesView = ImageView(context)
        picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER)
//            picturesView.setLayoutParams(GridView.LayoutParams(270, 270))
//            picturesView.setLayoutParams(GridView.)
//
//        }
//        else {
//            picturesView = convertView as ImageView?
//        }

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