package com.simenko.qmapp.ui.user.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.ui.user.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    private val userDataRepository: UserDataRepository,
    private val functions: FirebaseFunctions
) {
    private val _userState: MutableStateFlow<Event<UserState>> = MutableStateFlow(Event(UserInitialState))
    val userState: StateFlow<Event<UserState>>
        get() = _userState

    val username: String
        get() = storage.getString(REGISTERED_USER)

    fun isUserLoggedIn(): Boolean {
        return userDataRepository.username != null
    }

    suspend fun isUserRegistered() = suspendCoroutine { continuation ->
        val registeredUser = storage.getString(REGISTERED_USER)
        val registeredPassword = storage.getString("$username$PASSWORD_SUFFIX")

        if (registeredUser.isEmpty() || registeredPassword.isEmpty()) {
            _userState.value = Event(UserInitialState)
            continuation.resume(false)
        } else {
            auth.signInWithEmailAndPassword(registeredUser, registeredPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.signOut()
                        _userState.value = Event(UserRegisteredState("user is registered - verified with Firebase"))
                        continuation.resume(true)
                    } else {
                        when (task.exception) {
                            is FirebaseNetworkException -> {
                                _userState.value = Event(UserRegisteredState("No network - just continue with Storage"))
                                continuation.resume(true)
                            }

                            else -> {
                                _userState.value = Event(UserInitialState)
                                continuation.resume(false)
                            }
                        }
                    }
                }
        }
    }

    fun registerUser(username: String, password: String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storage.setString(REGISTERED_USER, auth.currentUser?.email ?: "no mail")
                    storage.setString("$username$PASSWORD_SUFFIX", password)
                    userJustLoggedIn(storage.getString(REGISTERED_USER))
                    _userState.value = Event(UserLoggedInState(auth.currentUser?.email ?: "no mail"))
                } else {
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            storage.setString(REGISTERED_USER, username)
                            storage.setString("$username$PASSWORD_SUFFIX", password)
                            _userState.value = Event(UserRegisteredState(task.exception?.message))
                        }

                        else -> {
                            _userState.value = Event(UserErrorState(task.exception?.message))
                        }
                    }
                }
            }
    }

    fun loginUser(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty())
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storage.setString(REGISTERED_USER, auth.currentUser?.email ?: "no mail")
                        storage.setString("$username$PASSWORD_SUFFIX", password)
                        userJustLoggedIn(username)
                        _userState.value = Event(UserLoggedInState(auth.currentUser?.email ?: "no mail"))
                    } else {
                        when (task.exception) {
                            is FirebaseNetworkException -> {
                                if (this.username == username && storage.getString("$username$PASSWORD_SUFFIX") == password) {
                                    userJustLoggedIn(username)
                                    _userState.value = Event(UserLoggedInState(username))
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
                    }
                }
        else {
            _userState.value = Event(UserErrorState("Email and Password fields should not be empty"))
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

    fun deleteProfile(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty())
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        auth.currentUser?.delete()?.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                storage.setString(REGISTERED_USER, "")
                                storage.setString("$username$PASSWORD_SUFFIX", "")
                                logout()
                                _userState.value = Event(UserInitialState)
                            } else {
                                _userState.value = Event(UserErrorState(task2.exception?.message ?: "Unknown error"))
                            }
                        }
                    } else {
                        if(task1.exception?.message?.contains("User has been disabled") == true) {
                            storage.setString(REGISTERED_USER, "")
                            storage.setString("$username$PASSWORD_SUFFIX", "")
                            logout()
                            _userState.value = Event(UserInitialState)
                        } else {
                            _userState.value = Event(UserErrorState(task1.exception?.message ?: "Unknown error"))
                        }
                    }
                }
        else {
            _userState.value = Event(UserErrorState("Email and Password fields should not be empty"))
        }
    }

    private fun userJustLoggedIn(username: String) {
        // When the user logs in, we create populate data in UserComponent
        userDataRepository.initData(username)
    }

    fun setUserJobRole(userJobRole: String) {
        Log.d(TAG, "setUserJobRole: auth = ${auth.currentUser}")
        addMessage(userJobRole).addOnCompleteListener { task ->
            val e = task.exception
            if (e is FirebaseFunctionsException) {
                Log.d(TAG, "setUserJobRole: exception: $e")
                _userState.value = Event(UserErrorState("${e.code}, ${e.details}"))
            } else {
                Log.d(TAG, "setUserJobRole: success: ${task.result}")
                _userState.value = Event(UserLoggedInState(task.result))
            }
        }
    }

    private fun addMessage(text: String): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "userJobRole" to text
        )

        return functions
            .getHttpsCallable("addRequest")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                Log.d(TAG, "addMessage: $task")
                val result = task.result?.data as String
                result
            }
    }
}

sealed class UserState
object UserInitialState : UserState()
data class UserRegisteredState(val msg: String?) : UserState()
data class UserLoggedInState(val msg: String) : UserState()
data class UserErrorState(val error: String?) : UserState()
