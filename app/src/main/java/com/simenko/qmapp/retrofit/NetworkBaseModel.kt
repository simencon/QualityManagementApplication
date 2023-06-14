package com.simenko.qmapp.retrofit

interface NetworkBaseModel<out T> {
    fun getId(): Any
    fun toDatabaseModel(): T
}