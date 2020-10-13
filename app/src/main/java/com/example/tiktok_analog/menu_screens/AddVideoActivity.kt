package com.example.tiktok_analog.menu_screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class AddVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_video)

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

    }
}
