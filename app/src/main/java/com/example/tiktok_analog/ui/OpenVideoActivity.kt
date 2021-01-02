package com.example.tiktok_analog.ui

import android.app.Activity
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
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_open_video.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.io.File


class OpenVideoActivity : AppCompatActivity() {

    private val updateHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_open_video)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from<View>(bottom_sheet)

//        // настройка состояний нижнего экрана
//        // настройка состояний нижнего экрана
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.isHideable = false

        val openVideoActivity = this

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                hideKeyboard(activity = openVideoActivity)
            }
        })

        openCommentsButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

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

        videoView.start()

        pauseButton.setOnClickListener {
            if (videoView.isPlaying) {
                pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                videoView.pause()
            } else {
                pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                videoView.start()
            }
        }

        videoView.setOnCompletionListener {
            pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Mean that the seekbar value is changed by user
                    // progress * videoView.duration) / 100
                    videoView.seekTo(progress)
                    Log.d("DEBUG", progress.toString())
                }
            }
        })

        sendButton.setOnClickListener {
            commentText.setText("")
        }

        likeButton.setOnClickListener {
            isLiked = !isLiked

            it.setBackgroundResource(
                if (isLiked) R.drawable.ic_like
                else R.drawable.ic_baseline_favorite_border_24
            )

            likeCount.text = "${if (isLiked) 1 else 0}"
        }
    }

    var isLiked = false

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
            videoView.setVideoPath(path)

            videoView.setOnPreparedListener { mediaPlayer ->
                val layoutParams = videoView.layoutParams
                val videoWidth = mediaPlayer.videoWidth.toFloat()
                val videoHeight = mediaPlayer.videoHeight.toFloat()
                val viewWidth = videoView.width.toFloat()
                layoutParams.height = (viewWidth * (videoHeight / videoWidth)).toInt()
                videoView.layoutParams = layoutParams

                seekBar.progress = 0
                seekBar.max = videoView.duration
                updateHandler.postDelayed(updateVideoTime, 100)
            }

//            videoView.setOnClickListener(null)
//            videoView.setOnTouchListener(null)
        }, 0)
    }

    private val updateVideoTime: Runnable by lazy {
        object : Runnable {
            override fun run() {
                val currentPosition: Long = videoView.currentPosition.toLong()
                seekBar.progress = currentPosition.toInt()

                val sec = currentPosition / 1000 % 60
                timeCode.text =
                    "${currentPosition / 60000}:${if (sec.toString().length > 1) "" else "0"}$sec"

                updateHandler.postDelayed(this, 100)
            }
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        // Store current position.
        savedInstanceState.putInt("CurrentPosition", videoView.currentPosition)
        videoView.pause()
    }

    // After rotating the phone. This method is called.
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Get saved position.
        val position = savedInstanceState.getInt("CurrentPosition")
        videoView.seekTo(position)
    }
}