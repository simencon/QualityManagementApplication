package com.simenko.qmapp.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.utils.StringUtils.getStringDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


private const val USER_FULL_NAME = "user_full_name"
private const val USER_DEPARTMENT = "user_department"
private const val USER_SUB_DEPARTMENT = "user_sub_department"
private const val USER_JOB_ROLE = "user_job_role"
private const val USER_EMAIL = "user_email"
private const val PASSWORD_SUFFIX = "password_suffix"
private const val IS_USER_LOG_IN = "is_user_log_in"
private const val FB_TOKEN = "fb_token"
private const val EPOCH_FB_DIFF = "epoch_fb_diff"
private const val FB_TOKEN_EXP = "fb_token_exp"

private const val IS_EMAIL_VERIFIED = "is_email_verified"
private const val IS_VERIFIED_BY_ORGANISATION = "is_verified_by_organisation"

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

    val user: UserRaw
        get() = UserRaw(
            fullName = storage.getString(USER_FULL_NAME),
            department = storage.getString(USER_DEPARTMENT),
            subDepartment = storage.getString(USER_SUB_DEPARTMENT),
            jobRole = storage.getString(USER_JOB_ROLE),
            email = storage.getString(USER_EMAIL),
            password = storage.getString("${storage.getString(USER_EMAIL)}$PASSWORD_SUFFIX"),
            isEmailVerified = storage.getBoolean(IS_EMAIL_VERIFIED),
            isUserLoggedIn = storage.getBoolean(IS_USER_LOG_IN),
            fbToken = storage.getString(FB_TOKEN),
            epochFbDiff = storage.getLong(EPOCH_FB_DIFF),
            fbTokenExp = storage.getLong(FB_TOKEN_EXP),
            storage
        )

    private fun storeUserData(user: UserRaw) {
        storage.setString(USER_FULL_NAME, user.fullName)
        storage.setString(USER_DEPARTMENT, user.department)
        storage.setString(USER_SUB_DEPARTMENT, user.subDepartment ?: EmptyString.str)
        storage.setString(USER_JOB_ROLE, user.jobRole)
        storage.setString(USER_EMAIL, user.email)
        storage.setBoolean(IS_EMAIL_VERIFIED, user.isEmailVerified)
        storage.setBoolean(IS_USER_LOG_IN, user.isUserLoggedIn)
        storage.setString("${user.email}$PASSWORD_SUFFIX", user.password)
        storage.setString(FB_TOKEN, user.fbToken)
        storage.setLong(EPOCH_FB_DIFF, user.epochFbDiff)
        storage.setLong(FB_TOKEN_EXP, user.fbTokenExp)
    }

    fun clearUserData() {
        storage.setString(USER_FULL_NAME, EmptyString.str)
        storage.setString(USER_DEPARTMENT, EmptyString.str)
        storage.setString(USER_SUB_DEPARTMENT, EmptyString.str)
        storage.setString(USER_JOB_ROLE, EmptyString.str)
        val email = storage.getString(USER_EMAIL)
        storage.setString(USER_EMAIL, EmptyString.str)
        storage.setString("$email$PASSWORD_SUFFIX", EmptyString.str)
        storage.setBoolean(IS_EMAIL_VERIFIED, false)
        storage.setBoolean(IS_USER_LOG_IN, false)
        storage.setString(FB_TOKEN, EmptyString.str)
        storage.setLong(EPOCH_FB_DIFF, NoRecord.num.toLong())
        storage.setLong(FB_TOKEN_EXP, NoRecord.num.toLong())
        _userState.value = Event(UserRegisteredState("not yet registered on the phone"))
    }

    suspend fun getActualUserState() = suspendCoroutine { continuation ->

        if (user.email.isEmpty() && user.password.isEmpty()) {
            _userState.value = Event(UserInitialState)
            continuation.resume(_userState.value.peekContent())
        } else if (user.email.isNotEmpty() && user.password.isEmpty()) {
            _userState.value = Event(UserLoggedOutState())
            continuation.resume(_userState.value.peekContent())
        } else if (user.email.isNotEmpty() && user.password.isNotEmpty()) {
            if (user.isUserLoggedIn) {
                _userState.value = Event(UserLoggedInState("Logged in, verified on device"))
                continuation.resume(_userState.value.peekContent())
            } else if (!user.isUserLoggedIn && user.isEmailVerified) {
                user.setUserIsUserLoggedIn(true)
                _userState.value = Event(UserLoggedInState("Logged in, verified on device"))
                continuation.resume(_userState.value.peekContent())
            } else if (!user.isUserLoggedIn && !user.isEmailVerified) {
                auth.signInWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (auth.currentUser?.isEmailVerified == true) {
                                user.setUserIsEmailVerified(true)
                                user.setUserIsUserLoggedIn(true)
                                _userState.value = Event(UserLoggedInState("user is registered - verified with Firebase"))
                                continuation.resume(_userState.value.peekContent())
                            } else {
                                _userState.value = Event(UserNeedToVerifyEmailState())
                                continuation.resume(_userState.value.peekContent())
                            }
                        } else {
                            when (task.exception) {
                                is FirebaseNetworkException -> {
                                    _userState.value = Event(UserLoggedOutState("No connection - check your network"))
                                    continuation.resume(_userState.value.peekContent())
                                }

                                is FirebaseAuthInvalidCredentialsException -> {
                                    _userState.value = Event(UserLoggedOutState("Password was reset or reset process not yet finished"))
                                    continuation.resume(_userState.value.peekContent())
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
    }

    fun registerUser(userRaw: UserRaw) {
        auth.createUserWithEmailAndPassword(userRaw.email, userRaw.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeUserData(userRaw)
                    sendVerificationEmail(auth.currentUser)
                } else {
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            storeUserData(userRaw)
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
                    user.setUserFullName(email)
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
            NoString.str
        }

    private fun refreshToken() {
        if (userState.value.peekContent() is UserLoggedInState) {
            auth.signInWithEmailAndPassword(user.email, user.password).addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    user.setUserFullName(auth.currentUser?.email ?: "no mail")
                    user.setUserPassword(user.password)
                    user.setUserIsUserLoggedIn(true)
                    auth.currentUser!!.getIdToken(true).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            val user = this.user
                            user.fbToken = task2.result.token ?: NoString.str
                            user.epochFbDiff = Instant.now().epochSecond - task2.result.authTimestamp
                            user.fbTokenExp = task2.result.expirationTimestamp
                            this.storeUserData(user)
                        }
                    }
                } else {
                    _userState.value = Event(UserErrorState(task1.exception?.message))
                }
            }
        }
    }

    fun loginUser(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty())
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.setUserFullName(auth.currentUser?.email ?: "no mail")
                        user.setUserPassword(password)
                        user.setUserIsUserLoggedIn(true)
                        auth.currentUser?.getIdToken(true)?.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                val user = this.user
                                user.fbToken = task2.result.token ?: ""
                                user.epochFbDiff = Instant.now().epochSecond - task2.result.authTimestamp
                                user.fbTokenExp = task2.result.expirationTimestamp
                                this.storeUserData(user)
                                Log.d(TAG, "loginUser, token: ${this.user.fbToken}")
                                Log.d(TAG, "loginUser, epochDiff: ${this.user.epochFbDiff}")
                                Log.d(TAG, "loginUser, tokenExp: ${this.user.fbTokenExp}")
                            }
                        }
                        _userState.value = Event(UserLoggedInState(auth.currentUser?.email ?: "no mail"))
                    } else {
                        when (task.exception) {
                            is FirebaseNetworkException -> {
//                                ToDo what if email is not verified?
                                if (user.email == username && user.password == password) {
                                    user.setUserIsUserLoggedIn(true)
                                    _userState.value = Event(UserLoggedInState(username))
                                } else if (user.email != username) {
                                    _userState.value = Event(UserErrorState("Wrong email"))
                                } else if (user.password != password) {
                                    _userState.value = Event(UserErrorState("Wrong password"))
                                }
                            }

                            is FirebaseAuthInvalidCredentialsException -> {
                                _userState.value = Event(UserErrorState(task.exception?.message))
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
                user.setUserIsUserLoggedIn(false)
                _userState.value = Event(UserLoggedOutState())
            }
        } else {
            user.setUserIsUserLoggedIn(false)
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
                                clearUserData()
                                _userState.value = Event(UserInitialState)
                            } else {
                                _userState.value = Event(UserErrorState(task2.exception?.message ?: "Unknown error"))
                            }
                        }
                    } else {
                        if (task1.exception?.message?.contains("User has been disabled") == true) {
                            clearUserData()
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

    fun updateUserData(userRaw: UserRaw?) {
        val user: UserRaw = userRaw ?: this.user
        updateUserDataTask(user, "updateUserData").addOnCompleteListener { task ->
            val e = task.exception
            if (e is FirebaseFunctionsException) {
                _userState.value = Event(UserErrorState("${e.code}, ${e.details}"))
            } else {
                _userState.value = Event(UserLoggedInState(task.result))
            }
        }
    }

    private fun updateUserDataTask(user: UserRaw, fbFunction: String): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "email" to user.email,
            "fullName" to user.fullName,
            "department" to user.department,
            "subDepartment" to user.subDepartment,
            "jobRole" to user.jobRole
        )

        return functions
            .getHttpsCallable(fbFunction)
            .call(data)
            .continueWith { task ->
                val result: Map<String, Any> = task.result?.data as Map<String, Any>
                buildString {
                    append("result: ")
                    append((result["result"] ?: "has no result") as String)
                    append("\n")
                    append("user email: ")
                    append((result["email"] ?: "has no email") as String)
                    append("\n")
                    append("user full name: ")
                    append((result["fullName"] ?: "has no full name") as String)
                    append("\n")
                    append("user department: ")
                    append((result["department"] ?: "has no department") as String)
                    append("\n")
                    append("user sub department: ")
                    append((result["subDepartment"] ?: "has no sub department") as String)
                    append("\n")
                    append("user job role: ")
                    append((result["jobRole"] ?: "has no job role") as String)
                }
            }
    }

    fun getUserData(): Task<UserRaw> {
        val repository = this
        return functions
            .getHttpsCallable("getUserData")
            .call(hashMapOf("email" to user.email))
            .continueWith { task ->
                runBlocking {
                    repository.getActualToken()
                    val result: Map<String, Any> = task.result?.data as Map<String, Any>
                    UserRaw(
                        fullName = (result["fullName"] ?: "has no full name") as String,
                        department = (result["department"] ?: "has no department") as String,
                        subDepartment = (result["subDepartment"] ?: "has no sub department") as String,
                        jobRole = (result["jobRole"] ?: "has no job role") as String,
                        email = (result["email"] ?: "has no email") as String,
                        password = user.password,
                        isEmailVerified = true,
                        isUserLoggedIn = true,
                        fbToken = "JWT provided by Firebase,\n" +
                                "diff: ${user.epochFbDiff} seconds\n" +
                                "expiration: ${getStringDate(user.fbTokenExp * 1000) ?: NoString.str}",
                        epochFbDiff = user.epochFbDiff,
                        fbTokenExp = user.fbTokenExp
                    )
                }
            }.addOnCompleteListener { result ->
                val e = result.exception
                if (e is FirebaseFunctionsException) {
                    _userState.value = Event(UserErrorState("${e.code}, ${e.details}"))
                } else {
                    _userState.value = Event(UserLoggedInState(result.result.toString()))
                }
            }
    }
}

