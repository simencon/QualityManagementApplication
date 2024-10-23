package com.simenko.qmapp.data.cache.prefs

import android.content.SharedPreferences
import com.simenko.qmapp.data.cache.prefs.model.Principal
import com.simenko.qmapp.data.remote.serializer.JsonSingleton
import kotlinx.serialization.encodeToString

class ProfilePrefs(private val sharedPrefs: SharedPreferences) {
    companion object {
        const val PROFILE_STORAGE = "profile_storage"

        const val PREF_PRINCIPLE = "pref_principle"
    }

    private val json = JsonSingleton.networkJson

    var principal: Principal
        get() = sharedPrefs.getString(PREF_PRINCIPLE, null)?.run { json.decodeFromString(this) } ?: Principal()
        set(value) {
            sharedPrefs.edit().putString(PREF_PRINCIPLE, json.encodeToString(value)).apply()
        }
}