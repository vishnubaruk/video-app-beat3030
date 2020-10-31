package com.example.tiktok_analog.data.login.model

/**
 * Data class that captures profile information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String
)