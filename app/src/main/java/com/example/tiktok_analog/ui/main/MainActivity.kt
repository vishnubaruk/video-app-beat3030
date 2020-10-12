package com.example.tiktok_analog.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import com.example.tiktok_analog.menu_screens.AddVideoActivity
import com.example.tiktok_analog.util.ScrollViewExtended
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter.*
import kotlinx.android.synthetic.main.menu.*
import kotlinx.android.synthetic.main.profile.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    var isMenuOpened = false
    var isFilterOpened = false
    var isProfileOpened = false
    var isFavouriteOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openMenuButton.setOnClickListener {
            openMenu()
        }

        openFilterButton.setOnClickListener {
            openFilter()
        }

        closeMenuButton.setOnClickListener {
            closeMenu()
        }

        closeFilterButton.setOnClickListener {
            closeFilter()
        }

        applyFilterButton.setOnClickListener {
            closeFilter()
        }

        openProfileButton.setOnClickListener {
            openProfile()
        }

        favouriteButton.setOnClickListener {
            openFavourite()
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

        addVideoButton.setOnClickListener {
            openAddVideo()
        }

        // filter panel
        oneMinuteButton.setOnClickListener {
            oneMinuteButton.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_selected)
            threeMinutesButton.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
        }

        threeMinutesButton.setOnClickListener {
            oneMinuteButton.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
            threeMinutesButton.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_selected)
        }

        sortByPopularity.setOnClickListener {
            sortByPopularity.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_selected)
            sortByDate.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
            sortByLength.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
        }

        sortByDate.setOnClickListener {
            sortByPopularity.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
            sortByDate.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_selected)
            sortByLength.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
        }

        sortByLength.setOnClickListener {
            sortByPopularity.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
            sortByDate.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_notselected)
            sortByLength.background =
                applicationContext.resources.getDrawable(R.drawable.ic_radiobutton_selected)
        }

        backArrowButton.setOnClickListener {
            onBackPressed()
        }

        for (i in 1..10) {
            addViewToNewsLine(
                "Title${Random.nextInt(10000, 99999999)}",
                arrayListOf(
                    "tag${Random.nextInt(100, 999)}",
                    "tag${Random.nextInt(100, 999)}",
                    "tag${Random.nextInt(100, 999)}"
                ),
                Random.nextInt(10, 9000),
                Random.nextInt(10, 1200),
                R.drawable.rectangle4
            )

            addViewToFavourite(
                Random.nextInt(100000, 999999),
                R.drawable.rectangle34
            )
        }

        scrollView.viewTreeObserver
            .addOnScrollChangedListener {
                if (scrollView.getChildAt(0).bottom
                    <= scrollView.height + scrollView.scrollY
                ) {
                    //scroll view is at bottom
                    // TODO: refactor to stateStack
                    if (sectionTitleText.text == "Главная") {
                        for (i in 1..10) {
                            addViewToNewsLine(
                                "Title${Random.nextInt(10000, 99999999)}",
                                arrayListOf(
                                    "tag${Random.nextInt(100, 999)}",
                                    "tag${Random.nextInt(100, 999)}",
                                    "tag${Random.nextInt(100, 999)}"
                                ),
                                Random.nextInt(10, 9000),
                                Random.nextInt(10, 1200),
                                R.drawable.rectangle4
                            )
                        }
                    } else if (isFavouriteOpened) {
                        for (i in 1..10) {
                            addViewToFavourite(
                                Random.nextInt(100000, 999999),
                                R.drawable.rectangle34
                            )
                        }
                    }
                } else {
                    //scroll view is not at bottom
                }
            }
    }

    private fun openNewsLine() {
        newsLineLayout.visibility = View.VISIBLE
        openFilterButton.visibility = View.VISIBLE
    }

    private fun closeNewsLine() {
        newsLineLayout.visibility = View.GONE
        openFilterButton.visibility = View.GONE
        closeFilterButton.visibility = View.GONE
    }

    private fun openMenu() {
        // Toast.makeText(applicationContext, "Menu Opened!", Toast.LENGTH_SHORT).show()
        closeFilter()
        closeNewsLine()

        openMenuButton.visibility = View.GONE
        closeMenuButton.visibility = View.VISIBLE

        menuLayout.visibility = View.VISIBLE

        isMenuOpened = true

        sectionTitleText.text = "Меню"
    }

    private fun closeMenu() {
        // Toast.makeText(applicationContext, "Menu Closed!", Toast.LENGTH_SHORT).show()
        openNewsLine()

        openMenuButton.visibility = View.VISIBLE
        closeMenuButton.visibility = View.GONE

        menuLayout.visibility = View.GONE

        isMenuOpened = false

        sectionTitleText.text = "Главная"
    }

    private fun openFilter() {
        // Toast.makeText(applicationContext, "Filter Opened!", Toast.LENGTH_SHORT).show()
        closeNewsLine()

        openFilterButton.visibility = View.GONE
        closeFilterButton.visibility = View.VISIBLE

        filterLayout.visibility = View.VISIBLE

        isFilterOpened = true

        sectionTitleText.text = "Главная"
    }

    private fun closeFilter() {
        // Toast.makeText(applicationContext, "Filter Closed!", Toast.LENGTH_SHORT).show()
        openNewsLine()

        openFilterButton.visibility = View.VISIBLE
        closeFilterButton.visibility = View.GONE

        filterLayout.visibility = View.GONE

        isFilterOpened = false
    }

    private fun openProfile() {
        closeMenu()
        closeNewsLine()

        profileLayout.visibility = View.VISIBLE

        openMenuButton.visibility = View.GONE
        closeMenuButton.visibility = View.GONE
        backArrowButton.visibility = View.VISIBLE

        isProfileOpened = true

        sectionTitleText.text = "Ваш профиль"
    }

    private fun closeProfile() {
        openMenu()

        profileLayout.visibility = View.GONE

        closeMenuButton.visibility = View.VISIBLE
        backArrowButton.visibility = View.GONE

        isProfileOpened = false

        sectionTitleText.text = "Меню"
    }

    // TODO: fix possible bugs
    private fun openAddVideo() {
        startActivity(Intent(this, AddVideoActivity::class.java))
        return
    }

    private fun openFavourite() {
        closeMenu()
        closeNewsLine()

        favouriteLayout.visibility = View.VISIBLE

        openMenuButton.visibility = View.GONE
        closeMenuButton.visibility = View.GONE
        backArrowButton.visibility = View.VISIBLE

        isFavouriteOpened = true

        sectionTitleText.text = "Избранное"
    }

    private fun closeFavourite() {
        openMenu()

        favouriteLayout.visibility = View.GONE

        closeMenuButton.visibility = View.VISIBLE
        backArrowButton.visibility = View.GONE

        isFavouriteOpened = false

        sectionTitleText.text = "Меню"
    }

    private fun addViewToNewsLine(
        title: String,
        tags: ArrayList<String>,
        likeCount: Int = 1200,
        length: Int = 90,
        imageId: Int
    ) {
        // replace with new pattern layout
        val newView =
            LayoutInflater.from(applicationContext).inflate(R.layout.video_feed_item, null, false)
        newView.findViewWithTag<TextView>("title").text = title

        var formattedTags = ""
        for (i in tags) {
            formattedTags += "#$i  "
        }
        newView.findViewWithTag<TextView>("tags").text = formattedTags

        newView.findViewWithTag<Button>("likeButton").text = "$likeCount  "
        newView.findViewWithTag<Button>("lengthButton").text =
            "${length / 60}:${if (length % 60 < 10) "0" else ""}${length % 60}"

        newView.findViewWithTag<ImageView>("previewImage").setImageResource(imageId)
        newsLineLayout.addView(newView)
        // newView.findViewWithTag<ProgressBar>("progressBar").progress = progress
        // properties[id]= newView
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

    override fun onBackPressed() {
        // super.onBackPressed()

        if (isFilterOpened) {
            closeFilter()
            return
        }

        if (isProfileOpened) {
            closeProfile()
            return
        }

        if (isMenuOpened) {
            closeMenu()
            return
        }

        if (isFavouriteOpened) {
            closeFavourite()
        }
    }
}