data class UserRaw(
    var fullName: String,
    var department: String,
    var subDepartment: String?,
    var jobRole: String,
    var email: String,
    var password: String,
    var isEmailVerified: Boolean,
    var isUserLoggedIn: Boolean,
    var fbToken: String,
    var epochFbDiff: Long,
    var fbTokenExp: Long,
    val userStorage: Storage? = null
) {
    fun setUserFullName(username: String) {
        userStorage?.setString(USER_EMAIL, username)
    }

    fun setUserPassword(password: String) {
        userStorage?.setString("$fullName$PASSWORD_SUFFIX", password)
    }

    fun setUserIsEmailVerified(isEmailVerified: Boolean) {
        userStorage?.setBoolean(IS_EMAIL_VERIFIED, isEmailVerified)
    }

    fun setUserIsUserLoggedIn(isUserLoggedIn: Boolean) {
        userStorage?.setBoolean(IS_USER_LOG_IN, isUserLoggedIn)
    }
}

sealed class UserState
object UserInitialState : UserState()

data class UserRegisteredState(val msg: String) : UserState()
data class UserNeedToVerifyEmailState(val msg: String = "Check your email box and perform verification") : UserState()
data class UserNeedToVerifiedByOrganisationState(val msg: String) : UserState()
data class UserLoggedOutState(val msg: String = "") : UserState()
data class UserLoggedInState(val msg: String) : UserState()
data class UserErrorState(val error: String?) : UserState()
