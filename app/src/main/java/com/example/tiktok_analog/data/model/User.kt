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

        fun newFakeUser(): User {
            return newFakeUser(JSONObject())
        }

        private fun newFakeUser(jsonObject: JSONObject): User {
            return User(
                username =
                if (jsonObject.has("username"))
                    jsonObject.getString("username")
                else "",
                password =
                if (jsonObject.has("password"))
                    jsonObject.getString("password")
                else "",
                email =
                if (jsonObject.has("email"))
                    jsonObject.getString("email")
                else "",
                phone =
                if (jsonObject.has("phone")) jsonObject.getString("phone")
                else "",
                birthDate =
                if (jsonObject.has("birthDate")) jsonObject.getString("birthDate")
                else "",
                city =
                if (jsonObject.has("city")) jsonObject.getString("city")
                else ""
            )
        }
    }
}

