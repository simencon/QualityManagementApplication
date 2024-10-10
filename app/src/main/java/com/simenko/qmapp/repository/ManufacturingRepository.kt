package com.simenko.qmapp.repository

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.domain.entities.DomainDepartment.DomainDepartmentComplete
import com.simenko.qmapp.domain.entities.DomainManufacturingLine.DomainManufacturingLineComplete
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel.DomainManufacturingChannelWithParents
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation.DomainManufacturingOperationComplete
import com.simenko.qmapp.domain.entities.DomainManufacturingLine.DomainManufacturingLineWithParents
import com.simenko.qmapp.domain.entities.products.DomainProductLineToDepartment
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.ManufacturingService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.simenko.qmapp.utils.EmployeesFilter
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
    private val service: ManufacturingService,
    private val crudeOperations: CrudeOperations
) {
    /**
     * Update Manufacturing from the network
     */
    suspend fun syncTeamMembers() = crudeOperations.syncRecordsAll(database.employeeDao) { service.getEmployees() }

    fun CoroutineScope.deleteTeamMember(orderId: ID): ReceiveChannel<Event<Resource<DomainEmployee>>> = crudeOperations.run {
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

    fun CoroutineScope.deleteDepartment(id: ID): ReceiveChannel<Event<Resource<DomainDepartment>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteDepartment(id) }) { r -> database.departmentDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertDepartment(record: DomainDepartment) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertDepartment(record.toDatabaseModel().toNetworkModel()) }) { r -> database.departmentDao.insertRecord(r) }
    }

    fun CoroutineScope.updateDepartment(record: DomainDepartment) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editDepartment(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.departmentDao.updateRecord(r) }
    }


    suspend fun syncSubDepartments() = crudeOperations.syncRecordsAll(database.subDepartmentDao) { service.getSubDepartments() }
    fun CoroutineScope.deleteSubDepartment(id: ID): ReceiveChannel<Event<Resource<DomainSubDepartment>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteSubDepartment(id) }) { r -> database.subDepartmentDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertSubDepartment(record: DomainSubDepartment) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertSubDepartment(record.toDatabaseModel().toNetworkModel()) }) { r -> database.subDepartmentDao.insertRecord(r) }
    }

    fun CoroutineScope.updateSubDepartment(record: DomainSubDepartment) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editSubDepartment(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.subDepartmentDao.updateRecord(r) }
    }


    suspend fun syncChannels() = crudeOperations.syncRecordsAll(database.channelDao) { service.getManufacturingChannels() }
    fun CoroutineScope.deleteChannel(id: ID): ReceiveChannel<Event<Resource<DomainManufacturingChannel>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteManufacturingChannel(id) }) { r -> database.channelDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertChannel(record: DomainManufacturingChannel) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertManufacturingChannel(record.toDatabaseModel().toNetworkModel()) }) { r -> database.channelDao.insertRecord(r) }
    }

    fun CoroutineScope.updateChannel(record: DomainManufacturingChannel) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editManufacturingChannel(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.channelDao.updateRecord(r) }
    }


    suspend fun syncLines() = crudeOperations.syncRecordsAll(database.lineDao) { service.getManufacturingLines() }
    fun CoroutineScope.deleteLine(id: ID): ReceiveChannel<Event<Resource<DomainManufacturingLine>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteManufacturingLine(id) }) { r -> database.lineDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertLine(record: DomainManufacturingLine) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertManufacturingLine(record.toDatabaseModel().toNetworkModel()) }) { r -> database.lineDao.insertRecord(r) }
    }

    fun CoroutineScope.updateLine(record: DomainManufacturingLine) = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editManufacturingLine(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.lineDao.updateRecord(r) }
    }


    suspend fun syncOperations() = crudeOperations.syncRecordsAll(database.operationDao) { service.getManufacturingOperations() }
    fun CoroutineScope.deleteOperation(id: ID): ReceiveChannel<Event<Resource<DomainManufacturingOperation>>> = crudeOperations.run {
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

    suspend fun syncProductLinesDepartments() = crudeOperations.syncRecordsAll(database.productLineToDepartmentDao) { service.getProductLinesToDepartments() }
    fun CoroutineScope.insertDepartmentProductLine(record: DomainProductLineToDepartment) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createProductLineToDepartment(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productLineToDepartmentDao.insertRecord(r) }
    }

    suspend fun syncProductKindsSubDepartments() = crudeOperations.syncRecordsAll(database.productKindToSubDepartmentDao) { service.getProductKindsToSubDepartments() }
    suspend fun syncComponentKindsSubDepartments() = crudeOperations.syncRecordsAll(database.componentKindToSubDepartmentDao) { service.getComponentKindsToSubDepartments() }
    suspend fun syncStageKindsSubDepartments() = crudeOperations.syncRecordsAll(database.stageKindToSubDepartmentDao) { service.getStageKindsToSubDepartments() }

    suspend fun syncProductKeysChannels() = crudeOperations.syncRecordsAll(database.productKeyToChannelDao) { service.getProductKeysToChannels() }
    suspend fun syncComponentKeysChannels() = crudeOperations.syncRecordsAll(database.componentKeyToChannelDao) { service.getComponentKeysToChannels() }
    suspend fun syncStageKeysChannels() = crudeOperations.syncRecordsAll(database.stageKeyToChannelDao) { service.getStageKeysToChannels() }

    suspend fun syncProductsToLines() = crudeOperations.syncRecordsAll(database.productToLineDao) { service.getProductsToLines() }
    suspend fun syncComponentsToLines() = crudeOperations.syncRecordsAll(database.componentToLineDao) { service.getComponentsToLines() }
    suspend fun syncComponentStagesToLines() = crudeOperations.syncRecordsAll(database.componentStageToLineDao) { service.getComponentStagesToLines() }

    suspend fun syncCharacteristicsOperations() = crudeOperations.syncRecordsAll(database.characteristicToOperationDao) { service.getCharacteristicsToOperations() }

    /**
     * Connecting with LiveData for ViewModel
     */
    val employees: Flow<List<DomainEmployee>> = database.employeeDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }
    val employeesByParentId: (ID) -> Flow<List<DomainEmployee>> = { pId -> database.employeeDao.getRecordsByParentId(pId).map { list -> list.map { it.toDomainModel() } } }
    val employeeById: (ID) -> DomainEmployee = { id ->
        database.employeeDao.getRecordById(id).let { it?.toDomainModel() ?: throw IOException("no such employee in local DB") }
    }
    val employeesComplete: (EmployeesFilter) -> Flow<List<DomainEmployeeComplete>> = { filter ->
        database.employeeDao.getRecordsCompleteFlowForUI("%${filter.stringToSearch}%", filter.parentId).map { list -> list.map { it.toDomainModel() } }
    }

    val companies: Flow<List<DomainCompany>> = database.companyDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }
    val companyByName: (String) -> DomainCompany? = { database.companyDao.getRecordByName(it)?.toDomainModel() }
    val companyById: suspend (ID) -> DomainCompany = { id ->
        database.companyDao.getRecordById(id).let { it?.toDomainModel() ?: throw IOException("no such company id ($id) in local DB") }
    }

    val jobRoles: Flow<List<DomainJobRole>> = database.jobRoleDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }

    val departments: Flow<List<DomainDepartment>> = database.departmentDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }
    val departmentsByParentId: (ID) -> Flow<List<DomainDepartment>> = { pId -> database.departmentDao.getRecordsByParentId(pId).map { list -> list.map { it.toDomainModel() } } }
    val departmentsComplete: (ID) -> Flow<List<DomainDepartmentComplete>> = { pId -> database.departmentDao.getRecordsComplete(pId).map { list -> list.map { it.toDomainModel() } } }
    val departmentById: (ID) -> DomainDepartmentComplete = { id -> database.departmentDao.getRecordCompleteById(id).toDomainModel() }

    val subDepartments: (ID) -> Flow<List<DomainSubDepartment>> = { pId -> database.subDepartmentDao.getRecordsFlowForUI(pId).map { list -> list.map { it.toDomainModel() } } }
    val subDepartmentWithParentsById: (ID) -> DomainSubDepartment.DomainSubDepartmentWithParents = { database.subDepartmentDao.getRecordWithParentsById(it).toDomainModel() }
    val subDepartmentById: (ID) -> DomainSubDepartment.DomainSubDepartmentComplete = { database.subDepartmentDao.getRecordCompleteById(it).toDomainModel() }

    val channels: (ID) -> Flow<List<DomainManufacturingChannel>> = { pId -> database.channelDao.getRecordsFlowForUI(pId).map { list -> list.map { it.toDomainModel() } } }
    val channelWithParentsById: (ID) -> DomainManufacturingChannelWithParents = { database.channelDao.getRecordWithParentsById(it).toDomainModel() }
    val channelById: (ID) -> DomainManufacturingChannel.DomainManufacturingChannelComplete = { database.channelDao.getRecordCompleteById(it).toDomainModel() }

    val lines: (ID) -> Flow<List<DomainManufacturingLine>> = { pId -> database.lineDao.getRecordsFlowForUI(pId).map { list -> list.map { it.toDomainModel() } } }
    val lineWithParentsById: (ID) -> DomainManufacturingLineWithParents = { database.lineDao.getRecordWithParentsById(it).toDomainModel() }
    val lineById: (ID) -> DomainManufacturingLineComplete = { database.lineDao.getRecordCompleteById(it).toDomainModel() }

    val operations: (ID) -> Flow<List<DomainManufacturingOperationComplete>> = { pId -> database.operationDao.getRecordsFlowForUI(pId).map { list -> list.map { it.toDomainModel() } } }
    val operationById: (ID) -> DomainManufacturingOperationComplete = { database.operationDao.getRecordCompleteById(it).toDomainModel() }

    val operationsFlows: Flow<List<DomainOperationsFlow>> = database.operationsFlowDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }

    val departmentProductLines: (ID) -> Flow<List<DomainProductLineToDepartment>> = { depId ->
        database.productLineToDepartmentDao.getRecordsByParentId(depId).map { it.map { item -> item.toDomainModel() } }
    }
}