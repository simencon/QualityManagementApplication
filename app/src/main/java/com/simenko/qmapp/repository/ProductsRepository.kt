package com.simenko.qmapp.repository

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.ProductsService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.simenko.qmapp.room.implementation.dao.ProductsDao
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ProductsRepository"

@Singleton
class ProductsRepository @Inject constructor(
    private val database: QualityManagementDB,
    private val crudeOperations: CrudeOperations,


    private val productsDao: ProductsDao,
    private val service: ProductsService,
    private val userRepository: UserRepository
) {
    /**
     * Update Products from the network
     */
    suspend fun syncCharacteristicGroups() = crudeOperations.syncRecordsAll(database.characteristicGroupDao) { service.getCharacteristicGroups() }
    suspend fun syncCharacteristicSubGroups() = crudeOperations.syncRecordsAll(database.characteristicSubGroupDao) { service.getCharacteristicSubGroups() }
    suspend fun syncManufacturingProjects() = crudeOperations.syncRecordsAll(database.manufacturingProjectDao) { service.getManufacturingProjects() }
    suspend fun syncCharacteristics() = crudeOperations.syncRecordsAll(database.characteristicDao) { service.getCharacteristics() }
    suspend fun syncMetrics() = crudeOperations.syncRecordsAll(database.metricDao) { service.getMetrics() }
    suspend fun syncProductKeys() = crudeOperations.syncRecordsAll(database.productKeyDao) { service.getKeys() }
    suspend fun syncProductBases() = crudeOperations.syncRecordsAll(database.productBaseDao) { service.getProductBases() }
    suspend fun syncProducts() = crudeOperations.syncRecordsAll(database.productDao) { service.getProducts() }
    suspend fun syncComponents() = crudeOperations.syncRecordsAll(database.componentDao) { service.getComponents() }
    suspend fun syncComponentStages() = crudeOperations.syncRecordsAll(database.componentStageDao) { service.getComponentStages() }
    suspend fun syncVersionStatuses() = crudeOperations.syncRecordsAll(database.versionStatusDao) { service.getVersionStatuses() }
    suspend fun syncProductVersions() = crudeOperations.syncRecordsAll(database.productVersionDao) { service.getProductVersions() }
    suspend fun syncComponentVersions() = crudeOperations.syncRecordsAll(database.componentVersionDao) { service.getComponentVersions() }
    suspend fun syncComponentStageVersions() = crudeOperations.syncRecordsAll(database.componentStageVersionDao) { service.getComponentStageVersions() }
    suspend fun syncProductTolerances() = crudeOperations.syncRecordsAll(database.productToleranceDao) { service.getProductTolerances() }
    suspend fun syncComponentTolerances() = crudeOperations.syncRecordsAll(database.componentToleranceDao) { service.getComponentTolerances() }
    suspend fun syncComponentStageTolerances() = crudeOperations.syncRecordsAll(database.componentStageToleranceDao) { service.getComponentStageTolerances() }
    suspend fun syncProductsToLines() = crudeOperations.syncRecordsAll(database.productToLineDao) { service.getProductsToLines() }
    suspend fun syncComponentsToLines() = crudeOperations.syncRecordsAll(database.componentToLineDao) { service.getComponentsToLines() }
    suspend fun syncComponentStagesToLines() = crudeOperations.syncRecordsAll(database.componentStageToLineDao) { service.getComponentStagesToLines() }

    suspend fun getMetricsByPrefixVersionIdActualityCharId(
        prefix: String,
        versionId: Int,
        actual: Boolean,
        charId: Int
    ): List<DomainMetrix> {
        val list = productsDao.getMetricsByPrefixVersionIdActualityCharId(
            prefix, versionId.toString(), if (actual) "1" else "0", charId.toString()
        )
        return list.map { it.toDomainModel() }
    }

    val characteristics = database.characteristicDao.getRecordsForUI().asFlow().map { list -> list.map { it.toDomainModel() } }
    val products = database.productDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }
    val components = database.componentDao.getRecordsForUI().map { list -> list.map { it.toDomainModel() } }
}