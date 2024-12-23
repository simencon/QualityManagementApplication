package com.simenko.qmapp.data.repository

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.domain.entities.DomainDepartment.DomainDepartmentComplete
import com.simenko.qmapp.domain.entities.DomainManufacturingLine.DomainManufacturingLineComplete
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel.DomainManufacturingChannelWithParents
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation.DomainManufacturingOperationComplete
import com.simenko.qmapp.domain.entities.DomainManufacturingLine.DomainManufacturingLineWithParents
import com.simenko.qmapp.domain.entities.products.DomainCharacteristicToOperation
import com.simenko.qmapp.domain.entities.products.DomainComponentInStageToLine
import com.simenko.qmapp.domain.entities.products.DomainComponentKeyToChannel
import com.simenko.qmapp.domain.entities.products.DomainComponentKindToSubDepartment
import com.simenko.qmapp.domain.entities.products.DomainComponentToLine
import com.simenko.qmapp.domain.entities.products.DomainProductKeyToChannel
import com.simenko.qmapp.domain.entities.products.DomainProductKindToSubDepartment
import com.simenko.qmapp.domain.entities.products.DomainProductLineToDepartment
import com.simenko.qmapp.domain.entities.products.DomainProductToLine
import com.simenko.qmapp.domain.entities.products.DomainStageKeyToChannel
import com.simenko.qmapp.domain.entities.products.DomainStageKindToSubDepartment
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.data.repository.contract.CrudeOperations
import com.simenko.qmapp.data.remote.implementation.ManufacturingService
import com.simenko.qmapp.data.cache.db.implementation.QualityManagementDB
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

    fun CoroutineScope.deleteDepartmentProductLine(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteProductLineToDepartment(recordId) }) { r -> database.productLineToDepartmentDao.deleteRecord(r) }
    }

    suspend fun syncProductKindsSubDepartments() = crudeOperations.syncRecordsAll(database.productKindToSubDepartmentDao) { service.getProductKindsToSubDepartments() }
    fun CoroutineScope.insertSubDepartmentProductKind(record: DomainProductKindToSubDepartment) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createProductKindToSubDepartment(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productKindToSubDepartmentDao.insertRecord(r) }
    }

    fun CoroutineScope.deleteSubDepartmentProductKind(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteProductKindToSubDepartment(recordId) }) { r -> database.productKindToSubDepartmentDao.deleteRecord(r) }
    }

    suspend fun syncComponentKindsSubDepartments() = crudeOperations.syncRecordsAll(database.componentKindToSubDepartmentDao) { service.getComponentKindsToSubDepartments() }
    fun CoroutineScope.insertSubDepartmentComponentKind(record: DomainComponentKindToSubDepartment) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createComponentKindToSubDepartment(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentKindToSubDepartmentDao.insertRecord(r) }
    }

    fun CoroutineScope.deleteSubDepartmentComponentKind(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteComponentKindToSubDepartment(recordId) }) { r -> database.componentKindToSubDepartmentDao.deleteRecord(r) }
    }

    suspend fun syncStageKindsSubDepartments() = crudeOperations.syncRecordsAll(database.stageKindToSubDepartmentDao) { service.getStageKindsToSubDepartments() }
    fun CoroutineScope.insertSubDepartmentStageKind(record: DomainStageKindToSubDepartment) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createStageKindToSubDepartment(record.toDatabaseModel().toNetworkModel()) }) { r -> database.stageKindToSubDepartmentDao.insertRecord(r) }
    }

    fun CoroutineScope.deleteSubDepartmentStageKind(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteStageKindToSubDepartment(recordId) }) { r -> database.stageKindToSubDepartmentDao.deleteRecord(r) }
    }


    suspend fun syncProductKeysChannels() = crudeOperations.syncRecordsAll(database.productKeyToChannelDao) { service.getProductKeysToChannels() }
    fun CoroutineScope.insertChannelProductKey(record: DomainProductKeyToChannel) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createProductKeyToChannel(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productKeyToChannelDao.insertRecord(r) }
    }

    fun CoroutineScope.deleteChannelProductKey(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteProductKeyToChannel(recordId) }) { r -> database.productKeyToChannelDao.deleteRecord(r) }
    }


    suspend fun syncComponentKeysChannels() = crudeOperations.syncRecordsAll(database.componentKeyToChannelDao) { service.getComponentKeysToChannels() }
    fun CoroutineScope.insertChannelComponentKey(record: DomainComponentKeyToChannel) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createComponentKeyToChannel(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentKeyToChannelDao.insertRecord(r) }
    }

    fun CoroutineScope.deleteChannelComponentKey(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteComponentKeyToChannel(recordId) }) { r -> database.componentKeyToChannelDao.deleteRecord(r) }
    }


    suspend fun syncStageKeysChannels() = crudeOperations.syncRecordsAll(database.stageKeyToChannelDao) { service.getStageKeysToChannels() }
    fun CoroutineScope.insertChannelStageKey(record: DomainStageKeyToChannel) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createStageKeyToChannel(record.toDatabaseModel().toNetworkModel()) }) { r -> database.stageKeyToChannelDao.insertRecord(r) }
    }

    fun CoroutineScope.deleteChannelStageKey(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteStageKeyToChannel(recordId) }) { r -> database.stageKeyToChannelDao.deleteRecord(r) }
    }


    suspend fun syncProductsToLines() = crudeOperations.syncRecordsAll(database.productToLineDao) { service.getProductsToLines() }
    fun CoroutineScope.insertLineProduct(record: DomainProductToLine) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createProductToLine(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productToLineDao.insertRecord(r) }
    }

    fun CoroutineScope.deleteLineProduct(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteProductToLine(recordId) }) { r -> database.productToLineDao.deleteRecord(r) }
    }


    suspend fun syncComponentsToLines() = crudeOperations.syncRecordsAll(database.componentToLineDao) { service.getComponentsToLines() }
    fun CoroutineScope.insertLineComponent(record: DomainComponentToLine) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createComponentToLine(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentToLineDao.insertRecord(r) }
    }

    fun CoroutineScope.deleteLineComponent(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteComponentToLine(recordId) }) { r -> database.componentToLineDao.deleteRecord(r) }
    }


    suspend fun syncComponentStagesToLines() = crudeOperations.syncRecordsAll(database.componentStageToLineDao) { service.getComponentStagesToLines() }
    fun CoroutineScope.insertLineStage(record: DomainComponentInStageToLine) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createStageToLine(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentStageToLineDao.insertRecord(r) }
    }

    fun CoroutineScope.deleteLineStage(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteStageToLine(recordId) }) { r -> database.componentStageToLineDao.deleteRecord(r) }
    }


    suspend fun syncCharacteristicsOperations() = crudeOperations.syncRecordsAll(database.characteristicToOperationDao) { service.getCharacteristicsToOperations() }
    fun CoroutineScope.insertOperationCharacteristic(record: DomainCharacteristicToOperation) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.createCharacteristicToOperation(record.toDatabaseModel().toNetworkModel()) }) { r -> database.characteristicToOperationDao.insertRecord(r) }
    }

    fun CoroutineScope.deleteOperationCharacteristic(recordId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteCharacteristicToOperation(recordId) }) { r -> database.characteristicToOperationDao.deleteRecord(r) }
    }

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
    val subDepartmentProductKinds: (ID) -> Flow<List<DomainProductKindToSubDepartment>> = { subDepId ->
        database.productKindToSubDepartmentDao.getRecordsByParentId(subDepId).map { it.map { item -> item.toDomainModel() } }
    }
    val subDepartmentComponentKinds: (ID) -> Flow<List<DomainComponentKindToSubDepartment>> = { subDepId ->
        database.componentKindToSubDepartmentDao.getRecordsByParentId(subDepId).map { it.map { item -> item.toDomainModel() } }
    }
    val subDepartmentStageKinds: (ID) -> Flow<List<DomainStageKindToSubDepartment>> = { subDepId ->
        database.stageKindToSubDepartmentDao.getRecordsByParentId(subDepId).map { it.map { item -> item.toDomainModel() } }
    }

    val channelProductKeys: (ID) -> Flow<List<DomainProductKeyToChannel>> = { subDepId ->
        database.productKeyToChannelDao.getRecordsByParentId(subDepId).map { it.map { item -> item.toDomainModel() } }
    }
    val channelComponentKeys: (ID) -> Flow<List<DomainComponentKeyToChannel>> = { subDepId ->
        database.componentKeyToChannelDao.getRecordsByParentId(subDepId).map { it.map { item -> item.toDomainModel() } }
    }
    val channelStageKeys: (ID) -> Flow<List<DomainStageKeyToChannel>> = { subDepId ->
        database.stageKeyToChannelDao.getRecordsByParentId(subDepId).map { it.map { item -> item.toDomainModel() } }
    }

    val lineProductItems: (ID) -> Flow<List<DomainProductToLine>> = { lineId ->
        database.productToLineDao.getRecordsByParentId(lineId).map { it.map { item -> item.toDomainModel() } }
    }
    val lineComponentItems: (ID) -> Flow<List<DomainComponentToLine>> = { lineId ->
        database.componentToLineDao.getRecordsByParentId(lineId).map { it.map { item -> item.toDomainModel() } }
    }
    val lineStageItems: (ID) -> Flow<List<DomainComponentInStageToLine>> = { lineId ->
        database.componentStageToLineDao.getRecordsByParentId(lineId).map { it.map { item -> item.toDomainModel() } }
    }

    val itemCharsByOperationId: (ID) -> Flow<List<DomainCharacteristicToOperation>> = { operationId ->
        database.characteristicToOperationDao.getRecordsByParentId(operationId).map { it.map { item -> item.toDomainModel() } }
    }
}