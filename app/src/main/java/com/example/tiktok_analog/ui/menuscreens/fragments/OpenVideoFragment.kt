package com.example.tiktok_analog.ui.menuscreens.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
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
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.databinding.CommentItemBinding
import com.example.tiktok_analog.databinding.FragmentOpenVideoBinding
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.example.tiktok_analog.util.GlobalDataStorage
import com.example.tiktok_analog.util.dataclasses.AppConfig
import com.example.tiktok_analog.util.enums.SortType
import com.example.tiktok_analog.util.viewpageradapters.ViewPagerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_open_video.*
import org.json.JSONObject
import java.util.*


class OpenVideoFragment : Fragment(R.layout.fragment_open_video) {
    lateinit var userData: User
    var isMenuOpened = false
    var isFilterOpened = false
    private var isMenuFragmentOpened = false
    var isAdDisplayed = false

    private lateinit var requestQueue: RequestQueue
    private lateinit var currentConfig: AppConfig

    private lateinit var videoIdList: List<Int>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private var _binding: FragmentOpenVideoBinding? = null
    private val binding get() = _binding!!

    fun getViewPager2(): ViewPager2 {
        return binding.viewPager2
    }

    private fun nextPage(pageId: Int) {
        binding.viewPager2.doOnLayout {
            binding.viewPager2.setCurrentItem(pageId + 1, true)
        }
    }

    fun pauseVideo() = (binding.viewPager2.adapter as ViewPagerAdapter).pauseVideo()

    private fun resumeVideo() =
        (binding.viewPager2.adapter as ViewPagerAdapter).resumeVideo()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpenVideoBinding.inflate(inflater, container, false)

