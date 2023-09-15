package com.simenko.qmapp.repository

import com.simenko.qmapp.domain.entities.DomainEmployee
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.domain.entities.DomainUserRole
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.SystemService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import kotlinx.coroutines.CoroutineScope
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

    fun CoroutineScope.updateUserCompanyData(record: DomainUser) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { service.editUserCompanyData(record.email, record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.userDao.updateRecord(r) }
    }

    fun CoroutineScope.authorizeUser(record: DomainUser) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { service.authorizeUser(record.email, record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.userDao.updateRecord(r) }
    }

    fun CoroutineScope.removeUser(record: DomainUser) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { service.removeUser(record.email) }
        ) { r -> database.userDao.updateRecord(r) }
    }

    suspend fun syncUsers() = crudeOperations.syncRecordsAll(
        database.userDao
    ) { service.getUsers() }

    fun getUserById(id: String) = database.userDao.getRecordById(id).let {
        it?.toDomainModel() ?: throw IOException("no such employee in local DB")
    }

    val users: Flow<List<DomainUser>> =
        database.userDao.getRecordsFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }

    val userRoles: Flow<List<DomainUserRole>> =
        database.userRoleDao.getRecordsFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }
}