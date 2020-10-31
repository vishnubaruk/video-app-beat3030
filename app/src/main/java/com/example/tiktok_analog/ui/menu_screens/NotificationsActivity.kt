package com.example.tiktok_analog.ui.menu_screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.profile.*

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.notifications)

        backArrowButton.setOnClickListener {
            onBackPressed()
        }
    }
}