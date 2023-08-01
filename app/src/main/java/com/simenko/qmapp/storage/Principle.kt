package com.simenko.qmapp.storage

import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import java.time.Instant

data class Principle(
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
    companion object {
        private const val USER_FULL_NAME = "user_full_name"
        private const val USER_DEPARTMENT = "user_department"
        private const val USER_SUB_DEPARTMENT = "user_sub_department"
        private const val USER_JOB_ROLE = "user_job_role"
        private const val USER_EMAIL = "user_email"
        private const val PASSWORD_SUFFIX = "password_suffix"
        private const val IS_USER_LOG_IN = "is_user_log_in"
        private const val IS_EMAIL_VERIFIED = "is_email_verified"
        private const val FB_TOKEN = "fb_token"
        private const val EPOCH_FB_DIFF = "epoch_fb_diff"
        private const val FB_TOKEN_EXP = "fb_token_exp"
    }

    constructor(
        userStorage: Storage
    ) : this(
        fullName = userStorage.getString(USER_FULL_NAME),
        department = userStorage.getString(USER_DEPARTMENT),
        subDepartment = userStorage.getString(USER_SUB_DEPARTMENT),
        jobRole = userStorage.getString(USER_JOB_ROLE),
        email = userStorage.getString(USER_EMAIL),
        password = userStorage.getString("${userStorage.getString(USER_EMAIL)}$PASSWORD_SUFFIX"),
        isEmailVerified = userStorage.getBoolean(IS_EMAIL_VERIFIED),
        isUserLoggedIn = userStorage.getBoolean(IS_USER_LOG_IN),
        fbToken = userStorage.getString(FB_TOKEN),
        epochFbDiff = userStorage.getLong(EPOCH_FB_DIFF),
        fbTokenExp = userStorage.getLong(FB_TOKEN_EXP),
        userStorage
    )

    fun setUserEmail(username: String) {
        userStorage?.setString(USER_EMAIL, username)
    }

    fun setUserPassword(password: String) {
        userStorage?.setString("$fullName$PASSWORD_SUFFIX", password)
    }

    fun setUserIsUserLoggedIn(isUserLoggedIn: Boolean) {
        userStorage?.setBoolean(IS_USER_LOG_IN, isUserLoggedIn)
    }

    fun setUserIsEmailVerified(isEmailVerified: Boolean) {
        userStorage?.setBoolean(IS_EMAIL_VERIFIED, isEmailVerified)
    }

    fun updateToken(fbToken: String, epochFbTimeStampSec: Long, epochFbTokenSec: Long) {
        userStorage?.let {
            it.setString(FB_TOKEN, fbToken)
            it.setLong(EPOCH_FB_DIFF, Instant.now().epochSecond - epochFbTimeStampSec)
            it.setLong(FB_TOKEN_EXP, epochFbTokenSec)
        }
    }

    fun storeUserData(user: Principle) {
        userStorage?.let {
            it.setString(USER_FULL_NAME, user.fullName)
            it.setString(USER_DEPARTMENT, user.department)
            it.setString(USER_SUB_DEPARTMENT, user.subDepartment ?: EmptyString.str)
            it.setString(USER_JOB_ROLE, user.jobRole)
            it.setString(USER_EMAIL, user.email)
            it.setString("${user.email}$PASSWORD_SUFFIX", user.password)
            it.setBoolean(IS_USER_LOG_IN, user.isUserLoggedIn)
            it.setBoolean(IS_EMAIL_VERIFIED, user.isEmailVerified)
            it.setString(FB_TOKEN, user.fbToken)
            it.setLong(EPOCH_FB_DIFF, user.epochFbDiff)
            it.setLong(FB_TOKEN_EXP, user.fbTokenExp)
        }
    }

    fun clearUser() {
        userStorage?.let {
            it.setString(USER_FULL_NAME, EmptyString.str)
            it.setString(USER_DEPARTMENT, EmptyString.str)
            it.setString(USER_SUB_DEPARTMENT, EmptyString.str)
            it.setString(USER_JOB_ROLE, EmptyString.str)
            it.setString(email, EmptyString.str)
            it.setString("$email$PASSWORD_SUFFIX", EmptyString.str)
            it.setBoolean(IS_USER_LOG_IN, false)
            it.setBoolean(IS_EMAIL_VERIFIED, false)
            it.setString(FB_TOKEN, EmptyString.str)
            it.setLong(EPOCH_FB_DIFF, NoRecord.num.toLong())
            it.setLong(FB_TOKEN_EXP, NoRecord.num.toLong())
        }
    }
}