package com.simenko.qmapp.storage

import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import java.time.Instant

data class Principle(
    var fullName: String,
    var company: String,
    var department: String,
    var subDepartment: String?,
    var jobRole: String,
    var email: String,
    var phoneNumber: Long,
    var restApiUrl: String,
    var password: String,
    var isEmailVerified: Boolean,
    var isUserLoggedIn: Boolean,
    var fbToken: String,
    var epochFbDiff: Long,
    var fbTokenExp: Long,
    val userStorage: Storage? = null
) {
    companion object {
        private const val USER_FULL_NAME = "fullName"
        private const val USER_COMPANY = "company"
        private const val USER_DEPARTMENT = "department"
        private const val USER_SUB_DEPARTMENT = "subDepartment"
        private const val USER_JOB_ROLE = "jobRole"
        private const val USER_EMAIL = "email"
        private const val USER_PHONE_NUMBER = "phoneNumber"
        private const val REST_API_URL = "restApiUrl"
        private const val PASSWORD_SUFFIX = "password_suffix"
        private const val IS_EMAIL_VERIFIED = "isEmailVerified"
        private const val IS_USER_LOGGED_IN = "is_user_log_in"
        private const val FB_TOKEN = "fb_token"
        private const val EPOCH_FB_DIFF = "epoch_fb_diff"
        private const val FB_TOKEN_EXP = "fb_token_exp"
    }

    constructor(
        userStorage: Storage
    ) : this(
        fullName = userStorage.getString(USER_FULL_NAME),
        company = userStorage.getString(USER_COMPANY),
        department = userStorage.getString(USER_DEPARTMENT),
        subDepartment = userStorage.getString(USER_SUB_DEPARTMENT),
        jobRole = userStorage.getString(USER_JOB_ROLE),
        email = userStorage.getString(USER_EMAIL),
        phoneNumber = userStorage.getLong(USER_PHONE_NUMBER),
        restApiUrl = userStorage.getString(REST_API_URL),
        password = userStorage.getString("${userStorage.getString(USER_EMAIL)}$PASSWORD_SUFFIX"),
        isEmailVerified = userStorage.getBoolean(IS_EMAIL_VERIFIED),
        isUserLoggedIn = userStorage.getBoolean(IS_USER_LOGGED_IN),
        fbToken = userStorage.getString(FB_TOKEN),
        epochFbDiff = userStorage.getLong(EPOCH_FB_DIFF),
        fbTokenExp = userStorage.getLong(FB_TOKEN_EXP),
        userStorage = userStorage
    )

    constructor(
        userStorage: Storage,
        result: Map<String, Any>
    ) : this(
        fullName = (result[USER_FULL_NAME] ?: EmptyString.str) as String,
        company = (result[USER_COMPANY] ?: EmptyString.str) as String,
        department = (result[USER_DEPARTMENT] ?: EmptyString.str) as String,
        subDepartment = (result[USER_SUB_DEPARTMENT] ?: EmptyString.str) as String,
        jobRole = (result[USER_JOB_ROLE] ?: EmptyString.str) as String,
        email = userStorage.getString(USER_EMAIL),
        phoneNumber = (result.getValue(USER_PHONE_NUMBER)).toString().toLong(),
        restApiUrl = (result[REST_API_URL] ?: EmptyString.str) as String,
        password = userStorage.getString("${userStorage.getString(USER_EMAIL)}$PASSWORD_SUFFIX"),
        isEmailVerified = (result[IS_EMAIL_VERIFIED] ?: EmptyString.str) as Boolean,
        isUserLoggedIn = userStorage.getBoolean(IS_USER_LOGGED_IN),
        fbToken = userStorage.getString(FB_TOKEN),
        epochFbDiff = userStorage.getLong(EPOCH_FB_DIFF),
        fbTokenExp = userStorage.getLong(FB_TOKEN_EXP),
        userStorage = userStorage
    )

    fun dataToFirebase() = hashMapOf(
        USER_EMAIL to email,
        USER_PHONE_NUMBER to phoneNumber,
        USER_FULL_NAME to fullName,
        USER_COMPANY to company,
        USER_DEPARTMENT to department,
        USER_SUB_DEPARTMENT to subDepartment,
        USER_JOB_ROLE to jobRole,
        IS_EMAIL_VERIFIED to isEmailVerified
    )

    fun setUserEmail(userEmail: String) {
        userStorage?.setString(USER_EMAIL, userEmail)
    }

    fun setUserPassword(password: String) {
        userStorage?.setString("$email$PASSWORD_SUFFIX", password)
    }

    fun setUserIsLoggedIn(isUserLoggedIn: Boolean) {
        userStorage?.setBoolean(IS_USER_LOGGED_IN, isUserLoggedIn)
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
            it.setString(USER_COMPANY, user.company)
            it.setString(USER_DEPARTMENT, user.department)
            it.setString(USER_SUB_DEPARTMENT, user.subDepartment ?: EmptyString.str)
            it.setString(USER_JOB_ROLE, user.jobRole)
            it.setString(USER_EMAIL, user.email)
            it.setLong(USER_PHONE_NUMBER, user.phoneNumber)
            it.setString(REST_API_URL, user.restApiUrl)
            it.setString("${user.email}$PASSWORD_SUFFIX", user.password)
            it.setBoolean(IS_USER_LOGGED_IN, user.isUserLoggedIn)
            it.setBoolean(IS_EMAIL_VERIFIED, user.isEmailVerified)
            it.setString(FB_TOKEN, user.fbToken)
            it.setLong(EPOCH_FB_DIFF, user.epochFbDiff)
            it.setLong(FB_TOKEN_EXP, user.fbTokenExp)
        }
    }


    fun clearUserData() {
        userStorage?.let {
            it.setString(USER_FULL_NAME, EmptyString.str)
            it.setString(USER_DEPARTMENT, EmptyString.str)
            it.setString(USER_SUB_DEPARTMENT, EmptyString.str)
            it.setString(USER_JOB_ROLE, EmptyString.str)
            it.setString(USER_EMAIL, EmptyString.str)
            it.setLong(USER_PHONE_NUMBER, NoRecord.num.toLong())
            it.setString(REST_API_URL, EmptyString.str)
            it.setString("$USER_EMAIL$PASSWORD_SUFFIX", EmptyString.str)
            it.setBoolean(IS_USER_LOGGED_IN, false)
            it.setBoolean(IS_EMAIL_VERIFIED, false)
            it.setString(FB_TOKEN, EmptyString.str)
            it.setLong(EPOCH_FB_DIFF, NoRecord.num.toLong())
            it.setLong(FB_TOKEN_EXP, NoRecord.num.toLong())
        }
    }
}