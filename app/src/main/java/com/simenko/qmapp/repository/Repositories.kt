package com.simenko.qmapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.QualityManagementNetwork
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.simenko.qmapp.utils.ListTransformer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
//            Log.d(TAG, "refreshPositionLevels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshTeamMembers: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshCompanies: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshDepartments: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshSubDepartments: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshManufacturingChannels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshManufacturingLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshManufacturingOperations: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshOperationsFlows: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    /**
     * Connecting with LiveData for ViewModel
     */
    val teamMembers: LiveData<List<DomainTeamMember>> =
        database.qualityManagementManufacturingDao.getTeamMembers().map {
            ListTransformer(
                it,
                DatabaseTeamMember::class,
                DomainTeamMember::class
            ).generateList()
        }

    val teamComplete: LiveData<List<DomainTeamMemberComplete>> =
        database.qualityManagementManufacturingDao.getTeamDetailed().map {
            it.asTeamCompleteDomainModel()
        }

    fun teamComplete(): Flow<List<DomainTeamMemberComplete>> =
        database.qualityManagementManufacturingDao.getTeamDetailedFlow().map {
        it.asTeamCompleteDomainModel()
    }

    val departments: LiveData<List<DomainDepartment>> =
        database.qualityManagementManufacturingDao.getDepartments().map {
            ListTransformer(
                it,
                DatabaseDepartment::class,
                DomainDepartment::class
            ).generateList()
        }

    val subDepartments: LiveData<List<DomainSubDepartment>> =
        database.qualityManagementManufacturingDao.getSubDepartments().map {
            ListTransformer(
                it,
                DatabaseSubDepartment::class,
                DomainSubDepartment::class
            ).generateList()
        }

    val channels: LiveData<List<DomainManufacturingChannel>> =
        database.qualityManagementManufacturingDao.getManufacturingChannels().map {
            ListTransformer(
                it,
                DatabaseManufacturingChannel::class,
                DomainManufacturingChannel::class
            ).generateList()
        }

    val lines: LiveData<List<DomainManufacturingLine>> =
        database.qualityManagementManufacturingDao.getManufacturingLines().map {
            ListTransformer(
                it,
                DatabaseManufacturingLine::class,
                DomainManufacturingLine::class
            ).generateList()
        }

    val operations: LiveData<List<DomainManufacturingOperation>> =
        database.qualityManagementManufacturingDao.getManufacturingOperations().map {
            ListTransformer(
                it,
                DatabaseManufacturingOperation::class,
                DomainManufacturingOperation::class
            ).generateList()
        }

    val operationsFlows: LiveData<List<DomainOperationsFlow>> =
        database.qualityManagementManufacturingDao.getOperationsFlows().map {
            ListTransformer(
                it,
                DatabaseOperationsFlow::class,
                DomainOperationsFlow::class
            ).generateList()
        }

    val departmentsDetailed: LiveData<List<DomainDepartmentComplete>> =
        database.qualityManagementManufacturingDao.getDepartmentsDetailed().map {
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
//            Log.d(TAG, "refreshElementIshModels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshIshSubCharacteristics: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshManufacturingProjects: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshCharacteristics: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshMetrixes: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshKeys: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshProductBases: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshProducts: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshComponents: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshComponentInStages: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshVersionStatuses: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshProductVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshComponentVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshComponentInStageVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshProductTolerances: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshComponentTolerances: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshComponentInStageTolerances: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshProductsToLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshComponentsToLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshComponentInStagesToLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    val characteristics: LiveData<List<DomainCharacteristic>> =
        database.qualityManagementProductsDao.getCharacteristics().map {
            ListTransformer(
                it,
                DatabaseCharacteristic::class,
                DomainCharacteristic::class
            ).generateList()
        }

    val metrixes: LiveData<List<DomainMetrix>> =
        database.qualityManagementProductsDao.getMetrixes().map {
            ListTransformer(
                it,
                DatabaseMetrix::class,
                DomainMetrix::class
            ).generateList()
        }

    val keys: LiveData<List<DomainKey>> =
        database.qualityManagementProductsDao.getKeys().map {
            ListTransformer(it, DatabaseKey::class, DomainKey::class).generateList()
        }
    val productBases: LiveData<List<DomainProductBase>> =
        database.qualityManagementProductsDao.getProductBases().map {
            ListTransformer(it, DatabaseProductBase::class, DomainProductBase::class).generateList()
        }
    val products: LiveData<List<DomainProduct>> =
        database.qualityManagementProductsDao.getProducts().map {
            ListTransformer(it, DatabaseProduct::class, DomainProduct::class).generateList()
        }
    val components: LiveData<List<DomainComponent>> =
        database.qualityManagementProductsDao.getComponents().map {
            ListTransformer(it, DatabaseComponent::class, DomainComponent::class).generateList()
        }
    val componentInStages: LiveData<List<DomainComponentInStage>> =
        database.qualityManagementProductsDao.getComponentInStages().map {
            ListTransformer(
                it,
                DatabaseComponentInStage::class,
                DomainComponentInStage::class
            ).generateList()
        }
    val versionStatuses: LiveData<List<DomainVersionStatus>> =
        database.qualityManagementProductsDao.getVersionStatuses().map {
            ListTransformer(
                it,
                DatabaseVersionStatus::class,
                DomainVersionStatus::class
            ).generateList()
        }
    val productVersions: LiveData<List<DomainProductVersion>> =
        database.qualityManagementProductsDao.getProductVersions().map {
            ListTransformer(
                it,
                DatabaseProductVersion::class,
                DomainProductVersion::class
            ).generateList()
        }
    val componentVersions: LiveData<List<DomainComponentVersion>> =
        database.qualityManagementProductsDao.getComponentVersions().map {
            ListTransformer(
                it,
                DatabaseComponentVersion::class,
                DomainComponentVersion::class
            ).generateList()
        }
    val componentInStageVersions: LiveData<List<DomainComponentInStageVersion>> =
        database.qualityManagementProductsDao.getComponentInStageVersions().map {
            ListTransformer(
                it,
                DatabaseComponentInStageVersion::class,
                DomainComponentInStageVersion::class
            ).generateList()
        }
    val productTolerances: LiveData<List<DomainProductTolerance>> =
        database.qualityManagementProductsDao.getProductTolerances().map {
            ListTransformer(
                it,
                DatabaseProductTolerance::class,
                DomainProductTolerance::class
            ).generateList()
        }
    val componentTolerances: LiveData<List<DomainComponentTolerance>> =
        database.qualityManagementProductsDao.getComponentTolerances().map {
            ListTransformer(
                it,
                DatabaseComponentTolerance::class,
                DomainComponentTolerance::class
            ).generateList()
        }
    val componentInStageTolerances: LiveData<List<DomainComponentInStageTolerance>> =
        database.qualityManagementProductsDao.getComponentInStageTolerances().map {
            ListTransformer(
                it,
                DatabaseComponentInStageTolerance::class,
                DomainComponentInStageTolerance::class
            ).generateList()
        }
    val productsToLines: LiveData<List<DomainProductToLine>> =
        database.qualityManagementProductsDao.getProductsToLines().map {
            ListTransformer(
                it,
                DatabaseProductToLine::class,
                DomainProductToLine::class
            ).generateList()
        }
    val componentsToLines: LiveData<List<DomainComponentToLine>> =
        database.qualityManagementProductsDao.getComponentsToLines().map {
            ListTransformer(
                it,
                DatabaseComponentToLine::class,
                DomainComponentToLine::class
            ).generateList()
        }
    val componentInStagesToLines: LiveData<List<DomainComponentInStageToLine>> =
        database.qualityManagementProductsDao.getComponentInStagesToLines().map {
            ListTransformer(
                it,
                DatabaseComponentInStageToLine::class,
                DomainComponentInStageToLine::class
            ).generateList()
        }
    val itemVersionsComplete: LiveData<List<DomainItemVersionComplete>> =
        database.qualityManagementProductsDao.getItemVersionsComplete().map {
            it.asDomainItem()
        }

    val itemsTolerances: LiveData<List<DomainItemTolerance>> =
        database.qualityManagementProductsDao.getItemsTolerances().map {
            ListTransformer(
                it,
                DatabaseItemTolerance::class,
                DomainItemTolerance::class
            ).generateList()
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
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
//            Log.d(TAG, "refreshInputForOrder: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshOrdersStatuses() {
        withContext(Dispatchers.IO) {
            val records = QualityManagementNetwork.serviceHolderInvestigations.getOrdersStatuses()
            database.qualityManagementInvestigationsDao.insertOrdersStatusesAll(
                ListTransformer(
                    records,
                    NetworkOrdersStatus::class,
                    DatabaseOrdersStatus::class
                ).generateList()
            )
//            Log.d(TAG, "refreshOrdersStatuses: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshInvestigationReasons() {
        withContext(Dispatchers.IO) {
            val records = QualityManagementNetwork.serviceHolderInvestigations.getMeasurementReasons()
            database.qualityManagementInvestigationsDao.insertMeasurementReasonsAll(
                ListTransformer(
                    records,
                    NetworkReason::class, DatabaseReason::class
                ).generateList()
            )
//            Log.d(TAG, "refreshMeasurementReasons: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshInvestigationTypes() {
        withContext(Dispatchers.IO) {
            val records = QualityManagementNetwork.serviceHolderInvestigations.getOrdersTypes()
            database.qualityManagementInvestigationsDao.insertOrdersTypesAll(
                ListTransformer(
                    records,
                    NetworkOrdersType::class,
                    DatabaseOrdersType::class
                ).generateList()
            )
//            Log.d(TAG, "refreshOrdersTypes: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshOrders() {
        withContext(Dispatchers.IO) {
            val ntOrders = QualityManagementNetwork.serviceHolderInvestigations.getOrders()
            val dbOrders = database.qualityManagementInvestigationsDao.getOrdersByList()

            syncOrders(dbOrders, ntOrders, database)
//            Log.d(TAG, "refreshOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
                subOrderTask.id
            )
        }
    }

    suspend fun deleteSample(sample: DomainSample) {
        withContext(Dispatchers.IO) {
            QualityManagementNetwork.serviceHolderInvestigations.deleteSample(sample.id)
        }
    }

    suspend fun deleteResults(charId: Int = 0, id: Int = 0) {
        withContext(Dispatchers.IO) {
            QualityManagementNetwork.serviceHolderInvestigations.deleteResults(charId, id)
        }
    }

    suspend fun refreshSubOrders() {
        withContext(Dispatchers.IO) {
            val ntSubOrder = QualityManagementNetwork.serviceHolderInvestigations.getSubOrders()
            val dbSubOrders = database.qualityManagementInvestigationsDao.getSubOrdersByList()

            syncSubOrders(dbSubOrders, ntSubOrder, database)

//            Log.d(TAG, "refreshSubOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSubOrderTasks() {
        withContext(Dispatchers.IO) {
            val ntSubOrderTasks = QualityManagementNetwork.serviceHolderInvestigations.getSubOrderTasks()
            val dbSubOrderTasks = database.qualityManagementInvestigationsDao.getSubOrderTasksByList()

            syncSubOrderTasks(dbSubOrderTasks, ntSubOrderTasks, database)

//            Log.d(TAG,"refreshSubOrderTasks: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSamples() {
        withContext(Dispatchers.IO) {
            val ntSamples = QualityManagementNetwork.serviceHolderInvestigations.getSamples()
            val dbSamples = database.qualityManagementInvestigationsDao.getSamplesByList()

            syncSamples(dbSamples, ntSamples, database)

//            Log.d(TAG, "refreshSamples: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
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
//            Log.d(TAG, "refreshResultsDecryptions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshResults() {
        withContext(Dispatchers.IO) {
            val ntResults = QualityManagementNetwork.serviceHolderInvestigations.getResults()
            val dbResults = database.qualityManagementInvestigationsDao.getResultsByList()

            syncResults(dbResults, ntResults, database)

//            Log.d(TAG, "refreshResults: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainOrder) = coroutineScope.produce {
        val newOrder = QualityManagementNetwork.serviceHolderInvestigations.createOrder(
            record.toNetworkOrderWithoutId()
        ).toDatabaseOrder()
        database.qualityManagementInvestigationsDao.insertOrder(newOrder)
        send(newOrder.toDomainOrder()) //cold send, can be this.trySend(l).isSuccess //hot send
    }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) = coroutineScope.produce {
        val newRecord = QualityManagementNetwork.serviceHolderInvestigations.createSubOrder(
            record.toNetworkSubOrderWithoutId()
        ).toDatabaseSubOrder()
        database.qualityManagementInvestigationsDao.insertSubOrder(newRecord)
        send(newRecord.toDomainSubOrder()) //cold send, can be this.trySend(l).isSuccess //hot send
    }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) = coroutineScope.produce {
        val newRecord = QualityManagementNetwork.serviceHolderInvestigations.createSubOrderTask(
            record.toNetworkSubOrderTaskWithoutId()
        ).toDatabaseSubOrderTask()
        database.qualityManagementInvestigationsDao.insertSubOrderTask(newRecord)
        send(newRecord.toDomainSubOrderTask()) //cold send, can be this.trySend(l).isSuccess //hot send
    }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainResult) = coroutineScope.produce {
        val newRecord = QualityManagementNetwork.serviceHolderInvestigations.createResult(
            record.toNetworkResultWithoutId()
        ).toDatabaseResult()
        database.qualityManagementInvestigationsDao.insertResult(newRecord)
        send(newRecord.toDomainResult()) //cold send, can be this.trySend(l).isSuccess //hot send
    }

    fun getCreatedRecords(coroutineScope: CoroutineScope, records: List<DomainResult>) = coroutineScope.produce {
        val newRecords = QualityManagementNetwork.serviceHolderInvestigations.createResults(
            records.map {
                it.toNetworkResultWithoutId()
            }
        )

        newRecords.forEach { nIt->
            database.qualityManagementInvestigationsDao.insertResult(nIt.toDatabaseResult())
        }

        send(newRecords) //cold send, can be this.trySend(l).isSuccess //hot send
    }


    fun updateRecord(coroutineScope: CoroutineScope, record: DomainOrder) = coroutineScope.produce {
        val nOrder = record.toNetworkOrderWithId()
        QualityManagementNetwork.serviceHolderInvestigations.editOrder(record.id, nOrder)
        database.qualityManagementInvestigationsDao.updateOrder(record.toDatabaseOrder())
        send(record)
    }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) = coroutineScope.produce {
        val nSubOrder = record.toNetworkSubOrderWithId()
        QualityManagementNetwork.serviceHolderInvestigations.editSubOrder(record.id, nSubOrder)
        database.qualityManagementInvestigationsDao.updateSubOrder(record.toDatabaseSubOrder())
        send(record)
    }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) = coroutineScope.produce {
        val nSubOrderTask = record.toNetworkSubOrderTaskWithId()
        QualityManagementNetwork.serviceHolderInvestigations.editSubOrderTask(record.id, nSubOrderTask)

        val dSubOrderTask = QualityManagementNetwork.serviceHolderInvestigations.getSubOrderTask(record.id).toDatabaseSubOrderTask()
        database.qualityManagementInvestigationsDao.updateSubOrderTask(dSubOrderTask)

        send(dSubOrderTask.toDomainSubOrderTask())
    }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainResult) = coroutineScope.produce {
        val nNetwork = record.toNetworkResultWithId()
        QualityManagementNetwork.serviceHolderInvestigations.editResult(record.id, nNetwork)
        database.qualityManagementInvestigationsDao.updateResult(record.toDatabaseResult())
        send(record)
    }

    fun getRecord(coroutineScope: CoroutineScope, record: DomainOrder) = coroutineScope.produce {
        val nOrder = QualityManagementNetwork.serviceHolderInvestigations.getOrder(record.id)
        database.qualityManagementInvestigationsDao.updateOrder(nOrder.toDatabaseOrder())
        send(nOrder.toDomainOrder())
    }

    fun getRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) = coroutineScope.produce {
        val nSubOrder = QualityManagementNetwork.serviceHolderInvestigations.getSubOrder(record.id)
        database.qualityManagementInvestigationsDao.updateSubOrder(nSubOrder.toDatabaseSubOrder())
        send(nSubOrder.toDomainSubOrder())
    }

    fun getRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) = coroutineScope.produce {
        val nSubOrderTask = QualityManagementNetwork.serviceHolderInvestigations.getSubOrderTask(record.id)
        database.qualityManagementInvestigationsDao.updateSubOrderTask(nSubOrderTask.toDatabaseSubOrderTask())
        send(nSubOrderTask.toDomainSubOrderTask())
    }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSample) = coroutineScope.produce {
        val newRecord = QualityManagementNetwork.serviceHolderInvestigations.createSample(
            record.toNetworkSampleWithoutId()
        ).toDatabaseSample()
        database.qualityManagementInvestigationsDao.insertSample(newRecord)
        send(newRecord.toDomainSample()) //cold send, can be this.trySend(l).isSuccess //hot send
    }



    val inputForOrder: LiveData<List<DomainInputForOrder>> =
        database.qualityManagementInvestigationsDao.getInputForOrder().map {
            ListTransformer(
                it,
                DatabaseInputForOrder::class,
                DomainInputForOrder::class
            ).generateList().sortedBy { item -> item.depOrder }
        }

    val investigationTypes: LiveData<List<DomainOrdersType>> =
        database.qualityManagementInvestigationsDao.getOrdersTypes().map {
            ListTransformer(it, DatabaseOrdersType::class, DomainOrdersType::class).generateList()
        }

    val investigationReasons: LiveData<List<DomainReason>> =
        database.qualityManagementInvestigationsDao.getMeasurementReasons().map {
            ListTransformer(
                it,
                DatabaseReason::class,
                DomainReason::class
            ).generateList()
        }

    val investigationStatuses: LiveData<List<DomainOrdersStatus>> =
        database.qualityManagementInvestigationsDao.getOrdersStatuses().map {
            ListTransformer(
                it,
                DatabaseOrdersStatus::class,
                DomainOrdersStatus::class
            ).generateList()
        }

    val orders: LiveData<List<DomainOrder>> =
        database.qualityManagementInvestigationsDao.getOrders().map {
            ListTransformer(
                it,
                DatabaseOrder::class,
                DomainOrder::class
            ).generateList()
        }

    private var currentOrder = 0
    fun setCurrentOrder(id: Int) {
        currentOrder = id
    }
    val completeOrders: LiveData<List<DomainOrderComplete>> =
        database.qualityManagementInvestigationsDao.getOrdersDetailed().map {
            it.asDomainOrdersComplete(currentOrder)
        }

    fun completeOrders(): Flow<List<DomainOrderComplete>> =
        database.qualityManagementInvestigationsDao.getOrdersDetailedFlow().map {
            it.asDomainOrdersComplete()
        }

    private var currentSubOrder = 0
    fun setCurrentSubOrder(id: Int) {
        currentSubOrder = id
    }
    val completeSubOrders: LiveData<List<DomainSubOrderComplete>> =
        database.qualityManagementInvestigationsDao.getSubOrdersDetailed().map {
            it.asDomainSubOrderDetailed(currentSubOrder)
        }

    private var currentTask = 0
    fun setCurrentTask(id: Int) {
        currentTask = id
    }
    val completeSubOrderTasks: LiveData<List<DomainSubOrderTaskComplete>> =
        database.qualityManagementInvestigationsDao.getSubOrderTasksDetailed().map {
            it.asDomainSubOrderTask(currentTask)
        }

    private var currentSample = 0
    fun setCurrentSample(id: Int) {
        currentSample = id
    }
    val completeSamples: LiveData<List<DomainSampleComplete>> =
        database.qualityManagementInvestigationsDao.getSamplesDetailed().map {
            it.asDomainSamples(currentSample)
        }

    val subOrdersWithChildren: LiveData<List<DomainSubOrderShort>> =
        database.qualityManagementInvestigationsDao.getSubOrderWithChildren().map {
            it.toDomainSubOrderShort()
        }

    private var currentResult = 0
    fun setCurrentResult(id: Int) {
        currentResult = id
    }
    val completeResults: LiveData<List<DomainResultComplete>> =
        database.qualityManagementInvestigationsDao.getResultsDetailed().map {
            it.asDomainResults(currentResult)
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
//            Log.d(TAG, "syncOrders: Order has been inserted / id = ${ntIt.id}")
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
//            Log.d(TAG, "syncOrders: Order status has been changed / id = ${ntIt.id}")
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
//            Log.d(TAG, "syncOrders: Order deleted from SQLite / id = ${dbIt.id}")
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
//            Log.d(TAG, "syncSubOrders: Sub order has been inserted / id = ${ntIt.id}")
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
//            Log.d(TAG, "syncSubOrders: Sub order status has been changed / id = ${ntIt.id}")
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
//            Log.d(TAG, "syncSubOrders: Sub order deleted from SQLite / id = ${dbIt.id}")
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
//            Log.d(TAG, "syncSubOrders: Sub order task has been inserted / id = ${ntIt.id}")
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
//            Log.d(TAG, "syncSubOrders: Sub order task status has been changed / id = ${ntIt.id}")
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
//            Log.d(TAG, "syncSubOrders: Sub order deleted from SQLite / id = ${dbIt.id}")
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
//            Log.d(TAG, "syncSamples: Sample has been inserted / id = ${ntIt.id}")
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
//            Log.d(TAG, "syncSamples: Sample deleted from SQLite / id = ${dbIt.id}")
        }
    }
}

fun syncResults(
    dbResults: List<DatabaseResult>,
    ntResults: List<NetworkResult>,
    database: QualityManagementDB
) {
    ntResults.forEach byBlock1@{ ntIt ->
        var recordExists = false
        dbResults.forEach byBlock2@{ dbIt ->
            if (ntIt.id == dbIt.id) {
                recordExists = true
                return@byBlock2
            }
        }
        if (!recordExists) {
            database.qualityManagementInvestigationsDao.insertResult(ntIt.toDatabaseResult())
//            Log.d(TAG, "syncSamples: Result has been inserted / id = ${ntIt.id}")
        }
    }
    dbResults.forEach byBlock1@{ dbIt ->
        var recordExists = false
        ntResults.forEach byBlock2@{ ntIt ->
            if (ntIt.id == dbIt.id) {
                recordExists = true
                return@byBlock2
            }
        }
        if (!recordExists) {
            database.qualityManagementInvestigationsDao.deleteResult(dbIt)
//            Log.d(TAG, "syncSamples: Result deleted from SQLite / id = ${dbIt.id}")
        }
    }
}