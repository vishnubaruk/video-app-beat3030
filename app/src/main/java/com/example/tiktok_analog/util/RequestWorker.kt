package com.example.tiktok_analog.util

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
    }
}