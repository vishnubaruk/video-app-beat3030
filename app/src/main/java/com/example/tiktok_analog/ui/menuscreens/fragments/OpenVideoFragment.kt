package com.example.tiktok_analog.ui.menuscreens.fragments

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
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
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.example.tiktok_analog.util.ViewPagerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_open_video.*
import kotlinx.android.synthetic.main.fragment_open_video.closeFilterButton
import kotlinx.android.synthetic.main.fragment_open_video.closeMenuButton
import kotlinx.android.synthetic.main.fragment_open_video.openFilterButton
import kotlinx.android.synthetic.main.fragment_open_video.openMenuButton
import kotlinx.android.synthetic.main.menu.*
import org.json.JSONObject

class OpenVideoFragment : Fragment(R.layout.fragment_open_video) {
    lateinit var userData: User
    private var isMenuOpened = false
    private var isFilterOpened = false

    private lateinit var requestQueue: RequestQueue
    private lateinit var rootView: View

    private lateinit var videoIdList: List<Int>

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    fun getViewPager2(): ViewPager2 {
        return rootView.findViewById(R.id.viewPager2)
    }

    private fun nextPage(pageId: Int) {
        rootView.findViewById<ViewPager2>(R.id.viewPager2).doOnLayout {
            rootView.findViewById<ViewPager2>(R.id.viewPager2).setCurrentItem(pageId + 1, true)
        }
    }

    fun pauseVideo() =
        (rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter).pauseVideo()

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

//        videoIdList = requireActivity().intent.getIntegerArrayListExtra("id")!!.toList()
        videoIdList = listOf(
            1997380,
            8885413,
            5485667,
            8931796,
            1997380,
            8885413,
            5485667,
            8931796,
            1997380,
            8885413,
            5485667,
            8931796,
            1997380,
            8885413,
            5485667,
            8931796
        )

        rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter =
            ViewPagerAdapter(
                videoIdList,
                requireActivity(),
                rootView.findViewById(R.id.seekBar),
                rootView.findViewById(R.id.progressBar),
                rootView.findViewById(R.id.timeCode),
                rootView.findViewById(R.id.pauseButton)
            )

        viewPagerAdapter =
            rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter

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
        rootView.findViewById<ImageButton>(R.id.openMenuButton).setOnClickListener {
            openMenu()
        }

        rootView.findViewById<ImageButton>(R.id.openFilterButton).setOnClickListener {
            openFilter()
        }

        rootView.findViewById<ImageButton>(R.id.closeMenuButton).setOnClickListener {
            closeMenu()
        }

        rootView.findViewById<ImageButton>(R.id.closeFilterButton).setOnClickListener {
            closeFilter()
        }

        rootView.findViewById<Button>(R.id.logout).setOnClickListener {
            val alertDialog =
                AlertDialog.Builder(requireActivity())
                    .setTitle("Вы уверены, что хотите выйти из аккаунта?")
                    .setMessage("Это приведет к удалению всех пользовательских данных")
                    .setPositiveButton("Да, я уверен") { _, _ ->
                        requireActivity().deleteFile("userData")
                        requireActivity().finish()
                    }.setNegativeButton("Нет, отмена") { dialog, _ ->
                        dialog.cancel()
                    }.create()
            alertDialog.show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

        }

