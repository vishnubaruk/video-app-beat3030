package com.example.tiktok_analog.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.tiktok_analog.MainActivity
import com.example.tiktok_analog.R
import com.example.tiktok_analog.SmsActivity
import kotlinx.android.synthetic.main.authorize.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val registerButton = findViewById<Button>(R.id.register_button)
        val authorizeButton = findViewById<Button>(R.id.authorize_button)
        val registerPanel = findViewById<ConstraintLayout>(R.id.register_panel)
        val authorizePanel = findViewById<ConstraintLayout>(R.id.authorize_panel)
        // val loading = findViewById<ProgressBar>(R.id.loading)

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

        // TODO: enable buttons only when all data is correct

        login.isEnabled = true
        login.backgroundTintList =
            applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)

        // login button in register panel
        login.setOnClickListener {
            startActivity(Intent(this, SmsActivity::class.java))
            Log.d("DEBUG", "login button pressed")
        }


        enter.isEnabled = true
        enter.backgroundTintList =
            applicationContext.resources.getColorStateList(R.color.buttonEnabledBg)
        // enter button in authorize panel
        enter.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            Log.d("DEBUG", "enter button pressed")
        }


        // TODO: refactor with authorization / registration
        return


        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
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

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
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


            // TODO: make load of the next scene with login viewModel
//            login.setOnClickListener {
//                // loading.visibility = View.VISIBLE
//                loginViewModel.login(username.text.toString(), password.text.toString())
//            }
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.actovity_sms)
//    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
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