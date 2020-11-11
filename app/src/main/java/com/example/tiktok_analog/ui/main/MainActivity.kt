package com.example.tiktok_analog.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.menu_screens.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter.*
import kotlinx.android.synthetic.main.menu.*
import org.json.JSONObject
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private var isMenuOpened = false
    private var isFilterOpened = false

    private lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
            nameTextHeader.text = userData.username
            emailTextHeader.text = userData.email
        }

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

        acceptFilter.setOnClickListener {
            closeFilter()
        }

        openProfileButton.setOnClickListener {
            openProfile()
        }

        favouriteButton.setOnClickListener {
            openFavourite()
        }

        addVideoButton.setOnClickListener {
            openAddVideo()
        }

        broadcastButton.setOnClickListener {
            openBroadcast()
        }

        notificationsButton.setOnClickListener {
            openNotifications()
        }

        logout.setOnClickListener {
            val alertDialog =
                AlertDialog.Builder(this).setTitle("Вы уверены, что хотите выйти из аккаунта?")
                    .setMessage("Это приведет к удалению всех пользовательских данных")
                    .setPositiveButton("Да, я уверен") { _, _ ->
                        deleteFile("userData")
                        finishAndRemoveTask()
                    }.setNegativeButton("Нет, отмена") { dialog, _ ->
                        dialog.cancel()
                    }.create()
            alertDialog.show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
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

        fun addPostsToNewsLine(count: Int) {
            for (i in 1..count) {
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
        }

        addPostsToNewsLine(10)

        newsRoot.viewTreeObserver.addOnScrollChangedListener {
            if (newsRoot.getChildAt(0).bottom <= newsRoot.height + newsRoot.scrollY) {
                if (sectionTitleText.text == "Главная") {
                    addPostsToNewsLine(10)
                }
            }
        }

        newsSwipeRefresh.setOnRefreshListener {
            newsSwipeRefresh.isRefreshing = false
            Toast.makeText(applicationContext, "News Updated", Toast.LENGTH_SHORT).show()
            newsLineLayout.removeAllViews()
            addPostsToNewsLine(10)
        }
    }

    private fun openNewsLine() {
        newsRoot.visibility = View.VISIBLE
        openFilterButton.visibility = View.VISIBLE
    }

    private fun closeNewsLine() {
        newsRoot.visibility = View.GONE
        openFilterButton.visibility = View.GONE
        closeFilterButton.visibility = View.GONE
    }

    private fun openMenu() {
        // Toast.makeText(applicationContext, "Menu Opened!", Toast.LENGTH_SHORT).show()
        closeFilter()
        closeNewsLine()

        openMenuButton.visibility = View.GONE
        closeMenuButton.visibility = View.VISIBLE

        menuRoot.visibility = View.VISIBLE

        isMenuOpened = true

        sectionTitleText.text = "Меню"
    }

    private fun closeMenu() {
        // Toast.makeText(applicationContext, "Menu Closed!", Toast.LENGTH_SHORT).show()
        openNewsLine()

        openMenuButton.visibility = View.VISIBLE
        closeMenuButton.visibility = View.GONE

        menuRoot.visibility = View.GONE

        isMenuOpened = false

        sectionTitleText.text = "Главная"
    }

    private fun openFilter() {
        // Toast.makeText(applicationContext, "Filter Opened!", Toast.LENGTH_SHORT).show()
        closeNewsLine()

        openFilterButton.visibility = View.GONE
        closeFilterButton.visibility = View.VISIBLE

        filterRoot.visibility = View.VISIBLE

        isFilterOpened = true

        sectionTitleText.text = "Главная"
    }

    private fun closeFilter() {
        // Toast.makeText(applicationContext, "Filter Closed!", Toast.LENGTH_SHORT).show()
        openNewsLine()

        openFilterButton.visibility = View.VISIBLE
        closeFilterButton.visibility = View.GONE

        filterRoot.visibility = View.GONE

        isFilterOpened = false
    }

    private fun openProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }


    private fun openAddVideo() {
        startActivity(Intent(this, AddVideoActivity::class.java))
    }

    private fun openFavourite() {
        startActivity(Intent(this, FavouriteActivity::class.java))
    }

    private fun openBroadcast() {
        startActivity(Intent(this, BroadcastActivity::class.java))
    }

    private fun openNotifications() {
        startActivity(Intent(this, NotificationsActivity::class.java))
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

        newView.setOnClickListener {
            Toast.makeText(
                applicationContext, "Opening $title video",
                Toast.LENGTH_SHORT
            ).show()
        }
        // newView.findViewWithTag<ProgressBar>("progressBar").progress = progress
        // properties[id]= newView
    }

    override fun onBackPressed() {
        // super.onBackPressed()

        if (isFilterOpened) {
            closeFilter()
            return
        }


        if (isMenuOpened) {
            closeMenu()
            return
        }
    }
}