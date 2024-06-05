package com.simenko.qmapp.room.contract

interface DatabaseBaseModel<out N, out D, out ID, in PID> {
    fun getRecordId(): ID
    fun toNetworkModel(): N
    fun toDomainModel(): D
}