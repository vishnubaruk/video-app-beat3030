package com.example.tiktok_analog.ui.menu_screens.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        fillProfileData()

        backArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        yourProfileTab.setOnClickListener {
            yourProfileTab.backgroundTintList =
                requireActivity().applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)
            yourProfileTab.setTextColor(resources.getColor(R.color.white))

            yourVideosTab.backgroundTintList =
                requireActivity().applicationContext.resources.getColorStateList(R.color.groupUnselected)
            yourVideosTab.setTextColor(resources.getColor(R.color.colorPrimary))

            yourProfileBlock.visibility = View.VISIBLE
            yourVideosBlock.visibility = View.GONE

            sectionTitleText.text = "Ваш профиль"

            editData.visibility = View.VISIBLE
        }

        yourVideosTab.setOnClickListener {
            yourProfileTab.backgroundTintList =
                requireActivity().applicationContext.resources.getColorStateList(R.color.groupUnselected)
            yourProfileTab.setTextColor(resources.getColor(R.color.colorPrimary))

            yourVideosTab.backgroundTintList =
                requireActivity().applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)
            yourVideosTab.setTextColor(resources.getColor(R.color.white))

            yourProfileBlock.visibility = View.GONE
            yourVideosBlock.visibility = View.VISIBLE

            sectionTitleText.text = "Ваши видео"

            editData.visibility = View.GONE

            updateData()
        }

        editData.setOnClickListener {
            Toast.makeText(requireActivity().applicationContext, "Edit data clicked", Toast.LENGTH_SHORT).show()
        }

        profileSwipeRefresh.setOnRefreshListener {
            profileSwipeRefresh.isRefreshing = false
            Toast.makeText(
                requireActivity().applicationContext,
                "Profile page refreshed", Toast.LENGTH_SHORT
            ).show()

            updateData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun updateData() {
        // updating video stats
        val url =
            "https://kepler88d.pythonanywhere.com/getUploadedVideosStats?email=${userData.email}&phone=${userData.phone}"

        val videoStatsQueue = Volley.newRequestQueue(requireActivity().applicationContext)

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

        val uploadedVideosQueue = Volley.newRequestQueue(requireActivity().applicationContext)

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
            LayoutInflater.from(requireActivity().applicationContext).inflate(R.layout.fav_video_item, null, false)

        newView.setOnLongClickListener {
            Toast.makeText(requireActivity().applicationContext, "Video $videoId disliked", Toast.LENGTH_SHORT).show()
            true
        }

        newView.setOnClickListener {
            val openVideoIntent = Intent(requireActivity().applicationContext, OpenVideoActivity::class.java)

            openVideoIntent.putIntegerArrayListExtra("id", arrayListOf(videoId))

            if (videoId != 0) {
                startActivity(openVideoIntent)
            }
        }

        newView.findViewWithTag<Button>("delete").setOnClickListener {
            uploadedVideosLayout.removeView(newView)
            // dislikeVideo(videoId)
            Toast.makeText(requireActivity().applicationContext, "Video $videoId disliked", Toast.LENGTH_SHORT).show()
        }

        val urlSrc = "https://res.cloudinary.com/kepler88d/video/upload/fl_attachment/$videoId.jpg"

        val previewImage = newView.findViewWithTag<ImageView>("previewImage")

        Picasso
            .get()
            .load(urlSrc)
            .placeholder(R.drawable.rectangle34)
            .into(previewImage)

        val viewCountUrl = "https://kepler88d.pythonanywhere.com/getViewCount?videoId=$videoId"
        val viewQueue = Volley.newRequestQueue(requireActivity().applicationContext)

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