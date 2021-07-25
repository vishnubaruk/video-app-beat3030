package com.example.tiktok_analog.ui.menuscreens.fragments

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Space
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.databinding.AddTextColorDialogBinding
import com.example.tiktok_analog.databinding.AddVideoBinding
import com.example.tiktok_analog.ui.afterTextChanged
import com.example.tiktok_analog.util.GlobalDataStorage
import org.json.JSONObject
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import kotlin.math.pow
import kotlin.random.Random

class AddVideoFragment() : Fragment(R.layout.add_video) {
    private val pickVideo = 23

    private var selectedVideoPath: String? = null
    private var selectedVideoLength: Long = 0
    private var selectedVideoSize: Long = 0

    private lateinit var userData: User

    private var _binding: AddVideoBinding? = null
    private val binding get() = _binding!!

    private val selectedTagList = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddVideoBinding.inflate(inflater, container, false)

        requireActivity().openFileInput("userData").use {
            userData = User.fromJson(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        binding.backArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.pickFileButton.setOnClickListener {
            openGalleryForVideo()
        }

        binding.videoTitle.afterTextChanged {
            checkIfCanUpload()
        }

        binding.videoDescription.afterTextChanged {
            checkIfCanUpload()
        }

        binding.uploadVideoButton.setOnClickListener {
            binding.uploadVideoButton.text = "Загрузка видео..."

            binding.uploadVideoButton.isEnabled = false
            binding.pickFileButton.isEnabled = false

            binding.uploadVideoButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.buttonDisabledBg)
            binding.pickFileButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.buttonDisabledBg)

            binding.progressBar.visibility = View.VISIBLE

            addVideo()
        }

        binding.addTagButton.setOnClickListener {
            binding.tagListLayout.root.visibility = View.VISIBLE
            fillTags()
        }

        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = "kepler88d"
        config["api_key"] = "829281113734147"
        config["api_secret"] = "HeZK9Blh5VYtfwxW2neN2tmk5YQ"

        try {
            MediaManager.init(requireContext(), config)
        } catch (e: Exception) {
            Log.e("ERROR", e.stackTraceToString())
        }

