package com.simenko.qmapp.data.remote

interface NetworkBaseModel<out T> {
    fun getRecordId(): Any
    fun toDatabaseModel(): T
}