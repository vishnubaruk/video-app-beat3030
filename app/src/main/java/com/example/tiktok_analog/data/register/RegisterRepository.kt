package com.example.tiktok_analog.data.register

import com.example.tiktok_analog.data.Result
import com.example.tiktok_analog.data.register.model.RegisteredUser

/**
 * Class that requests registration and profile information from the remote data source and
 * maintains an in-memory cache of login status and profile credentials information.
 */

class RegisterRepository(private val dataSource: RegisterDataSource) {

    // in-memory cache of the loggedInUser object
    var user: RegisteredUser? = null
        private set

    val isRegistered: Boolean
        get() = user != null

    init {
        // If profile credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun register(username: String, password: String): Result<RegisteredUser> {
        // handle login
        val result = dataSource.register(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(registeredUser: RegisteredUser) {
        this.user = registeredUser
        // If profile credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}