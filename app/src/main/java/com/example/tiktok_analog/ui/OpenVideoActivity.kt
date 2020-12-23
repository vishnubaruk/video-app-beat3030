package com.example.tiktok_analog.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.activity_open_video.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class OpenVideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_open_video)

        backArrowButton.setOnClickListener {
            super.onBackPressed()
        }

        if (File("/storage/emulated/0/Download/${intent.getIntExtra("id", 0)}.mp4").exists()) {
            video.setVideoPath(
                "/storage/emulated/0/Download/${
                    intent.getIntExtra(
                        "id",
                        0
                    )
                }.mp4"
            )
            video.setMediaController(MediaController(this))
            video.requestFocus(0)
            video.start()

            progressBar.visibility = View.GONE
        } else {
            downloadFile()
        }


//        download(
//            "http://res.cloudinary.com/kepler88d/video/upload/fl_attachment/8699893.mp4",
//            "file://tiktokanalog/8699893.mp4"
//        )
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

        val openVideoContext = this

        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                video.setVideoPath(
                    "/storage/emulated/0/Download/${
                        intent.getIntExtra(
                            "id",
                            0
                        )
                    }.mp4"
                )
                video.setMediaController(MediaController(openVideoContext))
                video.requestFocus(0)
                video.start()
                unregisterReceiver(this)
                progressBar.visibility = View.GONE
            }
        }

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


}