package com.thehoick.photolandia.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.thehoick.photolandia.models.Photo

class PhotolandiaDataSource(context: Context) {
    val TAG = PhotolandiaDataSource::class.java.simpleName
    val PhotolandiaSqliteHelper = PhotolandiaSqliteHelper(context)

    private fun open(): SQLiteDatabase {
        return PhotolandiaSqliteHelper.readableDatabase
    }

    private fun close(database: SQLiteDatabase) {
        database.close()
    }

    fun createPhoto(photo: Photo) {
        val db = open()

        // Implementation details... maybe don't need to use transactions for simple inserts.
        Log.d(TAG, "createPhoto photo.local_path: ${photo.local_path}")
        val photoValues = ContentValues()
        photoValues.put("id", photo.id)
        photoValues.put("image", photo.image)
        photoValues.put("caption", photo.caption)
        photoValues.put("local_filename", photo.local_filename)
        photoValues.put("local_path", photo.local_path)
        photoValues.put("local_id", photo.local_id)

        val photoId = db.insert("photos", null, photoValues)

        close(db)
    }

    fun getPhoto(local_path: String): Photo? {
        val db = open()

        val columns = arrayOf<String>(
                "_id",
                "id",
                "image",
                "caption",
                "local_filename",
                "local_path",
                "local_id"
        )

        val cursor = db.rawQuery("select * from photos where local_path = \"$local_path\";", null)
        var photo: Photo? = null
        if (cursor.moveToFirst()) {
            do {
                photo = Photo(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("image")),
                        cursor.getString(cursor.getColumnIndex("image")),
                        cursor.getString(cursor.getColumnIndex("caption")),
                        cursor.getString(cursor.getColumnIndex("local_filename")),
                        cursor.getString(cursor.getColumnIndex("local_path")),
                        cursor.getString(cursor.getColumnIndex("local_id"))
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        close(db)
        return photo
    }

    fun getPhotos(): List<Photo> {
        val db = open()

        val columns = arrayOf<String>(
                "_id",
                "id",
                "image",
                "caption",
                "local_filename",
                "local_path",
                "local_id"
        )
        val cursor = db.query(
                "photos",
                columns,
                null,
                null,
                null,
                null,
                null
        )

        var photos = arrayListOf<Photo>()
        if (cursor.moveToFirst()) {
            do {
                val photo = Photo(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("image")),
                        cursor.getString(cursor.getColumnIndex("image")),
                        cursor.getString(cursor.getColumnIndex("caption")),
                        cursor.getString(cursor.getColumnIndex("local_filename")),
                        cursor.getString(cursor.getColumnIndex("local_path")),
                        cursor.getString(cursor.getColumnIndex("local_id"))
                )
                photos.add(photo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close(db)
        return photos
    }

    fun getUnuploadedPhotos(): List<Photo> {
        val db = open()

        val columns = arrayOf<String>(
                "_id",
                "id",
                "image",
                "caption",
                "local_filename",
                "local_path",
                "local_id"
        )
        val cursor = db.query(
                "photos",
                columns,
                "id is null",
                null,
                null,
                null,
                null
        )

        var photos = arrayListOf<Photo>()
        if (cursor.moveToFirst()) {
            do {
                val photo = Photo(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("image")),
                        cursor.getString(cursor.getColumnIndex("image")),
                        cursor.getString(cursor.getColumnIndex("caption")),
                        cursor.getString(cursor.getColumnIndex("local_filename")),
                        cursor.getString(cursor.getColumnIndex("local_path")),
                        cursor.getString(cursor.getColumnIndex("local_id"))
                )
                photos.add(photo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close(db)
        return photos
    }


    fun updatePhoto(photo: Photo) {
        val db = open()

        val updatePhotoValues = ContentValues()
        updatePhotoValues.put("id", photo.id)
        updatePhotoValues.put("image", photo.image)
        updatePhotoValues.put("caption", photo.caption)
        updatePhotoValues.put("local_filename", photo.local_filename)
        updatePhotoValues.put("local_path", photo.local_path)
        updatePhotoValues.put("local_id", photo.local_id)

        db.update(
                "photos",
                updatePhotoValues,
                "local_path = \"${photo.local_path}\"",
                null
        )

        close(db)
    }

    fun deletePhoto(local_path: String) {
        val db = open()

        db.delete("photos", "local_path = ${local_path}", null)

        close(db)
    }

}