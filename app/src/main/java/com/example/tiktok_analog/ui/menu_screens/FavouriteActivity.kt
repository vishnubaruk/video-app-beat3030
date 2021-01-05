package com.example.tiktok_analog.ui.menu_screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_favourite.*
import org.json.JSONObject

class FavouriteActivity : AppCompatActivity() {
    private lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

        fillFavourites()

//        notificationsScrollView.viewTreeObserver.addOnScrollChangedListener {
//            if (notificationsScrollView.getChildAt(0).bottom <=
//                notificationsScrollView.height + notificationsScrollView.scrollY
//            ) {
//                fillFavourites()
//            }
//        }

        favouriteSwipeRefresh.setOnRefreshListener {
            favouriteSwipeRefresh.isRefreshing = false
            Toast.makeText(applicationContext, "Favourite Updated", Toast.LENGTH_SHORT).show()

            fillFavourites()
        }
    }

    private fun fillFavourites() {
        favouriteLayout.removeAllViews()

        val fillFavouritesQueue = Volley.newRequestQueue(this)

        val url =
            "https://kepler88d.pythonanywhere.com/getFavourite?email=${userData.email}&${userData.phone}"

        progressBar.visibility = View.VISIBLE

        val getFavouritesRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                val result = JSONObject(response).getJSONArray("result")
                for (index in 0 until result.length()) {
                    addViewToFavourite(
                        id = result.getInt(index)
                    )
                    // likeCount = video.getInt("likeCount"))
                }
                progressBar.visibility = View.GONE
            }
        }, {
            Log.e("GetFavourites", "Error at sign in : " + it.message)
        })

        fillFavouritesQueue.add(getFavouritesRequest)
    }

    private fun addViewToFavourite(id: Int) {
        val newView =
            LayoutInflater.from(applicationContext).inflate(R.layout.fav_video_item, null, false)

        newView.setOnLongClickListener {
            Toast.makeText(applicationContext, "Video $id disliked", Toast.LENGTH_SHORT).show()
            true
        }

        newView.setOnClickListener {
            val openVideoIntent = Intent(this, OpenVideoActivity::class.java)

            openVideoIntent.putExtra("id", id)

            if (id != 0) {
                startActivity(openVideoIntent)
            }
        }

        newView.findViewWithTag<Button>("delete").setOnClickListener {
            favouriteLayout.removeView(newView)
            Toast.makeText(applicationContext, "Video $id disliked", Toast.LENGTH_SHORT).show()
        }

        val urlSrc = "https://res.cloudinary.com/kepler88d/video/upload/fl_attachment/$id.jpg"

        Picasso.get().load(urlSrc).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                Log.d("DEBUG", urlSrc)
                newView.findViewWithTag<ImageView>("previewImage").setImageDrawable(
                    BitmapDrawable(
                        resources, bitmap
                    )
                )
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.e("PicassoError", e?.stackTraceToString())
            }
        })

//        for (i in 0..2) {
//            val newView =
//                LayoutInflater.from(applicationContext)
//                    .inflate(R.layout.fav_video_item, null, false)
//
//            newView.findViewWithTag<TextView>("viewCount").text = viewCount.toString()
//
//            newView.findViewWithTag<ImageView>("previewImage").setImageResource(imageId)
//            newViewLine.findViewWithTag<LinearLayout>("root").addView(newView)
//        }

//        val favLine =
//            LayoutInflater.from(applicationContext).inflate(R.layout.fav_line, null, false)
//        favLine.findViewWithTag<LinearLayout>("root").addView(newView)
        favouriteLayout.addView(newView)
    }
}