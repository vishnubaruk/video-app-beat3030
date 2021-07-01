package com.example.tiktok_analog.util.viewpageradapters

import android.app.Activity
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tiktok_analog.R
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.example.tiktok_analog.ui.menuscreens.fragments.OpenVideoFragment
import kotlinx.android.synthetic.main.fragment_open_video.view.*
import java.security.PrivateKey


class ViewPagerAdapter(
    private val videoIdList: List<Int>,
    private val activity: Activity,
    private val seekBar: SeekBar,
    private val timeCode: TextView,
    private val pauseButton: ImageButton,
    private val openVideoFragment: OpenVideoFragment,
    private val loadScreen: View
) :
    RecyclerView.Adapter<VideoViewHolder>() {

    var currentPosition: Int = 0
    private val updateHandler = Handler()

    private val viewHolderList = mutableListOf<VideoViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        updateSeekBar()
        return VideoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        )
    }

    override fun getItemCount(): Int = videoIdList.size

    val currentVideoView: VideoView
        get() = viewHolderList[currentPosition].videoView

    fun getCurrentVideoId(): Int {
        return videoIdList[currentPosition]
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        viewHolderList.add(holder)
    }

    fun setPage(position: Int) {
        if (currentPosition != position) {
            currentVideoView.setOnCompletionListener(null)
            currentVideoView.pause()

            currentPosition = position
        }

        (activity as OpenVideoActivity).updateCommentsFragment()

        val videoId = videoIdList[position]
        playVideoWithId(videoId)

        openVideoFragment.fillVideoData(videoId, viewHolderList[position].videoView)
    }

    fun isVideoPlaying(): Boolean {
        return currentVideoView.isPlaying
    }

    fun pauseVideo() {
        currentVideoView.pause()
        pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
    }

    fun resumeVideo() {
        if (!openVideoFragment.isAdDisplayed &&
            !openVideoFragment.isFilterOpened &&
            !openVideoFragment.isMenuOpened
        ) {
            currentVideoView.start()
            pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        }
    }

    private fun playVideoWithId(id: Int) {
        val link = activity.resources.getString(R.string.res_url) + "/$id.mp4"

        loadScreen.visibility = View.VISIBLE
        currentVideoView.setVideoURI(Uri.parse(link))

        currentVideoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()

            val layoutParams = currentVideoView.layoutParams
            val videoWidth = mediaPlayer.videoWidth.toFloat()
            val videoHeight = mediaPlayer.videoHeight.toFloat()
            val viewWidth = currentVideoView.width.toFloat()

            layoutParams.height = (viewWidth * (videoHeight / videoWidth)).toInt()
            if (layoutParams.height == 0) {
                layoutParams.height = videoHeight.toInt()
            }

            seekBar.progress = 0
            seekBar.max = currentVideoView.duration
            pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)

            Handler(Looper.getMainLooper()).postDelayed({
                loadScreen.visibility = View.GONE
            }, 200)
        }
        currentVideoView.requestFocus()

        if (!openVideoFragment.isAdDisplayed &&
            !openVideoFragment.isFilterOpened &&
            !openVideoFragment.isMenuOpened
        ) {
            currentVideoView.start()
        } else {
            pauseVideo()
        }
    }

    private fun updateSeekBar() {
        updateHandler.postDelayed(updateVideoTime, 10)
    }

    fun removeSeekBarCallbacks() {
        updateHandler.removeCallbacksAndMessages(null)
    }

    fun updateTimeIndicators() {
        val currentPosition: Long = currentVideoView.currentPosition.toLong()

        val sec = currentPosition / 1000 % 60
        timeCode.text =
            "${currentPosition / 60000}:${if (sec.toString().length > 1) "" else "0"}$sec"
    }

    private val updateVideoTime: Runnable by lazy {
        return@lazy object : Runnable {
            override fun run() {
                updateTimeIndicators()
                seekBar.progress = currentVideoView.currentPosition
                updateHandler.postDelayed(this, 10)
                if (openVideoFragment.isAdDisplayed) {
                    pauseVideo()
                }
            }
        }
    }
}

class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val videoView: VideoView = this.itemView.findViewWithTag("videoView")
}
