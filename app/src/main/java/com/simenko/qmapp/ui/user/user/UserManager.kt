package com.simenko.qmapp.ui.user.user

import android.util.Log
import com.simenko.qmapp.ui.user.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

private const val REGISTERED_USER = "registered_user"
private const val PASSWORD_SUFFIX = "password"

/**
 * Handles User lifecycle. Manages registrations, logs in and logs out.
 * Knows when the user is logged in.
 *
 * Marked with @Singleton since we only one an instance of UserManager in the application graph.
 */
private const val TAG = "UserManager"
@Singleton
class UserManager @Inject constructor(
    private val storage: Storage,
    // Since UserManager will be in charge of managing the UserComponent lifecycle,
    // it needs to know how to create instances of it
    private val userDataRepository: UserDataRepository
) {

    val username: String
        get() = storage.getString(REGISTERED_USER)

    fun isUserLoggedIn(): Boolean {
        Log.d(TAG, "isUserLoggedIn: ${storage.getString(REGISTERED_USER)}")
        return userDataRepository.username != null
    }

    fun isUserRegistered(): Boolean {
        Log.d(TAG, "isUserRegistered: ${storage.getString(REGISTERED_USER)}")
        return storage.getString(REGISTERED_USER).isNotEmpty()
    }

    fun registerUser(username: String, password: String) {
        storage.setString(REGISTERED_USER, username)
        storage.setString("$username$PASSWORD_SUFFIX", password)
        userJustLoggedIn(username)
    }

    fun loginUser(username: String, password: String): Boolean {
        val registeredUser = this.username
        if (registeredUser != username) return false

        val registeredPassword = storage.getString("$username$PASSWORD_SUFFIX")
        if (registeredPassword != password) return false

        userJustLoggedIn(username)
        return true
    }

    fun logout() {
        userDataRepository.cleanUp()
    }

    fun unregister() {
        val username = storage.getString(REGISTERED_USER)
        storage.setString(REGISTERED_USER, "")
        storage.setString("$username$PASSWORD_SUFFIX", "")
        logout()
    }

    private fun userJustLoggedIn(username: String) {
        // When the user logs in, we create populate data in UserComponent
        userDataRepository.initData(username)
    }
}
