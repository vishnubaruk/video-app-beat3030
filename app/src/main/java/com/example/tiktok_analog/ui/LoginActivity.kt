package com.example.tiktok_analog.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.tiktok_analog.R
import com.example.tiktok_analog.ui.login.LoggedInUserView
import com.example.tiktok_analog.ui.login.LoginViewModel
import com.example.tiktok_analog.ui.login.LoginViewModelFactory
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val registerButton = findViewById<Button>(R.id.register_button)
        val authorizeButton = findViewById<Button>(R.id.authorize_button)

        val registerPanel = findViewById<ConstraintLayout>(R.id.register_panel)
        val authorizePanel = findViewById<ConstraintLayout>(R.id.authorize_panel)

        registerButton.setOnClickListener {
            registerPanel.visibility = View.VISIBLE
            authorizePanel.visibility = View.GONE

            registerButton.setBackgroundResource(R.drawable.bg_btn_outline)
            authorizeButton.setBackgroundResource(R.drawable.bg_bth_no_outline)
        }

        authorizeButton.setOnClickListener {
            registerPanel.visibility = View.GONE
            authorizePanel.visibility = View.VISIBLE

            registerButton.setBackgroundResource(R.drawable.bg_bth_no_outline)
            authorizeButton.setBackgroundResource(R.drawable.bg_btn_outline)
        }
    }

    private fun initRegisterScreen() {
        val register = findViewById<Button>(R.id.register)

        val username = findViewById<EditText>(R.id.name)
        val phone = findViewById<EditText>(R.id.phone)
        val birthDate = findViewById<TextView>(R.id.birth_date_text)
        val city = findViewById<EditText>(R.id.city)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password)

        val mDateSetListener: OnDateSetListener

        register.isEnabled = false
        register.backgroundTintList =
            applicationContext.resources.getColorStateList(R.color.buttonDisabledBg)

        // TODO: refactor with authorization / registration

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            register.isEnabled = loginState.isDataValid

            register.backgroundTintList = applicationContext.resources.getColorStateList(
                if (loginState.isDataValid)
                    R.color.buttonEnabledBg else R.color.buttonDisabledBg
            )

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
            if (loginState.passwordMatchError != null) {
                confirmPassword.error = getString(loginState.passwordMatchError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            // loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            // Complete and destroy login activity once successful
            // finish()

            // Instead of destroying activity in case of correct registration we open SmsActivity
            startActivity(Intent(this, SmsActivity::class.java))
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString()
            )
        }

        phone.addTextChangedListener(PhoneNumberFormattingTextWatcher("RU"))

        mDateSetListener =
            OnDateSetListener { _, year, month, day ->
                birthDate.text = "${month + 1}.$day.$year"
            }

        birthDate.setOnClickListener {
            val cal: Calendar = Calendar.getInstance();
            val year: Int = cal.get(Calendar.YEAR);
            val month: Int = cal.get(Calendar.MONTH);
            val day: Int = cal.get(Calendar.DAY_OF_MONTH);

            val dialog = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString(),
                    confirmPassword.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }


            register.setOnClickListener {
                // loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun initLoginScreen() {
        val login = findViewById<Button>(R.id.login)
        login.isEnabled = true
        login.backgroundTintList =
            applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}