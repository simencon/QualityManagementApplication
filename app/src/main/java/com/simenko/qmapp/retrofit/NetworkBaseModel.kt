package com.simenko.qmapp.retrofit

interface NetworkBaseModel<out T> {
    fun toDatabaseModel(): T
}