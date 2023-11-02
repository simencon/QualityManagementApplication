package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.retrofit.implementation.ProductsService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.simenko.qmapp.room.implementation.dao.ProductsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ProductsRepository"

@Singleton
class ProductsRepository @Inject constructor(
    private val database: QualityManagementDB,

    private val productsDao: ProductsDao,
    private val productsService: ProductsService,
    private val userRepository: UserRepository
) {
    /**
     * Update Products from the network
     */

    suspend fun refreshElementIshModels() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val elementIshModels = productsService.getElementIshModels()
            productsDao.insertElementIshModelsAll(
                elementIshModels.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshElementIshModels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshIshSubCharacteristics() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val ishSubCharacteristics = productsService.getIshSubCharacteristics()
            productsDao.insertIshSubCharacteristicsAll(
                ishSubCharacteristics.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshIshSubCharacteristics: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshManufacturingProjects() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val manufacturingProjects = productsService.getManufacturingProjects()
            productsDao.insertManufacturingProjectsAll(
                manufacturingProjects.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshManufacturingProjects: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshCharacteristics() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val characteristics = productsService.getCharacteristics()
            productsDao.insertCharacteristicsAll(
                characteristics.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshCharacteristics: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshMetrixes() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val metrixes = productsService.getMetrixes()
            productsDao.insertMetrixesAll(
                metrixes.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshMetrixes: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshKeys() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getKeys()
            productsDao.insertKeysAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshKeys: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshProductBases() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getProductBases()
            productsDao.insertProductBasesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshProductBases: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshProducts() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getProducts()
            productsDao.insertProductsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshProducts: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponents() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getComponents()
            productsDao.insertComponentsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshComponents: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponentInStages() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getComponentInStages()
            productsDao.insertComponentInStagesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshComponentInStages: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshVersionStatuses() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getVersionStatuses()
            productsDao.insertVersionStatusesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshVersionStatuses: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshProductVersions() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getProductVersions()
            productsDao.insertProductVersionsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshProductVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponentVersions() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getComponentVersions()
            productsDao.insertComponentVersionsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshComponentVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponentInStageVersions() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getComponentInStageVersions()
            productsDao.insertComponentInStageVersionsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshComponentInStageVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshProductTolerances() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getProductTolerances()
            productsDao.insertProductTolerancesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshProductTolerances: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponentTolerances() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getComponentTolerances()
            productsDao.insertComponentTolerancesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshComponentTolerances: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponentInStageTolerances() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getComponentInStageTolerances()
            productsDao.insertComponentInStageTolerancesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshComponentInStageTolerances: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshProductsToLines() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getProductsToLines()
            productsDao.insertProductsToLinesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshProductsToLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponentsToLines() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getComponentsToLines()
            productsDao.insertComponentsToLinesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshComponentsToLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponentInStagesToLines() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = productsService.getComponentInStagesToLines()
            productsDao.insertComponentInStagesToLinesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshComponentInStagesToLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    val characteristics: Flow<List<DomainCharacteristic>> =
        productsDao.getCharacteristics().map { list ->
            list.map { it.toDomainModel() }
        }

    val metrixes: LiveData<List<DomainMetrix>> =
        productsDao.getMetrixes().map { list ->
            list.map { it.toDomainModel() }
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
        return list.map { it.toDomainModel() }
    }

    fun metrixes(): Flow<List<DomainMetrix>> =
        productsDao.getMetrixesFlow().map { list ->
            list.map { it.toDomainModel() }
        }

    val keys: LiveData<List<DomainKey>> =
        productsDao.getKeys().map { list ->
            list.map { it.toDomainModel() }
        }
    val productBases: LiveData<List<DomainProductBase>> =
        productsDao.getProductBases().map { list ->
            list.map { it.toDomainModel() }
        }
    val products: LiveData<List<DomainProduct>> =
        productsDao.getProducts().map { list ->
            list.map { it.toDomainModel() }
        }
    val components: LiveData<List<DomainComponent>> =
        productsDao.getComponents().map { list ->
            list.map { it.toDomainModel() }
        }
    val componentInStages: LiveData<List<DomainComponentInStage>> =
        productsDao.getComponentInStages().map { list ->
            list.map { it.toDomainModel() }
        }
    val versionStatuses: LiveData<List<DomainVersionStatus>> =
        productsDao.getVersionStatuses().map { list ->
            list.map { it.toDomainModel() }
        }
    val productVersions: LiveData<List<DomainProductVersion>> =
        productsDao.getProductVersions().map { list ->
            list.map { it.toDomainModel() }
        }
    val componentVersions: LiveData<List<DomainComponentVersion>> =
        productsDao.getComponentVersions().map { list ->
            list.map { it.toDomainModel() }
        }
    val componentInStageVersions: LiveData<List<DomainComponentInStageVersion>> =
        productsDao.getComponentInStageVersions().map { list ->
            list.map { it.toDomainModel() }
        }
    val productTolerances: LiveData<List<DomainProductTolerance>> =
        productsDao.getProductTolerances().map { list ->
            list.map { it.toDomainModel() }
        }

    fun productTolerances(): Flow<List<DomainProductTolerance>> =
        productsDao.getProductTolerancesFlow().map { list ->
            list.map { it.toDomainModel() }
        }

    val componentTolerances: LiveData<List<DomainComponentTolerance>> =
        productsDao.getComponentTolerances().map { list ->
            list.map { it.toDomainModel() }
        }

    fun componentTolerances(): Flow<List<DomainComponentTolerance>> =
        productsDao.getComponentTolerancesFlow().map { list ->
            list.map { it.toDomainModel() }
        }

    val componentInStageTolerances: LiveData<List<DomainComponentInStageTolerance>> =
        productsDao.getComponentInStageTolerances().map { list ->
            list.map { it.toDomainModel() }
        }

    fun componentInStageTolerances(): Flow<List<DomainComponentInStageTolerance>> =
        productsDao.getComponentInStageTolerancesFlow().map { list ->
            list.map { it.toDomainModel() }
        }

    val productsToLines: LiveData<List<DomainProductToLine>> =
        productsDao.getProductsToLines().map { list ->
            list.map { it.toDomainModel() }
        }
    val componentsToLines: LiveData<List<DomainComponentToLine>> =
        productsDao.getComponentsToLines().map { list ->
            list.map { it.toDomainModel() }
        }
    val componentInStagesToLines: LiveData<List<DomainComponentInStageToLine>> =
        productsDao.getComponentInStagesToLines().map { list ->
            list.map { it.toDomainModel() }
        }
    val itemVersionsComplete: Flow<List<DomainItemVersionComplete>> =
        productsDao.getItemVersionsComplete().map { list ->
            list.map { it.toDomainModel() }
        }

    val itemsTolerances: LiveData<List<DomainItemTolerance>> =
        productsDao.getItemsTolerances().map { list ->
            list.map { it.toDomainModel() }
        }
}