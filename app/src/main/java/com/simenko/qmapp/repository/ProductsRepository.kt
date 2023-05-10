package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.ProductsService
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.ProductsDao
import com.simenko.qmapp.utils.ListTransformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "ProductsRepository"

class ProductsRepository @Inject constructor(
    private val productsDao: ProductsDao,
    private val productsService: ProductsService
) {
    /**
     * Update Products from the network
     */

    suspend fun refreshElementIshModels() {
        withContext(Dispatchers.IO) {
            val elementIshModels =
                productsService.getElementIshModels()
            productsDao.insertElementIshModelsAll(
                ListTransformer(
                    elementIshModels,
                    NetworkElementIshModel::class, DatabaseElementIshModel::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshElementIshModels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshIshSubCharacteristics() {
        withContext(Dispatchers.IO) {
            val ishSubCharacteristics =
                productsService.getIshSubCharacteristics()
            productsDao.insertIshSubCharacteristicsAll(
                ListTransformer(
                    ishSubCharacteristics,
                    NetworkIshSubCharacteristic::class, DatabaseIshSubCharacteristic::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshIshSubCharacteristics: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshManufacturingProjects() {
        withContext(Dispatchers.IO) {
            val manufacturingProjects =
                productsService.getManufacturingProjects()
            productsDao.insertManufacturingProjectsAll(
                ListTransformer(
                    manufacturingProjects,
                    NetworkManufacturingProject::class, DatabaseManufacturingProject::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshManufacturingProjects: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshCharacteristics() {
        withContext(Dispatchers.IO) {
            val characteristics =
                productsService.getCharacteristics()
            productsDao.insertCharacteristicsAll(
                ListTransformer(
                    characteristics, NetworkCharacteristic::class,
                    DatabaseCharacteristic::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshCharacteristics: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshMetrixes() {
        withContext(Dispatchers.IO) {
            val metrixes = productsService.getMetrixes()
            productsDao.insertMetrixesAll(
                ListTransformer(
                    metrixes,
                    NetworkMetrix::class,
                    DatabaseMetrix::class
                ).generateList()
            )
            Log.d(TAG, "refreshMetrixes: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshKeys() {
        withContext(Dispatchers.IO) {
            val list = productsService.getKeys()
            productsDao.insertKeysAll(
                ListTransformer(
                    list,
                    NetworkKey::class,
                    DatabaseKey::class
                ).generateList()
            )
            Log.d(TAG, "refreshKeys: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshProductBases() {
        withContext(Dispatchers.IO) {
            val list = productsService.getProductBases()
            productsDao.insertProductBasesAll(
                ListTransformer(
                    list,
                    NetworkProductBase::class,
                    DatabaseProductBase::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshProductBases: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshProducts() {
        withContext(Dispatchers.IO) {
            val list = productsService.getProducts()
            productsDao.insertProductsAll(
                ListTransformer(
                    list,
                    NetworkProduct::class,
                    DatabaseProduct::class
                ).generateList()
            )
            Log.d(TAG, "refreshProducts: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponents() {
        withContext(Dispatchers.IO) {
            val list = productsService.getComponents()
            productsDao.insertComponentsAll(
                ListTransformer(
                    list,
                    NetworkComponent::class,
                    DatabaseComponent::class
                ).generateList()
            )
            Log.d(TAG, "refreshComponents: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponentInStages() {
        withContext(Dispatchers.IO) {
            val list = productsService.getComponentInStages()
            productsDao.insertComponentInStagesAll(
                ListTransformer(
                    list,
                    NetworkComponentInStage::class,
                    DatabaseComponentInStage::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshComponentInStages: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshVersionStatuses() {
        withContext(Dispatchers.IO) {
            val list = productsService.getVersionStatuses()
            productsDao.insertVersionStatusesAll(
                ListTransformer(
                    list,
                    NetworkVersionStatus::class,
                    DatabaseVersionStatus::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshVersionStatuses: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshProductVersions() {
        withContext(Dispatchers.IO) {
            val list = productsService.getProductVersions()
            productsDao.insertProductVersionsAll(
                ListTransformer(
                    list,
                    NetworkProductVersion::class,
                    DatabaseProductVersion::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshProductVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshComponentVersions() {
        withContext(Dispatchers.IO) {
            val list = productsService.getComponentVersions()
            productsDao.insertComponentVersionsAll(
                ListTransformer(
                    list,
                    NetworkComponentVersion::class,
                    DatabaseComponentVersion::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshComponentVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshComponentInStageVersions() {
        withContext(Dispatchers.IO) {
            val list = productsService.getComponentInStageVersions()
            productsDao.insertComponentInStageVersionsAll(
                ListTransformer(
                    list,
                    NetworkComponentInStageVersion::class,
                    DatabaseComponentInStageVersion::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshComponentInStageVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshProductTolerances() {
        withContext(Dispatchers.IO) {
            val list = productsService.getProductTolerances()
            productsDao.insertProductTolerancesAll(
                ListTransformer(
                    list,
                    NetworkProductTolerance::class,
                    DatabaseProductTolerance::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshProductTolerances: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshComponentTolerances() {
        withContext(Dispatchers.IO) {
            val list = productsService.getComponentTolerances()
            productsDao.insertComponentTolerancesAll(
                ListTransformer(
                    list,
                    NetworkComponentTolerance::class,
                    DatabaseComponentTolerance::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshComponentTolerances: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshComponentInStageTolerances() {
        withContext(Dispatchers.IO) {
            val list =
                productsService.getComponentInStageTolerances()
            productsDao.insertComponentInStageTolerancesAll(
                ListTransformer(
                    list,
                    NetworkComponentInStageTolerance::class,
                    DatabaseComponentInStageTolerance::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshComponentInStageTolerances: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshProductsToLines() {
        withContext(Dispatchers.IO) {
            val list = productsService.getProductsToLines()
            productsDao.insertProductsToLinesAll(
                ListTransformer(
                    list,
                    NetworkProductToLine::class,
                    DatabaseProductToLine::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshProductsToLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshComponentsToLines() {
        withContext(Dispatchers.IO) {
            val list = productsService.getComponentsToLines()
            productsDao.insertComponentsToLinesAll(
                ListTransformer(
                    list,
                    NetworkComponentToLine::class,
                    DatabaseComponentToLine::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshComponentsToLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshComponentInStagesToLines() {
        withContext(Dispatchers.IO) {
            val list = productsService.getComponentInStagesToLines()
            productsDao.insertComponentInStagesToLinesAll(
                ListTransformer(
                    list,
                    NetworkComponentInStageToLine::class,
                    DatabaseComponentInStageToLine::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshComponentInStagesToLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    val characteristics: LiveData<List<DomainCharacteristic>> =
        productsDao.getCharacteristics().map {
            ListTransformer(
                it,
                DatabaseCharacteristic::class,
                DomainCharacteristic::class
            ).generateList()
        }

    val metrixes: LiveData<List<DomainMetrix>> =
        productsDao.getMetrixes().map {
            ListTransformer(
                it,
                DatabaseMetrix::class,
                DomainMetrix::class
            ).generateList()
        }

    suspend fun getMetricsByPrefixVersionIdActualityCharId(
        prefix: String,
        versionId: Int,
        actual: Boolean,
        charId: Int
    ): List<DomainMetrix> {
        val list = productsDao.getMetricsByPrefixVersionIdActualityCharId(
            prefix, versionId.toString(), if (actual) "1" else "0", charId.toString()
        )
        return ListTransformer(
            list,
            DatabaseMetrix::class, DomainMetrix::class
        ).generateList()
    }

    fun metrixes(): Flow<List<DomainMetrix>> =
        productsDao.getMetrixesFlow().map {
            ListTransformer(
                it,
                DatabaseMetrix::class,
                DomainMetrix::class
            ).generateList()
        }

    val keys: LiveData<List<DomainKey>> =
        productsDao.getKeys().map {
            ListTransformer(it, DatabaseKey::class, DomainKey::class).generateList()
        }
    val productBases: LiveData<List<DomainProductBase>> =
        productsDao.getProductBases().map {
            ListTransformer(it, DatabaseProductBase::class, DomainProductBase::class).generateList()
        }
    val products: LiveData<List<DomainProduct>> =
        productsDao.getProducts().map {
            ListTransformer(it, DatabaseProduct::class, DomainProduct::class).generateList()
        }
    val components: LiveData<List<DomainComponent>> =
        productsDao.getComponents().map {
            ListTransformer(it, DatabaseComponent::class, DomainComponent::class).generateList()
        }
    val componentInStages: LiveData<List<DomainComponentInStage>> =
        productsDao.getComponentInStages().map {
            ListTransformer(
                it,
                DatabaseComponentInStage::class,
                DomainComponentInStage::class
            ).generateList()
        }
    val versionStatuses: LiveData<List<DomainVersionStatus>> =
        productsDao.getVersionStatuses().map {
            ListTransformer(
                it,
                DatabaseVersionStatus::class,
                DomainVersionStatus::class
            ).generateList()
        }
    val productVersions: LiveData<List<DomainProductVersion>> =
        productsDao.getProductVersions().map {
            ListTransformer(
                it,
                DatabaseProductVersion::class,
                DomainProductVersion::class
            ).generateList()
        }
    val componentVersions: LiveData<List<DomainComponentVersion>> =
        productsDao.getComponentVersions().map {
            ListTransformer(
                it,
                DatabaseComponentVersion::class,
                DomainComponentVersion::class
            ).generateList()
        }
    val componentInStageVersions: LiveData<List<DomainComponentInStageVersion>> =
        productsDao.getComponentInStageVersions().map {
            ListTransformer(
                it,
                DatabaseComponentInStageVersion::class,
                DomainComponentInStageVersion::class
            ).generateList()
        }
    val productTolerances: LiveData<List<DomainProductTolerance>> =
        productsDao.getProductTolerances().map {
            ListTransformer(
                it,
                DatabaseProductTolerance::class,
                DomainProductTolerance::class
            ).generateList()
        }

    fun productTolerances(): Flow<List<DomainProductTolerance>> =
        productsDao.getProductTolerancesFlow().map {
            ListTransformer(
                it,
                DatabaseProductTolerance::class,
                DomainProductTolerance::class
            ).generateList()
        }

    val componentTolerances: LiveData<List<DomainComponentTolerance>> =
        productsDao.getComponentTolerances().map {
            ListTransformer(
                it,
                DatabaseComponentTolerance::class,
                DomainComponentTolerance::class
            ).generateList()
        }

    fun componentTolerances(): Flow<List<DomainComponentTolerance>> =
        productsDao.getComponentTolerancesFlow().map {
            ListTransformer(
                it,
                DatabaseComponentTolerance::class,
                DomainComponentTolerance::class
            ).generateList()
        }

    val componentInStageTolerances: LiveData<List<DomainComponentInStageTolerance>> =
        productsDao.getComponentInStageTolerances().map {
            ListTransformer(
                it,
                DatabaseComponentInStageTolerance::class,
                DomainComponentInStageTolerance::class
            ).generateList()
        }

    fun componentInStageTolerances(): Flow<List<DomainComponentInStageTolerance>> =
        productsDao.getComponentInStageTolerancesFlow().map {
            ListTransformer(
                it,
                DatabaseComponentInStageTolerance::class,
                DomainComponentInStageTolerance::class
            ).generateList()
        }

    val productsToLines: LiveData<List<DomainProductToLine>> =
        productsDao.getProductsToLines().map {
            ListTransformer(
                it,
                DatabaseProductToLine::class,
                DomainProductToLine::class
            ).generateList()
        }
    val componentsToLines: LiveData<List<DomainComponentToLine>> =
        productsDao.getComponentsToLines().map {
            ListTransformer(
                it,
                DatabaseComponentToLine::class,
                DomainComponentToLine::class
            ).generateList()
        }
    val componentInStagesToLines: LiveData<List<DomainComponentInStageToLine>> =
        productsDao.getComponentInStagesToLines().map {
            ListTransformer(
                it,
                DatabaseComponentInStageToLine::class,
                DomainComponentInStageToLine::class
            ).generateList()
        }
    val itemVersionsComplete: LiveData<List<DomainItemVersionComplete>> =
        productsDao.getItemVersionsComplete().map {
            it.asDomainItem()
        }

    val itemsTolerances: LiveData<List<DomainItemTolerance>> =
        productsDao.getItemsTolerances().map {
            ListTransformer(
                it,
                DatabaseItemTolerance::class,
                DomainItemTolerance::class
            ).generateList()
        }
}