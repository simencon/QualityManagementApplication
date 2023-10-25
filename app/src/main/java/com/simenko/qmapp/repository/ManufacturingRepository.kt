package com.simenko.qmapp.repository

import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.domain.entities.DomainManufacturingLine.DomainManufacturingLineComplete
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel.DomainManufacturingChannelWithParents
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation.DomainManufacturingOperationComplete
import com.simenko.qmapp.domain.entities.DomainManufacturingLine.DomainManufacturingLineWithParents
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


    suspend fun syncCompanies() = crudeOperations.syncRecordsAll(database.companyDao) { service.getCompanies() }
    suspend fun syncJobRoles() = crudeOperations.syncRecordsAll(database.jobRoleDao) { service.getJobRoles() }
    suspend fun syncDepartments() = crudeOperations.syncRecordsAll(database.departmentDao) { service.getDepartments() }


    suspend fun syncSubDepartments() = crudeOperations.syncRecordsAll(database.subDepartmentDao) { service.getSubDepartments() }
    fun CoroutineScope.deleteSubDepartment(id: Int): ReceiveChannel<Event<Resource<DomainSubDepartment>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteSubDepartment(id) }) { r -> database.subDepartmentDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertSubDepartment(record: DomainSubDepartment) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertSubDepartment(record.toDatabaseModel().toNetworkModel()) }) { r -> database.subDepartmentDao.insertRecord(r) }
    }

    fun CoroutineScope.updateSubDepartment(record: DomainSubDepartment) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editSubDepartment(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.subDepartmentDao.updateRecord(r) }
    }


    suspend fun syncChannels() = crudeOperations.syncRecordsAll(database.channelDao) { service.getManufacturingChannels() }
    fun CoroutineScope.deleteChannel(id: Int): ReceiveChannel<Event<Resource<DomainManufacturingChannel>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteManufacturingChannel(id) }) { r -> database.channelDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertChannel(record: DomainManufacturingChannel) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertManufacturingChannel(record.toDatabaseModel().toNetworkModel()) }) { r -> database.channelDao.insertRecord(r) }
    }

    fun CoroutineScope.updateChannel(record: DomainManufacturingChannel) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editManufacturingChannel(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.channelDao.updateRecord(r) }
    }


    suspend fun syncLines() = crudeOperations.syncRecordsAll(database.lineDao) { service.getManufacturingLines() }
    fun CoroutineScope.deleteLine(id: Int): ReceiveChannel<Event<Resource<DomainManufacturingLine>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteManufacturingLine(id) }) { r -> database.lineDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertLine(record: DomainManufacturingLine) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertManufacturingLine(record.toDatabaseModel().toNetworkModel()) }) { r -> database.lineDao.insertRecord(r) }
    }

    fun CoroutineScope.updateLine(record: DomainManufacturingLine) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editManufacturingLine(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.lineDao.updateRecord(r) }
    }


    suspend fun syncOperations() = crudeOperations.syncRecordsAll(database.operationDao) { service.getManufacturingOperations() }
    fun CoroutineScope.deleteOperation(id: Int): ReceiveChannel<Event<Resource<DomainManufacturingOperation>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteManufacturingOperation(id) }) { r -> database.operationDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertOperation(record: DomainManufacturingOperation) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertManufacturingOperation(record.toDatabaseModel().toNetworkModel()) }) { r -> database.operationDao.insertRecord(r) }
    }

    fun CoroutineScope.updateOperation(record: DomainManufacturingOperation) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editManufacturingOperation(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.operationDao.updateRecord(r) }
    }


    suspend fun syncOperationsFlows() = crudeOperations.syncRecordsAll(database.operationsFlowDao) { service.getOperationsFlows() }
    fun CoroutineScope.deleteOpFlows(records: List<DomainOperationsFlow>) = crudeOperations.run {
        responseHandlerForListOfRecords(taskExecutor = { service.deleteOpFlows(records.map { it.toDatabaseModel().toNetworkModel() }) }) { r -> database.operationsFlowDao.deleteRecords(r) }
    }

    fun CoroutineScope.insertOpFlows(records: List<DomainOperationsFlow>) = crudeOperations.run {
        responseHandlerForListOfRecords(taskExecutor = { service.createOpFlows(records.map { it.toDatabaseModel().toNetworkModel() }) }) { r -> database.operationsFlowDao.insertRecords(r) }
    }

    /**
     * Connecting with LiveData for ViewModel
     */
    val employees: Flow<List<DomainEmployee>> = database.employeeDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
    val employeeById: (Int) -> DomainEmployee = { id ->
        database.employeeDao.getRecordById(id.toString()).let { it?.toDomainModel() ?: throw IOException("no such employee in local DB") }
    }
    val employeesComplete: (EmployeesFilter) -> Flow<List<DomainEmployeeComplete>> = { filter ->
        database.employeeDao.getRecordsCompleteFlowForUI("%${filter.stringToSearch}%").map { list -> list.map { it.toDomainModel() } }
    }

    val companies: Flow<List<DomainCompany>> = database.companyDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
    val companyByName: (String) -> DomainCompany? = { database.companyDao.getRecordByName(it)?.toDomainModel() }

    val jobRoles: Flow<List<DomainJobRole>> = database.jobRoleDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }

    val departments: Flow<List<DomainDepartment>> = database.departmentDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
    val departmentsComplete: (Int) -> Flow<List<DomainDepartmentComplete>> = { pId -> database.departmentDao.getRecordsComplete(pId).map { list -> list.map { it.toDomainModel() } } }
    val departmentById: (Int) -> DomainDepartment = { id -> database.departmentDao.getRecordById(id.toString()).let { it?.toDomainModel() ?: throw IOException("no such employee in local DB") } }

    val subDepartments: Flow<List<DomainSubDepartment>> = database.subDepartmentDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
    val subDepartmentWithParentsById: (Int) -> DomainSubDepartment.DomainSubDepartmentWithParents = { database.subDepartmentDao.getRecordWithParentsById(it).toDomainModel() }
    val subDepartmentById: (Int) -> DomainSubDepartment.DomainSubDepartmentComplete = { database.subDepartmentDao.getRecordCompleteById(it).toDomainModel() }

    val subDepartmentsByDepartment: (Int) -> Flow<List<DomainSubDepartment>> = { flow { emit(database.subDepartmentDao.getRecordsByParentId(it).map { list -> list.toDomainModel() }) } }

    val channels: (Int) -> Flow<List<DomainManufacturingChannel>> = { pId -> database.channelDao.getRecordsFlowForUI(pId).map { list -> list.map { it.toDomainModel() } } }
    val channelWithParentsById: (Int) -> DomainManufacturingChannelWithParents = { database.channelDao.getRecordWithParentsById(it).toDomainModel() }
    val channelById: (Int) -> DomainManufacturingChannel.DomainManufacturingChannelComplete = { database.channelDao.getRecordCompleteById(it).toDomainModel() }

    val lines: (Int) -> Flow<List<DomainManufacturingLine>> = { pId -> database.lineDao.getRecordsFlowForUI(pId).map { list -> list.map { it.toDomainModel() } } }
    val lineWithParentsById: (Int) -> DomainManufacturingLineWithParents = { database.lineDao.getRecordWithParentsById(it).toDomainModel() }
    val lineById: (Int) -> DomainManufacturingLineComplete = { database.lineDao.getRecordCompleteById(it).toDomainModel() }

    val operations: (Int) -> Flow<List<DomainManufacturingOperationComplete>> = { pId -> database.operationDao.getRecordsFlowForUI(pId).map { list -> list.map { it.toDomainModel() } } }
    val operationById: (Int) -> DomainManufacturingOperationComplete = { database.operationDao.getRecordCompleteById(it).toDomainModel() }

    val operationsFlows: Flow<List<DomainOperationsFlow>> = database.operationsFlowDao.getRecordsFlowForUI().map { list -> list.map { it.toDomainModel() } }
}