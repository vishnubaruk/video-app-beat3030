package com.example.tiktok_analog.util

import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.tiktok_analog.R
import com.example.tiktok_analog.ui.OpenVideoActivity
import kotlinx.android.synthetic.main.activity_open_video.view.*
import kotlinx.android.synthetic.main.item_page.view.*
import java.io.File


class ViewPagerAdapter(
    private val videoIdList: List<Int>,
    private val activity: Activity,
    private val viewPager2: ViewPager2
) :
    RecyclerView.Adapter<PagerVH>() {
    //    public lateinit var videoView: VideoView

    private val colors = intArrayOf(
        android.R.color.black,
        android.R.color.holo_red_light,
        android.R.color.holo_blue_dark,
        android.R.color.holo_purple
    )

    private lateinit var videoView: VideoView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false))

    override fun getItemCount(): Int = videoIdList.filter { it != 0 }.size

    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
//        videoView = this.findViewWithTag("videoView")
//        container.setBackgroundResource(colors.random())
//        this.findViewWithTag<TextView>("text").text = videoIdList[position].toString()
        videoView = findViewWithTag("videoView")
//        if (File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${videoIdList[position]}.mp4").exists()
//        ) {
//            playVideoWithPath(
//                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${videoIdList[position]}.mp4"
//            )
//        }
    }

    public fun setPage(position: Int) {
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

        videoView.setOnCompletionListener {
            (activity as OpenVideoActivity).nextPage(pageId = position)
        }
    }

    private fun playVideoWithPath(path: String) {
        videoView.setVideoPath(path)

        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()

            val layoutParams = videoView.layoutParams
            val videoWidth = mediaPlayer.videoWidth.toFloat()
            val videoHeight = mediaPlayer.videoHeight.toFloat()
            val viewWidth = videoView.width.toFloat()
            layoutParams.height = (viewWidth * (videoHeight / videoWidth)).toInt()
            videoView.layoutParams = layoutParams

//                seekBar.progress = 0
//                seekBar.max = videoView.duration
//                updateHandler.postDelayed(updateVideoTime, 100)
        }

        videoView.setOnClickListener {
            if (videoView.isPlaying) {
//                    pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                videoView.pause()
            } else {
//                    pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                videoView.start()
            }
        }

        videoView.start()
//            videoView.setOnTouchListener(null)
    }

    private fun downloadFile(videoId: Int) {
        val url = "https://res.cloudinary.com/kepler88d/video/upload/fl_attachment/$videoId.mp4"
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDescription("")
        request.setTitle("Загрузка видео")

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
                activity.recreate()

                playVideoWithPath(
                    "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$videoId.mp4"
                )

                activity.unregisterReceiver(this)
//                progressBar.visibility = View.GONE
            }
        }

        activity.registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)
