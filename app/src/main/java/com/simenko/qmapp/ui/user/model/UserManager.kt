package com.simenko.qmapp.ui.user.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.ui.user.registration.RegistrationActivity
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewState
import com.simenko.qmapp.ui.user.storage.Storage
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

@ActivityScoped
class UserManager @Inject constructor(
    private val storage: Storage,
    private val auth: FirebaseAuth,
    private val userDataRepository: UserDataRepository,
    @ActivityContext private val activity: Context,
) {
    private val _userRegisteredState: MutableStateFlow<Event<UserState>> = MutableStateFlow(Event(UserInitialState))
    val userRegisteredState: StateFlow<Event<UserState>>
        get() = _userRegisteredState

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
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener(activity as RegistrationActivity) { task ->
                if (task.isSuccessful) {
                    storage.setString(REGISTERED_USER, auth.currentUser?.email ?: "no mail")
                    storage.setString("$username$PASSWORD_SUFFIX", password)
                    userJustLoggedIn(storage.getString(REGISTERED_USER))
                    _userRegisteredState.value = Event(UserRegisteredState)
                    Log.d(TAG, "createUserWithEmail:success")
                } else {
                    _userRegisteredState.value = Event(UserErrorState(task.exception?.message))
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                }
            }
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

sealed class UserState

object UserInitialState : UserState()
object UserRegisteredState : UserState()
data class UserErrorState(val error: String?) : UserState()
