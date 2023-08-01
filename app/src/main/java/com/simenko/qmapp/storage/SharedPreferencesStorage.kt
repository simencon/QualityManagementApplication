package com.simenko.qmapp.storage

import android.content.Context
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// @Inject tells Dagger how to provide instances of this type
class SharedPreferencesStorage @Inject constructor(@ApplicationContext context: Context) : Storage {

    private val sharedPreferences = context.getSharedPreferences(
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
        return sharedPreferences.getLong(key, NoRecord.num.toLong())
    }

}
