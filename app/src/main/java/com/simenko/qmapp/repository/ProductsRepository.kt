package com.simenko.qmapp.repository

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.products.*
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.ProductsService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.simenko.qmapp.domain.entities.products.DomainProductLine.DomainProductLineComplete
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepository @Inject constructor(
    private val database: QualityManagementDB,
    private val crudeOperations: CrudeOperations,
    private val service: ProductsService
) {
    /**
     * Update Products from the network
     */
    suspend fun syncProductLines() = crudeOperations.syncRecordsAll(database.productLineDao) { service.getProductLines() }
    fun CoroutineScope.deleteProductLine(id: ID): ReceiveChannel<Event<Resource<DomainProductLine>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteProductLine(id) }) { r -> database.productLineDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertProductLine(record: DomainProductLine): ReceiveChannel<Event<Resource<DomainProductLine>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertProductLine(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productLineDao.insertRecord(r) }
    }

    fun CoroutineScope.updateProductLine(record: DomainProductLine): ReceiveChannel<Event<Resource<DomainProductLine>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editProductLine(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.productLineDao.updateRecord(r) }
    }


    suspend fun syncProductLineKeys() = crudeOperations.syncRecordsAll(database.productKeyDao) { service.getKeys() }
    fun CoroutineScope.deleteProductLineKey(id: ID): ReceiveChannel<Event<Resource<DomainKey>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.deleteProductLineKey(id) }) { r -> database.productKeyDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertProductLineKey(record: DomainKey): ReceiveChannel<Event<Resource<DomainKey>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertProductLineKey(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productKeyDao.insertRecord(r) }
    }

    fun CoroutineScope.updateProductLineKey(record: DomainKey): ReceiveChannel<Event<Resource<DomainKey>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.editProductLineKey(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.productKeyDao.updateRecord(r) }
    }


    suspend fun syncProductBases() = crudeOperations.syncRecordsAll(database.productBaseDao) { service.getProductBases() }
    suspend fun syncCharacteristicGroups() = crudeOperations.syncRecordsAll(database.characteristicGroupDao) { service.getCharacteristicGroups() }
    fun CoroutineScope.deleteCharGroup(charSubGroupId: ID): ReceiveChannel<Event<Resource<DomainCharGroup>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteCharacteristicGroup(charSubGroupId) }) { r -> database.characteristicGroupDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertCharGroup(record: DomainCharGroup) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertCharacteristicGroup(record.toDatabaseModel().toNetworkModel()) }) { r -> database.characteristicGroupDao.insertRecord(r) }
    }

    fun CoroutineScope.updateCharGroup(record: DomainCharGroup) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.editCharacteristicGroup(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.characteristicGroupDao.updateRecord(r) }
    }


    suspend fun syncCharacteristicSubGroups() = crudeOperations.syncRecordsAll(database.characteristicSubGroupDao) { service.getCharacteristicSubGroups() }
    fun CoroutineScope.deleteCharSubGroup(charSubGroupId: ID): ReceiveChannel<Event<Resource<DomainCharSubGroup>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteCharacteristicSubGroup(charSubGroupId) }) { r -> database.characteristicSubGroupDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertCharSubGroup(record: DomainCharSubGroup) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertCharacteristicSubGroup(record.toDatabaseModel().toNetworkModel()) }) { r -> database.characteristicSubGroupDao.insertRecord(r) }
    }

    fun CoroutineScope.updateCharSubGroup(record: DomainCharSubGroup) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.editCharacteristicSubGroup(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.characteristicSubGroupDao.updateRecord(r) }
    }


    suspend fun syncCharacteristics() = crudeOperations.syncRecordsAll(database.characteristicDao) { service.getCharacteristics() }
    fun CoroutineScope.deleteCharacteristic(id: ID): ReceiveChannel<Event<Resource<DomainCharacteristic>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteCharacteristic(id) }) { r -> database.characteristicDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertCharacteristic(record: DomainCharacteristic) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertCharacteristic(record.toDatabaseModel().toNetworkModel()) }) { r -> database.characteristicDao.insertRecord(r) }
    }

    fun CoroutineScope.updateCharacteristic(record: DomainCharacteristic) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.editCharacteristic(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.characteristicDao.updateRecord(r) }
    }

    suspend fun syncMetrics() = crudeOperations.syncRecordsAll(database.metricDao) { service.getMetrics() }
    suspend fun syncVersionStatuses() = crudeOperations.syncRecordsAll(database.versionStatusDao) { service.getVersionStatuses() }


    suspend fun syncProductKinds() = crudeOperations.syncRecordsAll(database.productKindDao) { service.getProductKinds() }
    fun CoroutineScope.deleteProductKind(id: ID): ReceiveChannel<Event<Resource<DomainProductKind>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteProductKind(id) }) { r -> database.productKindDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertProductKind(record: DomainProductKind) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertProductKind(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productKindDao.insertRecord(r) }
    }

    fun CoroutineScope.updateProductKind(record: DomainProductKind) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.editProductKind(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.productKindDao.updateRecord(r) }
    }


    suspend fun syncComponentKinds() = crudeOperations.syncRecordsAll(database.componentKindDao) { service.getComponentKinds() }
    suspend fun syncComponentStageKinds() = crudeOperations.syncRecordsAll(database.componentStageKindDao) { service.getComponentStageKinds() }
    suspend fun syncProductKindsKeys() = crudeOperations.syncRecordsAll(database.productKindKeyDao) { service.getProductKindsKeys() }
    suspend fun syncComponentKindsKeys() = crudeOperations.syncRecordsAll(database.componentKindKeyDao) { service.getComponentKindsKeys() }
    suspend fun syncComponentStageKindsKeys() = crudeOperations.syncRecordsAll(database.componentStageKindKeyDao) { service.getComponentStageKindsKeys() }
    suspend fun syncCharacteristicsProductKinds() = crudeOperations.syncRecordsAll(database.characteristicProductKindDao) { service.getCharacteristicsProductKinds() }
    suspend fun syncCharacteristicsComponentKinds() = crudeOperations.syncRecordsAll(database.characteristicComponentKindDao) { service.getCharacteristicsComponentKinds() }
    suspend fun syncCharacteristicsComponentStageKinds() = crudeOperations.syncRecordsAll(database.characteristicComponentStageKindDao) { service.getCharacteristicsComponentStageKinds() }
    suspend fun syncProducts() = crudeOperations.syncRecordsAll(database.productDao) { service.getProducts() }
    suspend fun syncComponents() = crudeOperations.syncRecordsAll(database.componentDao) { service.getComponents() }
    suspend fun syncComponentStages() = crudeOperations.syncRecordsAll(database.componentStageDao) { service.getComponentStages() }
    suspend fun syncProductsToLines() = crudeOperations.syncRecordsAll(database.productToLineDao) { service.getProductsToLines() }
    suspend fun syncComponentsToLines() = crudeOperations.syncRecordsAll(database.componentToLineDao) { service.getComponentsToLines() }
    suspend fun syncComponentStagesToLines() = crudeOperations.syncRecordsAll(database.componentStageToLineDao) { service.getComponentStagesToLines() }
    suspend fun syncProductKindsProducts() = crudeOperations.syncRecordsAll(database.productKindProductDao) { service.getProductKindsProducts() }
    suspend fun syncComponentKindsComponents() = crudeOperations.syncRecordsAll(database.componentKindComponentDao) { service.getComponentKindsComponents() }
    suspend fun syncComponentStageKindsComponentStages() = crudeOperations.syncRecordsAll(database.componentStageKindComponentStageDao) { service.getComponentStageKindsComponentStages() }
    suspend fun syncProductsComponents() = crudeOperations.syncRecordsAll(database.productComponentDao) { service.getProductsComponents() }
    suspend fun syncComponentsComponentStages() = crudeOperations.syncRecordsAll(database.componentComponentStageDao) { service.getComponentsComponentStages() }
    suspend fun syncProductVersions() = crudeOperations.syncRecordsAll(database.productVersionDao) { service.getProductVersions() }
    suspend fun syncComponentVersions() = crudeOperations.syncRecordsAll(database.componentVersionDao) { service.getComponentVersions() }
    suspend fun syncComponentStageVersions() = crudeOperations.syncRecordsAll(database.componentStageVersionDao) { service.getComponentStageVersions() }
    suspend fun syncProductTolerances() = crudeOperations.syncRecordsAll(database.productToleranceDao) { service.getProductTolerances() }
    suspend fun syncComponentTolerances() = crudeOperations.syncRecordsAll(database.componentToleranceDao) { service.getComponentTolerances() }
    suspend fun syncComponentStageTolerances() = crudeOperations.syncRecordsAll(database.componentStageToleranceDao) { service.getComponentStageTolerances() }

    val productLine: suspend (ID) -> DomainProductLine = { database.productLineDao.getRecordById(it)?.toDomainModel() ?: DomainProductLine() }
    val productLineById: suspend (ID) -> DomainProductLineComplete = { database.productLineDao.getRecordCompleteById(it)?.toDomainModel() ?: DomainProductLineComplete() }
    val productLines: (ID) -> Flow<List<DomainProductLineComplete>> = { pId ->
        database.productLineDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }

    val charGroups: (ID) -> Flow<List<DomainCharGroup.DomainCharGroupComplete>> = { pId ->
        database.characteristicGroupDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val charGroupById: suspend (ID) -> DomainCharGroup.DomainCharGroupComplete = { id -> database.characteristicGroupDao.getRecordCompleteById(id).toDomainModel() }

    val charSubGroups: (ID) -> Flow<List<DomainCharSubGroup.DomainCharSubGroupComplete>> = { pId ->
        database.characteristicSubGroupDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val charSubGroupById: suspend (ID) -> DomainCharSubGroup.DomainCharSubGroupComplete = { id -> database.characteristicSubGroupDao.getRecordCompleteById(id).toDomainModel() }


    val characteristicById: suspend (ID) -> DomainCharacteristic.DomainCharacteristicComplete = { id -> database.characteristicDao.getRecordCompleteById(id).toDomainModel() }
    val characteristicsByParent: (ID) -> Flow<List<DomainCharacteristic.DomainCharacteristicComplete>> = { pId ->
        database.characteristicDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val characteristics = database.characteristicDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }


    val metrics: (ID) -> Flow<List<DomainMetrix>> = { pId ->
        database.metricDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val metricsByPrefixVersionIdActualityCharId: suspend (String, ID, Boolean, ID) -> List<DomainMetrix> = { prefix, versionId, actual, charId ->
        database.metricDao.getMetricsByPrefixVersionIdActualityCharId(prefix, versionId.toString(), if (actual) "1" else "0", charId.toString()).map { it.toDomainModel() }
    }

    val productLineKeys: (ID) -> Flow<List<DomainKey.DomainKeyComplete>> = { pId ->
        database.productKeyDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val productLineKeyById: suspend (ID) -> DomainKey.DomainKeyComplete = { id ->
        database.productKeyDao.getRecordCompleteById(id)?.toDomainModel() ?: DomainKey.DomainKeyComplete()
    }

    val productKind: suspend (ID) -> DomainProductKind.DomainProductKindComplete = { id ->
        database.productKindDao.getRecordCompleteById(id)?.toDomainModel() ?: DomainProductKind.DomainProductKindComplete()
    }
    val productKinds: (ID) -> Flow<List<DomainProductKind.DomainProductKindComplete>> = { pId ->
        database.productKindDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }

    val itemKindCharsComplete: (String) -> Flow<List<DomainCharacteristicItemKind.DomainCharacteristicItemKindComplete>> = {
        database.productKindDao.getItemVersionTolerancesComplete(it).map { list -> list.map { it.toDomainModel() } }
    }

    val productKindKeys: (ID) -> Flow<List<DomainProductKindKey.DomainProductKindKeyComplete>> = { pId ->
        database.productKindKeyDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val productKindProducts: (ID) -> Flow<List<DomainProductKindProduct.DomainProductKindProductComplete>> = { pId ->
        database.productKindProductDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }


    val componentKind: suspend (ID) -> DomainComponentKind.DomainComponentKindComplete = { id ->
        database.componentKindDao.getRecordCompleteById(id)?.toDomainModel() ?: DomainComponentKind.DomainComponentKindComplete()
    }
    val componentKinds: (ID) -> Flow<List<DomainComponentKind.DomainComponentKindComplete>> = { pId ->
        database.componentKindDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val componentKindKeys: (ID) -> Flow<List<DomainComponentKindKey.DomainComponentKindKeyComplete>> = { pId ->
        database.componentKindKeyDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val components: (ID, ID) -> Flow<List<DomainProductComponent.DomainProductComponentComplete>> = { pId, ckId ->
        database.productComponentDao.getRecordsCompleteForUI(pId, ckId).map { list -> list.map { it.toDomainModel() } }
    }


    val componentStageKind: suspend (ID) -> DomainComponentStageKind.DomainComponentStageKindComplete = { id ->
        database.componentStageKindDao.getRecordCompleteById(id)?.toDomainModel() ?: DomainComponentStageKind.DomainComponentStageKindComplete()
    }
    val componentStageKinds: (ID) -> Flow<List<DomainComponentStageKind.DomainComponentStageKindComplete>> = { pId ->
        database.componentStageKindDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val componentStageKindKeys: (ID) -> Flow<List<DomainComponentStageKindKey.DomainComponentStageKindKeyComplete>> = { pId ->
        database.componentStageKindKeyDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val componentStages: (ID, ID) -> Flow<List<DomainComponentComponentStage.DomainComponentComponentStageComplete>> = { cId, cskId ->
        database.componentComponentStageDao.getRecordsCompleteForUI(cId, cskId).map { list -> list.map { it.toDomainModel() } }
    }
    val itemVersionsComplete: (String) -> Flow<List<DomainItemVersionComplete>> = { fpId ->
        database.productVersionDao.getRecordsCompleteForUI(fpId).map { list -> list.map { it.toDomainModel() } }
    }

    val itemVersionComplete: suspend (String) -> DomainItemVersionComplete = { fId ->
        database.productVersionDao.getRecordCompleteForUI(fId)?.toDomainModel() ?: DomainItemVersionComplete()
    }

    val versionTolerancesComplete: (String) -> Flow<List<DomainItemTolerance.DomainItemToleranceComplete>> = { versionFId ->
        database.productVersionDao.getItemVersionTolerancesComplete(versionFId).map { list -> list.map { it.toDomainModel() } }
    }

    val versionStatuses = database.versionStatusDao.getRecordsForUI()
}