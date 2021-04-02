package com.example.tiktok_analog.ui.menuscreens.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.squareup.picasso.Picasso
import org.json.JSONObject

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    lateinit var userData: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        requireActivity().openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        fillProfileData(view)

        view.findViewById<ImageButton>(R.id.backArrowButton).setOnClickListener {
            requireActivity().onBackPressed()
        }

        view.findViewById<Button>(R.id.yourProfileTab).setOnClickListener {
            view.findViewById<Button>(R.id.yourProfileTab).backgroundTintList =
                requireActivity().applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)
            view.findViewById<TextView>(R.id.yourProfileTab)
                .setTextColor(resources.getColor(R.color.white))

            view.findViewById<Button>(R.id.yourVideosTab).backgroundTintList =
                requireActivity().applicationContext.resources.getColorStateList(R.color.groupUnselected)
            view.findViewById<Button>(R.id.yourVideosTab)
                .setTextColor(resources.getColor(R.color.colorPrimary))

            view.findViewById<ConstraintLayout>(R.id.yourProfileBlock).visibility = View.VISIBLE
            view.findViewById<ConstraintLayout>(R.id.yourVideosBlock).visibility = View.GONE

            view.findViewById<TextView>(R.id.sectionTitleText).text = "Ваш профиль"

            view.findViewById<ImageButton>(R.id.editData).visibility = View.VISIBLE
        }

        view.findViewById<Button>(R.id.yourVideosTab).setOnClickListener {
            view.findViewById<Button>(R.id.yourProfileTab).backgroundTintList =
                requireActivity().applicationContext.resources.getColorStateList(R.color.groupUnselected)
            view.findViewById<Button>(R.id.yourProfileTab)
                .setTextColor(resources.getColor(R.color.colorPrimary))

            view.findViewById<Button>(R.id.yourVideosTab).backgroundTintList =
                requireActivity().applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)
            view.findViewById<Button>(R.id.yourVideosTab)
                .setTextColor(resources.getColor(R.color.white))

            view.findViewById<ConstraintLayout>(R.id.yourProfileBlock).visibility = View.GONE
            view.findViewById<ConstraintLayout>(R.id.yourVideosBlock).visibility = View.VISIBLE

            view.findViewById<TextView>(R.id.sectionTitleText).text = "Ваши видео"

            view.findViewById<ImageButton>(R.id.editData).visibility = View.GONE

            updateData(view)
        }

        view.findViewById<ImageButton>(R.id.editData).setOnClickListener {
            Toast.makeText(
                requireActivity().applicationContext,
                "Edit data clicked",
                Toast.LENGTH_SHORT
            ).show()
        }

        view.findViewById<SwipeRefreshLayout>(R.id.profileSwipeRefresh).setOnRefreshListener {
            view.findViewById<SwipeRefreshLayout>(R.id.profileSwipeRefresh).isRefreshing = false
            Toast.makeText(
                requireActivity().applicationContext,
                "Profile page refreshed", Toast.LENGTH_SHORT
            ).show()

            updateData(view)
        }

        return view
    }

    private fun updateData(view: View) {
        // updating video stats
        val url =
            "https://kepler88d.pythonanywhere.com/getUploadedVideosStats?email=${userData.email}&phone=${userData.phone}"

        val videoStatsQueue = Volley.newRequestQueue(requireActivity().applicationContext)

        val addCommentRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                val result = JSONObject(response)
                view.findViewById<TextView>(R.id.videoCount).text =
                    result.getInt("videoCount").toString()
                view.findViewById<TextView>(R.id.videoLikeCount).text =
                    result.getInt("likeCount").toString()
                view.findViewById<TextView>(R.id.videoViewCount).text =
                    result.getInt("viewCount").toString()
            }
        }, {
            Log.e("VideoStats", "Error at sign in : " + it.message)
        })

        videoStatsQueue.add(addCommentRequest)

        view.findViewById<GridLayout>(R.id.uploadedVideosLayout).removeAllViews()

        val uploadedVideosUrl = "https://kepler88d.pythonanywhere.com/getUploadedVideos?" +
                "email=${userData.email}&" +
                "phone=${userData.phone}"

        val uploadedVideosQueue = Volley.newRequestQueue(requireActivity().applicationContext)

        val uploadedVideosRequest =
            StringRequest(Request.Method.GET, uploadedVideosUrl, { response ->
                run {
                    val result = JSONObject(response).getJSONArray("result")

                    for (index in 0 until result.length()) {
                        addViewToUploadedVideos(
                            videoId = result.getInt(index),
                            view
                        )
                    }

                }
            }, {
                Log.e("UploadedVideos", "Error at sign in : " + it.message)
            })

        uploadedVideosQueue.add(uploadedVideosRequest)
    }

    private fun addViewToUploadedVideos(videoId: Int, view: View) {
        val newView =
            LayoutInflater.from(requireActivity().applicationContext)
                .inflate(R.layout.fav_video_item, null, false)

        newView.setOnLongClickListener {
            Toast.makeText(
                requireActivity().applicationContext,
                "Video $videoId disliked",
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        newView.setOnClickListener {
            val openVideoIntent =
                Intent(requireActivity().applicationContext, OpenVideoActivity::class.java)

            openVideoIntent.putIntegerArrayListExtra("id", arrayListOf(videoId))

            if (videoId != 0) {
                startActivity(openVideoIntent)
            }
        }

        newView.findViewWithTag<Button>("delete").setOnClickListener {
            view.findViewById<GridLayout>(R.id.uploadedVideosLayout).removeView(newView)

            Toast.makeText(
                requireActivity().applicationContext,
                "Video $videoId disliked",
                Toast.LENGTH_SHORT
            ).show()
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

        view.findViewById<GridLayout>(R.id.uploadedVideosLayout).addView(newView)
    }

    private fun fillProfileData(view: View) {
        view.findViewById<TextView>(R.id.nameText).text = userData.username
        view.findViewById<TextView>(R.id.nameTextHeader).text = userData.username

        view.findViewById<TextView>(R.id.phoneText).text = userData.phone
        view.findViewById<TextView>(R.id.birthDateText).text = userData.birthDate
        view.findViewById<TextView>(R.id.cityText).text = userData.city

        view.findViewById<TextView>(R.id.emailText).text = userData.email
        view.findViewById<TextView>(R.id.emailTextHeader).text = userData.email
    }
}