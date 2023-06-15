package com.simenko.qmapp.room

interface DatabaseBaseModel<out N, out D> {
    fun getRecordId(): Any
    fun toNetworkModel(): N
    fun toDomainModel(): D
}