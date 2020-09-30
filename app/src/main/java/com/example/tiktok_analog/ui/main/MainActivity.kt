package com.example.tiktok_analog.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter.*
import kotlinx.android.synthetic.main.menu.*
import kotlinx.android.synthetic.main.profile.*

class MainActivity : AppCompatActivity() {

    var isMenuOpened = false
    var isFilterOpened = false
    var isProfileOpened = false

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

    private fun openMenu() {
        // Toast.makeText(applicationContext, "Menu Opened!", Toast.LENGTH_SHORT).show()
        closeFilter()
        closeProfile()

        openMenuButton.visibility = View.GONE
        closeMenuButton.visibility = View.VISIBLE

        menuLayout.visibility = View.VISIBLE

        isMenuOpened = true

        sectionTitleText.text = "Меню"
    }

    private fun closeMenu() {
        // Toast.makeText(applicationContext, "Menu Closed!", Toast.LENGTH_SHORT).show()

        openMenuButton.visibility = View.VISIBLE
        closeMenuButton.visibility = View.GONE

        menuLayout.visibility = View.GONE

        isMenuOpened = false

        sectionTitleText.text = "Главная"
    }

    private fun openFilter() {
        // Toast.makeText(applicationContext, "Filter Opened!", Toast.LENGTH_SHORT).show()
        closeMenu()
        closeProfile()

        openFilterButton.visibility = View.GONE
        closeFilterButton.visibility = View.VISIBLE

        filterLayout.visibility = View.VISIBLE

        isFilterOpened = true
    }

    private fun closeFilter() {
        // Toast.makeText(applicationContext, "Filter Closed!", Toast.LENGTH_SHORT).show()

        openFilterButton.visibility = View.VISIBLE
        closeFilterButton.visibility = View.GONE

        filterLayout.visibility = View.GONE

        isFilterOpened = false
    }

    private fun openProfile() {
        closeMenu()
        closeFilter()

        profileLayout.visibility = View.VISIBLE

        isProfileOpened = true

        sectionTitleText.text = "Ваш профиль"
    }

    private fun closeProfile() {
        profileLayout.visibility = View.GONE

        isProfileOpened = false

        sectionTitleText.text = "Главная"
    }

    override fun onBackPressed() {
        // super.onBackPressed()

        if (isMenuOpened) {
            closeMenu()
        }

        if(isFilterOpened) {
            closeFilter()
        }

        if(isProfileOpened) {
            closeProfile()
        }
    }
}