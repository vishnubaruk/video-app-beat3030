package com.example.tiktok_analog.ui.menu_screens.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.util.ViewPagerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.json.JSONObject

class OpenVideoFragment : Fragment(R.layout.fragment_open_video) {
    lateinit var userData: User

    private lateinit var requestQueue: RequestQueue
    private lateinit var rootView: View

    private lateinit var videoIdList: List<Int>

    fun getViewPager2(): ViewPager2 {
        return rootView.findViewById(R.id.viewPager2)
    }

    fun nextPage(pageId: Int) {
        rootView.findViewById<ViewPager2>(R.id.viewPager2).doOnLayout {
            rootView.findViewById<ViewPager2>(R.id.viewPager2).setCurrentItem(pageId + 1, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        rootView = view

        requireActivity().openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        requestQueue = Volley.newRequestQueue(requireActivity().applicationContext)

        videoIdList = requireActivity().intent.getIntegerArrayListExtra("id")!!.toList()
        Log.d("videoIdList", videoIdList.toString())

        rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter =
            ViewPagerAdapter(
                videoIdList,
                requireActivity(),
                rootView.findViewById(R.id.seekBar),
                rootView.findViewById(R.id.progressBar),
                rootView.findViewById(R.id.timeCode),
                rootView.findViewById(R.id.pauseButton)
            )

        rootView.findViewById<ViewPager2>(R.id.viewPager2).offscreenPageLimit = 10

        rootView.findViewById<ViewPager2>(R.id.viewPager2)
            .registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    (view.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter).setPage(
                        position
                    )
                    Log.d("DEBUG", position.toString())
                }
            })

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(rootView.findViewById(R.id.bottomSheet))

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                hideKeyboard(activity = requireActivity())
            }
        })

        rootView.findViewById<Button>(R.id.openCommentsButton).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            if ((view.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter).isVideoPlaying()) {
                (view.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter).pauseVideo()
            }

            updateComments()
        }

        rootView.findViewById<ImageButton>(R.id.backArrowButton).setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    fun fillVideoData(videoId: Int, videoView: VideoView) {
        rootView.findViewById<ImageButton>(R.id.pauseButton).setOnClickListener {
            if (videoView.isPlaying) {
                rootView.findViewById<ImageButton>(R.id.pauseButton)
                    .setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                videoView.pause()
            } else {
                rootView.findViewById<ImageButton>(R.id.pauseButton)
                    .setBackgroundResource(R.drawable.ic_baseline_pause_24)
                videoView.start()
            }
        }

        videoView.setOnClickListener {
            if (videoView.isPlaying) {
                rootView.findViewById<ImageButton>(R.id.pauseButton)
                    .setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                videoView.pause()
            } else {
                rootView.findViewById<ImageButton>(R.id.pauseButton)
                    .setBackgroundResource(R.drawable.ic_baseline_pause_24)
                videoView.start()
            }
        }

        videoView.setOnCompletionListener {
            rootView.findViewById<ImageButton>(R.id.pauseButton)
                .setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
            nextPage(pageId = (rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter).currentPosition)
        }

        rootView.findViewById<SeekBar>(R.id.seekBar)
            .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {}

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        videoView.seekTo(progress)
                    }
                }
            })

        rootView.findViewById<Button>(R.id.sendButtonBottomSheet).setOnClickListener {
            addComment(rootView.findViewById<TextView>(R.id.commentTextBottomSheet).text.toString())
            rootView.findViewById<TextView>(R.id.commentTextBottomSheet).text = ""
        }

        rootView.findViewById<Button>(R.id.likeButton).setOnClickListener {
            val url =
                "https://kepler88d.pythonanywhere.com/likeVideo?videoId=" +
                        "$videoId&email=${userData.email}&phone=${userData.phone}"

            val videoLikeCountRequest = StringRequest(Request.Method.GET, url, { response ->
                run {
                    val result = JSONObject(response)
                    rootView.findViewById<Button>(R.id.likeButton).setBackgroundResource(
                        if (result.getBoolean("isLiked"))
                            R.drawable.ic_like
                        else
                            R.drawable.ic_baseline_favorite_border_24
                    )
                    rootView.findViewById<TextView>(R.id.likeCount).text =
                        result.getInt("likeCount").toString()
                }
            }, { Log.e("LikeVideo", "Error at sign in : " + it.message) })

            requestQueue.add(videoLikeCountRequest)
        }

        val url =
            "https://kepler88d.pythonanywhere.com/videoLikeCount?videoId=" +
                    "$videoId&email=${userData.email}&phone=${userData.phone}"

        val videoLikeCountRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                val result = JSONObject(response)
                rootView.findViewById<Button>(R.id.likeButton).setBackgroundResource(
                    if (result.getBoolean("isLiked"))
                        R.drawable.ic_like
                    else
                        R.drawable.ic_baseline_favorite_border_24
                )
                rootView.findViewById<TextView>(R.id.likeCount).text =
                    result.getInt("likeCount").toString()
            }
        }, {
            Log.e("LikeVideo", "Error at sign in : " + it.message)
        })

        requestQueue.add(videoLikeCountRequest)

        val openVideoUrl = "https://kepler88d.pythonanywhere.com/openVideo?videoId=$videoId"

        val openVideoRequest = StringRequest(Request.Method.GET, openVideoUrl, { response ->
            run {
                val result = JSONObject(response)

                rootView.findViewById<TextView>(R.id.viewCount).text = result.getString("viewCount")
                rootView.findViewById<TextView>(R.id.commentCount).text =
                    result.getString("commentCount")
            }
        }, {
            Log.e("OpenVideo", "Error at sign in : " + it.message)
        })

        requestQueue.add(openVideoRequest)
    }

    private fun updateComments() {
        rootView.findViewById<LinearLayout>(R.id.commentsContainerBottomSheet).removeAllViews()

        val url =
            "https://kepler88d.pythonanywhere.com/getComments?videoId=" +
                    "${(rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter).getCurrentVideoId()}"

        val commentRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                val result = JSONObject(response).getJSONArray("result")

                for (index in 0 until result.length()) {
                    addCommentView(result.getJSONObject(index))
                }
            }
        }, { Log.e("Comments", "Error at sign in : " + it.message) })

        requestQueue.add(commentRequest)

        fillVideoData(
            (rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter).getCurrentVideoId(),
            (rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter).currentVideoView
        )
    }

    private fun addCommentView(jsonObject: JSONObject) {
        val newView =
            LayoutInflater.from(requireActivity().applicationContext)
                .inflate(R.layout.comment_item, null, false)
        newView.findViewWithTag<TextView>("sender").text =
            jsonObject.getString("authorUsername")
        newView.findViewWithTag<TextView>("commentText").text =
            jsonObject.getString("text")
        rootView.findViewById<LinearLayout>(R.id.commentsContainerBottomSheet).addView(newView)

        newView.findViewWithTag<ImageView>("likeIcon").setOnClickListener {
            it.setBackgroundResource(R.drawable.ic_like)
        }

        rootView.findViewById<NestedScrollView>(R.id.commentsScrollViewBottomSheet).post {
            rootView.findViewById<NestedScrollView>(R.id.commentsScrollViewBottomSheet)
                .fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun addComment(commentText: String) {
        val url = "https://kepler88d.pythonanywhere.com/addComment?videoId=" +
                "${(rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter).getCurrentVideoId()}" +
                "&commentText=${commentText.trim()}&email=${userData.email}&phone=${userData.phone}"

        val addCommentRequest = StringRequest(Request.Method.GET, url, {
            run {
                updateComments()
            }
        }, {
            Log.e("Add comment", "Error at sign in : " + it.message)
        })

        requestQueue.add(addCommentRequest)
    }

    private fun hideKeyboard(activity: Activity) {
        (activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(
                (if (activity.currentFocus == null) View(activity)
                else activity.currentFocus)!!.windowToken, 0
            )
    }
}