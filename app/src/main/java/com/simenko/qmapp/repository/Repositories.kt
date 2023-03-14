package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.QualityManagementNetwork
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.simenko.qmapp.utils.ListTransformer
import kotlinx.coroutines.*
import java.time.Instant
import java.time.format.DateTimeFormatter

private const val TAG = "Repositories"

class QualityManagementManufacturingRepository(private val database: QualityManagementDB) {
    /**
     * Update Manufacturing from the network
     */
    suspend fun refreshPositionLevels() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceHolderManufacturing.getPositionLevels()
            database.qualityManagementManufacturingDao.insertPositionLevelsAll(
                ListTransformer(
                    list,
                    NetworkPositionLevel::class,
                    DatabasePositionLevel::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshPositionLevels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshTeamMembers() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceHolderManufacturing.getTeamMembers()
            database.qualityManagementManufacturingDao.insertTeamMembersAll(
                ListTransformer(
                    list,
                    NetworkTeamMembers::class,
                    DatabaseTeamMember::class
                ).generateList()
            )
            Log.d(TAG, "refreshTeamMembers: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshCompanies() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceHolderManufacturing.getCompanies()
            database.qualityManagementManufacturingDao.insertCompaniesAll(
                ListTransformer(
                    list,
                    NetworkCompany::class,
                    DatabaseCompany::class
                ).generateList()
            )
            Log.d(TAG, "refreshCompanies: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshDepartments() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceHolderManufacturing.getDepartments()
            database.qualityManagementManufacturingDao.insertDepartmentsAll(
                ListTransformer(
                    list,
                    NetworkDepartment::class,
                    DatabaseDepartment::class
                ).generateList()
            )
            Log.d(TAG, "refreshDepartments: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSubDepartments() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceHolderManufacturing.getSubDepartments()
            database.qualityManagementManufacturingDao.insertSubDepartmentsAll(
                ListTransformer(
                    list,
                    NetworkSubDepartment::class,
                    DatabaseSubDepartment::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshSubDepartments: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshManufacturingChannels() {
        withContext(Dispatchers.IO) {
            val list =
                QualityManagementNetwork.serviceHolderManufacturing.getManufacturingChannels()
            database.qualityManagementManufacturingDao.insertManufacturingChannelsAll(
                ListTransformer(
                    list,
                    NetworkManufacturingChannel::class,
                    DatabaseManufacturingChannel::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshManufacturingChannels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshManufacturingLines() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceHolderManufacturing.getManufacturingLines()
            database.qualityManagementManufacturingDao.insertManufacturingLinesAll(
                ListTransformer(
                    list,
                    NetworkManufacturingLine::class,
                    DatabaseManufacturingLine::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshManufacturingLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshManufacturingOperations() {
        withContext(Dispatchers.IO) {
            val list =
                QualityManagementNetwork.serviceHolderManufacturing.getManufacturingOperations()
            database.qualityManagementManufacturingDao.insertManufacturingOperationsAll(
                ListTransformer(
                    list,
                    NetworkManufacturingOperation::class,
                    DatabaseManufacturingOperation::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshManufacturingOperations: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshOperationsFlows() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceHolderManufacturing.getOperationsFlows()
            database.qualityManagementManufacturingDao.insertOperationsFlowsAll(
                ListTransformer(
                    list,
                    NetworkOperationsFlow::class,
                    DatabaseOperationsFlow::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshOperationsFlows: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    /**
     * Connecting with LiveData for ViewModel
     */
    val teamMembers: LiveData<List<DomainTeamMember>> =
        Transformations.map(database.qualityManagementManufacturingDao.getTeamMembers()) {
            ListTransformer(
                it,
                DatabaseTeamMember::class,
                DomainTeamMember::class
            ).generateList()
        }

    val departments: LiveData<List<DomainDepartment>> =
        Transformations.map(database.qualityManagementManufacturingDao.getDepartments()) {
            ListTransformer(
                it,
                DatabaseDepartment::class,
                DomainDepartment::class
            ).generateList()
        }

    val subDepartments: LiveData<List<DomainSubDepartment>> =
        Transformations.map(database.qualityManagementManufacturingDao.getSubDepartments()) {
            ListTransformer(
                it,
                DatabaseSubDepartment::class,
                DomainSubDepartment::class
            ).generateList()
        }

    val channels: LiveData<List<DomainManufacturingChannel>> =
        Transformations.map(database.qualityManagementManufacturingDao.getManufacturingChannels()) {
            ListTransformer(
                it,
                DatabaseManufacturingChannel::class,
                DomainManufacturingChannel::class
            ).generateList()
        }

    val lines: LiveData<List<DomainManufacturingLine>> =
        Transformations.map(database.qualityManagementManufacturingDao.getManufacturingLines()) {
            ListTransformer(
                it,
                DatabaseManufacturingLine::class,
                DomainManufacturingLine::class
            ).generateList()
        }

    val operations: LiveData<List<DomainManufacturingOperation>> =
        Transformations.map(database.qualityManagementManufacturingDao.getManufacturingOperations()) {
            ListTransformer(
                it,
                DatabaseManufacturingOperation::class,
                DomainManufacturingOperation::class
            ).generateList()
        }

    val operationsFlows: LiveData<List<DomainOperationsFlow>> =
        Transformations.map(database.qualityManagementManufacturingDao.getOperationsFlows()) {
            ListTransformer(
                it,
                DatabaseOperationsFlow::class,
                DomainOperationsFlow::class
            ).generateList()
        }

    val departmentsDetailed: LiveData<List<DomainDepartmentComplete>> =
        Transformations.map(database.qualityManagementManufacturingDao.getDepartmentsDetailed()) {
            it.asDepartmentsDetailedDomainModel()
        }
}

class QualityManagementProductsRepository(private val database: QualityManagementDB) {
    /**
     * Update Products from the network
     */

    suspend fun refreshElementIshModels() {
        withContext(Dispatchers.IO) {
            val elementIshModels =
                QualityManagementNetwork.serviceHolderProducts.getElementIshModels()
            database.qualityManagementProductsDao.insertElementIshModelsAll(
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
                QualityManagementNetwork.serviceHolderProducts.getIshSubCharacteristics()
            database.qualityManagementProductsDao.insertIshSubCharacteristicsAll(
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
                QualityManagementNetwork.serviceHolderProducts.getManufacturingProjects()
            database.qualityManagementProductsDao.insertManufacturingProjectsAll(
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
                QualityManagementNetwork.serviceHolderProducts.getCharacteristics()
            database.qualityManagementProductsDao.insertCharacteristicsAll(
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
            val metrixes = QualityManagementNetwork.serviceHolderProducts.getMetrixes()
            database.qualityManagementProductsDao.insertMetrixesAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getKeys()
            database.qualityManagementProductsDao.insertKeysAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getProductBases()
            database.qualityManagementProductsDao.insertProductBasesAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getProducts()
            database.qualityManagementProductsDao.insertProductsAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getComponents()
            database.qualityManagementProductsDao.insertComponentsAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getComponentInStages()
            database.qualityManagementProductsDao.insertComponentInStagesAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getVersionStatuses()
            database.qualityManagementProductsDao.insertVersionStatusesAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getProductVersions()
            database.qualityManagementProductsDao.insertProductVersionsAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getComponentVersions()
            database.qualityManagementProductsDao.insertComponentVersionsAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getComponentInStageVersions()
            database.qualityManagementProductsDao.insertComponentInStageVersionsAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getProductTolerances()
            database.qualityManagementProductsDao.insertProductTolerancesAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getComponentTolerances()
            database.qualityManagementProductsDao.insertComponentTolerancesAll(
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
                QualityManagementNetwork.serviceHolderProducts.getComponentInStageTolerances()
            database.qualityManagementProductsDao.insertComponentInStageTolerancesAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getProductsToLines()
            database.qualityManagementProductsDao.insertProductsToLinesAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getComponentsToLines()
            database.qualityManagementProductsDao.insertComponentsToLinesAll(
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
            val list = QualityManagementNetwork.serviceHolderProducts.getComponentInStagesToLines()
            database.qualityManagementProductsDao.insertComponentInStagesToLinesAll(
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
        Transformations.map(database.qualityManagementProductsDao.getCharacteristics()) {
            ListTransformer(
                it,
                DatabaseCharacteristic::class,
                DomainCharacteristic::class
            ).generateList()
        }

    val keys: LiveData<List<DomainKey>> =
        Transformations.map(database.qualityManagementProductsDao.getKeys()) {
            ListTransformer(it, DatabaseKey::class, DomainKey::class).generateList()
        }
    val productBases: LiveData<List<DomainProductBase>> =
        Transformations.map(database.qualityManagementProductsDao.getProductBases()) {
            ListTransformer(it, DatabaseProductBase::class, DomainProductBase::class).generateList()
        }
    val products: LiveData<List<DomainProduct>> =
        Transformations.map(database.qualityManagementProductsDao.getProducts()) {
            ListTransformer(it, DatabaseProduct::class, DomainProduct::class).generateList()
        }
    val components: LiveData<List<DomainComponent>> =
        Transformations.map(database.qualityManagementProductsDao.getComponents()) {
            ListTransformer(it, DatabaseComponent::class, DomainComponent::class).generateList()
        }
    val componentInStages: LiveData<List<DomainComponentInStage>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentInStages()) {
            ListTransformer(
                it,
                DatabaseComponentInStage::class,
                DomainComponentInStage::class
            ).generateList()
        }
    val versionStatuses: LiveData<List<DomainVersionStatus>> =
        Transformations.map(database.qualityManagementProductsDao.getVersionStatuses()) {
            ListTransformer(
                it,
                DatabaseVersionStatus::class,
                DomainVersionStatus::class
            ).generateList()
        }
    val productVersions: LiveData<List<DomainProductVersion>> =
        Transformations.map(database.qualityManagementProductsDao.getProductVersions()) {
            ListTransformer(
                it,
                DatabaseProductVersion::class,
                DomainProductVersion::class
            ).generateList()
        }
    val componentVersions: LiveData<List<DomainComponentVersion>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentVersions()) {
            ListTransformer(
                it,
                DatabaseComponentVersion::class,
                DomainComponentVersion::class
            ).generateList()
        }
    val componentInStageVersions: LiveData<List<DomainComponentInStageVersion>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentInStageVersions()) {
            ListTransformer(
                it,
                DatabaseComponentInStageVersion::class,
                DomainComponentInStageVersion::class
            ).generateList()
        }
    val productTolerances: LiveData<List<DomainProductTolerance>> =
        Transformations.map(database.qualityManagementProductsDao.getProductTolerances()) {
            ListTransformer(
                it,
                DatabaseProductTolerance::class,
                DomainProductTolerance::class
            ).generateList()
        }
    val componentTolerances: LiveData<List<DomainComponentTolerance>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentTolerances()) {
            ListTransformer(
                it,
                DatabaseComponentTolerance::class,
                DomainComponentTolerance::class
            ).generateList()
        }
    val componentInStageTolerances: LiveData<List<DomainComponentInStageTolerance>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentInStageTolerances()) {
            ListTransformer(
                it,
                DatabaseComponentInStageTolerance::class,
                DomainComponentInStageTolerance::class
            ).generateList()
        }
    val productsToLines: LiveData<List<DomainProductToLine>> =
        Transformations.map(database.qualityManagementProductsDao.getProductsToLines()) {
            ListTransformer(
                it,
                DatabaseProductToLine::class,
                DomainProductToLine::class
            ).generateList()
        }
    val componentsToLines: LiveData<List<DomainComponentToLine>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentsToLines()) {
            ListTransformer(
                it,
                DatabaseComponentToLine::class,
                DomainComponentToLine::class
            ).generateList()
        }
    val componentInStagesToLines: LiveData<List<DomainComponentInStageToLine>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentInStagesToLines()) {
            ListTransformer(
                it,
                DatabaseComponentInStageToLine::class,
                DomainComponentInStageToLine::class
            ).generateList()
        }


    val itemsVersionsCompleteP: LiveData<List<DomainItemVersionComplete>> =
        Transformations.map(database.qualityManagementProductsDao.getProductVersionsComplete()) {
            it.asDomainItemFromProduct()
        }
    val itemsVersionsCompleteC: LiveData<List<DomainItemVersionComplete>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentVersionsComplete()) {
            it.asDomainItemFromComponent()
        }
    val itemsVersionsCompleteS: LiveData<List<DomainItemVersionComplete>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentInStageVersionsComplete()) {
            it.asDomainItemFromStage()
        }

}

class QualityManagementInvestigationsRepository(private val database: QualityManagementDB) {
    /**
     * Update Investigations from the network
     */
    suspend fun refreshInputForOrder() {
        withContext(Dispatchers.IO) {
            val inputForOrder =
                QualityManagementNetwork.serviceHolderInvestigations.getInputForOrder()
            database.qualityManagementInvestigationsDao.insertInputForOrderAll(
                ListTransformer(
                    inputForOrder,
                    NetworkInputForOrder::class,
                    DatabaseInputForOrder::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshInputForOrder: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshOrdersStatuses() {
        withContext(Dispatchers.IO) {
            val ordersStatuses =
                QualityManagementNetwork.serviceHolderInvestigations.getOrdersStatuses()
            database.qualityManagementInvestigationsDao.insertOrdersStatusesAll(
                ListTransformer(
                    ordersStatuses,
                    NetworkOrdersStatus::class,
                    DatabaseOrdersStatus::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshOrdersStatuses: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshInvestigationReasons() {
        withContext(Dispatchers.IO) {
            val measurementReasons =
                QualityManagementNetwork.serviceHolderInvestigations.getMeasurementReasons()
            database.qualityManagementInvestigationsDao.insertMeasurementReasonsAll(
                ListTransformer(
                    measurementReasons,
                    NetworkMeasurementReason::class, DatabaseMeasurementReason::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshMeasurementReasons: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshInvestigationTypes() {
        withContext(Dispatchers.IO) {
            val ordersTypes = QualityManagementNetwork.serviceHolderInvestigations.getOrdersTypes()
            database.qualityManagementInvestigationsDao.insertOrdersTypesAll(
                ListTransformer(
                    ordersTypes,
                    NetworkOrdersType::class,
                    DatabaseOrdersType::class
                ).generateList()
            )
            Log.d(TAG, "refreshOrdersTypes: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshOrders() {
        withContext(Dispatchers.IO) {
            val ntOrders = QualityManagementNetwork.serviceHolderInvestigations.getOrders()
            val dbOrders = database.qualityManagementInvestigationsDao.getOrdersByList()

            syncOrders(dbOrders, ntOrders, database)
            Log.d(TAG, "refreshOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun deleteOrder(order: DomainOrder) {
        withContext(Dispatchers.IO) {
            QualityManagementNetwork.serviceHolderInvestigations.deleteOrder(order.id)
        }
    }

    suspend fun deleteSubOrder(subOrder: DomainSubOrder) {
        withContext(Dispatchers.IO) {
            QualityManagementNetwork.serviceHolderInvestigations.deleteSubOrder(subOrder.id)
        }
    }

    suspend fun deleteSubOrderTask(subOrderTask: DomainSubOrderTask) {
        withContext(Dispatchers.IO) {
            QualityManagementNetwork.serviceHolderInvestigations.deleteSubOrderTask(
                subOrderTask.subOrderId,
                subOrderTask.charId
            )
        }
    }

    suspend fun deleteSample(sample: DomainSample) {
        withContext(Dispatchers.IO) {
            QualityManagementNetwork.serviceHolderInvestigations.deleteSample(sample.id)
        }
    }

    suspend fun refreshSubOrders() {
        withContext(Dispatchers.IO) {
            val ntSubOrder = QualityManagementNetwork.serviceHolderInvestigations.getSubOrders()
            val dbSubOrders = database.qualityManagementInvestigationsDao.getSubOrdersByList()

            syncSubOrders(dbSubOrders, ntSubOrder, database)

            Log.d(TAG, "refreshSubOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSubOrderTasks() {
        withContext(Dispatchers.IO) {
            val ntSubOrderTasks = QualityManagementNetwork.serviceHolderInvestigations.getSubOrderTasks()
            val dbSubOrderTasks = database.qualityManagementInvestigationsDao.getSubOrderTasksByList()

            syncSubOrderTasks(dbSubOrderTasks, ntSubOrderTasks, database)

            Log.d(TAG,"refreshSubOrderTasks: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSamples() {
        withContext(Dispatchers.IO) {
            val ntSamples = QualityManagementNetwork.serviceHolderInvestigations.getSamples()
            val dbSamples = database.qualityManagementInvestigationsDao.getSamplesByList()

            syncSamples(dbSamples, ntSamples, database)

            Log.d(TAG, "refreshSamples: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshResultsDecryptions() {
        withContext(Dispatchers.IO) {
            val resultsDecryptions =
                QualityManagementNetwork.serviceHolderInvestigations.getResultsDecryptions()
            database.qualityManagementInvestigationsDao.insertResultsDecryptionsAll(
                ListTransformer(
                    resultsDecryptions,
                    NetworkResultsDecryption::class, DatabaseResultsDecryption::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshResultsDecryptions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshResults() {
        withContext(Dispatchers.IO) {
            val results = QualityManagementNetwork.serviceHolderInvestigations.getResults()
            database.qualityManagementInvestigationsDao.insertResultsAll(
                ListTransformer(results, NetworkResult::class, DatabaseResult::class).generateList()
            )
            Log.d(TAG, "refreshResults: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }


    val inputForOrder: LiveData<List<DomainInputForOrder>> =
        Transformations.map(database.qualityManagementInvestigationsDao.getInputForOrder()) {
            ListTransformer(
                it,
                DatabaseInputForOrder::class,
                DomainInputForOrder::class
            ).generateList().sortedBy { item -> item.depOrder }
        }

    val investigationTypes: LiveData<List<DomainOrdersType>> =
        Transformations.map(database.qualityManagementInvestigationsDao.getOrdersTypes()) {
            ListTransformer(it, DatabaseOrdersType::class, DomainOrdersType::class).generateList()
        }

    val investigationReasons: LiveData<List<DomainMeasurementReason>> =
        Transformations.map(database.qualityManagementInvestigationsDao.getMeasurementReasons()) {
            ListTransformer(
                it,
                DatabaseMeasurementReason::class,
                DomainMeasurementReason::class
            ).generateList()
        }

    val orders: LiveData<List<DomainOrder>> =
        Transformations.map(database.qualityManagementInvestigationsDao.getOrders()) {
            ListTransformer(
                it,
                DatabaseOrder::class,
                DomainOrder::class
            ).generateList()
        }

    val completeOrders: LiveData<List<DomainOrderComplete>> =
        Transformations.map(database.qualityManagementInvestigationsDao.getOrdersDetailed()) {
            it.asDomainOrdersComplete(-1)
        }

    val completeSubOrders: LiveData<List<DomainSubOrderComplete>> =
        Transformations.map(database.qualityManagementInvestigationsDao.getSubOrdersDetailed()) {
            it.asDomainSubOrderDetailed(-1)
        }

    val completeSubOrderTasks: LiveData<List<DomainSubOrderTaskComplete>> =
        Transformations.map(database.qualityManagementInvestigationsDao.getSubOrderTasksDetailed()) {
            it.asDomainSubOrderTask(-1)
        }


    val subOrdersWithChildren: LiveData<List<DomainSubOrderWithChildren>> =
        Transformations.map(database.qualityManagementInvestigationsDao.getSubOrderWithChildren()) {
            it.toDomainSubOrderWithChildren()
        }

}

fun syncOrders(
    dbOrders: List<DatabaseOrder>,
    ntOrders: List<NetworkOrder>,
    database: QualityManagementDB
) {
    ntOrders.forEach byBlock1@{ ntIt ->
        var recordExists = false
        dbOrders.forEach byBlock2@{ dbIt ->
            if (ntIt.id == dbIt.id) {
                recordExists = true
                return@byBlock2
            }
        }
        if (!recordExists) {
            database.qualityManagementInvestigationsDao.insertOrder(ntIt.toDatabaseOrder())
            Log.d(TAG, "syncOrders: Order has been inserted / id = ${ntIt.id}")
        }
    }
    ntOrders.forEach byBlock1@{ ntIt ->
        var recordStatusChanged = false
        dbOrders.forEach byBlock2@{ dbIt ->
            if (ntIt.id == dbIt.id) {
                if (ntIt.statusId != dbIt.statusId)
                    recordStatusChanged = true
                return@byBlock2
            }
        }
        if (recordStatusChanged) {
            database.qualityManagementInvestigationsDao.updateOrder(ntIt.toDatabaseOrder())
            Log.d(TAG, "syncOrders: Order status has been changed / id = ${ntIt.id}")
        }
    }
    dbOrders.forEach byBlock1@{ dbIt ->
        var recordExists = false
        ntOrders.forEach byBlock2@{ ntIt ->
            if (ntIt.id == dbIt.id) {
                recordExists = true
                return@byBlock2
            }
        }
        if (!recordExists) {
            database.qualityManagementInvestigationsDao.deleteOrder(dbIt)
            Log.d(TAG, "syncOrders: Order deleted from SQLite / id = ${dbIt.id}")
        }
    }
}

fun syncSubOrders(
    dbSubOrders: List<DatabaseSubOrder>,
    ntSubOrders: List<NetworkSubOrder>,
    database: QualityManagementDB
) {
    ntSubOrders.forEach byBlock1@{ ntIt ->
        var recordExists = false
        dbSubOrders.forEach byBlock2@{ dbIt ->
            if (ntIt.id == dbIt.id) {
                recordExists = true
                return@byBlock2
            }
        }
        if (!recordExists) {
            database.qualityManagementInvestigationsDao.insertSubOrder(ntIt.toDatabaseSubOrder())
            Log.d(TAG, "syncSubOrders: Sub order has been inserted / id = ${ntIt.id}")
        }
    }
    ntSubOrders.forEach byBlock1@{ ntIt ->
        var recordStatusChanged = false
        dbSubOrders.forEach byBlock2@{ dbIt ->
            if (ntIt.id == dbIt.id) {
                if (ntIt.statusId != dbIt.statusId)
                    recordStatusChanged = true
                return@byBlock2
            }
        }
        if (recordStatusChanged) {
            database.qualityManagementInvestigationsDao.updateSubOrder(ntIt.toDatabaseSubOrder())
            Log.d(TAG, "syncSubOrders: Sub order status has been changed / id = ${ntIt.id}")
        }
    }
    dbSubOrders.forEach byBlock1@{ dbIt ->
        var recordExists = false
        ntSubOrders.forEach byBlock2@{ ntIt ->
            if (ntIt.id == dbIt.id) {
                recordExists = true
                return@byBlock2
            }
        }
        if (!recordExists) {
            database.qualityManagementInvestigationsDao.deleteSubOrder(dbIt)
            Log.d(TAG, "syncSubOrders: Sub order deleted from SQLite / id = ${dbIt.id}")
        }
    }
}

fun syncSubOrderTasks(
    dbSubOrderTasks: List<DatabaseSubOrderTask>,
    ntSubOrderTasks: List<NetworkSubOrderTask>,
    database: QualityManagementDB
) {
    ntSubOrderTasks.forEach byBlock1@{ ntIt ->
        var recordExists = false
        dbSubOrderTasks.forEach byBlock2@{ dbIt ->
            if (ntIt.id == dbIt.id) {
                recordExists = true
                return@byBlock2
            }
        }
        if (!recordExists) {
            database.qualityManagementInvestigationsDao.insertSubOrderTask(ntIt.toDatabaseSubOrderTask())
            Log.d(TAG, "syncSubOrders: Sub order has been inserted / id = ${ntIt.id}")
        }
    }
    ntSubOrderTasks.forEach byBlock1@{ ntIt ->
        var recordStatusChanged = false
        dbSubOrderTasks.forEach byBlock2@{ dbIt ->
            if (ntIt.id == dbIt.id) {
                if (ntIt.statusId != dbIt.statusId)
                    recordStatusChanged = true
                return@byBlock2
            }
        }
        if (recordStatusChanged) {
            database.qualityManagementInvestigationsDao.updateSubOrderTask(ntIt.toDatabaseSubOrderTask())
            Log.d(TAG, "syncSubOrders: Sub order status has been changed / id = ${ntIt.id}")
        }
    }
    dbSubOrderTasks.forEach byBlock1@{ dbIt ->
        var recordExists = false
        ntSubOrderTasks.forEach byBlock2@{ ntIt ->
            if (ntIt.id == dbIt.id) {
                recordExists = true
                return@byBlock2
            }
        }
        if (!recordExists) {
            database.qualityManagementInvestigationsDao.deleteSubOrderTask(dbIt)
            Log.d(TAG, "syncSubOrders: Sub order deleted from SQLite / id = ${dbIt.id}")
        }
    }
}

fun syncSamples(
    dbSamples: List<DatabaseSample>,
    ntSamples: List<NetworkSample>,
    database: QualityManagementDB
) {
    ntSamples.forEach byBlock1@{ ntIt ->
        var recordExists = false
        dbSamples.forEach byBlock2@{ dbIt ->
            if (ntIt.id == dbIt.id) {
                recordExists = true
                return@byBlock2
            }
        }
        if (!recordExists) {
            database.qualityManagementInvestigationsDao.insertSample(ntIt.toDatabaseSample())
            Log.d(TAG, "syncSamples: Sample has been inserted / id = ${ntIt.id}")
        }
    }
    dbSamples.forEach byBlock1@{ dbIt ->
        var recordExists = false
        ntSamples.forEach byBlock2@{ ntIt ->
            if (ntIt.id == dbIt.id) {
                recordExists = true
                return@byBlock2
            }
        }
        if (!recordExists) {
            database.qualityManagementInvestigationsDao.deleteSample(dbIt)
            Log.d(TAG, "syncSamples: Sample deleted from SQLite / id = ${dbIt.id}")
        }
    }
}