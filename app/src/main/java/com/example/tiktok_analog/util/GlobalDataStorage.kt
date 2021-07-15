package com.example.tiktok_analog.util

class GlobalDataStorage {
    companion object {
        val videoIdList = arrayListOf(
            946366,
            8885413,
            5485667,
            8931796,
            946366,
            8885413,
            5485667,
            8931796,
            946366,
            8885413,
            5485667,
            8931796,
            946366,
            8885413,
            5485667,
            8931796
        )

        val tagList = arrayListOf<String>(
            "Еда",
            "Путешествия",
            "Юмор",
            "Наука и техника",
            "Игры"
        )

        private var totalVideoViewsDuringSession = 0

        fun viewVideo() = totalVideoViewsDuringSession++

        fun getTotalViews() = totalVideoViewsDuringSession
    }
}