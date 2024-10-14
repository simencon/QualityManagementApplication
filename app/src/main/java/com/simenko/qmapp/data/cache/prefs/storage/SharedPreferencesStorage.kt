package com.simenko.qmapp.data.cache.prefs.storage

import android.content.Context
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import javax.inject.Inject

class SharedPreferencesStorage @Inject constructor(app: BaseApplication) : Storage {

    private val sharedPreferences = app.getSharedPreferences(
        "QualityManagementApp",
        Context.MODE_PRIVATE
    )

    override fun setBoolean(key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun setString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    override fun getString(key: String): String {
        return sharedPreferences.getString(key, EmptyString.str)!!
    }

    override fun setLong(key: String, value: Long) {
        with(sharedPreferences.edit()) {
            putLong(key, value)
            apply()
        }
    }

    override fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, NoRecord.num)
    }

}
