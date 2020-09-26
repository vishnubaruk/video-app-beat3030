package com.example.tiktok_analog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok_analog.ui.login.afterTextChanged
import kotlinx.android.synthetic.main.activity_sms.*

class SmsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)

        val rootView = findViewById<View>(android.R.id.content).rootView

        et1.requestFocus()

        fun hideKeyboardFrom(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun checkSmsCode() {
            sendSms.isEnabled =
                et1.text.length == 1 && et2.text.length == 1 &&
                        et3.text.length == 1 && et4.text.length == 1

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
            // startActivity(Intent(this, MainActivity::class.java))
        }
    }
}