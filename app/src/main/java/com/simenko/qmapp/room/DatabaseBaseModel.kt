package com.simenko.qmapp.room

interface DatabaseBaseModel<out N, out D> {
    fun toNetworkModel(): N
    fun toDomainModel(): D
}