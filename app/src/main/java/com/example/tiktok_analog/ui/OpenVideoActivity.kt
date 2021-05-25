package com.example.tiktok_analog.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.menuscreens.fragments.CommentsFragment
import com.example.tiktok_analog.ui.menuscreens.fragments.OpenVideoFragment
import com.example.tiktok_analog.ui.menuscreens.fragments.ProfileFragment
import com.example.tiktok_analog.util.dataclasses.AppConfig
import com.example.tiktok_analog.util.viewpageradapters.TabViewPagerAdapter
import kotlinx.android.synthetic.main.activity_open_video.*
import kotlinx.android.synthetic.main.menu.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject


class OpenVideoActivity : AppCompatActivity() {
    private lateinit var userData: User

    private lateinit var requestQueue: RequestQueue

    private val profileFragment: ProfileFragment = ProfileFragment()
    private val openVideoFragment: OpenVideoFragment = OpenVideoFragment()
    private val commentsFragment: CommentsFragment = CommentsFragment()

    private lateinit var config: AppConfig
    private var savedLocation: Location? = null

    fun fillVideoData(videoId: Int, videoView: VideoView) {
        openVideoFragment.fillVideoData(videoId, videoView)
    }

    fun getViewPager2(): ViewPager2 {
        return openVideoFragment.getViewPager2()
    }

    fun updateCommentsFragment() {
        try {
            commentsFragment.updateComments()
        } catch (e: Exception) {
            // will handle this errors later (no)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userDataFile = applicationContext.getFileStreamPath("userData")
        if (userDataFile != null && userDataFile.exists()) {
            openFileInput("userData").use {
                userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
            }
        } else {
            finish()
        }

        updateLocation()
        config = getConfig()

        setContentView(R.layout.activity_open_video)

        openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        requestQueue = Volley.newRequestQueue(applicationContext)
        setupViewPager(tabViewPager)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = TabViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(profileFragment, "Profile")
        adapter.addFragment(openVideoFragment, "Videos")
        adapter.addFragment(commentsFragment, "Comments")
        viewPager.adapter = adapter
        viewPager.currentItem = 1
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        val locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (savedLocation == null) {
                    savedLocation = location

                    val url = resources.getString(R.string.base_url) +
                            "/updateCoordinates?" +
                            "email=${userData.email}&" +
                            "phone=${userData.phone}&" +
                            "coordinates=${location.latitude}:${location.longitude}"

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
                return Json.decodeFromString(it.readBytes().toString(Charsets.UTF_8))
            }
        } else {
            return AppConfig()
        }
    }

    private fun setConfig(config: AppConfig) {
        this.openFileOutput("appConfig", Context.MODE_PRIVATE)
            .write(Json.encodeToString(config).toByteArray())
    }
}