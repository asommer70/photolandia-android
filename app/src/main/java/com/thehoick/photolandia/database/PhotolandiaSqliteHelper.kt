package com.thehoick.photolandia.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val DB_NAME = "photolandia.db"
val DB_VERSION = 1

class PhotolandiaSqliteHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createPhotos = """create table photos (
                _id integer primary key autoincrement,
                id integer,
                image text,
                filename text,
                caption text,
                local_filename text,
                local_path text,
                local_id text,
                created_at text,
                updated_at text
            );
        """
        db?.execSQL(createPhotos)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}