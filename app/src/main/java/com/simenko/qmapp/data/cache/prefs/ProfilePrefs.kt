package com.simenko.qmapp.data.cache.prefs

import android.content.SharedPreferences
import com.simenko.qmapp.data.cache.prefs.model.Principal
import com.simenko.qmapp.data.cache.prefs.model.FcmToken
import com.simenko.qmapp.data.remote.serializer.JsonSingleton
import kotlinx.serialization.encodeToString

class ProfilePrefs(private val sharedPrefs: SharedPreferences) {
    companion object {
        const val PROFILE_STORAGE = "profile_storage"

        private const val PREF_PRINCIPLE = "pref_principle"
        private const val PREF_FCM_TOKEN = "pref_fcm_token"
    }

    private val json = JsonSingleton.networkJson

    fun clear() {
        sharedPrefs.edit().clear().apply()
    }

    var principal: Principal
        get() = sharedPrefs.getString(PREF_PRINCIPLE, null)?.run { json.decodeFromString(this) } ?: Principal()
        set(value) {
            sharedPrefs.edit().putString(PREF_PRINCIPLE, json.encodeToString(value)).apply()
        }

    var fcmToken: FcmToken
        get() = sharedPrefs.getString(PREF_FCM_TOKEN, null)?.run { json.decodeFromString(this) } ?: FcmToken()
        set(value) {
            sharedPrefs.edit().putString(PREF_FCM_TOKEN, json.encodeToString(value)).apply()
        }
}