        return view
    }

    fun fillVideoData(videoId: Int, videoView: VideoView) {
        rootView.findViewById<ImageButton>(R.id.pauseButton).setOnClickListener {
            if (videoView.isPlaying) {
                rootView.findViewById<ImageButton>(R.id.pauseButton)
                    .setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                videoView.pause()
                pauseAnimation()
            } else {
                rootView.findViewById<ImageButton>(R.id.pauseButton)
                    .setBackgroundResource(R.drawable.ic_baseline_pause_24)
                videoView.start()
                playAnimation()
            }
        }

        videoView.setOnClickListener {
            if (videoView.isPlaying) {
                rootView.findViewById<ImageButton>(R.id.pauseButton)
                    .setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                videoView.pause()
                pauseAnimation()
            } else {
                rootView.findViewById<ImageButton>(R.id.pauseButton)
                    .setBackgroundResource(R.drawable.ic_baseline_pause_24)
                videoView.seekTo(seekBar.progress)
                videoView.start()
                playAnimation()
            }
        }

        videoView.setOnCompletionListener {
            rootView.findViewById<ImageButton>(R.id.pauseButton)
                .setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
            nextPage(pageId = (rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter).currentPosition)
        }

        rootView.findViewById<SeekBar>(R.id.seekBar)
            .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {
//                    viewPagerAdapter.removeSeekBarCallbacks()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
//                    viewPagerAdapter.updateSeekBar()
                }

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        viewPagerAdapter.currentVideoView.seekTo(seekBar.progress)
                    }
                }
            })

        rootView.findViewById<Button>(R.id.sendButtonBottomSheet).setOnClickListener {
            addComment(rootView.findViewById<TextView>(R.id.commentTextBottomSheet).text.toString())
            rootView.findViewById<TextView>(R.id.commentTextBottomSheet).text = ""
        }

        rootView.findViewById<Button>(R.id.likeButton).setOnClickListener {
            val url = resources.getString(R.string.base_url) +
                    "/likeVideo?" +
                    "videoId=$videoId&" +
                    "email=${userData.email}&" +
                    "phone=${userData.phone}"

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

        val url = resources.getString(R.string.base_url) +
                "/videoLikeCount?" +
                "videoId=$videoId&" +
                "email=${userData.email}&" +
                "phone=${userData.phone}"

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

        val openVideoUrl = resources.getString(R.string.base_url) +
                "/openVideo?" +
                "videoId=$videoId"

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

        val url = resources.getString(R.string.base_url) +
                "/getComments?videoId=" +
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

    fun openMenu() {
        (rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter)
            .pauseVideo()
        closeFilter()

        rootView.findViewById<ImageButton>(R.id.openMenuButton).visibility = View.GONE
        rootView.findViewById<ImageButton>(R.id.closeMenuButton).visibility = View.VISIBLE

        rootView.findViewById<View>(R.id.menuLayout).visibility = View.VISIBLE

        isMenuOpened = true

//        sectionTitleText.text = "Меню"
    }

    fun closeMenu() {
        rootView.findViewById<ImageButton>(R.id.openMenuButton).visibility = View.VISIBLE
        rootView.findViewById<ImageButton>(R.id.closeMenuButton).visibility = View.GONE

        rootView.findViewById<View>(R.id.menuLayout).visibility = View.GONE

        isMenuOpened = false

//        sectionTitleText.text = "Главная"
    }

    fun openFilter() {
        (rootView.findViewById<ViewPager2>(R.id.viewPager2).adapter as ViewPagerAdapter)
            .pauseVideo()

        rootView.findViewById<ImageButton>(R.id.openFilterButton).visibility = View.GONE
        rootView.findViewById<ImageButton>(R.id.closeFilterButton).visibility = View.VISIBLE

        rootView.findViewById<View>(R.id.filterLayout).visibility = View.VISIBLE

        isFilterOpened = true
//        sectionTitleText.text = "Главная"

//        updateFilterButtons(getConfig().sortType)
    }

    fun closeFilter() {
        rootView.findViewById<ImageButton>(R.id.openFilterButton).visibility = View.VISIBLE
        rootView.findViewById<ImageButton>(R.id.closeFilterButton).visibility = View.GONE

        rootView.findViewById<View>(R.id.filterLayout).visibility = View.GONE

        isFilterOpened = false
//        currentConfig = getConfig()
    }

    fun playAnimation() {
        rootView.findViewById<ImageView>(R.id.bigPlayButton).alpha = 1F
        rootView.findViewById<ImageView>(R.id.bigPlayButton)
            .animate()
            .alpha(0f)
            .setDuration(500)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }

    fun pauseAnimation() {
        rootView.findViewById<ImageView>(R.id.bigPauseButton).alpha = 1F
        rootView.findViewById<ImageView>(R.id.bigPauseButton)
            .animate()
            .alpha(0f)
            .setDuration(500)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }
}