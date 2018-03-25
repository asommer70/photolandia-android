package com.thehoick.photolandia

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.thehoick.photolandia.database.PhotolandiaDataSource
import com.thehoick.photolandia.models.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PhotoAdapter(private val context: Activity, val photos: List<Photo>?, val serverPhotos: Boolean = false) : BaseAdapter() {
    val TAG = PhotoAdapter::class.java.simpleName
    var images: List<Photo>? = null
    var selectedPhotos = mutableListOf<Photo>()
    val api = Api(this.context)

    init {
        images = photos
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
        val layoutParams = LinearLayout.LayoutParams(520, 500)
        picturesView.setLayoutParams(layoutParams)
        picturesView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))

        picturesView.setOnClickListener {
            val photo = images!![position]

            val photoFragment = PhotoFragment()
            val data = Bundle()
            var photoFragmentName: String
            if (serverPhotos) {
                data.putString("photo", photo.image)
                data.putBoolean("local", false)
                photoFragmentName = "server_photo_fragment"
            } else {
                data.putString("photo", photo.local_path)
                data.putBoolean("local", true)
                photoFragmentName = "local_photo_fragment"
            }
            photoFragment.arguments = data

            val fragmentTransaction = this.context.fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, photoFragment, photoFragmentName)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        if (serverPhotos) {
            picturesView.setOnLongClickListener {
                val photo = images!![position]
                it.setPadding(4, 2,4 , 2)
                it.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSelect))
                this.selectedPhotos.add(photo)
                true
            }

            val fab = context.findViewById<FloatingActionButton>(R.id.fab)
            fab.setImageDrawable(context.getDrawable(R.drawable.ic_add_album_icon))
            fab.setOnClickListener {
                val ids = selectedPhotos.map { it.id }
                val idsString = ids.joinToString( ",")

                val callback = object: Callback<AlbumResult> {
                    override fun onFailure(call: Call<AlbumResult>?, t: Throwable?) {
                        Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<AlbumResult>?, response: Response<AlbumResult>?) {
                        val albums = response?.body()?.results!!
                        val albumNames = albums.map { "${it.id},${it.name}"} as ArrayList<String>

                        val albumDialogFragment = AlbumDialogFragment()
                        val bundle = Bundle()
                        bundle.putStringArrayList(albumDialogFragment.albums, albumNames)
                        bundle.putString(albumDialogFragment.photoIds, idsString)
                        albumDialogFragment.arguments = bundle
                        albumDialogFragment.show(context.fragmentManager, "AlbumsDialog")

                        // Deselect photos.
                        notifyDataSetChanged()
                    }

                }
                api.getAlbums(callback)
            }
        } else {
            // Try to upload a single photo.
            picturesView.setOnLongClickListener {
                val photo = images!![position]
                it.setPadding(4, 2,4 , 2)
                it.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSelect))
                this.selectedPhotos.add(photo)

                val callback = object: Callback<Photo> {
                    override fun onFailure(call: Call<Photo>?, t: Throwable?) {
                        Toast.makeText(context, "A problem occurred inside callback for getAlbums()...", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<Photo>?, response: Response<Photo>?) {
                        if (response?.body()?.local_id != null) {
                            // Update the local database with Photo details.
                            val serverPhoto = response.body()
                            val dataSource = PhotolandiaDataSource(context)
                            dataSource.updatePhoto(serverPhoto!!)

                            images = images!!.filter { it.local_path != serverPhoto.local_path }
                            notifyDataSetChanged()

                            Snackbar.make(picturesView, "${photo.local_filename} updated.", Snackbar.LENGTH_SHORT).show()
                        } else {
                            // Upload the photo.
                            LocalPhotosFragment().upload(photo, context)

                            images = images!!.filter { it.local_path != photo.local_path }
                            notifyDataSetChanged()
                        }
                    }

                }
                api.getPhoto(photo.local_id!!, callback)

                true
            }
        }

        if (serverPhotos) {
            Glide.with(context).load(images!![position].image).into(picturesView)
        } else {
            Glide.with(context).load(images!![position].local_path).into(picturesView)
        }

        return picturesView
    }
}