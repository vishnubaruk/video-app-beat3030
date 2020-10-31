package com.example.tiktok_analog.ui.register

/**
 * Authentication result : success (profile details) or error message.
 */
data class RegisterResult(
     val success: RegisteredUserView? = null,
     val error: Int? = null
)