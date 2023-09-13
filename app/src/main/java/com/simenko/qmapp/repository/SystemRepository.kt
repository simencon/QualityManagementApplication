package com.simenko.qmapp.repository

import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.SystemService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
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

    fun getUserById(id: String) = database.userDao.getRecordById(id.toString()).let {
        it?.toDomainModel() ?: throw IOException("no such employee in local DB")
    }

    val users: Flow<List<DomainUser>> =
        database.userDao.getRecordsFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }
}