package com.example.tiktok_analog.ui.legacy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.notifications.*

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.notifications)

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

        notificationsSwipeRefresh.setOnRefreshListener {
            notificationsSwipeRefresh.isRefreshing = false
            Toast.makeText(applicationContext,
                "Notifications Updated", Toast.LENGTH_SHORT).show()
        }
    }
}