package com.example.tiktok_analog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter.*


class MainActivity : AppCompatActivity() {

    var isMenuOpened = false
    var isFilterOpened = false

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
    }

    private fun openMenu() {
        Toast.makeText(applicationContext, "Menu Opened!", Toast.LENGTH_SHORT).show()
        closeFilter()

        openMenuButton.visibility = View.GONE
        closeMenuButton.visibility = View.VISIBLE

        menuLayout.visibility = View.VISIBLE

        isMenuOpened = true

        sectionTitleText.text = "Меню"
    }

    private fun closeMenu() {
        Toast.makeText(applicationContext, "Menu Closed!", Toast.LENGTH_SHORT).show()

        openMenuButton.visibility = View.VISIBLE
        closeMenuButton.visibility = View.GONE

        menuLayout.visibility = View.GONE

        isMenuOpened = false

        sectionTitleText.text = "Главная"
    }

    private fun openFilter() {
        Toast.makeText(applicationContext, "Filter Opened!", Toast.LENGTH_SHORT).show()
        closeMenu()

        openFilterButton.visibility = View.GONE
        closeFilterButton.visibility = View.VISIBLE

        filterLayout.visibility = View.VISIBLE

        isFilterOpened = true
    }

    private fun closeFilter() {
        Toast.makeText(applicationContext, "Filter Closed!", Toast.LENGTH_SHORT).show()

        openFilterButton.visibility = View.VISIBLE
        closeFilterButton.visibility = View.GONE

        filterLayout.visibility = View.GONE

        isFilterOpened = false
    }

    override fun onBackPressed() {
        // super.onBackPressed()

        if (isMenuOpened) {
            closeMenu()
        }

        if(isFilterOpened) {
            closeFilter()
        }
    }
}