        requireActivity().openFileInput("userData").use {
            userData = User.fromJson(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        requestQueue = Volley.newRequestQueue(requireActivity().applicationContext)
        videoIdList = requireActivity().intent.getIntegerArrayListExtra("id")!!.toList()

        binding.viewPager2.adapter =
            ViewPagerAdapter(
                videoIdList,
                requireActivity(),
                binding.seekBar,
                binding.progressBar,
                binding.timeCode,
                binding.pauseButton,
                this
            )

        viewPagerAdapter = (binding.viewPager2.adapter as ViewPagerAdapter)
        binding.viewPager2.offscreenPageLimit = 10

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                (binding.viewPager2.adapter as ViewPagerAdapter).setPage(
                    position
                )
                Log.d("DEBUG", position.toString())
            }
        })

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(binding.bottomSheet.bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                hideKeyboard(activity = requireActivity())
            }
        })

        binding.openCommentsButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            if ((binding.viewPager2.adapter as ViewPagerAdapter).isVideoPlaying()) {
                (binding.viewPager2.adapter as ViewPagerAdapter).pauseVideo()
            }

            updateComments()
        }

        binding.backArrowButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.openMenuButton.setOnClickListener { openMenu() }

        binding.openFilterButton.setOnClickListener { openFilter() }
        binding.closeMenuButton.setOnClickListener { closeMenu() }

        binding.closeFilterButton.setOnClickListener { closeFilter() }

        mapOf(
            binding.menuLayout.addVideoButton to AddVideoFragment::class.java,
            binding.menuLayout.broadcastButton to BroadcastFragment::class.java,
            binding.menuLayout.notificationsButton to NotificationsFragment::class.java,
            binding.menuLayout.favouriteButton to FavouriteFragment::class.java,
            binding.menuLayout.openProfileButton to ProfileFragment::class.java
        ).forEach { (k, v) ->
            k.setOnClickListener {
                isMenuFragmentOpened = true
                (requireActivity() as OpenVideoActivity).openFragment(v.newInstance())
            }
        }

        binding.menuLayout.logout.setOnClickListener {
            val alertDialog =
                AlertDialog.Builder(requireActivity())
                    .setTitle("Вы уверены, что хотитеR100 выйти из аккаунта?")
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

        binding.advertisement.skipButton.setOnClickListener {
            hideAdvertisement()
        }

        binding.filterLayout.acceptFilter.setOnClickListener {
            setConfig(currentConfig)
            closeFilter()
        }

        binding.filterLayout.sortByPopularity.setOnClickListener {
            updateFilterButtons(SortType.ByPopularity)
        }

        binding.filterLayout.sortByDate.setOnClickListener {
            updateFilterButtons(SortType.ByDate)
        }

        binding.filterLayout.sortByLength.setOnClickListener {
            updateFilterButtons(SortType.ByLength)
        }

        currentConfig = getConfig()
        updateFilterButtons((requireActivity() as OpenVideoActivity).getConfig().sortType)
        Handler(Looper.getMainLooper()).postDelayed({ displayAdvertisement() }, 500)

        return binding.root
    }

    fun fillVideoData(videoId: Int, videoView: VideoView) {
        binding.pauseButton.setOnClickListener {
            if (videoView.isPlaying) {
                binding.pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                videoView.pause()
                pauseAnimation()
            } else {
                binding.pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                videoView.start()
                playAnimation()
            }
        }

        videoView.setOnClickListener {
            if (videoView.isPlaying) {
                binding.pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                videoView.pause()
                pauseAnimation()
            } else {
                binding.pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                videoView.seekTo(seekBar.progress)
                videoView.start()
                playAnimation()
            }
        }

        videoView.setOnCompletionListener {
            binding.pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
            nextPage(pageId = (binding.viewPager2.adapter as ViewPagerAdapter).currentPosition)
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewPagerAdapter.currentVideoView.seekTo(seekBar.progress)
                }
            }
        })

        binding.bottomSheet.sendButtonBottomSheet.setOnClickListener {
            addComment(binding.bottomSheet.commentTextBottomSheet.text.toString())
            binding.bottomSheet.commentTextBottomSheet.setText("")
        }

        binding.likeButton.setOnClickListener {
            val url = resources.getString(R.string.base_url) +
                    "/likeVideo?" +
                    "videoId=$videoId&" +
                    "email=${userData.email}&" +
                    "phone=${userData.phone}"

            val videoLikeCountRequest = StringRequest(Request.Method.GET, url, { response ->
                run {
                    val result = JSONObject(response)
                    binding.likeButton.setBackgroundResource(
                        if (result.getBoolean("isLiked")) R.drawable.ic_like
                        else R.drawable.ic_baseline_favorite_border_24
                    )
                    binding.likeCount.text = result.getInt("likeCount").toString()
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
                binding.likeButton.setBackgroundResource(
                    if (result.getBoolean("isLiked")) R.drawable.ic_like
                    else R.drawable.ic_baseline_favorite_border_24
                )
                binding.likeCount.text = result.getInt("likeCount").toString()
            }
        }, {
            Log.e("LikeVideo", "Error at sign in : " + it.message)
        })

        requestQueue.add(videoLikeCountRequest)
        val openVideoUrl = resources.getString(R.string.base_url) + "/openVideo?videoId=$videoId"

        val openVideoRequest = StringRequest(Request.Method.GET, openVideoUrl, { response ->
            run {
                val result = JSONObject(response)
                binding.viewCount.text = result.getString("viewCount")
                binding.commentCount.text = result.getString("commentCount")
            }
        }, {
            Log.e("OpenVideo", "Error at sign in : " + it.message)
        })

        requestQueue.add(openVideoRequest)
        GlobalDataStorage.viewVideo()

        if (GlobalDataStorage.getTotalViews() % (requireActivity() as OpenVideoActivity)
                .getConfig().adFrequency == 0
        ) {
            displayAdvertisement()
        }
    }

    fun onBackPressed(elseRunnable: Runnable) {
        if (isMenuOpened.or(isFilterOpened).and(!isMenuFragmentOpened)) {
            closeMenu()
            closeFilter()
            return
        }

        isMenuFragmentOpened = false
        elseRunnable.run()
    }

    private fun displayAdvertisement() {
        isAdDisplayed = true
        Handler(Looper.getMainLooper()).postDelayed({ pauseVideo() }, 0)

        binding.advertisement.root.visibility = View.VISIBLE
        binding.advertisement.advertisementVideoView.visibility = View.VISIBLE
        binding.advertisement.splashScreen.visibility = View.VISIBLE

        val progressBar = binding.progressBar
        val requestUrl = resources.getString(R.string.base_url) + "/openPromotionalVideo"
        progressBar.visibility = View.VISIBLE

        requestQueue.add(StringRequest(Request.Method.GET, requestUrl, { response ->
            run {
                val result = JSONObject(response).getJSONObject("video")
                try {
                    val videoView = binding.advertisement.advertisementVideoView

                    binding.advertisement.maxTimeText.text = formatTime(result.getInt("length"))
                    binding.advertisement.adProgressBar.max = result.getInt("length") * 100

                    Timer().scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            requireActivity().runOnUiThread {
                                binding.advertisement.textView29.text =
                                    formatTime(videoView.currentPosition / 1000)
                                binding.advertisement.adProgressBar.progress =
                                    videoView.currentPosition / 10
                            }
                        }
                    }, 0, 10)

                    videoView.setMediaController(null)
                    videoView.setVideoURI(
                        Uri.parse(
                            resources.getString(R.string.res_url) +
                                    "/${result.getInt("videoId")}.mp4"
                        )
                    )
                    videoView.start()

                    videoView.setOnPreparedListener {
                        progressBar.visibility = View.GONE
                        binding.advertisement.splashScreen.visibility = View.GONE
                    }

                    videoView.setOnCompletionListener {
                        hideAdvertisement()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        requireActivity(),
                        "Error connecting", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, {
            Log.e("OpenAdvertisement", "Error at sign in : " + it.message)
        }))
    }

    private fun hideAdvertisement() {
        isAdDisplayed = false
        binding.advertisement.root.visibility = View.GONE
        binding.advertisement.advertisementVideoView.visibility = View.GONE
        binding.advertisement.advertisementVideoView.stopPlayback()
        resumeVideo()
    }

    private fun updateComments() {
        binding.bottomSheet.commentsContainerBottomSheet.removeAllViews()

        val url = resources.getString(R.string.base_url) +
                "/getComments?videoId=" +
                "${(binding.viewPager2.adapter as ViewPagerAdapter).getCurrentVideoId()}"

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
            (binding.viewPager2.adapter as ViewPagerAdapter).getCurrentVideoId(),
            (binding.viewPager2.adapter as ViewPagerAdapter).currentVideoView
        )
    }


    private fun addCommentView(jsonObject: JSONObject) {
        val viewBinding = CommentItemBinding.inflate(
            layoutInflater,
            binding.bottomSheet.commentsContainerBottomSheet,
            true
        )
        viewBinding.sender.text = jsonObject.getString("authorUsername")
        viewBinding.commentText.text = jsonObject.getString("text")

        viewBinding.likeIcon.setOnClickListener {
            it.setBackgroundResource(R.drawable.ic_like)
        }

        binding.bottomSheet.commentsScrollViewBottomSheet.post {
            binding.bottomSheet.commentsScrollViewBottomSheet.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun addComment(commentText: String) {
        val url = resources.getString(R.string.base_url) +
                "/addComment?videoId=" +
                "${(binding.viewPager2.adapter as ViewPagerAdapter).getCurrentVideoId()}" +
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

    private fun openMenu() {
        userData = readUserData()
        binding.menuLayout.nameTextHeader.text = userData.username
        binding.menuLayout.emailTextHeader.text = userData.email

        (binding.viewPager2.adapter as ViewPagerAdapter).pauseVideo()
        closeFilter()

        binding.openMenuButton.visibility = View.GONE
        binding.closeMenuButton.visibility = View.VISIBLE

        binding.menuLayout.root.visibility = View.VISIBLE
        isMenuOpened = true
        binding.sectionTitleText.text = "Меню"
    }

    private fun closeMenu() {
        if (!isMenuOpened) return

        binding.openMenuButton.visibility = View.VISIBLE
        binding.closeMenuButton.visibility = View.GONE

        binding.menuLayout.root.visibility = View.GONE
        isMenuOpened = false
        binding.sectionTitleText.text = "Главная"
    }

    private fun openFilter() {
        (binding.viewPager2.adapter as ViewPagerAdapter)
            .pauseVideo()

        binding.openFilterButton.visibility = View.GONE
        binding.closeFilterButton.visibility = View.VISIBLE

        binding.filterLayout.root.visibility = View.VISIBLE
        isFilterOpened = true
        binding.sectionTitleText.text = "Фильтр"

        updateFilterButtons(getConfig().sortType)
    }

    private fun closeFilter() {
        if (!isFilterOpened) return

        binding.openFilterButton.visibility = View.VISIBLE
        binding.closeFilterButton.visibility = View.GONE

        binding.filterLayout.root.visibility = View.GONE

        isFilterOpened = false
        binding.sectionTitleText.text = "Главная"
        currentConfig = getConfig()
    }

    private fun getConfig() = (requireActivity() as OpenVideoActivity).getConfig()

    private fun setConfig(config: AppConfig) = (requireActivity() as OpenVideoActivity).setConfig(
        getConfig().copy(sortType = config.sortType)
    )

    private fun playAnimation() {
        binding.bigPlayButton.alpha = 1F
        binding.bigPlayButton
            .animate()
            .alpha(0f)
            .setDuration(500)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }

    private fun pauseAnimation() {
        binding.bigPauseButton.alpha = 1F
        binding.bigPauseButton
            .animate()
            .alpha(0f)
            .setDuration(500)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }

    fun formatTime(seconds: Int) =
        "${seconds / 60}:${if (seconds.toString().length > 1) "" else "0"}$seconds"

    private fun updateFilterButtons(sortType: SortType) {
        mapOf(
            SortType.ByPopularity to binding.filterLayout.sortByPopularity,
            SortType.ByDate to binding.filterLayout.sortByDate,
            SortType.ByLength to binding.filterLayout.sortByLength
        ).forEach { (type, button) ->
            button.background = ContextCompat.getDrawable(
                requireActivity(),
                if (type == sortType) R.drawable.ic_radiobutton_selected
                else R.drawable.ic_radiobutton_notselected
            )

            currentConfig = currentConfig.copy(sortType = type)
        }
    }

    private fun readUserData(): User {
        requireActivity().openFileInput("userData").use {
            return User.fromJson(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }
    }

    private fun writeUserData(data: User): Unit {
        requireActivity().openFileOutput("userData", Context.MODE_PRIVATE)
            .write(data.toJsonString().toByteArray())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}