package com.example.tiktok_analog.util

import com.example.tiktok_analog.data.model.User
import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class RequestWorker {
    companion object {
        private val client = HttpClient {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "kepler88d.pythonanywhere.com"
                }
            }
        }

        private val gsonBuilder = GsonBuilder().create()

        fun deleteVideo(videoId: Int) {
            GlobalScope.launch {
                client.get {
                    url("/deleteVideo")
                    parameter("videoId", videoId.toString())
                }
            }
        }

        fun getViewCount(videoId: Int, handler: (Int) -> Unit) {
            GlobalScope.launch {
                handler(
                    JSONObject(
                        client.get<String> {
                            url("/getViewCount")
                            parameter("videoId", videoId.toString())
                        }
                    ).getInt("viewCount")
                )
            }
        }

        fun editUserName(userId: Int, userName: String, handler: (String) -> Unit) {
            GlobalScope.launch {
                handler(
                    client.get {
                        url("/editUserName")
                        parameter("userId", userId.toString())
                        parameter("username", userName)
                    }
                )
            }
        }

        fun editUserBirthDate(userId: Int, birthDate: String, handler: (String) -> Unit) {
            GlobalScope.launch {
                handler(
                    client.get {
                        url("/editUserBirthDate")
                        parameter("userId", userId.toString())
                        parameter("birthDate", birthDate)
                    }
                )
            }
        }

        fun editUserCity(userId: Int, city: String, handler: (String) -> Unit) {
            GlobalScope.launch {
                handler(
                    client.get {
                        url("/editUserCity")
                        parameter("userId", userId.toString())
                        parameter("city", city)
                    }
                )
            }
        }

        fun getUser(userId: Int, handler: (User) -> Unit) {
            GlobalScope.launch {
                handler(
                    User.fromJsonString(JSONObject(client.get<String> {
                        url("/getUser")
                        parameter("userId", userId.toString())
                    }).getString("userData"))
                )
            }
        }
    }
}