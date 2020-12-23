package com.example.tiktok_analog.data.model

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.json.JSONObject


data class Video(
    val title: String,
    val description: String,
    val tags: String,
    val size: Int,
    val length: Int,
    val views: Int,
    val likes: Int
) {
    fun toJson(): JSONObject {
        val builder = GsonBuilder()
        val json: String = builder.create().toJson(this)
        return JSONObject(json)
    }

}