package com.example.tiktok_analog.ui.login

/**
 * Authentication result : success (profile details) or error message.
 */
data class LoginResult(
     val success: LoggedInUserView? = null,
     val error: Int? = null
)