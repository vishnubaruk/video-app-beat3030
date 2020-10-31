package com.example.tiktok_analog.data.register.model

/**
 * Data class that captures profile information for registered users retrieved from RegisterRepository
 */
data class RegisteredUser(
    val userId: String,
    val displayName: String
)