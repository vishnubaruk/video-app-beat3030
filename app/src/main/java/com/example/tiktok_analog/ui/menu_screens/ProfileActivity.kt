package com.example.tiktok_analog.ui.menu_screens

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.profile.*
import android.view.View
import android.widget.Toast
import com.example.tiktok_analog.data.model.User
import kotlinx.android.synthetic.main.authorize.*
import org.json.JSONObject


class ProfileActivity : AppCompatActivity() {

    lateinit var userData: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        fillProfileData()

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

            editData.visibility = View.VISIBLE
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

            editData.visibility = View.GONE
        }

        editData.setOnClickListener {
            Toast.makeText(applicationContext, "Edit data clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fillProfileData() {
        nameText.text = userData.username
        nameTextHeader.text = userData.username

        phoneText.text = userData.phone
        birthDateText.text = userData.birthDate
        cityText.text = userData.city

        emailText.text = userData.email
        emailTextHeader.text = userData.email
    }
}