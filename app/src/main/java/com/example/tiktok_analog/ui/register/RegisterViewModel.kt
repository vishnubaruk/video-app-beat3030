package com.example.tiktok_analog.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiktok_analog.data.login.LoginRepository
import com.example.tiktok_analog.data.Result

import com.example.tiktok_analog.R

class RegisterViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFromState>()
    val registerFormState: LiveData<RegisterFromState> = _registerForm

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun register(
        username: String,
        phone: String,
        birthDate: String,
        city: String,
        email: String,
        password: String
    ) {

        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _registerResult.value =
                RegisterResult(success = RegisteredUserView(displayName = result.data.username))
        } else {
            _registerResult.value = RegisterResult(error = R.string.login_failed)
        }
    }

    fun registerDataChanged(
        username: String,
        phone: String,
        city: String,
        birthDate: String,
        email: String,
        password: String,
        password2: String
    ) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegisterFromState(usernameError = R.string.invalid_username)
        } else if (!isPhoneNumberValid(phone)) {
            _registerForm.value = RegisterFromState(phoneError = R.string.invalid_phone_number)
        } else if (!isEmailValid(email)) {
            _registerForm.value = RegisterFromState(emailError = R.string.invalid_email)
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
        return username.isNotBlank()
    }

    // A placeholder phone number validation check
    private fun isPhoneNumberValid(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    // A placeholder email validation check
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun doesPasswordsMatch(password1: String, password2: String): Boolean {
        return password1 == password2
    }
}