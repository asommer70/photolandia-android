package com.thehoick.photolandia

import android.content.Context
import com.thehoick.photolandia.models.Photo
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.*

interface PhotolandiaApi {
    @GET("/albums/api")
    fun getAlbums(): Call<AlbumResult>

    @GET("/albums/api/{id}")
    fun getAlbum(@Path("id") albumId: Int): Call<Album>

    @GET("/photos/api")
    fun getPhotos(): Call<PhotosResult>

    @Multipart
    @POST("/photos/api")
    fun uploadImage(
            @Part("albums") albumId: RequestBody,
            @Part("local_filename") localFilename: RequestBody,
            @Part("local_path") localPath: RequestBody,
            @Part("local_id") localId: RequestBody,
            @Part image: MultipartBody.Part
    ): Call<Photo>

    @FormUrlEncoded
    @POST("/albums/api/{id}/add_photos")
    fun addToAlbum(@Path("id") albumId: String, @Field("photo_ids") photo_ids: String): Call<Album>

    @FormUrlEncoded
    @POST("/albums/api/{id}/remove_photos")
    fun removeFromAlbum(@Path("id") albumId: String, @Field("photo_ids") photo_ids: String): Call<Album>

    @FormUrlEncoded
    @POST("/albums/api")
    fun createAlbum(@Field("name") name: String): Call<Album>

    @FormUrlEncoded
    @POST("/api/login")
    fun login(@Field("username") username: String, @Field("password") password: String): Call<User>
}

class Album(val id: Int, val name: String, val description: String, val created_at: Date, val updated_at: Date, val photo_set: Array<Photo>)

class AlbumResult(val count: Float, val next: Int?, val previous: Int?, val results: Array<Album>)

class PhotosResult(val count: Int, val next: String?, val previous: String?, val results: Array<Photo>)

class User(val id: Int, val username: String, val token: String, val message: String)

class Api(val context: Context) {
    val service: PhotolandiaApi
    var token: String? = null
    var baseUrl: String? = null
    private val USER_ID = "user_id"
    private val USERNAME = "username"
    private val TOKEN = "token"


    init {
        val prefs = context.getSharedPreferences(context.getPackageName() + "_preferences", 0)
        token = prefs?.getString(TOKEN, "")
        baseUrl = prefs?.getString("url", "")

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

    fun uploadImage(albumId: RequestBody,
                    localFilename: RequestBody,
                    localPath: RequestBody,
                    localId: RequestBody,
                    image: MultipartBody.Part,
                    callback: Callback<Photo>) {
        val call = service.uploadImage(albumId, localFilename, localPath, localId, image)
        call.enqueue(callback)
    }

    fun addToAlbum(albumId: String, photo_ids: String, callback: Callback<Album>) {
        val call = service.addToAlbum(albumId, photo_ids)
        call.enqueue(callback)
    }

    fun removeFromAlbum(albumId: String, photo_ids: String, callback: Callback<Album>) {
        val call = service.removeFromAlbum(albumId, photo_ids)
        call.enqueue(callback)
    }

    fun createAlbum(name: String, callback: Callback<Album>) {
        val call = service.createAlbum(name)
        call.enqueue(callback)
    }

    fun login(username: String, password: String, callback: Callback<User>) {
        val call = service.login(username, password)
        call.enqueue(callback)
    }
}
