package com.example.tiktok_analog.data.login

import com.example.tiktok_analog.data.Result
import com.example.tiktok_analog.data.model.User

/**
 * Class that requests authentication and profile information from the remote data source and
 * maintains an in-memory cache of register status and profile credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    private var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If profile credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun login(username: String, password: String): Result<User> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: User) {
        this.user = loggedInUser
        // If profile credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}