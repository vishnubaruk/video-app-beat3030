package com.example.tiktok_analog.ui.menu_screens

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import com.example.tiktok_analog.ui.afterTextChanged
import kotlinx.android.synthetic.main.activity_main.backArrowButton
import kotlinx.android.synthetic.main.add_video.*
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import kotlin.math.pow


class AddVideoActivity : AppCompatActivity() {

    private val pickVideo = 23

    private var selectedVideoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_video)

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

        pickFileButton.setOnClickListener {
            openGalleryForVideo()
        }

        videoTitle.afterTextChanged {
            checkIfCanUpload()
        }

        videoDescription.afterTextChanged {
            checkIfCanUpload()
        }

        videoTags.afterTextChanged {
            checkIfCanUpload()
        }

        uploadVideoButton.setOnClickListener {
            uploadVideoButton.text = "Загрузка видео..."
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun openGalleryForVideo() {
        val videoPickerIntent = Intent(Intent.ACTION_PICK)
        videoPickerIntent.type = "video/*"
        startActivityForResult(videoPickerIntent, pickVideo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == pickVideo) {
            val videoUri: Uri = data?.data!!

            val videoPath1: String = videoUri.path!!

            val videoPath2: String = getPath(videoUri)!!

            selectedVideoPath = videoPath2

            val mediaMetadataRetriever = FFmpegMediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath2)
            val videoDuration =
                mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
            val milliseconds = videoDuration.toLong()

            val videoWeight =
                mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE)

            // TODO: make this safe
            if (videoTitle.text.isBlank()) {
                videoTitle.setText(File(videoPath2).name.split(".")[0])
            }

            videoSize.text = formatFileSizeFromBytes(videoWeight.toLong())
            videoLength.text = formatTimeFromMilliseconds(milliseconds)

            val thumbnail = ThumbnailUtils.createVideoThumbnail(
                videoPath2,
                MediaStore.Images.Thumbnails.MINI_KIND
            )

            videoPreview.setImageBitmap(thumbnail)

            checkIfCanUpload()
        }
    }

    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor =
            contentResolver.query(uri!!, projection, null, null, null) ?: return null
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        cursor.moveToFirst()

        val returnValue = cursor.getString(columnIndex)
        cursor.close()
        return returnValue
    }

    private fun formatTimeFromMilliseconds(millis: Long): String {
        return String.format(
            "%02d:%02d:%02d", java.util.concurrent.TimeUnit.MILLISECONDS.toHours(millis),
            java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(millis) -
                    java.util.concurrent.TimeUnit.HOURS.toMinutes(
                        java.util.concurrent.TimeUnit.MILLISECONDS.toHours(
                            millis
                        )
                    ),
            java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(millis) -
                    java.util.concurrent.TimeUnit.MINUTES.toSeconds(
                        java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(
                            millis
                        )
                    )
        )
    }

    private fun formatFileSizeFromBytes(bytes: Long): String {
        if (bytes >= 2.0.pow(30.0)) {
            return "${(bytes / 2.0.pow(30.0)).toInt()} GB"
        }

        if (bytes >= 2.0.pow(20.0)) {
            return "${(bytes / 2.0.pow(20.0)).toInt()} MB"
        }

        if (bytes >= 2.0.pow(10.0)) {
            return "${(bytes / 2.0.pow(10.0)).toInt()} KB"
        }

        return "$bytes B"
    }

    private fun checkIfCanUpload(): Unit {
        var dataContainsErrors = false

        if (videoTitle.text.isBlank()) {
            videoTitle.error = "Введите название видео"
            dataContainsErrors = true
        }

        if (videoDescription.text.isBlank()) {
            videoDescription.error = "Введите описание видео"
            dataContainsErrors = true
        }

        if (selectedVideoPath == null) {
            dataContainsErrors = true
        }

        uploadVideoButton.isEnabled = !dataContainsErrors
        uploadVideoButton.backgroundTintList = applicationContext.resources.getColorStateList(
            if (dataContainsErrors) R.color.buttonDisabledBg
            else R.color.buttonEnabledBg
        )
    }


    override fun onBackPressed() {
        val alertDialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("Вы уверены, что хотите прервать добавление видео?")
            .setMessage("Это приведет к удалению введенных вами данных")
            .setPositiveButton("Да, я уверен") { _, _ ->
                super.onBackPressed()
            }.setNegativeButton("Нет, остаться") { dialog, _ ->
                dialog.cancel()
            }.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }
}
