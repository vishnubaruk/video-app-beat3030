package com.example.tiktok_analog.ui.menu_screens

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.favourite.*
import kotlinx.android.synthetic.main.favourite.backArrowButton
import kotlinx.android.synthetic.main.favourite.favouriteSwipeRefresh
import kotlin.random.Random

class FavouriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourite)

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

        fun addPostsToFavourite(count: Int) {
            for (i in 1..count) {
                addViewToFavourite(
                    Random.nextInt(100000, 999999),
                    R.drawable.rectangle34
                )
            }
        }

        addPostsToFavourite(10)

        favouriteScrollView.viewTreeObserver.addOnScrollChangedListener {
            if (favouriteScrollView.getChildAt(0).bottom <=
                favouriteScrollView.height + favouriteScrollView.scrollY
            ) {
                addPostsToFavourite(10)
            }
        }

        favouriteSwipeRefresh.setOnRefreshListener {
            favouriteSwipeRefresh.isRefreshing = false
            Toast.makeText(applicationContext, "Favourite Updated", Toast.LENGTH_SHORT).show()
            favouriteLayout.removeAllViews()
            addPostsToFavourite(10)
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