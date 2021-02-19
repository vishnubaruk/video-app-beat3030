package com.example.tiktok_analog.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.ui.main.MainActivity
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.util.GenericTextWatcher
import kotlinx.android.synthetic.main.activity_sms.*
import kotlinx.android.synthetic.main.menu.*
import org.json.JSONObject

class SmsActivity : AppCompatActivity() {

    private lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)

        openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        val rootView = findViewById<View>(android.R.id.content).rootView

        fun hideKeyboardFrom(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showKeyboardFrom(context: Context, ed: EditText) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(ed, 0)
        }

        et1.isFocusableInTouchMode = true
        et1.requestFocus()
        showKeyboardFrom(applicationContext, et1)


        fun checkSmsCode() {
            sendSms.isEnabled =
                et1.text.length == 1 && et2.text.length == 1 &&
                        et3.text.length == 1 && et4.text.length == 1

            sendSms.backgroundTintList =
                applicationContext.resources.getColorStateList(
                    if (sendSms.isEnabled) R.color.buttonEnabledBg else R.color.buttonDisabledBg
                )

            if (sendSms.isEnabled) {
                hideKeyboardFrom(application.applicationContext, rootView)
                sendSms.callOnClick()
            }
        }

        et1.afterTextChanged { checkSmsCode() }
        et1.addTextChangedListener(GenericTextWatcher(rootView, et1))

        et2.afterTextChanged { checkSmsCode() }
        et2.addTextChangedListener(GenericTextWatcher(rootView, et2))

        et3.afterTextChanged { checkSmsCode() }
        et3.addTextChangedListener(GenericTextWatcher(rootView, et3))

        et4.afterTextChanged { checkSmsCode() }
        et4.addTextChangedListener(GenericTextWatcher(rootView, et4))

        sendSms.setOnClickListener {
            val sendSmsQueue = Volley.newRequestQueue(this)

            val url =
                "https://kepler88d.pythonanywhere.com/smsConfirmation?" +
                        "phone=${userData.phone}&email=${userData.email}&code=${et1.text}${et2.text}${et3.text}${et4.text}"

            val registerRequest = StringRequest(Request.Method.GET, url, { response ->
                run {
                    val jsonResponse = JSONObject(response)

                    if (jsonResponse.getBoolean("ok")) {
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        AlertDialog.Builder(this).setTitle("Неверный код из смс")
                            .setMessage("Попробуйте еще раз")
                            .setPositiveButton("Хорошо") { dialog, _ ->
                                dialog.cancel()
                                onBackPressed()
                            }.create().show()
                    }
                }
            }, {
                Log.e("ERROR", "Error at sign in : " + it.message)
            })

            sendSmsQueue.add(registerRequest)
        }
    }
}