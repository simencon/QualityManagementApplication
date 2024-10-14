package com.simenko.qmapp.data.cache.db.contract

interface DatabaseBaseModel<out N, out D, out ID, in PID> {
    fun getRecordId(): ID
    fun toNetworkModel(): N
    fun toDomainModel(): D
}