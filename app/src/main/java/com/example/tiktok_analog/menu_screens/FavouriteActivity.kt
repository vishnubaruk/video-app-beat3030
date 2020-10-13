package com.example.tiktok_analog.menu_screens

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.favourite.*
import kotlin.random.Random

class FavouriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourite)

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

        for (i in 1..10) {
            addViewToFavourite(
                Random.nextInt(100000, 999999),
                R.drawable.rectangle34
            )
        }

        favouriteScrollView.viewTreeObserver.addOnScrollChangedListener {
            if (favouriteScrollView.getChildAt(0).bottom <= favouriteScrollView.height + favouriteScrollView.scrollY) {
                //scroll view is at bottom
                for (i in 1..10) {
                    addViewToFavourite(
                        Random.nextInt(100000, 999999),
                        R.drawable.rectangle34
                    )
                }
            }
        }
    }

    private fun addViewToFavourite(viewCount: Int, imageId: Int) {
        val newViewLine =
            LayoutInflater.from(applicationContext).inflate(R.layout.fav_line, null, false)


//        for (i in 0..2) {
//            val newView =
//                LayoutInflater.from(applicationContext)
//                    .inflate(R.layout.fav_video_item, null, false)
//
//            newView.findViewWithTag<TextView>("viewCount").text = viewCount.toString()
//
//            newView.findViewWithTag<ImageView>("previewImage").setImageResource(imageId)
//            newViewLine.findViewWithTag<LinearLayout>("root").addView(newView)
//        }

        favouriteLayout.addView(newViewLine)
    }
}