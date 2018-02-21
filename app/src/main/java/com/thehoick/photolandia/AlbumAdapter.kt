//package com.thehoick.photolandia
//
//import android.content.Context
//import android.content.SharedPreferences
//import android.support.v7.app.AppCompatActivity
//import android.support.v7.widget.RecyclerView
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import org.json.JSONArray
//import org.json.JSONObject
//import org.json.JSONException
//
//
//
//
//class AlbumAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    val TAG = AlbumAdapter::class.java.simpleName
//    var prefs: SharedPreferences? = null
//    var albums: ArrayList<JSONObject>? = null
//    var context: Context? = null
//
//    init {
//        prefs = context!!.getSharedPreferences(context!!.getPackageName() + "_preferences", 0)
//        val url = prefs!!.getString("url", "") + "/api/albums"
//
//        // TODO:as get Albums from the server.
//        val queue = PhotoLandiaApi.getInstance(context!!).requestQueue
//        val request = JsonArrayRequest(url,
//        Response.Listener { jsonArray ->
////            albums = jsonArray
//            for (i in 0 until jsonArray.length()) {
////                try {
//                val jsonObject = jsonArray.getJSONObject(i)
//                albums!!.add(jsonObject)
////                } catch (e: JSONException) {
////                    albums.add("Error: " + e.localizedMessage)
////                }
//
//            }
//
//        },
//        Response.ErrorListener { volleyError -> Toast.makeText(context, "Unable to fetch data: " + volleyError.message, Toast.LENGTH_SHORT).show() })
//
//        albums = ArrayList()
//        PhotoLandiaApi.getInstance(context!!).addToRequestQueue(request)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
//        context = parent!!.getContext()
//        val view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.album_item, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return albums!!.size
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
//        (holder as ViewHolder).bindView(position)
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
//        var name: TextView? = null
//        var albumImage: ImageView? = null
//        var createdAt: TextView? = null
//
//        fun ViewHolder(v: View) {
//            name = itemView.findViewById(R.id.name)
//            albumImage = itemView.findViewById(R.id.albumImage)
//            createdAt = itemView.findViewById(R.id.createdAt)
//            itemView.setOnClickListener(this)
//        }
//
//        fun bindView(position: Int) {
//            name!!.setText(albums!![position].get("name").toString())
////            albumImage!!.setImageResource(albums!![position].get("photos")[0])
//            createdAt!!.setText(albums!![position].get("created_at").toString())
//        }
//
//        override fun onClick(v: View) {
//
//        }
//    }
//}