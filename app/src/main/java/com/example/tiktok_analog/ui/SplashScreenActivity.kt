package com.example.tiktok_analog.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.main.MainActivity
import com.example.tiktok_analog.util.GlobalDataStorage
import org.json.JSONObject

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        ActivityCompat.requestPermissions(
            this,
            requiredPermissions,
            300
        )

        val userDataFile = applicationContext.getFileStreamPath("userData")
        if (userDataFile != null && userDataFile.exists() && requiredPermissions.all {
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            openFileInput("userData").use {
                userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
            }

            val url =
                "https://kepler88d.pythonanywhere.com/exist?email=${userData.email}&phone=${userData.phone}"
            val userExistQueue = Volley.newRequestQueue(applicationContext)

            val userExistRequest =
                StringRequest(Request.Method.GET, url, { response ->
                    run {
                        val result = JSONObject(response)
                        val intent = Intent(
                            this,
                            if (result.getBoolean("ok")) {
                                OpenVideoActivity::class.java
                            } else {
                                StartActivity::class.java
                            }
                        )
                        intent.putIntegerArrayListExtra("id", GlobalDataStorage.videoIdList)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }, {
                    Log.e("Does user exist", "Error at sign in : " + it.message)
                })

            userExistQueue.add(userExistRequest)
        } else {
            val intent = Intent(this, StartActivity::class.java)
            intent.putIntegerArrayListExtra("id", GlobalDataStorage.videoIdList)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        setContentView(R.layout.activity_splashscreen)
    }

    override fun onResume() {
        super.onResume()
    }
}