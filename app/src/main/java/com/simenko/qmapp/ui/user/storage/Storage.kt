package com.simenko.qmapp.ui.user.storage

interface Storage {
    fun setString(key: String, value: String)
    fun getString(key: String): String
}
