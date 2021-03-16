package com.example.tiktok_analog.ui

import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.menu_screens.fragments.CommentsFragment
import com.example.tiktok_analog.ui.menu_screens.fragments.OpenVideoFragment
import com.example.tiktok_analog.ui.menu_screens.fragments.ProfileFragment
import com.example.tiktok_analog.util.TabViewPagerAdapter
import kotlinx.android.synthetic.main.activity_open_video.*
import org.json.JSONObject


class OpenVideoActivity : AppCompatActivity() {
    private lateinit var userData: User

    private lateinit var requestQueue: RequestQueue

    private val profileFragment: ProfileFragment = ProfileFragment()
    private val openVideoFragment: OpenVideoFragment = OpenVideoFragment()
    private val commentsFragment: CommentsFragment = CommentsFragment()

    fun fillVideoData(videoId: Int, videoView: VideoView) {
        openVideoFragment.fillVideoData(videoId, videoView)
    }

    fun getViewPager2(): ViewPager2 {
        return openVideoFragment.getViewPager2()
    }

    fun updateCommentsFragment() {
        commentsFragment.updateComments()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
}