        return binding.root
    }

    private fun fillVideoEditor() {
        binding.videoEditor.musicButton.setOnClickListener {

        }

        binding.videoEditor.cropButton.setOnClickListener {

        }

        binding.videoEditor.addTextButton.setOnClickListener {
            binding.videoEditor.addTextLayout.visibility = View.VISIBLE
            binding.videoEditor.mainNavigation.visibility = View.GONE
            binding.videoEditor.addTextNavigation.visibility = View.VISIBLE

            binding.videoEditor.addTextBack.setOnClickListener {
                binding.videoEditor.addTextLayout.visibility = View.GONE
                binding.videoEditor.mainNavigation.visibility = View.VISIBLE
                binding.videoEditor.addTextNavigation.visibility = View.GONE
            }

            binding.videoEditor.addTextDone.setOnClickListener {
                binding.videoEditor.addTextLayout.visibility = View.GONE
                binding.videoEditor.mainNavigation.visibility = View.VISIBLE
                binding.videoEditor.addTextNavigation.visibility = View.GONE
            }

            binding.videoEditor.addTextButtonRound.setOnClickListener {
                val inputFieldBinding = AddTextColorDialogBinding.inflate(
                    layoutInflater, null, false
                )

                inputFieldBinding.editText.setText("")

                val alertDialog =
                    AlertDialog.Builder(requireContext()).setTitle("Добавление текста")
                        .setView(
                            inputFieldBinding.root
                        )
                        .setPositiveButton("Применить") { _, _ ->
                            run {

                            }
                        }
                        .setNegativeButton("Отмена") { _, _ -> run {} }.create()
                alertDialog.show()
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
            }
        }

        binding.videoEditor.continueButton.setOnClickListener {
            binding.videoEditor.root.visibility = View.GONE
        }
    }

    private fun fillTags() {
        binding.tagListLayout.liearLayout.removeAllViews()

        GlobalDataStorage.tagList.forEach { tag ->
            val checkBox = CheckBox(requireContext())
            checkBox.text = tag
            checkBox.isChecked = selectedTagList.contains(tag)
            checkBox.setOnClickListener {
                if (selectedTagList.contains(tag)) {
                    selectedTagList.remove(tag)
                } else {
                    selectedTagList.add(tag)
                }
            }

            binding.tagListLayout.liearLayout.addView(checkBox)

            val space = Space(requireContext())
            space.layoutParams = ViewGroup.LayoutParams(-1, 20)
            binding.tagListLayout.liearLayout.addView(space)
        }
        binding.tagListLayout.accept.setOnClickListener {
            binding.tagListLayout.root.visibility = View.GONE
            binding.videoTags.text = selectedTagList.joinToString(separator = ", ")
        }
    }

    private fun addVideo() {
        val videoId = Random.nextInt(10000, 10000000)
        MediaManager.get().upload(selectedVideoPath)
            .option("public_id", videoId.toString())
            .option("resource_type", "video")
            .callback(
                object : UploadCallback {
                    override fun onStart(requestId: String) {}

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = bytes.toDouble() / totalBytes
                        binding.progressBar.progress = (progress * 100).toInt()
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                        createAddVideoRequest(videoId)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e("CloudError", error.description)
                        val builder =
                            AlertDialog.Builder(requireContext())
                                .setTitle("Произошла непредвиденная ошибка!")
                                .setMessage("Попробуйте еще раз")
                                .setPositiveButton("Понятно") { dialog, _ ->
                                    dialog.cancel()
                                }
                        builder.create()
                        builder.show()
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                },
            ).dispatch()
    }

    private fun createAddVideoRequest(videoId: Int) {
        val addVideoQueue = Volley.newRequestQueue(requireContext())

        val url =
            resources.getString(R.string.base_url) + "/addVideo?" +
                    "email=${userData.email}&phone=${userData.phone}&" +
                    "title=${binding.videoTitle.text}&description=${binding.videoDescription.text}&" +
                    "tags=${binding.videoTags.text}&size=${selectedVideoSize}&" +
                    "length=${selectedVideoLength / 1000}&" +
                    "videoId=$videoId"

        val addVideoRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                if (JSONObject(response).getBoolean("ok")) {
                    binding.progressBar.visibility = View.GONE
                    val builder = AlertDialog.Builder(requireContext())
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
                        AlertDialog.Builder(requireContext())
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
        requireActivity().onBackPressed()
    }

    private fun openGalleryForVideo() {
        val videoPickerIntent = Intent(Intent.ACTION_PICK)
        videoPickerIntent.type = "video/*"
        startActivityForResult(videoPickerIntent, pickVideo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == pickVideo && data != null) {
            val videoUri: Uri = data.data!!

            val videoPath1: String = videoUri.path!!

            val videoPath2: String = getPath(videoUri)!!

            val mediaMetadataRetriever = FFmpegMediaMetadataRetriever()

            try {
                selectedVideoPath = videoPath1
                mediaMetadataRetriever.setDataSource(videoPath1)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }

            try {
                selectedVideoPath = videoPath2
                mediaMetadataRetriever.setDataSource(videoPath2)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }

            val videoDuration =
                mediaMetadataRetriever.extractMetadata(
                    FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION
                )
            val milliseconds = videoDuration.toLong()

            val videoWeight =
                mediaMetadataRetriever.extractMetadata(
                    FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE
                )

            if (binding.videoTitle.text.isBlank()) {
                binding.videoTitle.setText(File(videoPath2).name.split(".")[0])
            }

            selectedVideoSize = videoWeight.toLong()
            selectedVideoLength = milliseconds

            binding.timeCode.text = formatFileSizeFromBytes(videoWeight.toLong())
            binding.videoLength.text = formatTimeFromMilliseconds(milliseconds)

            val thumbnail = ThumbnailUtils.createVideoThumbnail(
                videoPath2,
                MediaStore.Images.Thumbnails.MINI_KIND
            )

            binding.videoPreview.setImageBitmap(thumbnail)
            checkIfCanUpload()
            binding.videoEditor.root.visibility = View.VISIBLE
            fillVideoEditor()
        }
    }

    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor =
            requireActivity()
                .contentResolver
                .query(uri!!, projection, null, null, null)
                ?: return null
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

    private fun checkIfCanUpload() {
        var dataContainsErrors = false

        if (binding.videoTitle.text.isBlank()) {
            binding.videoTitle.error = "Введите название видео"
            dataContainsErrors = true
        }

        if (binding.videoDescription.text.isBlank()) {
            binding.videoDescription.error = "Введите описание видео"
            dataContainsErrors = true
        }

        if (selectedVideoPath == null) {
            dataContainsErrors = true
        }

        binding.uploadVideoButton.isEnabled = !dataContainsErrors
        binding.uploadVideoButton.backgroundTintList =
            ContextCompat.getColorStateList(
                requireContext(), if (dataContainsErrors) R.color.buttonDisabledBg
                else R.color.buttonEnabledBg
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}