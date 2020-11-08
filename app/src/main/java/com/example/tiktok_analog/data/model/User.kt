package com.example.tiktok_analog.data.model

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/*
    Data class that contains data about user.
 */
data class User(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("email")
    val email: String = "",
    @SerializedName("phone")
    val phone: String = "",
    @SerializedName("birthDate")
    val birthDate: String = "",
    @SerializedName("city")
    val city: String = ""
) {
    fun toJson(): JSONObject {
        val builder = GsonBuilder()
        val json = builder.create().toJson(this)
        return JSONObject(json)
    }

    fun toJsonString(): String {
        return toJson().toString()
    }

    companion object {
        fun newUser(jsonObject: JSONObject): User {
            return User(
                username = jsonObject.getString("username").toString(),
                password = jsonObject.getString("password").toString(),
                email = jsonObject.getString("email").toString(),
                phone = jsonObject.getString("phone").toString(),
                birthDate = jsonObject.getString("birthDate").toString(),
                city = jsonObject.getString("city").toString()
            )
        }
    }
}

