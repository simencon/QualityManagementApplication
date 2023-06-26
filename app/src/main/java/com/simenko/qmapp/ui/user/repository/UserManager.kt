package com.simenko.qmapp.ui.user.repository

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.ui.user.storage.Storage
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

@Singleton
class UserManager @Inject constructor(
    private val storage: Storage,
    private val auth: FirebaseAuth,
    private val userDataRepository: UserDataRepository
) {
    private val _userState: MutableStateFlow<Event<UserState>> = MutableStateFlow(Event(UserInitialState))
    val userState: StateFlow<Event<UserState>>
        get() = _userState

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
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storage.setString(REGISTERED_USER, auth.currentUser?.email ?: "no mail")
                    storage.setString("$username$PASSWORD_SUFFIX", password)
                    userJustLoggedIn(storage.getString(REGISTERED_USER))
                    _userState.value = Event(UserLoggedInState)
                    Log.d(TAG, "createUserWithEmail:success")
                } else {
                    when(task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            _userState.value = Event(UserRegisteredState(task.exception?.message))
                        }
                        else -> {
                            _userState.value = Event(UserErrorState(task.exception?.message))
                        }
                    }
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    fun loginUser(username: String, password: String) {
        val registeredUser = this.username
        val registeredPassword = storage.getString("$username$PASSWORD_SUFFIX")

        auth.signInWithEmailAndPassword(storage.getString(REGISTERED_USER), storage.getString("$username$PASSWORD_SUFFIX"))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storage.setString(REGISTERED_USER, auth.currentUser?.email ?: "no mail")
                    storage.setString("$username$PASSWORD_SUFFIX", password)
                    _userState.value = Event(UserLoggedInState)
                    userJustLoggedIn(username)
                } else {
                    when (task.exception) {
                        is FirebaseNetworkException -> {
                            if (this.username == username && storage.getString("$username$PASSWORD_SUFFIX") == password) {
                                _userState.value = Event(UserLoggedInState)
                                userJustLoggedIn(username)
                            } else if (this.username != username) {
                                _userState.value = Event(UserErrorState("Wrong email"))
                            } else if (storage.getString("$username$PASSWORD_SUFFIX") != password) {
                                _userState.value = Event(UserErrorState("Wrong password"))
                            }
                        }
                        else -> {
                            _userState.value = Event(UserErrorState(task.exception?.message))
                        }
                    }
                    Log.d(TAG, "loginUser/error: ${task.exception}")
                }
            }
    }

    fun logout() {
        if (auth.currentUser != null) {
            auth.signOut()
            if (auth.currentUser == null) {
                userDataRepository.cleanUp()
                _userState.value = Event(UserRegisteredState("Registered, not logged In"))
            }
        } else {
            userDataRepository.cleanUp()
            _userState.value = Event(UserRegisteredState("Registered, not logged In"))
        }
    }

    fun unregister() {
        auth.signInWithEmailAndPassword(storage.getString(REGISTERED_USER), storage.getString("$username$PASSWORD_SUFFIX"))
            .addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    auth.currentUser?.delete()?.addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            val username = storage.getString(REGISTERED_USER)
                            storage.setString(REGISTERED_USER, "")
                            storage.setString("$username$PASSWORD_SUFFIX", "")
                            logout()
                            _userState.value = Event(UserInitialState)
                            Log.d(TAG, "deleteUser:success")
                        } else {
                            _userState.value = Event(UserErrorState(task2.exception?.message ?: "Unknown error"))
                        }
                    }
                } else {
                    _userState.value = Event(UserErrorState(task1.exception?.message ?: "Unknown error"))
                }
            }
    }

    private fun userJustLoggedIn(username: String) {
        // When the user logs in, we create populate data in UserComponent
        userDataRepository.initData(username)
    }
}

sealed class UserState

object UserInitialState : UserState()
data class UserRegisteredState(val msg: String?) : UserState()
object UserLoggedInState : UserState()
data class UserErrorState(val error: String?) : UserState()
