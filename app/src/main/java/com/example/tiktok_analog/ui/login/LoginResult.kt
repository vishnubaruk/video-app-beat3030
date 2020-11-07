package com.example.tiktok_analog.ui.login

import com.example.tiktok_analog.data.model.User

/**
 * Authentication result : success (profile details) or error message.
 */
data class LoginResult(
     val success: LoggedInUserView? = null,
     val error: Int? = null,
     val userData: User? = null
)