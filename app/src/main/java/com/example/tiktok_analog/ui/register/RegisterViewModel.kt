package com.example.tiktok_analog.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiktok_analog.data.login.LoginRepository
import com.example.tiktok_analog.data.Result

import com.example.tiktok_analog.R

class RegisterViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<RegisterFromState>()
    val loginFormState: LiveData<RegisterFromState> = _loginForm

    private val _loginResult = MutableLiveData<RegisterResult>()
    val loginResult: LiveData<RegisterResult> = _loginResult

    fun login(username: String, password: String) {

        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.value =
                RegisterResult(success = RegisteredUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = RegisterResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String, password2: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = RegisterFromState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = RegisterFromState(passwordError = R.string.invalid_password)
        } else if (!doesPasswordsMatch(password, password2)) {
            _loginForm.value = RegisterFromState(passwordMatchError = R.string.passwords_not_match)
        } else {
            _loginForm.value = RegisterFromState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
//        return if (username.contains('@')) {
//            Patterns.EMAIL_ADDRESS.matcher(username).matches()
//        } else {
//            username.isNotBlank()
//        }
        return username.isNotBlank()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun doesPasswordsMatch(password1: String, password2: String): Boolean {
        return password1 == password2
    }
}