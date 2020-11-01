package com.example.tiktok_analog.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiktok_analog.data.login.RegisterRepository
import com.example.tiktok_analog.data.Result

import com.example.tiktok_analog.R

class RegisterViewModel(private val registerRepository: RegisterRepository) : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFromState>()
    val registerFormState: LiveData<RegisterFromState> = _registerForm

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun register(username: String, password: String) {

        // can be launched in a separate asynchronous job
        val result = registerRepository.login(username, password)

        if (result is Result.Success) {
            _registerResult.value =
                RegisterResult(success = RegisteredUserView(displayName = result.data.displayName))
        } else {
            _registerResult.value = RegisterResult(error = R.string.login_failed)
        }
    }

    fun registerDataChanged(username: String, password: String, password2: String) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegisterFromState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = RegisterFromState(passwordError = R.string.invalid_password)
        } else if (!doesPasswordsMatch(password, password2)) {
            _registerForm.value =
                RegisterFromState(passwordMatchError = R.string.passwords_not_match)
        } else {
            _registerForm.value = RegisterFromState(isDataValid = true)
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