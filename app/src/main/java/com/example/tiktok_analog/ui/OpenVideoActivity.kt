package com.example.tiktok_analog.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.activity_open_video.*
import java.io.File


class OpenVideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_open_video)

        backArrowButton.setOnClickListener {
            super.onBackPressed()
        }

        if (File(
                "/storage/emulated/0/Download/${
                    intent.getIntExtra("id", 0)
                }.mp4"
            ).exists()
        ) {
            playVideoWithPath(
                "/storage/emulated/0/Download/${
                    intent.getIntExtra(
                        "id",
                        0
                    )
                }.mp4"
            )
            progressBar.visibility = View.GONE
        } else {
            downloadFile()
        }
    }

    private fun downloadFile() {
        val url = "https://res.cloudinary.com/kepler88d/video/upload/fl_attachment/${
            intent.getIntExtra(
                "id",
                0
            )
        }.mp4"
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDescription("")
        request.setTitle("Загрузка видео")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "${intent.getIntExtra("id", 0)}.mp4"
        )

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        val currentActivity = this

        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                currentActivity.recreate()

                playVideoWithPath(
                    "/storage/emulated/0/Download/${
                        intent.getIntExtra(
                            "id",
                            0
                        )
                    }.mp4"
                )

                unregisterReceiver(this)
                progressBar.visibility = View.GONE
            }
        }

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


    private fun playVideoWithPath(path: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            val controller = MediaController(this)
            controller.setAnchorView(videoView)
            controller.setMediaPlayer(videoView)
            videoView.setMediaController(controller)
            videoView.setVideoPath(path)

            videoView.setOnPreparedListener { mediaPlayer ->
                val layoutParams = videoView.layoutParams
                val videoWidth = mediaPlayer.videoWidth.toFloat()
                val videoHeight = mediaPlayer.videoHeight.toFloat()
                val viewWidth = videoView.width.toFloat()
                layoutParams.height = (viewWidth * (videoHeight / videoWidth)).toInt()
                videoView.layoutParams = layoutParams

                videoView.start()
            }
        }, 0)
    }
}