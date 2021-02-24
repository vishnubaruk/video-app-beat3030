package com.example.tiktok_analog.ui.menu_screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.activity_profile.*
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.backArrowButton
import kotlinx.android.synthetic.main.activity_profile.sectionTitleText
import org.json.JSONObject


class ProfileActivity : AppCompatActivity() {

    lateinit var userData: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        fillProfileData()

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

        yourProfileTab.setOnClickListener {
            yourProfileTab.backgroundTintList =
                applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)
            yourProfileTab.setTextColor(resources.getColor(R.color.white))

            yourVideosTab.backgroundTintList =
                applicationContext.resources.getColorStateList(R.color.groupUnselected)
            yourVideosTab.setTextColor(resources.getColor(R.color.colorPrimary))

            yourProfileBlock.visibility = View.VISIBLE
            yourVideosBlock.visibility = View.GONE

            sectionTitleText.text = "Ваш профиль"

            editData.visibility = View.VISIBLE
        }

        yourVideosTab.setOnClickListener {
            yourProfileTab.backgroundTintList =
                applicationContext.resources.getColorStateList(R.color.groupUnselected)
            yourProfileTab.setTextColor(resources.getColor(R.color.colorPrimary))

            yourVideosTab.backgroundTintList =
                applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)
            yourVideosTab.setTextColor(resources.getColor(R.color.white))

            yourProfileBlock.visibility = View.GONE
            yourVideosBlock.visibility = View.VISIBLE

            sectionTitleText.text = "Ваши видео"

            editData.visibility = View.GONE

            updateData()
        }

        editData.setOnClickListener {
            Toast.makeText(applicationContext, "Edit data clicked", Toast.LENGTH_SHORT).show()
        }

        profileSwipeRefresh.setOnRefreshListener {
            profileSwipeRefresh.isRefreshing = false
            Toast.makeText(
                applicationContext,
                "Profile page refreshed", Toast.LENGTH_SHORT
            ).show()

            updateData()
        }
    }

    private fun updateData() {
        // updating video stats
        val url =
            "https://kepler88d.pythonanywhere.com/getUploadedVideosStats?email=${userData.email}&phone=${userData.phone}"

        val videoStatsQueue = Volley.newRequestQueue(this)

        val addCommentRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                val result = JSONObject(response)
                videoCount.text = result.getInt("videoCount").toString()
                videoLikeCount.text = result.getInt("likeCount").toString()
                videoViewCount.text = result.getInt("viewCount").toString()
            }
        }, {
            Log.e("VideoStats", "Error at sign in : " + it.message)
        })

        videoStatsQueue.add(addCommentRequest)

        // getting uploaded videos list
        uploadedVideosLayout.removeAllViews()

        val uploadedVideosUrl =
            "https://kepler88d.pythonanywhere.com/getUploadedVideos?email=${userData.email}&phone=${userData.phone}"

        val uploadedVideosQueue = Volley.newRequestQueue(this)

        val uploadedVideosRequest =
            StringRequest(Request.Method.GET, uploadedVideosUrl, { response ->
                run {
                    val result = JSONObject(response).getJSONArray("result")

                    for (index in 0 until result.length()) {
                        addViewToUploadedVideos(
                            videoId = result.getInt(index)
                        )
                    }

                }
            }, {
                Log.e("UploadedVideos", "Error at sign in : " + it.message)
            })

        uploadedVideosQueue.add(uploadedVideosRequest)
    }

    private fun addViewToUploadedVideos(videoId: Int) {
        val newView =
            LayoutInflater.from(applicationContext).inflate(R.layout.fav_video_item, null, false)

        newView.setOnLongClickListener {
            Toast.makeText(applicationContext, "Video $videoId disliked", Toast.LENGTH_SHORT).show()
            true
        }

        newView.setOnClickListener {
            val openVideoIntent = Intent(this, OpenVideoActivity::class.java)

            openVideoIntent.putIntegerArrayListExtra("id", arrayListOf(videoId))

            if (videoId != 0) {
                startActivity(openVideoIntent)
            }
        }

        newView.findViewWithTag<Button>("delete").setOnClickListener {
            uploadedVideosLayout.removeView(newView)
            // dislikeVideo(videoId)
            Toast.makeText(applicationContext, "Video $videoId disliked", Toast.LENGTH_SHORT).show()
        }

        val urlSrc = "https://res.cloudinary.com/kepler88d/video/upload/fl_attachment/$videoId.jpg"

        val previewImage = newView.findViewWithTag<ImageView>("previewImage")

        Picasso
            .get()
            .load(urlSrc)
            .placeholder(R.drawable.rectangle34)
            .into(previewImage)

        val viewCountUrl = "https://kepler88d.pythonanywhere.com/getViewCount?videoId=$videoId"
        val viewQueue = Volley.newRequestQueue(this)

        val viewCountRequest = StringRequest(Request.Method.GET, viewCountUrl, { response ->
            run {
                val result = JSONObject(response)
                newView.findViewWithTag<TextView>("viewCount").text =
                    result.getInt("viewCount").toString()
            }
        }, {
            Log.e("ViewCount", "Error at sign in : " + it.message)
        })

        viewQueue.add(viewCountRequest)

        uploadedVideosLayout.addView(newView)
    }

    private fun fillProfileData() {
        nameText.text = userData.username
        nameTextHeader.text = userData.username

        phoneText.text = userData.phone
        birthDateText.text = userData.birthDate
        cityText.text = userData.city

        emailText.text = userData.email
        emailTextHeader.text = userData.email
    }
}