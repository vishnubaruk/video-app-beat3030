package com.example.tiktok_analog.menu_screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import com.example.tiktok_analog.util.ScrollViewExtended
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter.*
import kotlinx.android.synthetic.main.menu.*
import kotlinx.android.synthetic.main.profile.*
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