package com.simenko.qmapp.retrofit

interface NetworkBaseModel<out T> {
    fun getRecordId(): Any
    fun toDatabaseModel(): T
}