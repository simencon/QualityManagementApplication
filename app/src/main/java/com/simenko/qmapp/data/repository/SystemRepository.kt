package com.simenko.qmapp.data.repository

import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.domain.entities.DomainUserRole
import com.simenko.qmapp.data.repository.contract.CrudeOperations
import com.simenko.qmapp.data.remote.implementation.SystemService
import com.simenko.qmapp.data.cache.db.entities.NotificationRegisterEntity
import com.simenko.qmapp.data.cache.db.implementation.QualityManagementDB
import com.simenko.qmapp.utils.UsersFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.Instant
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

    fun CoroutineScope.removeUser(recordId: String) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { service.removeUser(recordId) }
        ) { r -> database.userDao.updateRecord(r) }
    }

    suspend fun syncUsers() = crudeOperations.syncRecordsAll(
        database.userDao
    ) { service.getUsers() }

    fun getUserById(id: String) = database.userDao.getRecordById(id).let {
        it?.toDomainModel() ?: throw IOException("no such employee in local DB")
    }

    val users: (filter: UsersFilter) -> Flow<List<DomainUser>> = { filter ->
        database.userDao.getRecordsFlowForUI(filter.newUsers, "%${filter.stringToSearch}%").map { list -> list.map { it.toDomainModel() } }
    }

    val userRoles: Flow<List<DomainUserRole>> = database.userRoleDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }

    fun cacheNotificationData(email: String) = database.notificationRegisterDao.insertRecord(NotificationRegisterEntity(id = Instant.now().epochSecond.toInt(), email = email))
}