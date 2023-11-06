package com.simenko.qmapp.repository

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.ProductsService
import com.simenko.qmapp.room.implementation.QualityManagementDB
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
    suspend fun syncManufacturingProjects() = crudeOperations.syncRecordsAll(database.manufacturingProjectDao) { service.getManufacturingProjects() }
    suspend fun syncProductKeys() = crudeOperations.syncRecordsAll(database.productKeyDao) { service.getKeys() }
    suspend fun syncProductBases() = crudeOperations.syncRecordsAll(database.productBaseDao) { service.getProductBases() }
    suspend fun syncCharacteristicGroups() = crudeOperations.syncRecordsAll(database.characteristicGroupDao) { service.getCharacteristicGroups() }
    suspend fun syncCharacteristicSubGroups() = crudeOperations.syncRecordsAll(database.characteristicSubGroupDao) { service.getCharacteristicSubGroups() }
    suspend fun syncCharacteristics() = crudeOperations.syncRecordsAll(database.characteristicDao) { service.getCharacteristics() }
    suspend fun syncMetrics() = crudeOperations.syncRecordsAll(database.metricDao) { service.getMetrics() }
    suspend fun syncVersionStatuses() = crudeOperations.syncRecordsAll(database.versionStatusDao) { service.getVersionStatuses() }

    suspend fun syncProductKinds() = crudeOperations.syncRecordsAll(database.productKindDao) { service.getProductKinds() }
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


    val metricsByPrefixVersionIdActualityCharId: suspend (String, Int, Boolean, Int) -> List<DomainMetrix> = { prefix, versionId, actual, charId ->
        database.metricDao.getMetricsByPrefixVersionIdActualityCharId(prefix, versionId.toString(), if (actual) "1" else "0", charId.toString()).map { it.toDomainModel() }
    }

    val itemVersionsComplete: Flow<List<DomainItemVersionComplete>> = database.productVersionDao.getItemVersionsComplete().map { list -> list.map { it.toDomainModel() } }
    val characteristics = database.characteristicDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }
}