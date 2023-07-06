package com.simenko.qmapp.repository

import androidx.annotation.NonNull
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.HttpsCallableResult
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType


private const val USER_FULL_NAME = "user_full_name"
private const val USER_DEPARTMENT = "user_department"
private const val USER_SUB_DEPARTMENT = "user_sub_department"
private const val USER_JOB_ROLE = "user_job_role"
private const val USER_EMAIL = "user_email"
private const val PASSWORD_SUFFIX = "password_suffix"

private const val IS_EMAIL_VERIFIED = "is_email_verified"
private const val IS_USER_LOG_IN = "is_user_log_in"
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

    val userEmail: String
        get() = storage.getString(USER_EMAIL)

    val user: UserRaw
        get() = UserRaw(
            fullName = storage.getString(USER_FULL_NAME),
            department = storage.getString(USER_DEPARTMENT),
            subDepartment = storage.getString(USER_SUB_DEPARTMENT),
            jobRole = storage.getString(USER_JOB_ROLE),
            email = storage.getString(USER_EMAIL),
            password = storage.getString("${storage.getString(USER_EMAIL)}$PASSWORD_SUFFIX")
        )

    private fun storeUserData(user: UserRaw) {
        storage.setString(USER_FULL_NAME, user.fullName)
        storage.setString(USER_DEPARTMENT, user.department)
        storage.setString(USER_SUB_DEPARTMENT, user.subDepartment ?: "")
        storage.setString(USER_JOB_ROLE, user.jobRole)
        storage.setString(USER_EMAIL, user.email)
        storage.setString("${user.email}$PASSWORD_SUFFIX", user.password)
    }

    fun getUserPassword(userEmail: String) = storage.getString("$userEmail$PASSWORD_SUFFIX")

    suspend fun getActualUserState() = suspendCoroutine { continuation ->
        val registeredUserEmail = storage.getString(USER_EMAIL)
        val registeredPassword = storage.getString("$userEmail$PASSWORD_SUFFIX")
        val isVerifiedEmail = storage.getBoolean(IS_EMAIL_VERIFIED)
        val isUserLogIn = storage.getBoolean(IS_USER_LOG_IN)

        if (registeredUserEmail.isEmpty() || registeredPassword.isEmpty()) {
            _userState.value = Event(UserInitialState)
            continuation.resume(_userState.value.peekContent())
        } else {
            auth.signInWithEmailAndPassword(registeredUserEmail, registeredPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (auth.currentUser?.isEmailVerified == true) {
                            if (isUserLogIn) {
                                storage.setBoolean(IS_EMAIL_VERIFIED, true)
                                _userState.value = Event(UserLoggedInState("user is registered - verified with Firebase"))
                                continuation.resume(_userState.value.peekContent())
                            } else {
                                storage.setBoolean(IS_EMAIL_VERIFIED, true)
                                _userState.value = Event(UserLoggedOutState())
                                continuation.resume(_userState.value.peekContent())
                            }
                        } else {
                            _userState.value = Event(UserNeedToVerifyEmailState())
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
                                        _userState.value = Event(UserLoggedOutState())
                                        continuation.resume(_userState.value.peekContent())
                                    }
                                } else {
                                    _userState.value = Event(UserNeedToVerifyEmailState())
                                    continuation.resume(_userState.value.peekContent())
                                }
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

    fun setLocalEmptyUser() {
        val email = storage.getString(USER_EMAIL)
        storage.setString(USER_EMAIL, "")
        storage.setString("$email$PASSWORD_SUFFIX", "")
        storage.setBoolean(IS_EMAIL_VERIFIED, false)
        storage.setBoolean(IS_USER_LOG_IN, false)
        _userState.value = Event(UserRegisteredState("not yet registered on the phone"))
    }

    fun sendVerificationEmail(firebaseUser: FirebaseUser?) {
        firebaseUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _userState.value = Event(UserNeedToVerifyEmailState())
                updateUserData(this.user)
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
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _userState.value = Event(UserLoggedOutState("Check your email box and set new password"))
            } else {
                _userState.value = Event(UserErrorState(task.exception?.message))
            }
        }
    }

    fun loginUser(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty())
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storage.setString(USER_EMAIL, auth.currentUser?.email ?: "no mail")
                        storage.setString("$username$PASSWORD_SUFFIX", password)
                        storage.setBoolean(IS_USER_LOG_IN, true)
                        _userState.value = Event(UserLoggedInState(auth.currentUser?.email ?: "no mail"))
                    } else {
                        when (task.exception) {
                            is FirebaseNetworkException -> {
                                if (this.userEmail == username && storage.getString("$username$PASSWORD_SUFFIX") == password) {
                                    storage.setBoolean(IS_USER_LOG_IN, true)
                                    _userState.value = Event(UserLoggedInState(username))
                                } else if (this.userEmail != username) {
                                    _userState.value = Event(UserErrorState("Wrong email"))
                                } else if (storage.getString("$username$PASSWORD_SUFFIX") != password) {
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
                storage.setBoolean(IS_USER_LOG_IN, false)
                _userState.value = Event(UserLoggedOutState())
            }
        } else {
            storage.setBoolean(IS_USER_LOG_IN, false)
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
                                storage.setString(USER_EMAIL, "")
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
                            storage.setString(USER_EMAIL, "")
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
        updateUserData(user.copy(jobRole = userJobRole))
    }

    fun updateUserData(userRaw: UserRaw?) {
        val user: UserRaw = userRaw ?: this.user
        updateUserDataTask(user).addOnCompleteListener { task ->
            val e = task.exception
            if (e is FirebaseFunctionsException) {
                _userState.value = Event(UserErrorState("${e.code}, ${e.details}"))
            } else {
                _userState.value = Event(UserLoggedInState(task.result))
            }
        }
    }

    private fun updateUserDataTask(user: UserRaw): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "userDisplayName" to user.fullName,
            "userDepartment" to user.department,
            "userSubDepartment" to user.subDepartment,
            "userJobRole" to user.jobRole,
            "email" to user.email
        )

        return functions
            .getHttpsCallable("updateUserData")
            .call(data)
            .continueWith(object : Continuation<HttpsCallableResult, String> {
                override fun then(task: Task<HttpsCallableResult>): String {
                    val result: Map<String, Any> = task.getResult().getData() as Map<String, Any>
                    return result.get("result") as String
                }
            })
    }
}

data class UserRaw(
    val fullName: String,
    val department: String,
    val subDepartment: String?,
    val jobRole: String,
    val email: String,
    val password: String
)

sealed class UserState
object UserInitialState : UserState()

data class UserRegisteredState(val msg: String) : UserState()
data class UserNeedToVerifyEmailState(val msg: String = "Check your email box and perform verification") : UserState()
data class UserNeedToVerifiedByOrganisationState(val msg: String) : UserState()
data class UserLoggedOutState(val msg: String = "") : UserState()
data class UserLoggedInState(val msg: String) : UserState()
data class UserErrorState(val error: String?) : UserState()
