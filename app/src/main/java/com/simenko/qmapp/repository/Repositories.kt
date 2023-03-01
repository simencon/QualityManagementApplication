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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            val orders = QualityManagementNetwork.serviceholderInvestigations.getOrders();
            database.qualityManagementInvestigationsDao.insertOrdersAll(
                ListTransformer(orders, NetworkOrder::class, DatabaseOrder::class).generateList()
            )
            Log.d(TAG, "refreshOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun placeOrder(order: NetworkOrder) {
        withContext(Dispatchers.IO) {
            val result = QualityManagementNetwork.serviceholderInvestigations.createOrder(order)
            Log.d(TAG, "placeOrder: $result")
        }
    }

    suspend fun refreshSubOrders() {
        withContext(Dispatchers.IO) {
            val subOrders = QualityManagementNetwork.serviceholderInvestigations.getSubOrders();
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

}