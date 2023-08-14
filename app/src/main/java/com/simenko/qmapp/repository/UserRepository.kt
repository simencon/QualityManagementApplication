package com.simenko.qmapp.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.storage.Principle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "UserRepository"

@Singleton
class UserRepository @Inject constructor(
    private val storage: Storage,
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) {
    private val _userState: MutableStateFlow<Event<UserState>> = MutableStateFlow(Event(UserInitialState))

    val userState: StateFlow<Event<UserState>>
        get() = _userState

    val user: Principle
        get() = Principle(storage)

    fun clearUserData() {
        user.clearUserData()
        _userState.value = Event(UserRegisteredState("not yet registered on the phone"))
    }

    /**
     *@link (https://app.diagrams.net/#G1vvhdmr_4ATIBjb91JfzASgCwj16VsOkY)
     * */
    suspend fun getActualUserState() = suspendCoroutine { continuation ->
//        user.refreshModelDataFromStorage()
        if (user.email.isEmpty()) {
            user.clearUserData()
            _userState.value = Event(UserInitialState)
            continuation.resume(_userState.value.peekContent())
        } else if (user.email.isNotEmpty() && user.password.isEmpty()) {
            _userState.value = Event(UserLoggedOutState())
            continuation.resume(_userState.value.peekContent())
        } else if (user.email.isNotEmpty() && user.password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (user.isEmailVerified) {
                            if (user.isUserLoggedIn) {
                                _userState.value = Event(UserLoggedInState("Logged in, email is verified"))
                                continuation.resume(_userState.value.peekContent())
                            } else {
                                _userState.value = Event(UserLoggedOutState())
                                continuation.resume(_userState.value.peekContent())
                            }
                        } else {
                            if (auth.currentUser?.isEmailVerified == true) {
                                val principle = this.user
                                principle.isEmailVerified = true
                                callFirebaseFunction(principle, "updateUserData").addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful) {
                                        user.setUserIsEmailVerified(true)
                                        if (user.isUserLoggedIn) {
                                            _userState.value = Event(UserLoggedInState("Logged in, email is verified"))
                                            continuation.resume(_userState.value.peekContent())
                                        } else {
                                            _userState.value = Event(UserLoggedOutState())
                                            continuation.resume(_userState.value.peekContent())
                                        }
                                    } else {
                                        _userState.value = Event(UserNeedToVerifyEmailState())
                                        continuation.resume(_userState.value.peekContent())
                                    }
                                }
                            } else {
                                _userState.value = Event(UserNeedToVerifyEmailState())
                                continuation.resume(_userState.value.peekContent())
                            }
                        }
                    } else {
                        when (task.exception) {
                            is FirebaseNetworkException -> {
                                if (user.isEmailVerified) {
                                    if (user.isUserLoggedIn) {
                                        _userState.value = Event(UserLoggedInState("Logged in, email is verified"))
                                        continuation.resume(_userState.value.peekContent())
                                    } else {
                                        _userState.value = Event(UserLoggedOutState())
                                        continuation.resume(_userState.value.peekContent())
                                    }
                                } else {
                                    _userState.value = Event(UserNeedToVerifyEmailState())
                                    continuation.resume(_userState.value.peekContent())
                                }
                            }

                            is FirebaseAuthInvalidCredentialsException -> {
                                _userState.value = Event(UserLoggedOutState("Password was changed"))
                                continuation.resume(_userState.value.peekContent())
                            }

                            is FirebaseAuthInvalidUserException -> {
                                if (task.exception?.message?.contains("account has been disabled") == true) {
                                    _userState.value = Event(UserLoggedOutState("Account has been disabled"))
                                    continuation.resume(_userState.value.peekContent())
                                } else if (task.exception?.message?.contains("user may have been deleted") == true) {
                                    user.clearUserData()
                                    _userState.value = Event(UserInitialState)
                                    continuation.resume(_userState.value.peekContent())
                                }
                            }

                            else -> {
                                user.clearUserData()
                                _userState.value = Event(UserInitialState)
                                continuation.resume(_userState.value.peekContent())
                            }
                        }
                    }
                }
        }
    }

    /**
     * @link (https://app.diagrams.net/#G150Nrkavd2FyyPkCRrxgTbk2PX9qSbDwF)
     * */
    fun registerUser(principle: Principle) {
        auth.createUserWithEmailAndPassword(principle.email, principle.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.storeUserData(principle)
                    sendVerificationEmail(auth.currentUser)
                } else {
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            user.storeUserData(principle)
                            _userState.value = Event(UserRegisteredState(task.exception?.message ?: "user already registered"))
                        }

                        else -> {
                            _userState.value = Event(UserErrorState(task.exception?.message))
                        }
                    }
                }
            }
    }

    fun sendVerificationEmail(firebaseUser: FirebaseUser?) {
        firebaseUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _userState.value = Event(UserNeedToVerifyEmailState())
            } else {
                _userState.value = Event(UserErrorState(task.exception?.message))
            }
        }
            ?: auth.signInWithEmailAndPassword(this.user.email, this.user.password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendVerificationEmail(auth.currentUser)
                } else {
                    _userState.value = Event(UserErrorState(task.exception?.message))
                }
            }
    }

    fun sendResetPasswordEmail(email: String) {
        if (email.isNotEmpty())
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.setUserEmail(email)
                    _userState.value = Event(UserLoggedOutState("Check your email box and set new password"))
                } else {
                    _userState.value = Event(UserErrorState(task.exception?.message))
                }
            }
        else
            Event(UserErrorState("Wrong email"))
    }

    fun getActualToken(): String =
        if (Instant.now().epochSecond + user.epochFbDiff < user.fbTokenExp) {
            user.fbToken
        } else {
            refreshToken()
            EmptyString.str
        }

    private fun refreshToken() {
        if (userState.value.peekContent() is UserLoggedInState) {
            auth.signInWithEmailAndPassword(user.email, user.password).addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    user.setUserEmail(auth.currentUser?.email ?: "no mail")
                    user.setUserPassword(user.password)
                    user.setUserIsLoggedIn(true)
                    auth.currentUser!!.getIdToken(true).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            user.updateToken(task2.result.token ?: EmptyString.str, task2.result.authTimestamp, task2.result.expirationTimestamp)
                        }
                    }
                } else {
                    _userState.value = Event(UserErrorState(task1.exception?.message))
                }
            }
        }
    }

    /**
     * @link (https://app.diagrams.net/#G1lU_2CKxfilfFdA1n6L66gEJCqNSH_wlY)
     * */
    fun loginUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty())
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                        Refresh locally user data
                        callFirebaseFunction(user, "getUserData").addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                auth.currentUser?.getIdToken(true)?.addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        val principle = task1.result
                                        user.storeUserData(principle)
                                        user.setUserEmail(auth.currentUser?.email ?: "no mail")
                                        user.setUserPassword(password)
                                        user.updateToken(task2.result.token ?: EmptyString.str, task2.result.authTimestamp, task2.result.expirationTimestamp)
                                        user.setUserIsEmailVerified(auth.currentUser?.isEmailVerified ?: false)
                                        user.setUserIsLoggedIn(true)
                                        if (user.isEmailVerified) {
                                            _userState.value = Event(UserLoggedInState(auth.currentUser?.email ?: "no mail"))
                                        } else {
                                            _userState.value = Event(UserNeedToVerifyEmailState())
                                        }
                                    } else {
                                        _userState.value = Event(UserErrorState(task2.exception?.message))
                                    }
                                }
                            } else {
                                _userState.value = Event(UserErrorState(task1.exception?.message?:"Cannot obtain user data"))
                            }
                        }
                    } else {
                        when (task.exception) {
                            is FirebaseNetworkException -> {
                                if (user.email == email && user.password == password) {
                                    user.setUserIsLoggedIn(true)
                                    if (user.isEmailVerified) {
                                        _userState.value = Event(UserLoggedInState(email))
                                    } else {
                                        _userState.value = Event(UserNeedToVerifyEmailState())
                                    }
                                } else if (user.email != email) {
                                    _userState.value = Event(UserErrorState("Wrong email"))
                                } else if (user.password != password) {
                                    _userState.value = Event(UserErrorState("Wrong password"))
                                }
                            }

                            is FirebaseAuthInvalidCredentialsException -> {
                                _userState.value = Event(UserErrorState(task.exception?.message?:"Wrong username or password"))
                            }

                            is FirebaseAuthInvalidUserException -> {
                                if (task.exception?.message?.contains("account has been disabled") == true) {
                                    _userState.value = Event(UserErrorState("Account has been disabled"))
                                } else if (task.exception?.message?.contains("user may have been deleted") == true) {
                                    user.clearUserData()
                                    _userState.value = Event(UserInitialState)
                                }
                            }

                            else -> {
                                user.clearUserData()
                                _userState.value = Event(UserInitialState)
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
                user.setUserIsLoggedIn(false)
                _userState.value = Event(UserLoggedOutState())
            }
        } else {
            user.setUserIsLoggedIn(false)
            _userState.value = Event(UserLoggedOutState())
        }
    }

    fun deleteAccount(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty())
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        auth.currentUser?.delete()?.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                this.clearUserData()
                                _userState.value = Event(UserInitialState)
                            } else {
                                _userState.value = Event(UserErrorState(task2.exception?.message ?: "Unknown error"))
                            }
                        }
                    } else {
                        if (task1.exception?.message?.contains("User has been disabled") == true) {
                            this.clearUserData()
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

    fun getUserData() = this.callFirebaseFunction(fbFunction = "getUserData")
        .addOnCompleteListener { result ->
            if (result.isSuccessful) {
                _userState.value = Event(UserLoggedInState(result.result.toString()))
            } else {
                val e = result.exception
                if (e is FirebaseFunctionsException) {
                    _userState.value = Event(UserErrorState("${e.code}, ${e.details}"))
                } else {
                    _userState.value = Event(UserErrorState(e.toString()))
                }
            }
        }

    fun updateUserCompleteData() = this.callFirebaseFunction(fbFunction = "updateUserData")
        .addOnCompleteListener { result ->
            if (result.isSuccessful) {
                _userState.value = Event(UserLoggedInState(result.result.toString()))
            } else {
                val e = result.exception
                if (e is FirebaseFunctionsException) {
                    _userState.value = Event(UserErrorState("${e.code}, ${e.details}"))
                } else {
                    _userState.value = Event(UserErrorState(e.toString()))
                }
            }
        }

    private fun callFirebaseFunction(user: Principle = this.user, fbFunction: String): Task<Principle> {
        return functions
            .getHttpsCallable(fbFunction)
            .call(user.dataToFirebase())
            .continueWith { task ->
                Principle(storage, task.result?.data as Map<String, Any>)
            }
    }
}

sealed class UserState
object UserInitialState : UserState()
data class UserRegisteredState(val msg: String) : UserState()
data class UserNeedToVerifyEmailState(val msg: String = "Check your email box and perform verification") : UserState()
data class UserNeedToVerifiedByOrganisationState(val msg: String) : UserState()
data class UserLoggedOutState(val msg: String = "Password was changed") : UserState()
data class UserLoggedInState(val msg: String) : UserState()
data class UserErrorState(val error: String?) : UserState()
