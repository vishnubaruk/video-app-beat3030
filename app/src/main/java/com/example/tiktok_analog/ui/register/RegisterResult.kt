package com.example.tiktok_analog.ui.register

import com.example.tiktok_analog.data.model.User

/**
 * Authentication result : success (profile details) or error message.
 */
data class RegisterResult(
     val success: RegisteredUserView? = null,
     val error: Int? = null,
     val userData: User? = null
)