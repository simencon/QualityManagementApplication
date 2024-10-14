package com.simenko.qmapp.repository

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.products.*
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.ProductsService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.simenko.qmapp.domain.entities.products.DomainProductLine.DomainProductLineComplete
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.retrofit.entities.NetworkErrorBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Converter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepository @Inject constructor(
    private val database: QualityManagementDB,
    private val crudeOperations: CrudeOperations,
    private val service: ProductsService,
    private val errorConverter: Converter<ResponseBody, NetworkErrorBody>,
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
    fun CoroutineScope.insertProductBase(record: DomainProductBase): ReceiveChannel<Event<Resource<DomainProductBase>>> = crudeOperations.run {
        responseHandlerForSingleRecord(taskExecutor = { service.insertProductBase(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productBaseDao.insertRecord(r) }
    }


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
    fun CoroutineScope.deleteMetric(id: ID): ReceiveChannel<Event<Resource<DomainMetrix>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteMetric(id) }) { r -> database.metricDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertMetric(record: DomainMetrix) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertMetric(record.toDatabaseModel().toNetworkModel()) }) { r -> database.metricDao.insertRecord(r) }
    }

    fun CoroutineScope.updateMetric(record: DomainMetrix) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.editMetric(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.metricDao.updateRecord(r) }
    }


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
    fun CoroutineScope.deleteComponentKind(id: ID): ReceiveChannel<Event<Resource<DomainComponentKind>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteComponentKind(id) }) { r -> database.componentKindDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertComponentKind(record: DomainComponentKind) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertComponentKind(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentKindDao.insertRecord(r) }
    }

    fun CoroutineScope.updateComponentKind(record: DomainComponentKind) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.editComponentKind(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentKindDao.updateRecord(r) }
    }


    suspend fun syncComponentStageKinds() = crudeOperations.syncRecordsAll(database.componentStageKindDao) { service.getComponentStageKinds() }
    fun CoroutineScope.deleteComponentStageKind(id: ID): ReceiveChannel<Event<Resource<DomainComponentStageKind>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteComponentStageKind(id) }) { r -> database.componentStageKindDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertComponentStageKind(record: DomainComponentStageKind) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertComponentStageKind(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentStageKindDao.insertRecord(r) }
    }

    fun CoroutineScope.updateComponentStageKind(record: DomainComponentStageKind) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.editComponentStageKind(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentStageKindDao.updateRecord(r) }
    }


    suspend fun syncProductKindsKeys() = crudeOperations.syncRecordsAll(database.productKindKeyDao) { service.getProductKindsKeys() }
    fun CoroutineScope.deleteProductKindKey(id: ID): ReceiveChannel<Event<Resource<DomainProductKindKey>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteProductKindKey(id) }) { r -> database.productKindKeyDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertProductKindKey(record: DomainProductKindKey) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertProductKindKey(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productKindKeyDao.insertRecord(r) }
    }


    suspend fun syncComponentKindsKeys() = crudeOperations.syncRecordsAll(database.componentKindKeyDao) { service.getComponentKindsKeys() }
    fun CoroutineScope.deleteComponentKindKey(id: ID): ReceiveChannel<Event<Resource<DomainComponentKindKey>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteComponentKindKey(id) }) { r -> database.componentKindKeyDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertComponentKindKey(record: DomainComponentKindKey) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertComponentKindKey(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentKindKeyDao.insertRecord(r) }
    }


    suspend fun syncComponentStageKindsKeys() = crudeOperations.syncRecordsAll(database.componentStageKindKeyDao) { service.getComponentStageKindsKeys() }
    fun CoroutineScope.deleteComponentStageKindKey(id: ID): ReceiveChannel<Event<Resource<DomainComponentStageKindKey>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteComponentStageKindKey(id) }) { r -> database.componentStageKindKeyDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertComponentStageKindKey(record: DomainComponentStageKindKey) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertComponentStageKindKey(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentStageKindKeyDao.insertRecord(r) }
    }


    suspend fun syncCharacteristicsProductKinds() = crudeOperations.syncRecordsAll(database.characteristicProductKindDao) { service.getCharacteristicsProductKinds() }
    fun CoroutineScope.deleteProductKindCharacteristic(id: ID): ReceiveChannel<Event<Resource<DomainCharacteristicProductKind>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteCharacteristicProductKind(id) }) { r -> database.characteristicProductKindDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertProductKindCharacteristic(record: DomainCharacteristicProductKind) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertCharacteristicProductKind(record.toDatabaseModel().toNetworkModel()) }) { r -> database.characteristicProductKindDao.insertRecord(r) }
    }


    suspend fun syncCharacteristicsComponentKinds() = crudeOperations.syncRecordsAll(database.characteristicComponentKindDao) { service.getCharacteristicsComponentKinds() }
    fun CoroutineScope.deleteComponentKindCharacteristic(id: ID): ReceiveChannel<Event<Resource<DomainCharacteristicComponentKind>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteCharacteristicComponentKind(id) }) { r -> database.characteristicComponentKindDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertComponentKindCharacteristic(record: DomainCharacteristicComponentKind) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertCharacteristicComponentKind(record.toDatabaseModel().toNetworkModel()) }) { r -> database.characteristicComponentKindDao.insertRecord(r) }
    }


    suspend fun syncCharacteristicsComponentStageKinds() = crudeOperations.syncRecordsAll(database.charComponentStageKindDao) { service.getCharacteristicsComponentStageKinds() }
    fun CoroutineScope.deleteComponentStageKindCharacteristic(id: ID): ReceiveChannel<Event<Resource<DomainCharacteristicComponentStageKind>>> = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteCharacteristicComponentStageKind(id) }) { r -> database.charComponentStageKindDao.deleteRecord(r) }
    }

    fun CoroutineScope.insertComponentStageKindCharacteristic(record: DomainCharacteristicComponentStageKind) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertCharacteristicComponentStageKind(record.toDatabaseModel().toNetworkModel()) }) { r -> database.charComponentStageKindDao.insertRecord(r) }
    }


    suspend fun syncProducts() = crudeOperations.syncRecordsAll(database.productDao) { service.getProducts() }
    fun CoroutineScope.insertProduct(record: DomainProduct) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertProduct(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productDao.insertRecord(r) }
    }

    fun CoroutineScope.updateProduct(record: DomainProduct) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.editProduct(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.productDao.updateRecord(r) }
    }


    suspend fun syncComponents() = crudeOperations.syncRecordsAll(database.componentDao) { service.getComponents() }
    fun CoroutineScope.insertComponent(record: DomainComponent) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertComponent(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentDao.insertRecord(r) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.updateComponent(record: DomainComponent): ReceiveChannel<Event<Resource<DomainComponent>>> {
        val existingRecord = database.componentDao.getRecordById(record.id)?.component?.toDomainModel()
        return if (existingRecord == record) {
            produce {
                send(Event(Resource.success(existingRecord)))
            }
        } else {
            crudeOperations.run {
                responseHandlerForSingleRecord({ service.editComponent(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentDao.updateRecord(r) }
            }
        }
    }


    suspend fun syncComponentStages() = crudeOperations.syncRecordsAll(database.componentStageDao) { service.getComponentStages() }
    fun CoroutineScope.insertComponentStage(record: DomainComponentStage) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertComponentStage(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentStageDao.insertRecord(r) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.updateComponentStage(record: DomainComponentStage): ReceiveChannel<Event<Resource<DomainComponentStage>>> {
        val existingRecord = database.componentStageDao.getRecordById(record.id)?.toDomainModel()
        return if (existingRecord == record) {
            produce { send(Event(Resource.success(existingRecord))) }
        } else {
            crudeOperations.run {
                responseHandlerForSingleRecord({ service.editComponentStage(record.id, record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentStageDao.updateRecord(r) }
            }
        }
    }

    suspend fun syncProductKindsProducts() = crudeOperations.syncRecordsAll(database.productKindProductDao) { service.getProductKindsProducts() }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.deleteProductKindProduct(id: ID): ReceiveChannel<Event<Resource<DomainProductKindProduct>>> {
        return produce(Dispatchers.IO) {
            send(Event(Resource.loading(null)))
            val response = service.deleteProductKindProduct(id)
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    database.productKindProductDao.deleteRecord(result.productKindProduct.toDatabaseModel())
                    result.product?.let { database.productDao.deleteRecord(it.toDatabaseModel()) }
                    send(Event(Resource.success(result.productKindProduct.toDatabaseModel().toDomainModel())))
                } ?: run {
                    send(Event(Resource.error("No response body", null)))
                }
            } else {
                send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", null)))
            }
        }
    }

    fun CoroutineScope.insertProductKindProduct(record: DomainProductKindProduct) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.insertProductKindProduct(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productKindProductDao.insertRecord(r) }
    }


    suspend fun syncComponentKindsComponents() = crudeOperations.syncRecordsAll(database.componentKindComponentDao) { service.getComponentKindsComponents() }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.insertComponentKindComponent(record: DomainComponentKindComponent): ReceiveChannel<Event<Resource<DomainComponentKindComponent>>> {
        val existingRecord = runBlocking { database.componentKindComponentDao.findExistingRecord(record.componentKindId, record.componentId) }
        return if (existingRecord != null) {
            produce { send(Event(Resource.success(existingRecord.toDomainModel()))) }
        } else {
            crudeOperations.run {
                responseHandlerForSingleRecord({ service.insertComponentKindComponent(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentKindComponentDao.insertRecord(r) }
            }
        }
    }

    suspend fun syncComponentStageKindsComponentStages() = crudeOperations.syncRecordsAll(database.componentStageKindComponentStageDao) { service.getComponentStageKindsComponentStages() }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.insertComponentStageKindComponentStage(record: DomainComponentStageKindComponentStage): ReceiveChannel<Event<Resource<DomainComponentStageKindComponentStage>>> {
        val existingRecord = runBlocking { database.componentStageKindComponentStageDao.findExistingRecord(record.componentStageKindId, record.componentStageId) }
        return if (existingRecord != null) {
            produce { send(Event(Resource.success(existingRecord.toDomainModel()))) }
        } else {
            crudeOperations.run {
                responseHandlerForSingleRecord({ service.insertStageKindStage(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentStageKindComponentStageDao.insertRecord(r) }
            }
        }
    }


    suspend fun syncProductsComponents() = crudeOperations.syncRecordsAll(database.productComponentDao) { service.getProductsComponents() }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.deleteProductComponent(id: ID): ReceiveChannel<Event<Resource<DomainProductComponent>>> {
        return produce(Dispatchers.IO) {
            send(Event(Resource.loading(null)))
            val response = service.deleteProductComponent(id)
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    database.productComponentDao.deleteRecord(result.productComponent.toDatabaseModel())
                    result.componentKindComponent?.let { database.componentKindComponentDao.deleteRecord(it.toDatabaseModel()) }
                    result.component?.let { database.componentDao.deleteRecord(it.toDatabaseModel()) }
                    send(Event(Resource.success(result.productComponent.toDatabaseModel().toDomainModel())))
                } ?: run {
                    send(Event(Resource.error("No response body", null)))
                }
            } else {
                send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", null)))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.insertProductComponent(record: DomainProductComponent): ReceiveChannel<Event<Resource<DomainProductComponent>>> {
        val existingRecord = runBlocking { database.productComponentDao.findExistingRecord(record.productId, record.componentKindComponentId)?.toDomainModel() }
        return if (existingRecord != null) {
            val newRecord = record.copy(id = existingRecord.id)
            if (existingRecord == newRecord) {
                produce {
                    send(Event(Resource.success(existingRecord)))
                }
            } else {
                crudeOperations.run {
                    responseHandlerForSingleRecord({ service.editProductComponent(newRecord.id, newRecord.toDatabaseModel().toNetworkModel()) }) { r -> database.productComponentDao.updateRecord(r) }
                }
            }
        } else {
            crudeOperations.run {
                responseHandlerForSingleRecord({ service.insertProductComponent(record.toDatabaseModel().toNetworkModel()) }) { r -> database.productComponentDao.insertRecord(r) }
            }
        }
    }


    suspend fun syncComponentsComponentStages() = crudeOperations.syncRecordsAll(database.componentComponentStageDao) { service.getComponentsComponentStages() }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.deleteComponentComponentStage(id: ID): ReceiveChannel<Event<Resource<DomainComponentComponentStage>>> {
        return produce(Dispatchers.IO) {
            send(Event(Resource.loading(null)))
            val response = service.deleteComponentComponentStage(id)
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    database.componentComponentStageDao.deleteRecord(result.componentComponentStage.toDatabaseModel())
                    result.stageStageKind?.let { database.componentStageKindComponentStageDao.deleteRecord(it.toDatabaseModel()) }
                    result.componentInStage?.let { database.componentStageDao.deleteRecord(it.toDatabaseModel()) }
                    send(Event(Resource.success(result.componentComponentStage.toDatabaseModel().toDomainModel())))
                } ?: run {
                    send(Event(Resource.error("No response body", null)))
                }
            } else {
                send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", null)))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun CoroutineScope.insertComponentComponentStage(record: DomainComponentComponentStage): ReceiveChannel<Event<Resource<DomainComponentComponentStage>>> {
        val existingRecord = runBlocking { database.componentComponentStageDao.findExistingRecord(record.componentId, record.stageKindStageId)?.toDomainModel() }
        return if (existingRecord != null) {
            val newRecord = record.copy(id = existingRecord.id)
            if (newRecord == existingRecord) {
                produce {
                    send(Event(Resource.success(newRecord)))
                }
            } else {
                crudeOperations.run {
                    responseHandlerForSingleRecord({
                        service.editComponentComponentStage(
                            newRecord.id,
                            newRecord.toDatabaseModel().toNetworkModel()
                        )
                    }) { r -> database.componentComponentStageDao.insertRecord(r) }
                }
            }
        } else {
            crudeOperations.run {
                responseHandlerForSingleRecord({ service.insertComponentComponentStage(record.toDatabaseModel().toNetworkModel()) }) { r -> database.componentComponentStageDao.insertRecord(r) }
            }
        }
    }

    fun CoroutineScope.makeProductVersion(version: Pair<DomainProductVersion, List<DomainProductTolerance>>) = crudeOperations.run {
        val requestBody = Pair(
            first = version.first.toDatabaseModel().toNetworkModel(),
            second = version.second.map { it.toDatabaseModel().toNetworkModel() }
        )
        syncParentWithChildren(parentDao = database.productVersionDao, childrenDao = database.productToleranceDao, taskExecutor = { service.makeProductVersion(version = requestBody) }) { r ->
            if (r.isDefault) {
                database.productVersionDao.getRecordsByParentId(r.productId).filter { it.id != r.id }.map { it.copy(isDefault = false) }.let { rCpy ->
                    if (rCpy.isNotEmpty()) database.productVersionDao.updateRecords(rCpy)
                }
            }
        }
    }

    fun CoroutineScope.makeComponentVersion(version: Pair<DomainComponentVersion, List<DomainComponentTolerance>>) = crudeOperations.run {
        val requestBody = Pair(
            first = version.first.toDatabaseModel().toNetworkModel(),
            second = version.second.map { it.toDatabaseModel().toNetworkModel() }
        )
        syncParentWithChildren(parentDao = database.componentVersionDao, childrenDao = database.componentToleranceDao, taskExecutor = { service.makeComponentVersion(version = requestBody) }) { r ->
            if (r.isDefault) {
                database.componentVersionDao.getRecordsByParentId(r.componentId).filter { it.id != r.id }.map { it.copy(isDefault = false) }.let { rCpy ->
                    if (rCpy.isNotEmpty()) database.componentVersionDao.updateRecords(rCpy)
                }
            }
        }
    }

    fun CoroutineScope.makeStageVersion(version: Pair<DomainComponentStageVersion, List<DomainComponentInStageTolerance>>) = crudeOperations.run {
        val requestBody = Pair(
            first = version.first.toDatabaseModel().toNetworkModel(),
            second = version.second.map { it.toDatabaseModel().toNetworkModel() }
        )
        syncParentWithChildren(parentDao = database.componentStageVersionDao, childrenDao = database.stageToleranceDao, taskExecutor = { service.makeStageVersion(version = requestBody) }) { r ->
            if (r.isDefault) {
                database.componentStageVersionDao.getRecordsByParentId(r.componentInStageId).filter { it.id != r.id }.map { it.copy(isDefault = false) }.let { rCpy ->
                    if (rCpy.isNotEmpty()) database.componentStageVersionDao.updateRecords(rCpy)
                }
            }
        }
    }

    fun CoroutineScope.deleteProductVersion(versionId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteProductVersion(versionId) }) { r -> database.productVersionDao.deleteRecord(r) }
    }

    fun CoroutineScope.deleteComponentVersion(versionId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteComponentVersion(versionId) }) { r -> database.componentVersionDao.deleteRecord(r) }
    }

    fun CoroutineScope.deleteStageVersion(versionId: ID) = crudeOperations.run {
        responseHandlerForSingleRecord({ service.deleteStageVersion(versionId) }) { r -> database.componentStageVersionDao.deleteRecord(r) }
    }


    suspend fun syncProductVersions() = crudeOperations.syncRecordsAll(database.productVersionDao) { service.getProductVersions() }
    suspend fun syncComponentVersions() = crudeOperations.syncRecordsAll(database.componentVersionDao) { service.getComponentVersions() }
    suspend fun syncComponentStageVersions() = crudeOperations.syncRecordsAll(database.componentStageVersionDao) { service.getComponentStageVersions() }
    suspend fun syncProductTolerances() = crudeOperations.syncRecordsAll(database.productToleranceDao) { service.getProductTolerances() }
    suspend fun syncComponentTolerances() = crudeOperations.syncRecordsAll(database.componentToleranceDao) { service.getComponentTolerances() }
    suspend fun syncComponentStageTolerances() = crudeOperations.syncRecordsAll(database.stageToleranceDao) { service.getComponentStageTolerances() }


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

    val productCharsByLineId: (ID) -> Flow<List<DomainCharacteristic.DomainCharacteristicWithParents>> = { pId ->
        database.characteristicDao.getAllProductCharsByLineId(pId).map { list -> list.distinctBy { it.charId }.map { it.toDomainModel() } }
    }
    val componentCharsByLineId: (ID) -> Flow<List<DomainCharacteristic.DomainCharacteristicWithParents>> = { pId ->
        database.characteristicDao.getAllComponentCharsByLineId(pId).map { list -> list.distinctBy { it.charId }.map { it.toDomainModel() } }
    }
    val stageCharsByLineId: (ID) -> Flow<List<DomainCharacteristic.DomainCharacteristicWithParents>> = { pId ->
        database.characteristicDao.getAllStageCharsByLineId(pId).map { list -> list.distinctBy { it.charId }.map { it.toDomainModel() } }
    }


    val metrics: (ID) -> Flow<List<DomainMetrix>> = { pId ->
        database.metricDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val metricsByPrefixVersionIdActualityCharId: suspend (String, ID, Boolean, ID) -> List<DomainMetrix> = { prefix, versionId, actual, charId ->
        database.metricDao.getMetricsByPrefixVersionIdActualityCharId(prefix, versionId.toString(), if (actual) "1" else "0", charId.toString()).map { it.toDomainModel() }
    }
    val metricById: suspend (ID) -> DomainMetrix.DomainMetricWithParents = { database.metricDao.getRecordCompleteById(it).toDomainModel() }
    val metricByCharacteristicId: suspend (ID) -> List<DomainMetrix> = { database.metricDao.getRecordsByParentId(it).map { item -> item.toDomainModel() } }

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
    val productKindsByDepartmentId: (ID) -> Flow<List<DomainProductKind.DomainProductKindComplete>> = { depId ->
        database.productKindDao.getRecordsByDepartmentId(depId).map { list -> list.map { it.toDomainModel() } }
    }

    val productLineCharacteristics: (ID) -> Flow<List<DomainCharacteristic.DomainCharacteristicWithParents>> = { id ->
        database.characteristicDao.getAllCharacteristicsPerProductLine(id).map { list -> list.map { it.toDomainModel() } }
    }

    val itemKindCharsComplete: (String) -> Flow<List<DomainCharacteristicItemKind.DomainCharacteristicItemKindComplete>> = { id ->
        database.productKindDao.getItemKindCharacteristics(id).map { list -> list.map { it.toDomainModel() } }
    }

    val productBases: (ID) -> Flow<List<DomainProductBase>> = { pId ->
        database.productBaseDao.getRecordsByParentId(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val productKindKeys: (ID) -> Flow<List<DomainProductKindKey.DomainProductKindKeyComplete>> = { pId ->
        database.productKindKeyDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val productKeysBySubDepartmentId: (ID) -> Flow<List<DomainKey>> = { subDepId ->
        database.productKeyDao.getAllProductKeysBySubDepartmentId(subDepId).map { list -> list.map { it.toDomainModel() }.distinctBy { it.id } }
    }
    val productKindKeysByParent: suspend (ID) -> List<DomainProductKindKey.DomainProductKindKeyComplete> = { pId ->
        database.productKindKeyDao.getRecordsByParentId(pId).map { it.toDomainModel() }
    }
    val productKindProducts: (ID) -> Flow<List<DomainProductKindProduct.DomainProductKindProductComplete>> = { pId ->
        database.productKindProductDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val productKindProductById: suspend (ID, ID) -> DomainProductKindProduct.DomainProductKindProductComplete = { pkId, pId ->
        database.productKindProductDao.getRecordCompleteById(pkId, pId)?.toDomainModel() ?: DomainProductKindProduct.DomainProductKindProductComplete()
    }
    val allProductKindProducts: () -> Flow<List<DomainProductKindProduct.DomainProductKindProductComplete>> = {
        database.productKindProductDao.getAllRecordsComplete().map { list -> list.map { it.toDomainModel() } }
    }
    val productById: suspend (ID) -> DomainProduct.DomainProductComplete = { id ->
        database.productDao.getRecordById(id)?.toDomainModel() ?: DomainProduct.DomainProductComplete()
    }
    val productsItemsBySubDepartmentIdAndChannelId: (ID, ID) -> Flow<List<DomainProduct.DomainProductComplete>> = { subDepId, chId ->
        database.productDao.getRecordsBySubDepIdAndChannelId(subDepId, chId).map { list -> list.map { it.toDomainModel() } }
    }


    val componentKind: suspend (ID) -> DomainComponentKind.DomainComponentKindComplete = { id ->
        database.componentKindDao.getRecordCompleteById(id)?.toDomainModel() ?: DomainComponentKind.DomainComponentKindComplete()
    }
    val componentKinds: (ID) -> Flow<List<DomainComponentKind.DomainComponentKindComplete>> = { pId ->
        database.componentKindDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val componentKindsByDepartmentId: (ID) -> Flow<List<DomainComponentKind.DomainComponentKindComplete>> = { depId ->
        database.componentKindDao.getRecordsByDepartmentId(depId).map { list -> list.map { it.toDomainModel() } }
    }
    val componentKindKeysByParent: suspend (ID) -> List<DomainComponentKindKey.DomainComponentKindKeyComplete> = { pId ->
        database.componentKindKeyDao.getRecordsByParentId(pId).map { it.toDomainModel() }
    }
    val componentKindKeys: (ID) -> Flow<List<DomainComponentKindKey.DomainComponentKindKeyComplete>> = { pId ->
        database.componentKindKeyDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val componentKeysBySubDepartmentId: (ID) -> Flow<List<DomainKey>> = { subDepId ->
        database.productKeyDao.getAllComponentKeysBySubDepartmentId(subDepId).map { list -> list.map { it.toDomainModel() }.distinctBy { it.id } }
    }
    val components: (ID, ID) -> Flow<List<DomainProductComponent.DomainProductComponentComplete>> = { pId, ckId ->
        database.productComponentDao.getRecordsCompleteForUI(pId, ckId).map { list -> list.map { it.toDomainModel() } }
    }
    val productComponentById: suspend (ID, ID, ID) -> DomainProductComponent.DomainProductComponentComplete = { productId, compKindId, componentId ->
        database.productComponentDao.getRecordCompleteById(productId, compKindId, componentId)?.toDomainModel() ?: DomainProductComponent.DomainProductComponentComplete()
    }
    val allProductComponents: () -> Flow<List<DomainProductComponent.DomainProductComponentComplete>> = {
        database.productComponentDao.getAllRecordsComplete().map { list -> list.map { it.toDomainModel() } }
    }
    val componentById: suspend (ID) -> DomainComponent.DomainComponentComplete = { id ->
        database.componentDao.getRecordById(id)?.toDomainModel() ?: DomainComponent.DomainComponentComplete()
    }
    val componentsItemsBySubDepartmentIdAndChannelId: (ID, ID) -> Flow<List<DomainComponent.DomainComponentComplete>> = { subDepId, chId ->
        database.componentDao.getRecordsBySubDepIdAndChannelId(subDepId, chId).map { list -> list.map { it.toDomainModel() } }
    }


    val componentStageKind: suspend (ID) -> DomainComponentStageKind.DomainComponentStageKindComplete = { id ->
        database.componentStageKindDao.getRecordCompleteById(id)?.toDomainModel() ?: DomainComponentStageKind.DomainComponentStageKindComplete()
    }
    val componentStageKinds: (ID) -> Flow<List<DomainComponentStageKind.DomainComponentStageKindComplete>> = { pId ->
        database.componentStageKindDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val stageKindsByDepartmentId: (ID) -> Flow<List<DomainComponentStageKind.DomainComponentStageKindComplete>> = { depId ->
        database.componentStageKindDao.getRecordsByDepartmentId(depId).map { list -> list.map { it.toDomainModel() } }
    }
    val componentStageKindKeysByParent: suspend (ID) -> List<DomainComponentStageKindKey.DomainComponentStageKindKeyComplete> = { pId ->
        database.componentStageKindKeyDao.getRecordsByParentId(pId).map { it.toDomainModel() }
    }
    val componentStageKindKeys: (ID) -> Flow<List<DomainComponentStageKindKey.DomainComponentStageKindKeyComplete>> = { pId ->
        database.componentStageKindKeyDao.getRecordsCompleteForUI(pId).map { list -> list.map { it.toDomainModel() } }
    }
    val stageKeysBySubDepartmentId: (ID) -> Flow<List<DomainKey>> = { subDepId ->
        database.productKeyDao.getAllStageKeysBySubDepartmentId(subDepId).map { list -> list.map { it.toDomainModel() }.distinctBy { it.id } }
    }
    val componentStages: (ID, ID) -> Flow<List<DomainComponentComponentStage.DomainComponentComponentStageComplete>> = { cId, cskId ->
        database.componentComponentStageDao.getRecordsCompleteForUI(cId, cskId).map { list -> list.map { it.toDomainModel() } }
    }
    val componentComponentStageById: suspend (ID, ID, ID) -> DomainComponentComponentStage.DomainComponentComponentStageComplete = { componentId, compStageKindId, compStageId ->
        database.componentComponentStageDao.getRecordCompleteById(componentId, compStageKindId, compStageId)?.toDomainModel() ?: DomainComponentComponentStage.DomainComponentComponentStageComplete()
    }
    val allComponentComponentStages: () -> Flow<List<DomainComponentComponentStage.DomainComponentComponentStageComplete>> = {
        database.componentComponentStageDao.getAllRecordsComplete().map { list -> list.map { it.toDomainModel() } }
    }
    val stageItemsBySubDepartmentIdAndChannelId: (ID, ID) -> Flow<List<DomainComponentStage.DomainComponentStageComplete>> = { subDepId, chId ->
        database.componentStageDao.getRecordsBySubDepIdAndChannelId(subDepId, chId).map { list -> list.map { it.toDomainModel() } }
    }


    val itemVersionsComplete: (String) -> Flow<List<DomainItemVersionComplete>> = { fpId ->
        database.productVersionDao.getRecordsCompleteForUI(fpId).map { list -> list.map { it.toDomainModel() } }
    }
    val itemComplete: suspend (String) -> DomainItemComplete = { fId ->
        database.productVersionDao.getParentRecordCompleteForUI(fId)?.toDomainModel() ?: DomainItemComplete()
    }
    val itemVersionComplete: suspend (String) -> DomainItemVersionComplete = { fId ->
        database.productVersionDao.getRecordCompleteForUI(fId)?.toDomainModel() ?: DomainItemVersionComplete()
    }
    val versionTolerancesComplete: suspend (String) -> List<DomainItemTolerance.DomainItemToleranceComplete> = { versionFId ->
        database.productVersionDao.getItemVersionTolerancesComplete(versionFId).map { it.toDomainModel() }
    }

    val versionStatuses = database.versionStatusDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }
}