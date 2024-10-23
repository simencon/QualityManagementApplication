package com.simenko.qmapp.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.messaging.FirebaseMessaging
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.data.cache.prefs.storage.FcmToken
import com.simenko.qmapp.data.cache.prefs.model.Principal
import com.simenko.qmapp.data.cache.prefs.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class UserRepository @Inject constructor(
    private val storage: Storage,
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
    private val messaging: FirebaseMessaging
) {
    private val _userState: MutableStateFlow<UserState> = MutableStateFlow(NoState)
    val userState: StateFlow<UserState> get() = _userState
    var rawUser: Principal? = null
    private val _user: Principal get() = Principal(storage)
    val user: Principal get() = _user
    fun clearErrorMessage() {
        _userState.value = UserErrorState(UserError.NO_ERROR.error)
    }

    fun clearUserData() {
        _user.clearUserData()
        clearErrorMessage()
    }

    /**
     *@link (https://app.diagrams.net/#G1vvhdmr_4ATIBjb91JfzASgCwj16VsOkY)
     * */
    fun getActualUserState() {
        if (_user.email.isEmpty()) {
            _user.clearUserData()
            _userState.value = UnregisteredState
        } else if (_user.email.isNotEmpty() && _user.password.isEmpty()) {
            _userState.value = UserLoggedOutState()
        } else if (_user.email.isNotEmpty() && _user.password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(_user.email, _user.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (_user.isEmailVerified) {

                            if (_user.restApiUrl != EmptyString.str) {
                                if (_user.isUserLoggedIn) {
                                    _userState.value = UserLoggedInState("Logged in, email is verified")
                                } else {
                                    _userState.value = UserLoggedOutState()
                                }
                            } else {
                                callFirebaseFunction(_user, "getUserData").addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful) {
                                        val principle = task1.result
                                        if (principle.restApiUrl != EmptyString.str) {
                                            _user.setUserRestApiUrl(principle.restApiUrl)
                                            if (_user.isUserLoggedIn) {
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
                                var principle = this._user
                                principle.isEmailVerified = true
                                callFirebaseFunction(principle, "updateUserData").addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful) {
                                        _user.setUserIsEmailVerified(true)
                                        this.updateFcmToken(_user.email)
                                        callFirebaseFunction(_user, "getUserData").addOnCompleteListener { task2 ->
                                            if (task2.isSuccessful) {
                                                principle = task2.result
                                                if (principle.restApiUrl != EmptyString.str) {
                                                    _user.setUserRestApiUrl(principle.restApiUrl)
                                                    if (_user.isUserLoggedIn) {
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
                                if (_user.isEmailVerified) {
                                    if (_user.restApiUrl != EmptyString.str) {
                                        if (_user.isUserLoggedIn) {
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
                                    _user.clearUserData()
                                    _userState.value = UnregisteredState
                                }
                            }

                            else -> {
                                _user.clearUserData()
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
    fun registerUser(principal: Principal) {
        auth.createUserWithEmailAndPassword(principal.email, principal.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.storeUserData(principal)
                    sendVerificationEmail(auth.currentUser)
                } else {
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            _user.storeUserData(principal)
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
            ?: auth.signInWithEmailAndPassword(this._user.email, this._user.password).addOnCompleteListener { task ->
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
                    _user.setUserEmail(email)
                    _userState.value = UserLoggedOutState("Check your email box and set new password")
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            if (task.exception?.message?.contains("account has been disabled") == true) {
                                _userState.value = UserErrorState(UserError.ACCOUNT_DISABLED.error)
                            } else if (task.exception?.message?.contains("user may have been deleted") == true) {
                                _userState.value = UserErrorState(UserError.USER_NOT_REGISTERED.error)
                            }
                        }

                        else -> _userState.value = UserErrorState(task.exception?.message)
                    }
                }
            }
        else
            UserErrorState(UserError.WRONG_EMAIL.error)
    }

    val getRestApiUrl: String
        get() = _user.restApiUrl

    val authToken: String
        get() = _user.fbToken

    //    ToDoMe - not yet in CleanArchitecture
    suspend fun getActualFbToken() = suspendCoroutine { continuation ->
        if (Instant.now().epochSecond + _user.epochFbDiff < _user.fbTokenExp) {
            continuation.resume(_user.fbToken)
        } else {
            auth.signInWithEmailAndPassword(_user.email, _user.password).addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    auth.currentUser!!.getIdToken(true).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            _user.updateToken(task2.result.token ?: EmptyString.str, task2.result.authTimestamp, task2.result.expirationTimestamp)
                            continuation.resume(_user.fbToken)
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
                        auth.currentUser?.email?.let { this.updateFcmToken(it) }
                        callFirebaseFunction(_user, "getUserData").addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                val principle = task1.result
                                _user.storeUserData(principle)
                                _user.setUserEmail(auth.currentUser?.email ?: "no mail")
                                _user.setUserPassword(password)
                                _user.setUserIsEmailVerified(auth.currentUser?.isEmailVerified ?: false)
                                _user.setUserIsLoggedIn(true)
                                if (_user.isEmailVerified) {
                                    if (_user.restApiUrl != EmptyString.str) {
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
                        println("loginUser error is ${task.exception?.message}")
                        when (task.exception) {
                            is FirebaseNetworkException -> {
                                if (_user.email == email && _user.password == password) {
                                    _user.setUserIsLoggedIn(true)
                                    if (_user.isEmailVerified) {
                                        if (_user.restApiUrl != EmptyString.str) {
                                            _userState.value = UserLoggedInState(auth.currentUser?.email ?: "no mail")
                                        } else {
                                            _userState.value = UserAuthoritiesNotVerifiedState()
                                        }
                                    } else {
                                        _userState.value = UserNeedToVerifyEmailState()
                                    }
                                } else if (_user.email != email) {
                                    _userState.value = UserErrorState(UserError.WRONG_EMAIL.error)
                                } else if (_user.password != password) {
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
                                    _userState.value = UserErrorState(UserError.USER_NOT_REGISTERED.error)
                                }
                            }
                            //  ToDoMe - make sure this error is handled "An internal error has occurred. [ Requests from this Android client application com.simenko.qmapp are blocked. ]" - it means App is not allowed to FireBase project.
                            is FirebaseException -> {
                                if (task.exception?.message?.contains("application com.simenko.qmapp are blocked") == true) {
                                    _userState.value = UserErrorState(UserError.APPLICATION_NOT_REGISTERED.error)
                                } else {
                                    _userState.value = UserErrorState(UserError.UNKNOWN_ERROR.error)
                                }
                            }

                            else -> {
                                _user.clearUserData()
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
                _user.setUserIsLoggedIn(false)
                _userState.value = UserLoggedOutState()
            }
        } else {
            _user.setUserIsLoggedIn(false)
            _userState.value = UserLoggedOutState()
        }
    }

    fun deleteAccount(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty())
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task1 ->
                    this.updateFcmToken(EmptyString.str)
                    if (task1.isSuccessful) {
                        auth.currentUser?.delete()?.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                _user.clearUserData()
                                _userState.value = UnregisteredState
                            } else {
                                _userState.value = UserErrorState(task2.exception?.message ?: UserError.UNKNOWN_ERROR.error)
                            }
                        }
                    } else {
                        if (task1.exception?.message?.contains("User has been disabled") == true) {
                            _user.clearUserData()
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

    //    ToDoMe - not yet in CleanArchitecture
    fun updateUserData() = this.callFirebaseFunction(fbFunction = "getUserData")
        .addOnCompleteListener { result ->
            if (result.isSuccessful) {
                _user.storeUserData(result.result)
                _userState.value = UserLoggedInState()
            } else {
                val e = result.exception
                if (e is FirebaseFunctionsException) {
                    _userState.value = UserErrorState("${e.code}, ${e.details}")
                } else {
                    _userState.value = UserErrorState(e.toString())
                }
            }
        }

    fun editUserData(principal: Principal) = this.callFirebaseFunction(principal, "updateUserData")
        .addOnCompleteListener { result ->
            if (result.isSuccessful) {
                _user.storeUserData(result.result)
                _userState.value = UserLoggedInState()
            } else {
                val e = result.exception
                if (e is FirebaseFunctionsException) {
                    _userState.value = UserErrorState("${e.code}, ${e.details}")
                } else {
                    _userState.value = UserErrorState(e.toString())
                }
            }
        }

    private fun callFirebaseFunction(user: Principal = this._user, fbFunction: String): Task<Principal> {
        return functions
            .getHttpsCallable(fbFunction)
            .call(user.dataToFirebase())
            .continueWith { task ->
                Principal(storage, task.result?.data as Map<String, Any>)
            }
    }

    private val _deviceFcmToken: FcmToken get() = FcmToken(storage)

    /**
     * @link (https://app.diagrams.net/#G1BMga3T4D0UNVDUc4v1pVe5eZw5lomqJj)
     * */
    private fun updateFcmToken(userEmail: String) {
        if (userEmail.isNotEmpty() && _deviceFcmToken.fcmEmail != userEmail && _deviceFcmToken.fcmEmail.isNotEmpty()) {
            this.callFirebaseFunction(_deviceFcmToken, "deleteFcmToken").addOnCompleteListener { deleteTask ->
                if (deleteTask.isSuccessful) {
                    println("updateFcmToken - changed email deleted ${deleteTask.result}")
                    messaging.token.addOnCompleteListener { result ->
                        _deviceFcmToken.setValues(Instant.now().epochSecond, userEmail, result.result)
                        this.callFirebaseFunction(_deviceFcmToken, "createFcmToken").addOnCompleteListener { createTask ->
                            if (createTask.isSuccessful) {
                                println("updateFcmToken - changed email created ${createTask.result}")
                            } else {
                                println("updateFcmToken - changed email created/exception ${createTask.exception}")
                            }
                        }
                    }
                } else {
                    println("updateFcmToken - changed email deleted ${deleteTask.exception}")
                }
            }
        } else if (userEmail.isEmpty()) {
            this.callFirebaseFunction(_deviceFcmToken, "deleteFcmToken").addOnCompleteListener { deleteTask ->
                if (deleteTask.isSuccessful) {
                    println("updateFcmToken - email is empty deleted ${deleteTask.result}")
                    _deviceFcmToken.setValues(NoRecord.num.toLong(), EmptyString.str, EmptyString.str)
                } else {
                    println("updateFcmToken - email is empty deleted/exception ${deleteTask.exception}")
                }
            }
        } else {
            messaging.token.addOnCompleteListener { currentTokenTask ->
                if (currentTokenTask.isSuccessful) {
                    if (_deviceFcmToken.fcmToken.isNotEmpty() && _deviceFcmToken.fcmToken != currentTokenTask.result) {
                        this.callFirebaseFunction(_deviceFcmToken, "deleteFcmToken").addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                println("updateFcmToken - changed token deleted ${deleteTask.result}")
                                _deviceFcmToken.setValues(Instant.now().epochSecond, userEmail, currentTokenTask.result)
                                this.callFirebaseFunction(_deviceFcmToken, "createFcmToken").addOnCompleteListener { createTask ->
                                    if (createTask.isSuccessful) {
                                        println("updateFcmToken - changed token created ${createTask.result}")
                                    } else {
                                        println("updateFcmToken - changed token created/exception ${createTask.exception}")
                                    }
                                }
                            } else {
                                println("updateFcmToken - changed token deleted/exception ${deleteTask.exception}")
                            }
                        }
                    } else if (_deviceFcmToken.fcmToken.isEmpty()) {
                        _deviceFcmToken.setValues(Instant.now().epochSecond, userEmail, currentTokenTask.result)
                        this.callFirebaseFunction(_deviceFcmToken, "createFcmToken").addOnCompleteListener { createTask ->
                            if (createTask.isSuccessful) {
                                println("updateFcmToken - new token created ${createTask.result}")
                            } else {
                                println("updateFcmToken - new token created/exception ${createTask.exception}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun callFirebaseFunction(fcmToken: FcmToken, fbFunction: String): Task<FcmToken> {
        return functions
            .getHttpsCallable(fbFunction)
            .call(fcmToken.dataToFirebase())
            .continueWith { task ->
                FcmToken(storage, task.result?.data as Map<String, Any>)
            }
    }
}

sealed class UserState
data object NoState : UserState()
data object UnregisteredState : UserState()
data class UserNeedToVerifyEmailState(val msg: String = "Check your email box and perform verification (${DateTimeFormatter.ISO_INSTANT.format(Instant.now())})") :
    UserState()

data class UserAuthoritiesNotVerifiedState(
    val msg: String = "You are not yet verified by your organization (${
        DateTimeFormatter.ISO_INSTANT.format(
            Instant.now()
        )
    })"
) : UserState()

data class UserLoggedOutState(val msg: String = "") : UserState()
data class UserLoggedInState(val msg: String = "State on (${DateTimeFormatter.ISO_INSTANT.format(Instant.now())})") : UserState()
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
    USER_EXISTS("User already registered"),
    USER_NOT_REGISTERED("User with current email is not registered"),
    APPLICATION_NOT_REGISTERED("Application is not registered")
}
