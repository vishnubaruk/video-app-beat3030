package com.example.tiktok_analog.ui.menuscreens

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.afterTextChanged
import kotlinx.android.synthetic.main.activity_main.backArrowButton
import kotlinx.android.synthetic.main.add_video.*
import org.json.JSONObject
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.lang.Exception
import kotlin.math.pow
import kotlin.random.Random


class AddVideoActivity : AppCompatActivity() {

    private val pickVideo = 23

    private var selectedVideoPath: String? = null
    private var selectedVideoLength: Long = 0
    private var selectedVideoSize: Long = 0

    private lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_video)

        openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

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

            uploadVideoButton.isEnabled = false
            pickFileButton.isEnabled = false

            uploadVideoButton.backgroundTintList =
                applicationContext.resources.getColorStateList(R.color.buttonDisabledBg)
            pickFileButton.backgroundTintList =
                applicationContext.resources.getColorStateList(R.color.buttonDisabledBg)

            progressBar.visibility = View.VISIBLE

            addVideo()
        }

        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = "kepler88d"
        config["api_key"] = "829281113734147"
        config["api_secret"] = "HeZK9Blh5VYtfwxW2neN2tmk5YQ"

        try {
            MediaManager.init(this, config)
        } catch (e: Exception) {
            Log.e("ERROR", e.stackTraceToString())
        }
    }

    private fun addVideo() {
        val addVideoContext = this

        val videoId = Random.nextInt(10000, 10000000)

        val requestId: String =
            MediaManager.get().upload(selectedVideoPath)
                .option("public_id", videoId.toString())
                .option("resource_type", "video")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        // your code here
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        // example code starts here
                        val progress = bytes.toDouble() / totalBytes
                        progressBar.progress = (progress * 100).toInt()
                        // post progress to app UI (e.g. progress bar, notification)
                        // example code ends here
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                        // your code here
                        createAddVideoRequest(videoId)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        // your code here
                        // progressBar.visibility = View.GONE
                        Log.e("CloudError", error.description)
                        val builder =
                            AlertDialog.Builder(addVideoContext)
                                .setTitle("Произошла непредвиденная ошибка!")
                                .setMessage("Попробуйте еще раз")
                                .setPositiveButton("Понятно") { dialog, _ ->
                                    dialog.cancel()
                                }
                        builder.create()
                        builder.show()
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        // your code here
                    }
                }).dispatch()
    }

    private fun createAddVideoRequest(videoId: Int) {
        val addVideoQueue = Volley.newRequestQueue(this)

        val url =
            "https://kepler88d.pythonanywhere.com/addVideo?" +
                    "email=${userData.email}&phone=${userData.phone}&" +
                    "title=${videoTitle.text}&description=${videoDescription.text}&" +
                    "tags=${videoTags.text}&size=${selectedVideoSize}&" +
                    "length=${selectedVideoLength / 1000}&" +
                    "videoId=$videoId"

        Log.d("DEBUG", url)

        val addVideoRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                Log.d("DEBUG", response)

                if (JSONObject(response).getBoolean("ok")) {
                    progressBar.visibility = View.GONE
                    val builder = AlertDialog.Builder(this)
                        .setMessage("Ваше видео было успешно загружено")
                        .setPositiveButton("Хорошо") { dialog, _ ->
                            back()
                        }
                    builder.setOnCancelListener {
                        back()
                    }
                    builder.create()
                    builder.show()
                } else {
                    val builder =
                        AlertDialog.Builder(this)
                            .setTitle("Произошла непредвиденная ошибка!")
                            .setMessage("Попробуйте еще раз")
                            .setPositiveButton("Понятно") { dialog, _ ->
                                dialog.cancel()
                            }
                    builder.create()
                    builder.show()
                }
            }
        }, {
            Log.e("ERROR", "Error at sign in : " + it.message)
        })

        addVideoQueue.add(addVideoRequest)
    }

    private fun back() {
        super.onBackPressed()
    }

    private fun openGalleryForVideo() {
        val videoPickerIntent = Intent(Intent.ACTION_PICK)
        videoPickerIntent.type = "video/*"
        startActivityForResult(videoPickerIntent, pickVideo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == pickVideo && data != null) {
            val videoUri: Uri = data.data!!

            val videoPath1: String = videoUri.path!!

            val videoPath2: String = getPath(videoUri)!!

            val mediaMetadataRetriever = FFmpegMediaMetadataRetriever()

            try {
                selectedVideoPath = videoPath1
                mediaMetadataRetriever.setDataSource(videoPath1)
            } catch (e: Exception) {
                e.printStackTrace();
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show();
            }

            try {
                selectedVideoPath = videoPath2
                mediaMetadataRetriever.setDataSource(videoPath2)
            } catch (e: Exception) {
                e.printStackTrace();
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show();
            }

            val videoDuration =
                mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
            val milliseconds = videoDuration.toLong()

            val videoWeight =
                mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE)

            if (videoTitle.text.isBlank()) {
                videoTitle.setText(File(videoPath2).name.split(".")[0])
            }

            selectedVideoSize = videoWeight.toLong()
            selectedVideoLength = milliseconds

            timeCode.text = formatFileSizeFromBytes(videoWeight.toLong())
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
