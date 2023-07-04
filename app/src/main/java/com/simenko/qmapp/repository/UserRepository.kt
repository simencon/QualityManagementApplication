package com.simenko.qmapp.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val REGISTERED_USER = "registered_user"
private const val PASSWORD_SUFFIX = "password"
private const val IS_EMAIL_VERIFIED = "is_email_verified"
private const val IS_USER_LOG_IN = "is_user_log_in"
private const val IS_VERIFIED_BY_ORGANISATION = "is_verified_by_organisation"

@Singleton
class UserRepository @Inject constructor(
    private val storage: Storage,
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) {
    private val _userState: MutableStateFlow<Event<UserState>> = MutableStateFlow(Event(UserInitialState))

    val userState: StateFlow<Event<UserState>>
        get() = _userState

    val username: String
        get() = storage.getString(REGISTERED_USER)

    suspend fun getUserState() = suspendCoroutine { continuation ->
        val registeredUser = storage.getString(REGISTERED_USER)
        val registeredPassword = storage.getString("$username$PASSWORD_SUFFIX")
        val isVerifiedEmail = storage.getBoolean(IS_EMAIL_VERIFIED)
        val isUserLogIn = storage.getBoolean(IS_USER_LOG_IN)

        if (registeredUser.isEmpty() || registeredPassword.isEmpty()) {
            _userState.value = Event(UserInitialState)
            continuation.resume(_userState.value.peekContent())
        } else {
            auth.signInWithEmailAndPassword(registeredUser, registeredPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (auth.currentUser?.isEmailVerified == true) {
                            if (isUserLogIn) {
                                storage.setBoolean(IS_EMAIL_VERIFIED, true)
                                _userState.value = Event(UserLoggedInState("user is registered - verified with Firebase"))
                                continuation.resume(_userState.value.peekContent())
                            } else {
                                storage.setBoolean(IS_EMAIL_VERIFIED, true)
                                _userState.value = Event(UserLoggedOutState("user is registered - verified with Firebase - log out in the app"))
                                continuation.resume(_userState.value.peekContent())
                            }
                        } else {
                            _userState.value = Event(UserNeedToVerifyEmailState("Verification link was send to email: ${auth.currentUser?.email}"))
                            continuation.resume(_userState.value.peekContent())
                        }
                    } else {
                        when (task.exception) {
                            is FirebaseNetworkException -> {
                                if (isVerifiedEmail) {
                                    if (isUserLogIn) {
                                        _userState.value = Event(UserLoggedInState("No network - just continue with Storage"))
                                        continuation.resume(_userState.value.peekContent())
                                    } else {
                                        _userState.value = Event(UserLoggedOutState("No network - just continue with Storage - log out in the app"))
                                        continuation.resume(_userState.value.peekContent())
                                    }
                                } else {
                                    _userState.value = Event(UserNeedToVerifyEmailState("Please check your email box"))
                                    continuation.resume(_userState.value.peekContent())
                                }
                            }

                            else -> {
                                _userState.value = Event(UserInitialState)
                                continuation.resume(_userState.value.peekContent())
                            }
                        }
                    }
                }
        }
    }

    fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storage.setString(REGISTERED_USER, auth.currentUser?.email ?: "no mail")
                    storage.setString("$email$PASSWORD_SUFFIX", password)
                    sendVerificationEmail(auth.currentUser)
                } else {
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            storage.setString(REGISTERED_USER, email)
                            storage.setString("$email$PASSWORD_SUFFIX", password)
                            _userState.value = Event(UserRegisteredState(task.exception?.message ?: "user already registered"))
                        }

                        else -> {
                            _userState.value = Event(UserErrorState(task.exception?.message))
                        }
                    }
                }
            }
    }

    fun sendVerificationEmail(user: FirebaseUser?) {
        user.let {
            if (it != null) {
                it.sendEmailVerification()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _userState.value = Event(UserNeedToVerifyEmailState("Verification link was send to email: ${auth.currentUser?.email}"))
                    } else {
                        _userState.value = Event(UserErrorState(task.exception?.message))
                    }
                }
            } else {
                val email = storage.getString(REGISTERED_USER)
                auth.signInWithEmailAndPassword(email, storage.getString("$email$PASSWORD_SUFFIX")).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendVerificationEmail(auth.currentUser)
                    } else {
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
                        storage.setBoolean(IS_USER_LOG_IN, true)
                        _userState.value = Event(UserLoggedInState(auth.currentUser?.email ?: "no mail"))
                    } else {
                        when (task.exception) {
                            is FirebaseNetworkException -> {
                                if (this.username == username && storage.getString("$username$PASSWORD_SUFFIX") == password) {
                                    storage.setBoolean(IS_USER_LOG_IN, true)
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
                storage.setBoolean(IS_USER_LOG_IN, false)
                _userState.value = Event(UserLoggedOutState("Registered, not logged In"))
            }
        } else {
            storage.setBoolean(IS_USER_LOG_IN, false)
            _userState.value = Event(UserLoggedOutState("Registered, not logged In"))
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
                                storage.setBoolean(IS_EMAIL_VERIFIED, false)
                                logout()
                                _userState.value = Event(UserInitialState)
                            } else {
                                _userState.value = Event(UserErrorState(task2.exception?.message ?: "Unknown error"))
                            }
                        }
                    } else {
                        if (task1.exception?.message?.contains("User has been disabled") == true) {
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

    fun setUserJobRole(userJobRole: String) {
        addMessage(userJobRole).addOnCompleteListener { task ->
            val e = task.exception
            if (e is FirebaseFunctionsException) {
                _userState.value = Event(UserErrorState("${e.code}, ${e.details}"))
            } else {
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
                val result = task.result?.data as String
                result
            }
    }
}

sealed class UserState
object UserInitialState : UserState()

data class UserRegisteredState(val msg: String) : UserState()
data class UserNeedToVerifyEmailState(val msg: String) : UserState()
data class UserNeedToVerifiedByOrganisationState(val msg: String) : UserState()
data class UserLoggedOutState(val msg: String) : UserState()
data class UserLoggedInState(val msg: String) : UserState()
data class UserErrorState(val error: String?) : UserState()
