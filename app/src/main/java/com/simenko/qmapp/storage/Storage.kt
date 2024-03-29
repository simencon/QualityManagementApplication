package com.simenko.qmapp.storage

interface Storage {
    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Boolean
    fun setString(key: String, value: String)
    fun getString(key: String): String
    fun setLong(key: String, value: Long)
    fun getLong(key: String): Long
}
