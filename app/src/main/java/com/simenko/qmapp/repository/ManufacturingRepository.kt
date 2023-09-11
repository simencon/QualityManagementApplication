package com.simenko.qmapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.ManufacturingService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ManufacturingRepository @Inject constructor(
    private val database: QualityManagementDB,
    private val manufacturingService: ManufacturingService,
    private val crudeOperations: CrudeOperations
) {
    /**
     * Update Manufacturing from the network
     */
    suspend fun syncTeamMembers() = crudeOperations.syncRecordsAll(
        database.employeeDao
    ) { manufacturingService.getEmployees() }

    fun CoroutineScope.deleteTeamMember(orderId: Int): ReceiveChannel<Event<Resource<DomainEmployee>>> = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { manufacturingService.deleteEmployee(orderId) }
        ) { r -> database.employeeDao.deleteRecord(r) }
    }
    fun CoroutineScope.insertTeamMember(record: DomainEmployee) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { manufacturingService.insertEmployee(record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.employeeDao.insertRecord(r) }
    }
    fun CoroutineScope.updateTeamMember(record: DomainEmployee) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { manufacturingService.editEmployee(record.id, record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.employeeDao.updateRecord(r) }
    }

    fun getEmployeeById(id: Int) = database.employeeDao.getRecordById(id.toString()).let {
        it?.toDomainModel() ?: throw IOException("no such employee in local DB")
    }

    suspend fun syncCompanies() = crudeOperations.syncRecordsAll(
        database.companyDao
    ) { manufacturingService.getCompanies() }

    suspend fun syncJobRoles() = crudeOperations.syncRecordsAll(
        database.jobRoleDao
    ) { manufacturingService.getJobRoles() }

    suspend fun syncDepartments() = crudeOperations.syncRecordsAll(
        database.departmentDao
    ) { manufacturingService.getDepartments() }

    suspend fun syncSubDepartments() = crudeOperations.syncRecordsAll(
        database.subDepartmentDao
    ) { manufacturingService.getSubDepartments() }

    suspend fun syncChannels() = crudeOperations.syncRecordsAll(
        database.channelDao
    ) { manufacturingService.getManufacturingChannels() }

    suspend fun syncLines() = crudeOperations.syncRecordsAll(
        database.lineDao
    ) { manufacturingService.getManufacturingLines() }

    suspend fun syncOperations() = crudeOperations.syncRecordsAll(
        database.operationDao
    ) { manufacturingService.getManufacturingOperations() }

    suspend fun syncOperationsFlows() = crudeOperations.syncRecordsAll(
        database.operationsFlowDao
    ) { manufacturingService.getOperationsFlows() }

    /**
     * Connecting with LiveData for ViewModel
     */
    val getTeamMembers: Flow<List<DomainEmployee>> =
        database.employeeDao.getRecordsFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }

    val employees: Flow<List<DomainEmployeeComplete>> =
        database.employeeDao.getRecordsCompleteFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }

    val companies: Flow<List<DomainCompany>> =
        database.companyDao.getRecordsFlowForUI().map {list ->
            list.map { it.toDomainModel() }
        }

    val jobRoles: Flow<List<DomainJobRole>> =
        database.jobRoleDao.getRecordsFlowForUI().map {list ->
            list.map { it.toDomainModel() }
        }

    val departments: Flow<List<DomainDepartment>> =
        database.departmentDao.getRecordsFlowForUI().map {list ->
            list.map { it.toDomainModel() }
        }

    val subDepartments: Flow<List<DomainSubDepartment>> =
        database.subDepartmentDao.getRecordsFlowForUI().map {list ->
            list.map { it.toDomainModel() }
        }

    val channels: Flow<List<DomainManufacturingChannel>> =
        database.channelDao.getRecordsFlowForUI().map {list ->
            list.map { it.toDomainModel() }
        }

    val lines: Flow<List<DomainManufacturingLine>> =
        database.lineDao.getRecordsFlowForUI().map {list ->
            list.map { it.toDomainModel() }
        }

    val operations: Flow<List<DomainManufacturingOperation>> =
        database.operationDao.getRecordsFlowForUI().map {list ->
            list.map { it.toDomainModel() }
        }

    val operationsFlows: Flow<List<DomainOperationsFlow>> =
        database.operationsFlowDao.getRecordsFlowForUI().map {list ->
            list.map { it.toDomainModel() }
        }

    val departmentsDetailed: LiveData<List<DomainDepartmentComplete>> =
        database.departmentDao.getRecordsDetailedFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }
}