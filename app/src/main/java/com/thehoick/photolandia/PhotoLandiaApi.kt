package com.thehoick.photolandia

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import okhttp3.Interceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.http.*
import java.io.IOException


val baseUrl = "http://gallium:3000"

interface PhotolandiaApi {
    @GET("/albums/api")
    fun getAlbums(): Call<AlbumResult>

    @GET("/albums/api/{id}")
    fun getAlbum(@Path("id") albumId: Int): Call<Album>

    @GET("/photos/api")
    fun getPhotos(): Call<PhotosResult>

    @FormUrlEncoded
    @POST("/api/login")
    fun login(@Field("username") username: String, @Field("password") password: String): Call<User>
}

class Album(val id: Int, val name: String, val description: String, val created_at: Date, val updated_at: Date, val photo_set: Array<Photo>)

class AlbumResult(val count: Float, val next: Int?, val previous: Int?, val results: Array<Album>)

class Photo(val id: Int, val image: String, val caption: String, val createdAt: Date, val updatedAt: Date)

class PhotosResult(val count: Int, val next: String?, val previous: String?, val results: Array<Photo>)

class User(val id: Int, val username: String, val token: String, val message: String)

class Api(val context: Context) {
    val service: PhotolandiaApi
    var token: String? = null
    private val USER_ID = "user_id"
    private val USERNAME = "username"
    private val TOKEN = "token"


    init {
        val prefs = context.getSharedPreferences(context.getPackageName() + "_preferences", 0)
        token = prefs?.getString(TOKEN, "")

        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                        .newBuilder()
                        .addHeader("Authorization", "Token $token")
                        .build()
                return chain.proceed(request)
            }
        })

        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create(PhotolandiaApi::class.java)
    }

    fun getAlbums(callback: Callback<AlbumResult>) {
        val call = service.getAlbums()
        call.enqueue(callback)
    }

    fun getAlbum(id: Int, callback: Callback<Album>) {
        val call = service.getAlbum(id)
        call.enqueue(callback)
    }

    fun getPhotos(callback: Callback<PhotosResult>) {
        val call = service.getPhotos()
        call.enqueue(callback)
    }

    fun login(username: String, password: String, callback: Callback<User>) {
        val call = service.login(username, password)
        call.enqueue(callback)
    }
}
