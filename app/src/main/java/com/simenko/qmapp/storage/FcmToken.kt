package com.simenko.qmapp.storage

import com.simenko.qmapp.domain.EmptyString

data class FcmToken(
    var fcmTimeStamp: Long,
    var fcmEmail: String,
    var fcmToken: String,
    val userStorage: Storage? = null,
) {
    companion object {
        private const val TIME_STAMP = "fcmTimeStamp"
        private const val TOKEN_EMAIL = "fcmEmail"
        private const val TOKEN = "fcmToken"
    }

    constructor(
        userStorage: Storage
    ) : this(
        fcmTimeStamp = userStorage.getLong(TIME_STAMP),
        fcmEmail = userStorage.getString(TOKEN_EMAIL),
        fcmToken = userStorage.getString(TOKEN),
        userStorage = userStorage
    )

    constructor(
        userStorage: Storage,
        result: Map<String, Any>
    ) : this(
        fcmTimeStamp = userStorage.getLong(TIME_STAMP),
        fcmEmail = (result[TOKEN_EMAIL] ?: EmptyString.str) as String,
        fcmToken = (result[TOKEN] ?: EmptyString.str) as String,
        userStorage = userStorage
    )

    fun dataToFirebase() = hashMapOf(
        TIME_STAMP to fcmTimeStamp,
        TOKEN_EMAIL to fcmEmail,
        TOKEN to fcmToken
    )

    fun setTokenTimeStamp(value: Long) {
        userStorage?.setLong(TIME_STAMP, value)
    }

    fun setTokenEmail(value: String) {
        userStorage?.setString(TOKEN_EMAIL, value)
    }

    fun setToken(value: String) {
        userStorage?.setString(TOKEN, value)
    }
}