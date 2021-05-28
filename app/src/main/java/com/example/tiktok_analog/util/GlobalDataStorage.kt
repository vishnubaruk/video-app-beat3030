package com.example.tiktok_analog.util

class GlobalDataStorage {
    companion object {
        private var totalVideoViewsDuringSession = 0

        fun viewVideo() = totalVideoViewsDuringSession++

        fun getTotalViews() = totalVideoViewsDuringSession
    }
}