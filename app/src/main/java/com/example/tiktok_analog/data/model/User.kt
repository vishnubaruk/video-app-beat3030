package com.example.tiktok_analog.data.model

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/*
    Data class that contains data about user.
 */
data class User(
    @SerializedName("username")
    var username: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("email")
    var email: String = "",
    @SerializedName("phone")
    var phone: String = "",
    @SerializedName("birthDate")
    var birthDate: String = "",
    @SerializedName("city")
    var city: String = ""
) {
    fun toJson(): JSONObject = JSONObject(toJsonString())

    fun toJsonString(): String = GsonBuilder().create().toJson(this)

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

        fun newFakeUser(): User {
            return newFakeUser(JSONObject())
        }

        private fun newFakeUser(jsonObject: JSONObject): User {
            fun getStringOrEmpty(name: String): String {
                return if (jsonObject.has(name)) jsonObject.getString(name) else ""
            }

            return User(
                username = getStringOrEmpty("username"),
                password = getStringOrEmpty("password"),
                email = getStringOrEmpty("email"),
                phone = getStringOrEmpty("phone"),
                birthDate = getStringOrEmpty("birthDate"),
                city = getStringOrEmpty("city")
            )
        }
    }
}

