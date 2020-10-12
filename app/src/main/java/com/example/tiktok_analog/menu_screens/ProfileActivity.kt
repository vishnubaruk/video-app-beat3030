package com.example.tiktok_analog.menu_screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.profile.*
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.tiktok_analog.menu_screens.AddVideoActivity
import com.example.tiktok_analog.menu_screens.ProfileActivity
import com.example.tiktok_analog.util.ScrollViewExtended
import kotlinx.android.synthetic.main.filter.*
import kotlinx.android.synthetic.main.menu.*
import kotlin.random.Random


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
        }
    }
}