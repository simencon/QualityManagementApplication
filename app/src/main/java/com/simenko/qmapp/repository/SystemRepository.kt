package com.simenko.qmapp.repository

import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.SystemService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemRepository @Inject constructor(
    private val database: QualityManagementDB,
    private val service: SystemService,
    private val crudeOperations: CrudeOperations
) {
    suspend fun syncUserRoles() = crudeOperations.syncRecordsAll(
        database.userRoleDao
    ) { service.getUserRoles() }

    suspend fun syncUsers() = crudeOperations.syncRecordsAll(
        database.userDao
    ) { service.getUsers() }
}