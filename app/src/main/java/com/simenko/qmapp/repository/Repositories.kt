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
            val positionLevels =
                QualityManagementNetwork.serviceholderManufacturing.getPositionLevels();
            database.qualityManagementManufacturingDao.insertPositionLevelsAll(
                ListTransformer(
                    positionLevels,
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
            val teamMembers = QualityManagementNetwork.serviceholderManufacturing.getTeamMembers();
            database.qualityManagementManufacturingDao.insertTeamMembersAll(
                ListTransformer(
                    teamMembers,
                    NetworkTeamMembers::class,
                    DatabaseTeamMember::class
                ).generateList()
            )
            Log.d(TAG, "refreshTeamMembers: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshCompanies() {
        withContext(Dispatchers.IO) {
            val companies = QualityManagementNetwork.serviceholderManufacturing.getCompanies();
            database.qualityManagementManufacturingDao.insertCompaniesAll(
                ListTransformer(
                    companies,
                    NetworkCompany::class,
                    DatabaseCompany::class
                ).generateList()
            )
            Log.d(TAG, "refreshCompanies: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshDepartments() {
        withContext(Dispatchers.IO) {
            val departments = QualityManagementNetwork.serviceholderManufacturing.getDepartments();
            database.qualityManagementManufacturingDao.insertDepartmentsAll(
                ListTransformer(
                    departments,
                    NetworkDepartment::class,
                    DatabaseDepartment::class
                ).generateList()
            )
            Log.d(TAG, "refreshDepartments: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSubDepartments() {
        withContext(Dispatchers.IO) {
            val subDepartments =
                QualityManagementNetwork.serviceholderManufacturing.getSubDepartments();
            database.qualityManagementManufacturingDao.insertSubDepartmentsAll(
                ListTransformer(
                    subDepartments,
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
            val subDepartments =
                QualityManagementNetwork.serviceholderManufacturing.getManufacturingChannels();
            database.qualityManagementManufacturingDao.insertManufacturingChannelsAll(
                ListTransformer(
                    subDepartments,
                    NetworkManufacturingChannel::class, DatabaseManufacturingChannel::class
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
            val manufacturingLines =
                QualityManagementNetwork.serviceholderManufacturing.getManufacturingLines();
            database.qualityManagementManufacturingDao.insertManufacturingLinesAll(
                ListTransformer(
                    manufacturingLines,
                    NetworkManufacturingLine::class, DatabaseManufacturingLine::class
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
            val manufacturingOperations =
                QualityManagementNetwork.serviceholderManufacturing.getManufacturingOperations();
            database.qualityManagementManufacturingDao.insertManufacturingOperationsAll(
                ListTransformer(
                    manufacturingOperations,
                    NetworkManufacturingOperation::class, DatabaseManufacturingOperation::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshManufacturingOperations: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    /**
     * Connecting with LiveData for ViewModel
     */
    val teamMembers: LiveData<List<DomainTeamMember>> =
        Transformations.map(database.qualityManagementManufacturingDao.getTeamMembers()) {
            ListTransformer(it, DatabaseTeamMember::class, DomainTeamMember::class).generateList()
        }

    val departments: LiveData<List<DomainDepartment>> =
        Transformations.map(database.qualityManagementManufacturingDao.getDepartments()) {
            ListTransformer(it, DatabaseDepartment::class, DomainDepartment::class).generateList()
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
                QualityManagementNetwork.serviceholderProducts.getElementIshModels();
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
                QualityManagementNetwork.serviceholderProducts.getIshSubCharacteristics();
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
                QualityManagementNetwork.serviceholderProducts.getManufacturingProjects();
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
                QualityManagementNetwork.serviceholderProducts.getCharacteristics();
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
            val metrixes = QualityManagementNetwork.serviceholderProducts.getMetrixes();
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
            val list = QualityManagementNetwork.serviceholderProducts.getKeys();
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
            val list = QualityManagementNetwork.serviceholderProducts.getProductBases();
            database.qualityManagementProductsDao.insertProductBasesAll(
                ListTransformer(
                    list,
                    NetworkProductBase::class,
                    DatabaseProductBase::class
                ).generateList()
            )
            Log.d(TAG, "refreshProductBases: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshProducts() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceholderProducts.getProducts();
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
            val list = QualityManagementNetwork.serviceholderProducts.getComponents();
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
            val list = QualityManagementNetwork.serviceholderProducts.getComponentInStages();
            database.qualityManagementProductsDao.insertComponentInStagesAll(
                ListTransformer(
                    list,
                    NetworkComponentInStage::class,
                    DatabaseComponentInStage::class
                ).generateList()
            )
            Log.d(TAG, "refreshComponentInStages: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshVersionStatuses() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceholderProducts.getVersionStatuses();
            database.qualityManagementProductsDao.insertVersionStatusesAll(
                ListTransformer(
                    list,
                    NetworkVersionStatus::class,
                    DatabaseVersionStatus::class
                ).generateList()
            )
            Log.d(TAG, "refreshVersionStatuses: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshProductVersions() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceholderProducts.getProductVersions();
            database.qualityManagementProductsDao.insertProductVersionsAll(
                ListTransformer(
                    list,
                    NetworkProductVersion::class,
                    DatabaseProductVersion::class
                ).generateList()
            )
            Log.d(TAG, "refreshProductVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshComponentVersions() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceholderProducts.getComponentVersions();
            database.qualityManagementProductsDao.insertComponentVersionsAll(
                ListTransformer(
                    list,
                    NetworkComponentVersion::class,
                    DatabaseComponentVersion::class
                ).generateList()
            )
            Log.d(TAG, "refreshComponentVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }
/*
    suspend fun refreshComponentInStageVersions() {
        withContext(Dispatchers.IO) {
            val list = QualityManagementNetwork.serviceholderProducts.getComponentInStageVersions();
            database.qualityManagementProductsDao.insertComponentInStageVersionsAll(
                ListTransformer(
                    list,
                    NetworkComponentInStageVersion::class,
                    DatabaseComponentInStageVersion::class
                ).generateList()
            )
            Log.d(TAG, "refreshComponentInStageVersions: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }*/

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
            ListTransformer(it, DatabaseComponentInStage::class, DomainComponentInStage::class).generateList()
        }
    val versionStatuses: LiveData<List<DomainVersionStatus>> =
        Transformations.map(database.qualityManagementProductsDao.getVersionStatuses()) {
            ListTransformer(it, DatabaseVersionStatus::class, DomainVersionStatus::class).generateList()
        }
    val productVersions: LiveData<List<DomainProductVersion>> =
        Transformations.map(database.qualityManagementProductsDao.getProductVersions()) {
            ListTransformer(it, DatabaseProductVersion::class, DomainProductVersion::class).generateList()
        }
    val componentVersions: LiveData<List<DomainComponentVersion>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentVersions()) {
            ListTransformer(it, DatabaseComponentVersion::class, DomainComponentVersion::class).generateList()
        }
    /*val componentInStageVersions: LiveData<List<DomainComponentInStageVersion>> =
        Transformations.map(database.qualityManagementProductsDao.getComponentInStageVersions()) {
            ListTransformer(it, DatabaseComponentInStageVersion::class, DomainComponentInStageVersion::class).generateList()
        }*/
}

class QualityManagementInvestigationsRepository(private val database: QualityManagementDB) {
    /**
     * Update Investigations from the network
     */
    suspend fun refreshInputForOrder() {
        withContext(Dispatchers.IO) {
            val inputForOrder =
                QualityManagementNetwork.serviceholderInvestigations.getInputForOrder();
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
                QualityManagementNetwork.serviceholderInvestigations.getOrdersStatuses();
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
                QualityManagementNetwork.serviceholderInvestigations.getMeasurementReasons();
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
            val ordersTypes = QualityManagementNetwork.serviceholderInvestigations.getOrdersTypes();
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
            val ntOrders = QualityManagementNetwork.serviceholderInvestigations.getOrders()
            val dbOrders = database.qualityManagementInvestigationsDao.getOrdersByList()

            syncOrders(dbOrders, ntOrders, database)
            Log.d(TAG, "refreshOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun deleteOrder(order: DomainOrder) {
        withContext(Dispatchers.IO) {
            QualityManagementNetwork.serviceholderInvestigations.deleteOrder(order.id)
            val dbOrder = order.toDatabaseOrder()
//            ToDo this action reload view model completely because it triggers to update DomainModel completely
            database.qualityManagementInvestigationsDao.deleteOrder(dbOrder)
        }
    }

    suspend fun refreshSubOrders() {
        withContext(Dispatchers.IO) {
            val subOrders = QualityManagementNetwork.serviceholderInvestigations.getSubOrders();
//            if (subOrders.isNotEmpty())
//                database.qualityManagementInvestigationsDao.deleteSubOrdersAll()
            database.qualityManagementInvestigationsDao.insertSubOrdersAll(
                ListTransformer(
                    subOrders,
                    NetworkSubOrder::class,
                    DatabaseSubOrder::class
                ).generateList()
            )
            Log.d(TAG, "refreshSubOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSubOrderTasks() {
        withContext(Dispatchers.IO) {
            val subOrderTasks =
                QualityManagementNetwork.serviceholderInvestigations.getSubOrderTasks();
//            if (subOrderTasks.isNotEmpty())
//                database.qualityManagementInvestigationsDao.deleteSubOrderTasksAll()
            database.qualityManagementInvestigationsDao.insertSubOrderTasksAll(
                ListTransformer(
                    subOrderTasks,
                    NetworkSubOrderTask::class,
                    DatabaseSubOrderTask::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshSubOrderTasks: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshSamples() {
        withContext(Dispatchers.IO) {
            val samples = QualityManagementNetwork.serviceholderInvestigations.getSamples();
            database.qualityManagementInvestigationsDao.insertSamplesAll(
                ListTransformer(samples, NetworkSample::class, DatabaseSample::class).generateList()
            )
            Log.d(TAG, "refreshSamples: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshResultsDecryptions() {
        withContext(Dispatchers.IO) {
            val resultsDecryptions =
                QualityManagementNetwork.serviceholderInvestigations.getResultsDecryptions();
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
            val results = QualityManagementNetwork.serviceholderInvestigations.getResults();
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
            ).generateList()
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
    val latestOrder: LiveData<DomainOrder> =
        Transformations.map(database.qualityManagementInvestigationsDao.getLatestOrder()) {
            it.toDomainOrder()
        }

    val completeSubOrders: LiveData<List<DomainSubOrderComplete>> =
        Transformations.map(database.qualityManagementInvestigationsDao.getSubOrdersDetailed()) {
            it.asDomainSubOrderDetailed(-1)
        }

    val completeSubOrderTasks: LiveData<List<DomainSubOrderTaskComplete>> =
        Transformations.map(database.qualityManagementInvestigationsDao.getSubOrderTasksDetailed()) {
            it.asDomainSubOrderTask(-1)
        }

}

fun syncOrders(
    dbOrders: List<DatabaseOrder>,
    ntOrders: List<NetworkOrder>,
    database: QualityManagementDB
) {
    ntOrders.forEach byBlock1@{ ntIt ->
        var recordStatusChanged = false
        dbOrders.forEach byBlock2@{ dbIt ->
            if (ntIt.id == dbIt.id) {
                if (ntIt.statusId != dbIt.statusId)
                    recordStatusChanged = true
                return@byBlock2
            }
        }
        database.qualityManagementInvestigationsDao.insertOrder(ntIt.toDatabaseOrder())
        if (recordStatusChanged) {
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