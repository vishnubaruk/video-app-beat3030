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
                username = jsonObject.getString("username"),
                password = jsonObject.getString("password"),
                email = jsonObject.getString("email"),
                phone = jsonObject.getString("phone"),
                birthDate = jsonObject.getString("birthDate"),
                city = jsonObject.getString("city")
            )
        }
    }
}

