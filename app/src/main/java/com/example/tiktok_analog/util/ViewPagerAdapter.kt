package com.example.tiktok_analog.util

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tiktok_analog.R
import com.example.tiktok_analog.ui.OpenVideoActivity
import java.io.File


class ViewPagerAdapter(
    private val videoIdList: List<Int>,
    private val activity: Activity,
    private val seekBar: SeekBar,
    private val progressBar: ProgressBar,
    private val timeCode: TextView,
    private val pauseButton: ImageButton
) :
    RecyclerView.Adapter<VideoViewHolder>() {
    //    public lateinit var videoView: VideoView

    private val colors = intArrayOf(
        android.R.color.black,
        android.R.color.holo_red_light,
        android.R.color.holo_blue_dark,
        android.R.color.holo_purple
    )

    var currentPosition: Int = 0

    private val updateHandler = Handler()

    private val viewHolderList = mutableListOf<VideoViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        Log.d("DEBUG", "New viewHolder created")
        return VideoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        )
    }

    override fun getItemCount(): Int = videoIdList.size

    public val currentVideoView: VideoView
        get() = viewHolderList[currentPosition].videoView

    fun getCurrentVideoId(): Int {
        return videoIdList[currentPosition]
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        Log.d("DEBUG", "New viewHolder bind")
        viewHolderList.add(holder)
    }

    fun setPage(position: Int) {
        if (currentPosition != position) {
            currentVideoView.setOnCompletionListener(null)
            currentVideoView.pause()

            currentPosition = position
        }

        val videoId = videoIdList[position]
        Log.d("DEBUG", videoId.toString())

        if (File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$videoId.mp4").exists()
        ) {
            playVideoWithPath(
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$videoId.mp4"
            )
        } else {
            downloadFile(videoIdList[position])
        }

//        currentVideoView.setOnCompletionListener {
//            pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
//            (activity as OpenVideoActivity).nextPage(pageId = position)
//        }

        (activity as OpenVideoActivity).fillVideoData(videoId, viewHolderList[position].videoView)

//        currentVideoView.setOnClickListener {
//            if (currentVideoView.isPlaying) {
//                pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
//                currentVideoView.pause()
//            } else {
//                pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)
//                currentVideoView.start()
//            }
//        }

        updateHandler.removeCallbacksAndMessages(null)
        updateHandler.postDelayed(updateVideoTime, 100)
    }

    fun isVideoPlaying(): Boolean {
        return currentVideoView.isPlaying
    }

    fun pauseVideo() {
        currentVideoView.pause()
        pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
    }

    private fun playVideoWithPath(path: String) {
        progressBar.visibility = View.GONE
        currentVideoView.setVideoPath(path)

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
            // layoutParams.height = if (videoHeight.toInt() != 0) videoHeight.toInt() else 1000

            Log.d(
                "VIDEO",
                "layoutParams.height = ${layoutParams.height} " +
                        "videoWidth = ${mediaPlayer.videoWidth.toFloat()} " +
                        "videoHeight = ${mediaPlayer.videoHeight.toFloat()}"
            )
            // videoView.layoutParams = layoutParams

            seekBar.progress = 0
            seekBar.max = currentVideoView.duration
            pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        }

        viewHolderList[currentPosition].videoView.start()
//            videoView.setOnTouchListener(null)
    }

    private fun downloadFile(videoId: Int) {
        val url = "https://res.cloudinary.com/kepler88d/video/upload/fl_attachment/$videoId.mp4"
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDescription("")
        request.setTitle("Загрузка видео")
        progressBar.visibility = View.VISIBLE

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "$videoId.mp4"
        )

        val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                playVideoWithPath(
                    "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$videoId.mp4"
                )

                activity.unregisterReceiver(this)
                progressBar.visibility = View.GONE
            }
        }

        activity.registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    private val updateVideoTime: Runnable by lazy {
        return@lazy object : Runnable {
            override fun run() {
                val currentPosition: Long = currentVideoView.currentPosition.toLong()
                seekBar.progress = currentPosition.toInt()

                val sec = currentPosition / 1000 % 60
                timeCode.text =
                    "${currentPosition / 60000}:${if (sec.toString().length > 1) "" else "0"}$sec"

                updateHandler.postDelayed(this, 100)
            }
        }
    }
}

class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val videoView: VideoView = this.itemView.findViewWithTag("videoView")
}
