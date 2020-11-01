package com.example.tiktok_analog.ui.register

/**
 * Data validation state of the registration form.
 */
data class RegisterFromState(
    val usernameError: Int? = null,
    val phoneError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val passwordMatchError: Int? = null,
    val isDataValid: Boolean = false
)