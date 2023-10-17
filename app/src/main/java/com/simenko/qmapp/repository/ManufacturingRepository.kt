package com.simenko.qmapp.repository

import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.ManufacturingService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.simenko.qmapp.utils.EmployeesFilter
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

@ViewModelScoped
class ManufacturingRepository @Inject constructor(
    private val database: QualityManagementDB,
    private val service: ManufacturingService,
    private val crudeOperations: CrudeOperations
) {
    /**
     * Update Manufacturing from the network
     */
    suspend fun syncTeamMembers() = crudeOperations.syncRecordsAll(database.employeeDao) { service.getEmployees() }

    fun CoroutineScope.deleteTeamMember(orderId: Int): ReceiveChannel<Event<Resource<DomainEmployee>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteEmployee(orderId) }) { r -> database.employeeDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertTeamMember(record: DomainEmployee) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertEmployee(record.toDatabaseModel().toNetworkModel()) }) { r -> database.employeeDao.insertRecord(r) }
    }

    fun CoroutineScope.updateTeamMember(record: DomainEmployee) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editEmployee(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.employeeDao.updateRecord(r) }
    }

    fun getEmployeeById(id: Int) = database.employeeDao.getRecordById(id.toString()).let {
        it?.toDomainModel() ?: throw IOException("no such employee in local DB")
    }

    suspend fun syncCompanies() = crudeOperations.syncRecordsAll(database.companyDao) { service.getCompanies() }
    suspend fun syncJobRoles() = crudeOperations.syncRecordsAll(database.jobRoleDao) { service.getJobRoles() }
    suspend fun syncDepartments() = crudeOperations.syncRecordsAll(database.departmentDao) { service.getDepartments() }
    suspend fun syncSubDepartments() = crudeOperations.syncRecordsAll(database.subDepartmentDao) { service.getSubDepartments() }
    suspend fun syncChannels() = crudeOperations.syncRecordsAll(database.channelDao) { service.getManufacturingChannels() }
    suspend fun syncLines() = crudeOperations.syncRecordsAll(database.lineDao) { service.getManufacturingLines() }
    suspend fun syncOperations() = crudeOperations.syncRecordsAll(database.operationDao) { service.getManufacturingOperations() }
    suspend fun syncOperationsFlows() = crudeOperations.syncRecordsAll(database.operationsFlowDao) { service.getOperationsFlows() }

    /**
     * Connecting with LiveData for ViewModel
     */
    val employees: Flow<List<DomainEmployee>> = database.employeeDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
    val employeesComplete: (EmployeesFilter) -> Flow<List<DomainEmployeeComplete>> = { filter ->
        database.employeeDao.getRecordsCompleteFlowForUI("%${filter.stringToSearch}%").map { list -> list.map { it.toDomainModel() } }
    }

    val companies: Flow<List<DomainCompany>> = database.companyDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }

    val jobRoles: Flow<List<DomainJobRole>> = database.jobRoleDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }

    val departments: Flow<List<DomainDepartment>> = database.departmentDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
    val departmentsComplete: Flow<List<DomainDepartmentComplete>> = database.departmentDao.getRecordsComplete().map { list -> list.map { it.toDomainModel() } }

    val subDepartments: Flow<List<DomainSubDepartment>> = database.subDepartmentDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
    val subDepartmentsByDepartment: (Int) -> Flow<List<DomainSubDepartment>> = { flow { emit(database.subDepartmentDao.getRecordsByParentId(it).map { list -> list.toDomainModel() }) } }

    val channels: Flow<List<DomainManufacturingChannel>> = database.channelDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
    val channelsBySubDepartment:(Int) ->  Flow<List<DomainManufacturingChannel>> = { flow { emit(database.channelDao.getRecordsByParentId(it).map { list -> list.toDomainModel() }) } }

    val lines: Flow<List<DomainManufacturingLine>> = database.lineDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
    val linesByChannel:(Int) ->  Flow<List<DomainManufacturingLine>> = { flow { emit(database.lineDao.getRecordsByParentId(it).map { list -> list.toDomainModel() }) } }

    val operations: Flow<List<DomainManufacturingOperation>> = database.operationDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
    val operationsByLine:(Int) ->  Flow<List<DomainManufacturingOperation>> = { flow { emit(database.operationDao.getRecordsByParentId(it).map { list -> list.toDomainModel() }) } }

    val operationsFlows: Flow<List<DomainOperationsFlow>> = database.operationsFlowDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
}