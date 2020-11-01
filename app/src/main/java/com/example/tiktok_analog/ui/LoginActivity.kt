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
import com.example.tiktok_analog.ui.login.LoginViewModel
import com.example.tiktok_analog.ui.login.LoginViewModelFactory
import com.example.tiktok_analog.ui.main.MainActivity
import com.example.tiktok_analog.ui.register.RegisterViewModel
import com.example.tiktok_analog.ui.register.RegisterViewModelFactory
import com.example.tiktok_analog.ui.register.RegisteredUserView
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel
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

        initRegisterScreen()
        initLoginScreen()
    }

    private fun initRegisterScreen() {
        val register = findViewById<Button>(R.id.register)

        val username = findViewById<EditText>(R.id.name)
        val phone = findViewById<EditText>(R.id.phone)
        val birthDate = findViewById<TextView>(R.id.birth_date_text)
        val city = findViewById<EditText>(R.id.city)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.passwordRegistration)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password)

        val mDateSetListener: OnDateSetListener

        // TODO: refactor with authorization / registration

        registerViewModel = ViewModelProviders.of(this, RegisterViewModelFactory())
            .get(RegisterViewModel::class.java)

        registerViewModel.registerFormState.observe(this@LoginActivity, Observer {
            val registerState = it ?: return@Observer

            // disable login button unless both username / password is valid
            register.isEnabled = registerState.isDataValid

            register.backgroundTintList = applicationContext.resources.getColorStateList(
                if (registerState.isDataValid)
                    R.color.buttonEnabledBg
                else
                    R.color.buttonDisabledBg
            )

            if (registerState.usernameError != null) {
                username.error = getString(registerState.usernameError)
            }
            if (registerState.passwordError != null) {
                password.error = getString(registerState.passwordError)
            }
            if (registerState.passwordMatchError != null) {
                confirmPassword.error = getString(registerState.passwordMatchError)
            }
        })

        registerViewModel.registerResult.observe(this@LoginActivity, Observer {
            val registerResult = it ?: return@Observer

            // loading.visibility = View.GONE
            if (registerResult.error != null) {
                showLoginFailed(registerResult.error)
            }

            if (registerResult.success != null) {
                updateUiWithUser(registerResult.success)
            }
            setResult(Activity.RESULT_OK)

            // Complete and destroy login activity once successful
            // finish()

            // Instead of destroying activity in case of correct registration we open SmsActivity
            startActivity(Intent(this, SmsActivity::class.java))
        })

        username.afterTextChanged {
            registerViewModel.registerDataChanged(
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
                registerViewModel.registerDataChanged(
                    username.text.toString(),
                    password.text.toString(),
                    confirmPassword.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        registerViewModel.register(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }
        }

        confirmPassword.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    username.text.toString(),
                    password.text.toString(),
                    confirmPassword.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        registerViewModel.register(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }
        }

        register.setOnClickListener {
            // loading.visibility = View.VISIBLE
            registerViewModel.register(username.text.toString(), password.text.toString())
        }
    }

    private fun initLoginScreen() {
        val login = findViewById<Button>(R.id.login)

        val username = findViewById<EditText>(R.id.usernameField)
        val password = findViewById<EditText>(R.id.passwordAuthorization)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            login.backgroundTintList = applicationContext.resources.getColorStateList(
                if (loginState.isDataValid)
                    R.color.buttonEnabledBg
                else
                    R.color.buttonDisabledBg
            )

            if (loginState.usernameError != null) {
                username.error = "Некорректное имя пользователя"
            }
            if (loginState.passwordError != null) {
                password.error = "Неправильный пароль"
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val registerResult = it ?: return@Observer

            // here may be errors that i should catch

            startActivity(Intent(this, MainActivity::class.java))
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        login.setOnClickListener {
            // loading.visibility = View.VISIBLE
            loginViewModel.login(username.text.toString(), password.text.toString())
        }
    }

    private fun updateUiWithUser(model: RegisteredUserView) {
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