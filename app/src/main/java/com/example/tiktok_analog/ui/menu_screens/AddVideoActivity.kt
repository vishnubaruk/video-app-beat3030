package com.example.tiktok_analog.ui.menu_screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.activity_main.backArrowButton
import kotlinx.android.synthetic.main.add_video.*
import wseemann.media.FFmpegMediaMetadataRetriever


class AddVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_video)

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

        pickFileButton.setOnClickListener {
            openGalleryForVideo()
        }
    }

    private fun openGalleryForVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Video"), 23)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
//                val selectedImageUri = data.data
//
//                // OI FILE Manager
//                filemanagerstring = selectedImageUri!!.path
//
//                // MEDIA GALLERY
//                selectedImagePath = getPath(selectedImageUri)
//                if (selectedImagePath != null) {
//                    val intent = Intent(
//                        this@HomeActivity,
//                        VideoplayAvtivity::class.java
//                    )
//                    intent.putExtra("path", selectedImagePath)
//                    startActivity(intent)
//                }
//            }
//        }
//    }
//
//    // UPDATED!
//    fun getPath(uri: Uri?): String? {
//        val projection = arrayOf(MediaStore.Video.Media.DATA)
//        val cursor: Cursor? = contentResolver.query(uri!!, projection, null, null, null)
//        return if (cursor != null) {
//            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
//            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
//            val column_index: Int = cursor
//                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//            cursor.moveToFirst()
//            cursor.getString(column_index)
//        } else null
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 23) {
            if (data?.data != null) {
                val videoUri: Uri = data.data!! // Use this video path according to your logic

                val retriever = FFmpegMediaMetadataRetriever()
                Log.d("DEBUG", videoUri.path!!.substring(6))
                retriever.setDataSource(videoUri.path!!.substring(6))

                val time =
                    retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
                val timeInMilliseconds = time.toLong()

                retriever.release()

                videoLength.text = timeInMilliseconds.toString()

//                // if you want to play video just after picking it to check is it working
//                if (videoFullPath != null) {
//                    playVideoInDevicePlayer(videoFullPath);
//                }
            }
        }
    }
}
