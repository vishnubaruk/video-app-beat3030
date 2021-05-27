package com.example.tiktok_analog.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.example.tiktok_analog.ui.menuscreens.*
import com.example.tiktok_analog.ui.menuscreens.fragments.CommentsFragment
import com.example.tiktok_analog.ui.menuscreens.fragments.OpenVideoFragment
import com.example.tiktok_analog.ui.menuscreens.fragments.ProfileFragment
import com.example.tiktok_analog.util.ViewPagerAdapter
import com.example.tiktok_analog.util.dataclasses.AppConfig
import com.example.tiktok_analog.util.enums.SortType
import com.example.tiktok_analog.util.viewpageradapters.TabViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.activity_open_video.*
import kotlinx.android.synthetic.main.filter.*
import kotlinx.android.synthetic.main.fragment_open_video.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject


class OpenVideoActivity : AppCompatActivity() {
    lateinit var userData: User

    private lateinit var requestQueue: RequestQueue
    private lateinit var currentConfig: AppConfig

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
            // will handle this error later (no)
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

        tabViewPager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    if (position != 1) {
                        openVideoFragment.pauseVideo()
                    } else {
                        (viewPager2.adapter as ViewPagerAdapter).currentVideoView.requestFocus()
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {}
            }
        )
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

    private fun updateFilterButtons(sortType: SortType) {
        currentConfig = currentConfig.copy(sortType = sortType)

        for (entry: Map.Entry<SortType, Button> in mapOf(
            SortType.ByPopularity to sortByPopularity,
            SortType.ByDate to sortByDate,
            SortType.ByLength to sortByLength
        )) {
            entry.value.background = applicationContext.resources.getDrawable(
                if (entry.key == sortType) {
                    R.drawable.ic_radiobutton_selected
                } else {
                    R.drawable.ic_radiobutton_notselected
                }
            )
        }
    }
}