package com.example.tiktok_analog.ui.menu_screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.profile.*
import android.view.View


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

        yourProfileTab.setOnClickListener {
            yourProfileTab.backgroundTintList =
                applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)
            yourProfileTab.setTextColor(resources.getColor(R.color.white))

            yourVideosTab.backgroundTintList =
                applicationContext.resources.getColorStateList(R.color.groupUnselected)
            yourVideosTab.setTextColor(resources.getColor(R.color.colorPrimary))

            yourProfileBlock.visibility = View.VISIBLE
            yourVideosBlock.visibility = View.GONE

            sectionTitleText.text = "Ваш профиль"
        }

        yourVideosTab.setOnClickListener {
            yourProfileTab.backgroundTintList =
                applicationContext.resources.getColorStateList(R.color.groupUnselected)
            yourProfileTab.setTextColor(resources.getColor(R.color.colorPrimary))

            yourVideosTab.backgroundTintList =
                applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)
            yourVideosTab.setTextColor(resources.getColor(R.color.white))

            yourProfileBlock.visibility = View.GONE
            yourVideosBlock.visibility = View.VISIBLE

            sectionTitleText.text = "Ваши видео"
        }
    }
}