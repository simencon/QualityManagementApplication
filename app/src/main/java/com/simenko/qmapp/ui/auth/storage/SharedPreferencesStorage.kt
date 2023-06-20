package com.simenko.qmapp.ui.auth.storage

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// @Inject tells Dagger how to provide instances of this type
class SharedPreferencesStorage @Inject constructor(@ApplicationContext context: Context) : Storage {

    private val sharedPreferences = context.getSharedPreferences(
        "Dagger",
        Context.MODE_PRIVATE
    )

    override fun setString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    override fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }
}
