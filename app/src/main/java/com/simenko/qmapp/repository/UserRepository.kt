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
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class UserRepository @Inject constructor(
    private val storage: Storage,
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) {
    private val _userState: MutableStateFlow<UserState> = MutableStateFlow(NoState)
    val userState: StateFlow<UserState> get() = _userState
    val user: Principle get() = Principle(storage)

    fun clearErrorMessage() {
        _userState.value = UserErrorState(UserError.NO_ERROR.error)
    }

    fun clearUserData() {
        user.clearUserData()
        clearErrorMessage()
    }

    /**
     *@link (https://app.diagrams.net/#G1vvhdmr_4ATIBjb91JfzASgCwj16VsOkY)
     * */
    fun getActualUserState() {
        if (user.email.isEmpty()) {
            user.clearUserData()
            _userState.value = UnregisteredState
        } else if (user.email.isNotEmpty() && user.password.isEmpty()) {
            _userState.value = UserLoggedOutState()
        } else if (user.email.isNotEmpty() && user.password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (user.isEmailVerified) {

                            if (user.restApiUrl != EmptyString.str) {
                                if (user.isUserLoggedIn) {
                                    _userState.value = UserLoggedInState("Logged in, email is verified")
                                } else {
                                    _userState.value = UserLoggedOutState()
                                }
                            } else {
                                callFirebaseFunction(user, "getUserData").addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful) {
                                        val principle = task1.result
                                        if (principle.restApiUrl != EmptyString.str) {
                                            user.setUserRestApiUrl(principle.restApiUrl)
                                            if (user.isUserLoggedIn) {
                                                _userState.value = UserLoggedInState("Logged in, email is verified")
                                            } else {
                                                _userState.value = UserLoggedOutState()
                                            }
                                        } else {
                                            _userState.value = UserAuthoritiesNotVerifiedState()
                                        }
                                    } else {
                                        _userState.value = UserAuthoritiesNotVerifiedState()
                                    }
                                }
                            }

                        } else {
                            if (auth.currentUser?.isEmailVerified == true) {
                                var principle = this.user
                                principle.isEmailVerified = true
                                callFirebaseFunction(principle, "updateUserData").addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful) {
                                        user.setUserIsEmailVerified(true)
                                        callFirebaseFunction(user, "getUserData").addOnCompleteListener { task2 ->
                                            if (task2.isSuccessful) {
                                                principle = task2.result
                                                if (principle.restApiUrl != EmptyString.str) {
                                                    user.setUserRestApiUrl(principle.restApiUrl)
                                                    if (user.isUserLoggedIn) {
                                                        _userState.value = UserLoggedInState("Logged in, email is verified")
                                                    } else {
                                                        _userState.value = UserLoggedOutState()
                                                    }
                                                } else {
                                                    _userState.value = UserAuthoritiesNotVerifiedState()
                                                }
                                            } else {
                                                _userState.value = UserAuthoritiesNotVerifiedState()
                                            }
                                        }
                                    } else {
                                        _userState.value = UserNeedToVerifyEmailState()
                                    }
                                }
                            } else {
                                _userState.value = UserNeedToVerifyEmailState()
                            }
                        }
                    } else {
                        when (task.exception) {
                            is FirebaseNetworkException -> {
                                if (user.isEmailVerified) {
                                    if (user.restApiUrl != EmptyString.str) {
                                        if (user.isUserLoggedIn) {
                                            _userState.value = UserLoggedInState("Logged in, email is verified")
                                        } else {
                                            _userState.value = UserLoggedOutState()
                                        }
                                    } else {
                                        _userState.value = UserAuthoritiesNotVerifiedState()
                                    }
                                } else {
                                    _userState.value = UserNeedToVerifyEmailState()
                                }
                            }

                            is FirebaseAuthInvalidCredentialsException -> {
                                _userState.value = UserLoggedOutState("Password was changed")
                            }

                            is FirebaseAuthInvalidUserException -> {
                                if (task.exception?.message?.contains("account has been disabled") == true) {
                                    _userState.value = UserLoggedOutState("Account has been disabled")
                                } else if (task.exception?.message?.contains("user may have been deleted") == true) {
                                    user.clearUserData()
                                    _userState.value = UnregisteredState
                                }
                            }

                            else -> {
                                user.clearUserData()
                                _userState.value = UnregisteredState
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
                            _userState.value = UserErrorState(UserError.USER_EXISTS.error)
                        }

                        else -> {
                            _userState.value = UserErrorState(task.exception?.message)
                        }
                    }
                }
            }
    }

    fun sendVerificationEmail(firebaseUser: FirebaseUser?) {
        firebaseUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _userState.value = UserNeedToVerifyEmailState()
            } else {
                _userState.value = UserErrorState(task.exception?.message)
            }
        }
            ?: auth.signInWithEmailAndPassword(this.user.email, this.user.password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendVerificationEmail(auth.currentUser)
                } else {
                    _userState.value = UserErrorState(task.exception?.message)
                }
            }
    }

    fun sendResetPasswordEmail(email: String) {
        if (email.isNotEmpty())
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.setUserEmail(email)
                    _userState.value = UserLoggedOutState("Check your email box and set new password")
                } else {
                    _userState.value = UserErrorState(task.exception?.message)
                }
            }
        else
            UserErrorState(UserError.WRONG_EMAIL.error)
    }

    val getRestApiUrl: String
        get() = user.restApiUrl

    val authToken: String
        get() = user.fbToken

    suspend fun refreshTokenIfNecessary() = suspendCoroutine { continuation ->
        if (Instant.now().epochSecond + user.epochFbDiff < user.fbTokenExp) {
            continuation.resume(user.fbToken)
        } else {
            auth.signInWithEmailAndPassword(user.email, user.password).addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    auth.currentUser!!.getIdToken(true).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            user.updateToken(task2.result.token ?: EmptyString.str, task2.result.authTimestamp, task2.result.expirationTimestamp)
                            continuation.resume(user.fbToken)
                        } else {
                            throw IOException("Not possible to obtain token")
                        }
                    }
                } else {
                    throw IOException("Not possible to obtain token")
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
                                val principle = task1.result
                                user.storeUserData(principle)
                                user.setUserEmail(auth.currentUser?.email ?: "no mail")
                                user.setUserPassword(password)
                                user.setUserIsEmailVerified(auth.currentUser?.isEmailVerified ?: false)
                                user.setUserIsLoggedIn(true)
                                if (user.isEmailVerified) {
                                    if (user.restApiUrl != EmptyString.str) {
                                        _userState.value = UserLoggedInState(auth.currentUser?.email ?: "no mail")
                                    } else {
                                        _userState.value = UserAuthoritiesNotVerifiedState()
                                    }
                                } else {
                                    _userState.value = UserNeedToVerifyEmailState()
                                }
                            } else {
                                _userState.value = UserErrorState(task1.exception?.message ?: UserError.NO_USER_DATA.error)
                            }
                        }
                    } else {
                        when (task.exception) {
                            is FirebaseNetworkException -> {
                                if (user.email == email && user.password == password) {
                                    user.setUserIsLoggedIn(true)
                                    if (user.isEmailVerified) {
                                        if (user.restApiUrl != EmptyString.str) {
                                            _userState.value = UserLoggedInState(auth.currentUser?.email ?: "no mail")
                                        } else {
                                            _userState.value = UserAuthoritiesNotVerifiedState()
                                        }
                                    } else {
                                        _userState.value = UserNeedToVerifyEmailState()
                                    }
                                } else if (user.email != email) {
                                    _userState.value = UserErrorState(UserError.WRONG_EMAIL.error)
                                } else if (user.password != password) {
                                    _userState.value = UserErrorState(UserError.WRONG_PASSWORD.error)
                                }
                            }

                            is FirebaseAuthInvalidCredentialsException -> {
                                _userState.value = UserErrorState(task.exception?.message ?: UserError.WRONG_CREDENTIALS.error)
                            }

                            is FirebaseAuthInvalidUserException -> {
                                if (task.exception?.message?.contains("account has been disabled") == true) {
                                    _userState.value = UserErrorState(UserError.ACCOUNT_DISABLED.error)
                                } else if (task.exception?.message?.contains("user may have been deleted") == true) {
                                    user.clearUserData()
                                    _userState.value = UnregisteredState
                                }
                            }

                            else -> {
                                user.clearUserData()
                                _userState.value = UnregisteredState
                            }
                        }
                    }
                }
        else {
            _userState.value = UserErrorState(UserError.EMPTY_CREDENTIALS.error)
        }
    }

    fun logout() {
        if (auth.currentUser != null) {
            auth.signOut()
            if (auth.currentUser == null) {
                user.setUserIsLoggedIn(false)
                _userState.value = UserLoggedOutState()
            }
        } else {
            user.setUserIsLoggedIn(false)
            _userState.value = UserLoggedOutState()
        }
    }

    fun deleteAccount(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty())
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        auth.currentUser?.delete()?.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                user.clearUserData()
                                _userState.value = UnregisteredState
                            } else {
                                _userState.value = UserErrorState(task2.exception?.message ?: UserError.UNKNOWN_ERROR.error)
                            }
                        }
                    } else {
                        if (task1.exception?.message?.contains("User has been disabled") == true) {
                            user.clearUserData()
                            _userState.value = UnregisteredState
                        } else {
                            _userState.value = UserErrorState(task1.exception?.message ?: UserError.UNKNOWN_ERROR.error)
                        }
                    }
                }
        else {
            _userState.value = UserErrorState(UserError.EMPTY_CREDENTIALS.error)
        }
    }

    fun getUserData() = this.callFirebaseFunction(fbFunction = "getUserData")
        .addOnCompleteListener { result ->
            if (result.isSuccessful) {
                _userState.value = UserLoggedInState(result.result.toString())
            } else {
                val e = result.exception
                if (e is FirebaseFunctionsException) {
                    _userState.value = UserErrorState("${e.code}, ${e.details}")
                } else {
                    _userState.value = UserErrorState(e.toString())
                }
            }
        }

    fun updateUserCompleteData() = this.callFirebaseFunction(fbFunction = "updateUserData")
        .addOnCompleteListener { result ->
            if (result.isSuccessful) {
                _userState.value = UserLoggedInState(result.result.toString())
            } else {
                val e = result.exception
                if (e is FirebaseFunctionsException) {
                    _userState.value = UserErrorState("${e.code}, ${e.details}")
                } else {
                    _userState.value = UserErrorState(e.toString())
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
object NoState : UserState()
object UnregisteredState : UserState()
data class UserNeedToVerifyEmailState(val msg: String = "Check your email box and perform verification") : UserState()
data class UserAuthoritiesNotVerifiedState(val msg: String = "You are not yet verified by your organization") : UserState()
data class UserLoggedOutState(val msg: String = "") : UserState()
data class UserLoggedInState(val msg: String) : UserState()
data class UserErrorState(val error: String?) : UserState()

enum class UserError(val error: String) {
    NO_ERROR(""),
    UNKNOWN_ERROR("Unknown error"),
    WRONG_EMAIL("Wrong email"),
    WRONG_PASSWORD("Wrong password"),
    WRONG_CREDENTIALS("Wrong username or password"),
    EMPTY_CREDENTIALS("Email and Password fields should not be empty"),
    NO_USER_DATA("Cannot obtain user data"),
    ACCOUNT_DISABLED("Account has been disabled"),
    USER_EXISTS("user already registered")
}
