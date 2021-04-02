package com.example.tiktok_analog.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.example.tiktok_analog.ui.menuscreens.*
import com.example.tiktok_analog.util.dataclasses.AppConfig
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter.*
import kotlinx.android.synthetic.main.menu.*
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    private var isMenuOpened = false
    private var isFilterOpened = false

    private lateinit var userData: User

    private val videoViewList: MutableList<Pair<View, Int>> = arrayListOf()
    private lateinit var requestQueue: RequestQueue

    private var savedLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val userDataFile = applicationContext.getFileStreamPath("userData")

        if (userDataFile != null && userDataFile.exists()) {
            openFileInput("userData").use {
                userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
                nameTextHeader.text = userData.username
                emailTextHeader.text = userData.email
            }
        } else {
            finish()
        }

        updateLocation()

        requestQueue = Volley.newRequestQueue(applicationContext)

        openMenuButton.setOnClickListener {
            openMenu()
        }

        openFilterButton.setOnClickListener {
            openFilter()
        }

        closeMenuButton.setOnClickListener {
            closeMenu()
        }

        closeFilterButton.setOnClickListener {
            closeFilter()
        }

        acceptFilter.setOnClickListener {

            closeFilter()
        }

        openProfileButton.setOnClickListener {
            openProfile()
        }

        favouriteButton.setOnClickListener {
            openFavourite()
        }

        addVideoButton.setOnClickListener {
            openAddVideo()
        }

        broadcastButton.setOnClickListener {
            openBroadcast()
        }

        notificationsButton.setOnClickListener {
            openNotifications()
        }

        logout.setOnClickListener {
            val alertDialog =
                AlertDialog.Builder(this).setTitle("Вы уверены, что хотите выйти из аккаунта?")
                    .setMessage("Это приведет к удалению всех пользовательских данных")
                    .setPositiveButton("Да, я уверен") { _, _ ->
                        deleteFile("userData")
                        finish()
                    }.setNegativeButton("Нет, отмена") { dialog, _ ->
                        dialog.cancel()
                    }.create()
            alertDialog.show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }

        oneMinuteButton.setOnClickListener {
            oneMinuteButton.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_selected)
            threeMinutesButton.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
        }

        threeMinutesButton.setOnClickListener {
            oneMinuteButton.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
            threeMinutesButton.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_selected)
        }

        sortByPopularity.setOnClickListener {
            sortByPopularity.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_selected)
            sortByDate.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
            sortByLength.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
        }

        sortByDate.setOnClickListener {
            sortByPopularity.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
            sortByDate.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_selected)
            sortByLength.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
        }

        sortByLength.setOnClickListener {
            sortByPopularity.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
            sortByDate.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
            sortByLength.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_selected)
        }

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

        fun addPostsToNewsLine(count: Int) {
            getVideos(count)
        }

        newsRoot.viewTreeObserver.addOnScrollChangedListener {
            if (newsRoot.getChildAt(0).bottom <= newsRoot.height + newsRoot.scrollY) {
                if (sectionTitleText.text == "Главная") {
                    addPostsToNewsLine(25)
                }
            }
        }

        newsSwipeRefresh.setOnRefreshListener {
            newsSwipeRefresh.isRefreshing = false
            newsLineLayout.removeAllViews()
            videoViewList.clear()
            addPostsToNewsLine(25)
        }

        addPostsToNewsLine(25)

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                for (i in 0 until videoViewList.size) {
                    val v = videoViewList[i]

                    val url =
                        "https://kepler88d.pythonanywhere.com/videoLikeCount?videoId=${v.second}&email=${userData.email}&phone=${userData.phone}"

                    val videoLikeCountRequest =
                        StringRequest(Request.Method.GET, url, { response ->
                            run {
                                val result = JSONObject(response)
                                v.first.findViewWithTag<ImageView>("likeIcon")
                                    .setBackgroundResource(
                                        if (result.getBoolean("isLiked"))
                                            R.drawable.ic_like
                                        else
                                            R.drawable.ic_baseline_favorite_border_24
                                    )
                                v.first.findViewWithTag<TextView>("likeText").text =
                                    result.getInt("likeCount").toString()
                            }
                        }, {
                            Log.e("VideoLikeCount", "Error at sign in : " + it.message)
                        })

                    requestQueue.add(videoLikeCountRequest)
                }
            }
        }, 0, 10000)
    }

    override fun onBackPressed() {
        if (isFilterOpened) {
            closeFilter()
            return
        }


        if (isMenuOpened) {
            closeMenu()
            return
        }


        super.onBackPressed()
    }

    private fun openNewsLine() {
        newsRoot.visibility = View.VISIBLE
        openFilterButton.visibility = View.VISIBLE
    }

    private fun closeNewsLine() {
        newsRoot.visibility = View.GONE
        openFilterButton.visibility = View.GONE
        closeFilterButton.visibility = View.GONE
    }

    private fun openMenu() {
        closeFilter()
        closeNewsLine()

        openMenuButton.visibility = View.GONE
        closeMenuButton.visibility = View.VISIBLE

        menuRoot.visibility = View.VISIBLE

        isMenuOpened = true

        sectionTitleText.text = "Меню"
    }

    private fun closeMenu() {
        openNewsLine()

        openMenuButton.visibility = View.VISIBLE
        closeMenuButton.visibility = View.GONE

        menuRoot.visibility = View.GONE

        isMenuOpened = false

        sectionTitleText.text = "Главная"
    }

    private fun openFilter() {
        closeNewsLine()

        openFilterButton.visibility = View.GONE
        closeFilterButton.visibility = View.VISIBLE

        filterRoot.visibility = View.VISIBLE

        isFilterOpened = true

        sectionTitleText.text = "Главная"
    }

    private fun closeFilter() {
        openNewsLine()

        openFilterButton.visibility = View.VISIBLE
        closeFilterButton.visibility = View.GONE

        filterRoot.visibility = View.GONE

        isFilterOpened = false
    }

    private fun openProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun openAddVideo() {
        startActivity(Intent(this, AddVideoActivity::class.java))
    }

    private fun openFavourite() {
        startActivity(Intent(this, FavouriteActivity::class.java))
    }

    private fun openBroadcast() {
        startActivity(Intent(this, BroadcastActivity::class.java))
    }

    private fun openNotifications() {
        startActivity(Intent(this, NotificationsActivity::class.java))
    }

    private fun addViewToNewsLine(
        title: String,
        tags: String,
        videoIdList: ArrayList<Int>,
        likeCount: Int,
        length: Int = 90,
    ) {

        val newView: View =
            LayoutInflater.from(applicationContext).inflate(R.layout.video_feed_item, null, false)
        newView.findViewWithTag<TextView>("title").text = title

        val videoId = videoIdList.getOrElse(0) { 0 }

        videoViewList.add(Pair(newView, videoId))

        var formattedTags = ""
        for (i in tags) {
            formattedTags += "#$i  "
        }
        newView.findViewWithTag<TextView>("tags").text = formattedTags

        newView.findViewWithTag<TextView>("likeText").text = "$likeCount"
        newView.findViewWithTag<ConstraintLayout>("likeButton").setOnClickListener {
            val url =
                "https://kepler88d.pythonanywhere.com/likeVideo?videoId=$videoId&email=${userData.email}&phone=${userData.phone}"

            val videoLikeCountRequest = StringRequest(Request.Method.GET, url, { response ->
                run {
                    val result = JSONObject(response)
                    newView.findViewWithTag<ImageView>("likeIcon").setBackgroundResource(
                        if (result.getBoolean("isLiked"))
                            R.drawable.ic_like
                        else
                            R.drawable.ic_baseline_favorite_border_24
                    )
                    newView.findViewWithTag<TextView>("likeText").text =
                        result.getInt("likeCount").toString()
                }
            }, {
                Log.e("LikeVideo", "Error at sign in : " + it.message)
            })

            requestQueue.add(videoLikeCountRequest)
        }

        newView.findViewWithTag<Button>("lengthButton").text =
            "${length / 60}:${if (length % 60 < 10) "0" else ""}${length % 60}"

        val url =
            "https://kepler88d.pythonanywhere.com/videoLikeCount?videoId=${videoId}&email=${userData.email}&phone=${userData.phone}"

        val videoLikeCountRequest =
            StringRequest(Request.Method.GET, url, { response ->
                run {
                    val result = JSONObject(response)
                    newView.findViewWithTag<ImageView>("likeIcon")
                        .setBackgroundResource(
                            if (result.getBoolean("isLiked"))
                                R.drawable.ic_like
                            else
                                R.drawable.ic_baseline_favorite_border_24
                        )
                    newView.findViewWithTag<TextView>("likeText").text =
                        result.getInt("likeCount").toString()
                }
            }, {
                Log.e("VideoLikeCount", "Error at sign in : " + it.message)
            })

        requestQueue.add(videoLikeCountRequest)

        if (videoId != 0) {
            val urlSrc =
                "https://res.cloudinary.com/kepler88d/video/upload/fl_attachment/$videoId.jpg"

            val previewImage = newView.findViewWithTag<ImageView>("previewImage")

            Picasso
                .get()
                .load(urlSrc)
                .placeholder(R.drawable.rectangle4)
                .into(previewImage)
        }

        newsLineLayout.addView(newView)

        newView.setOnClickListener {
            if (videoId != 0) {
                val openVideoIntent = Intent(this, OpenVideoActivity::class.java)
                openVideoIntent.putIntegerArrayListExtra("id", videoIdList)
                startActivity(openVideoIntent)
            }
        }
    }

    private fun getVideos(count: Int) {
        val url = "https://kepler88d.pythonanywhere.com/getVideos?count=$count"

        progressBar.visibility = View.VISIBLE

        val addVideoRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                val videoList = JSONObject(response).getJSONArray("videos")

                val videoIdList: MutableList<Int> = mutableListOf()
                for (index in 0 until videoList.length()) {
                    videoIdList.add(element = videoList.getJSONObject(index).getInt("videoId"))
                }

                for (index in 0 until videoList.length()) {
                    val video = videoList.getJSONObject(index)

                    addViewToNewsLine(
                        title = video.getString("title"),
                        tags = "",
                        videoIdList = ArrayList<Int>(
                            listOf(videoIdList[index]) + videoIdList.filter { it != 0 }
                                .slice(0 until 10).shuffled()
                        ),
                        likeCount = video.getInt("likeCount"),
                        length = video.getInt("length")
                    )
                }
                progressBar.visibility = View.GONE
            }
        }, {
            Log.e("GetVideos", "Error at sign in : " + it.message)
        })

        requestQueue.add(addVideoRequest)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        val locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (savedLocation == null) {
                    savedLocation = location

                    val url =
                        "https://kepler88d.pythonanywhere.com/updateCoordinates?email=${userData.email}&phone=${userData.phone}&coordinates=${location.latitude}:${location.longitude}"

                    requestQueue.add(
                        StringRequest(Request.Method.GET, url, { _ -> run {} }, {
                            Log.e("Does user exist", "Error at sign in : " + it.message)
                        })
                    )

                    Log.d("LocationUpdated", url)
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    private fun getConfig(): AppConfig {
        val configDataFile = applicationContext.getFileStreamPath("appConfig")

        if (configDataFile != null && configDataFile.exists()) {
            openFileInput("appConfig").use {
                return  AppConfig()
            }
        } else {
            return AppConfig()
        }
    }

    private fun setConfig(config: AppConfig) {
//        this.openFileOutput("appConfig", Context.MODE_PRIVATE)
//            .write(config)
    }
}