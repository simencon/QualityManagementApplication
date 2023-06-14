package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.InvestigationsService
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.InvestigationsDao
import com.simenko.qmapp.utils.InvestigationsUtils.getOrdersRange
import com.simenko.qmapp.utils.NotificationData
import com.simenko.qmapp.utils.NotificationReasons
import com.simenko.qmapp.works.SyncPeriods
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "InvestigationsRepository"

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class InvestigationsRepository @Inject constructor(
    private val invDao: InvestigationsDao,
    private val invService: InvestigationsService,
    private val errorConverter: Converter<ResponseBody, NetworkErrorBody>,
    private val crudeOperations: CrudeOperations
) {
    private val timeFormatter = DateTimeFormatter.ISO_INSTANT

    /**
     * Update Investigations from the network
     */
    suspend fun insertInputForOrder() {
        withContext(Dispatchers.IO) {
            val inputForOrder = invService.getInputForOrder()
            invDao.insertInputForOrderAll(
                inputForOrder.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshInputForOrder: ${timeFormatter.format(Instant.now())}")
        }
    }

    suspend fun insertOrdersStatuses() {
        withContext(Dispatchers.IO) {
            val records = invService.getOrdersStatuses()
            invDao.insertOrdersStatusesAll(
                records.map { it.toDatabaseModel() }
            )
            Log.d(
                TAG,
                "refreshOrdersStatuses: ${timeFormatter.format(Instant.now())}"
            )
        }
    }

    suspend fun insertInvestigationReasons() {
        withContext(Dispatchers.IO) {
            val records = invService.getMeasurementReasons()
            invDao.insertMeasurementReasonsAll(
                records.map { it.toDatabaseModel() }
            )
            Log.d(
                TAG,
                "refreshMeasurementReasons: ${timeFormatter.format(Instant.now())}"
            )
        }
    }

    suspend fun insertInvestigationTypes() {
        withContext(Dispatchers.IO) {
            val records = invService.getOrdersTypes()
            invDao.insertOrdersTypesAll(
                records.map { it.toDatabaseModel() }
            )
            Log.d(
                TAG,
                "refreshOrdersTypes: ${timeFormatter.format(Instant.now())}"
            )
        }
    }

    suspend fun insertResultsDecryptions() {
        withContext(Dispatchers.IO) {
            val resultsDecryptions = invService.getResultsDecryptions()
            invDao.insertResultsDecryptionsAll(
                resultsDecryptions.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshResultsDecryptions: ${timeFormatter.format(Instant.now())}")
        }
    }

    /**
     * Investigations sync work
     * */
    suspend fun syncOrders(timeRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            val ntOrders = invService.getOrdersByDateRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, orders not available.")
            }
            val dbOrders = invDao.getOrdersByDateRange(timeRange)
            ntOrders.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    invDao.insertOrder(ntIt.toDatabaseModel())
                }
            }
            ntOrders.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        if (dbIt != ntIt.toDatabaseModel())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateOrder(ntIt.toDatabaseModel())
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
                    invDao.deleteOrder(dbIt)
                }
            }
            Log.d(TAG, "refreshOrders: ${timeFormatter.format(Instant.now())}")
        }
    }

    suspend fun syncSubOrders(timeRange: Pair<Long, Long>): List<NotificationData> {
        val result = mutableListOf<NotificationData>()
        withContext(Dispatchers.IO) {
            val ntSubOrders = invService.getSubOrdersByDateRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, sub orders not available.")
            }
            val dbSubOrders = invDao.getSubOrdersByDateRangeL(timeRange)
            ntSubOrders.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbSubOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    invDao.insertSubOrder(ntIt.toDatabaseModel())
                    invDao.getSubOrdersById(ntIt.id)?.let {
                        result.add(
                            it.toNotificationData(NotificationReasons.CREATED)
                        )
                    }
                }
            }
            ntSubOrders.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbSubOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        if (dbIt != ntIt.toDatabaseModel())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateSubOrder(ntIt.toDatabaseModel())
                    invDao.getSubOrdersById(ntIt.id)?.let {
                        result.add(
                            it.toNotificationData(NotificationReasons.CHANGED)
                        )
                    }
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
                    invDao.getSubOrdersById(dbIt.id)?.let {
                        result.add(
                            it.toNotificationData(NotificationReasons.DELETED)
                        )
                    }
                    invDao.deleteSubOrder(dbIt)
                }
            }

            Log.d(TAG, "refreshSubOrders: ${timeFormatter.format(Instant.now())}")
        }
        return result
    }

    suspend fun syncSubOrderTasks(timeRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            val ntTasks = invService.getTasksDateRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, tasks not available.")
            }
            val dbTasks = invDao.getTasksByDateRangeL(timeRange)
            ntTasks.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbTasks.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    invDao.insertSubOrderTask(ntIt.toDatabaseModel())
                }
            }
            ntTasks.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbTasks.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        if (dbIt != ntIt.toDatabaseModel())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateSubOrderTask(ntIt.toDatabaseModel())
                }
            }
            dbTasks.forEach byBlock1@{ dbIt ->
                var recordExists = false
                ntTasks.forEach byBlock2@{ ntIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    invDao.deleteSubOrderTask(dbIt)
                }
            }
            Log.d(TAG, "refreshSubOrderTasks: ${timeFormatter.format(Instant.now())}")
        }
    }

    suspend fun syncSamples(timeRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            val ntSamples = invService.getSamplesByDateRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, samples not available.")
            }
            val dbSamples = invDao.getSamplesByDateRange(timeRange)
            ntSamples.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbSamples.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    invDao.insertSample(ntIt.toDatabaseModel())
                }
            }
            ntSamples.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbSamples.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        if (dbIt != ntIt.toDatabaseModel())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateSample(ntIt.toDatabaseModel())
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
                    invDao.deleteSample(dbIt)
                }
            }
            Log.d(TAG, "refreshSamples: ${timeFormatter.format(Instant.now())}")
        }
    }

    suspend fun syncResults(timeRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            val ntResults = invService.getResultsByDateRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, results not available.")
            }
            val dbResults = invDao.getResultsByDateRange(timeRange)
            ntResults.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbResults.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    invDao.insertResult(ntIt.toDatabaseModel())
                }
            }
            ntResults.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbResults.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        if (dbIt != ntIt.toDatabaseModel())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateResult(ntIt.toDatabaseModel())
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
                    invDao.deleteResult(dbIt)
                }
            }
            Log.d(TAG, "refreshResults: ${timeFormatter.format(Instant.now())}")
        }
    }

    /**
     * Investigations sync logic
     * */
    private suspend fun insertInvEntities(ntOrders: List<NetworkOrder>) {
        runBlocking {
            val timeRange = ntOrders.getOrdersRange()

            val ntSubOrders = invService.getSubOrdersByDateRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, sub orders not available.")
            }
            val ntTasks = invService.getTasksDateRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, tasks not available.")
            }
            val ntSamples = invService.getSamplesByDateRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, samples not available.")
            }
            val ntResults = invService.getResultsByDateRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, results not available.")
            }

            invDao.insertOrdersAll(ntOrders.map { it.toDatabaseModel() })
            invDao.insertSubOrdersAll(ntSubOrders.map { it.toDatabaseModel() })
            invDao.insertSubOrderTasksAll(ntTasks.map { it.toDatabaseModel() })
            invDao.insertSamplesAll(ntSamples.map { it.toDatabaseModel() })
            invDao.insertResultsAll(ntResults.map { it.toDatabaseModel() })
        }
    }

    fun uploadNewInvestigations(): Flow<Event<Resource<Boolean>>> = flow {
        runCatching {
            invService.getLatestOrderDate().let {
                val rmLatestDate = when (it.isSuccessful) {
                    true -> {
                        it.body() ?: NoRecord.num.toLong()
                    }
                    else -> {
                        emit(Event(Resource.error("Network error, latest order date not available.", false)))
                        NoRecord.num.toLong()
                    }
                }

                if (rmLatestDate != NoRecord.num.toLong())
                    invDao.getLatestOrderDate().let { lcLatestDate ->
                        if (lcLatestDate != null && rmLatestDate > lcLatestDate) {
                            emit(Event(Resource.loading(true)))
                            invService.getOrdersByDateRange(Pair(lcLatestDate, rmLatestDate)).let { response ->
                                if (response.isSuccessful) {
                                    if ((response.body() ?: listOf()).isNotEmpty()) {
                                        runCatching { insertInvEntities(response.body()!!) }.exceptionOrNull().also { e ->
                                            if (e != null) emit(Event(Resource.error(e.message ?: "", true))) else emit(Event(Resource.success(true)))
                                        }
                                    } else {
                                        emit(Event(Resource.error("Data not loaded", true)))
                                    }
                                } else {
                                    emit(Event(Resource.error("Network error, orders not available.", true)))
                                }
                            }
                        } else if (lcLatestDate == null) {
                            emit(Event(Resource.loading(true)))
                            invService.getOrdersByDateRange(Pair(rmLatestDate - SyncPeriods.LAST_DAY.latestMillis, rmLatestDate)).let { response ->
                                if (response.isSuccessful) {
                                    if ((response.body() ?: listOf()).isNotEmpty()) {
                                        runCatching { insertInvEntities(response.body()!!) }.exceptionOrNull().also { e ->
                                            if (e != null) emit(Event(Resource.error(e.message ?: "", true))) else emit(Event(Resource.success(true)))
                                        }
                                    } else {
                                        emit(Event(Resource.error("Data not loaded", true)))
                                    }
                                } else {
                                    emit(Event(Resource.error("Network error, orders not available.", true)))
                                }
                            }
                        } else {
                            emit(Event(Resource.success(false)))
                        }
                    }
                else
                    emit(Event(Resource.success(false)))
            }
        }.exceptionOrNull().also {
            if (it != null)
                emit(Event(Resource.error("Network error.", false)))
        }
    }

    fun uploadOldInvestigations(earliestOrderDate: Long): Flow<Event<Resource<Boolean>>> = flow {
        runCatching {
            if (earliestOrderDate == invDao.getEarliestOrderDate()) {
                emit(Event(Resource.loading(true)))
                invService.getEarliestOrdersByStartingOrderDate(earliestOrderDate)
                    .let { response ->
                        if (response.isSuccessful) {
                            if ((response.body() ?: listOf()).isNotEmpty()) {
                                runCatching { insertInvEntities(response.body()!!) }.exceptionOrNull().also { e ->
                                    if (e != null) emit(Event(Resource.error(e.message ?: "", true))) else emit(Event(Resource.success(true)))
                                }
                            } else {
                                emit(Event(Resource.error("Data not loaded", true)))
                            }
                        } else {
                            emit(Event(Resource.error("Network error, orders not available.", true)))
                        }
                    }
            } else {
                emit(Event(Resource.success(false)))
            }
        }.exceptionOrNull().also {
            if (it != null)
                emit(Event(Resource.error("Network error.", false)))
        }
    }

    suspend fun getCompleteOrdersRange(): Pair<Long, Long> {
        return Pair(invDao.getEarliestOrderDate() ?: NoRecord.num.toLong(), invDao.getLatestOrderDate() ?: NoRecord.num.toLong())
    }

    suspend fun syncInvEntitiesByTimeRange(timeRange: Pair<Long, Long>): List<NotificationData> {
        val mList = mutableListOf<NotificationData>()

        val oList = invDao.getOrdersByDateRange(timeRange)
        val localOrdersHashCode = oList.sumOf { it.hashCode() }
        val remoteOrdersHashCode = invService.getOrdersHashCodeForDatePeriod(timeRange)
        if (localOrdersHashCode != remoteOrdersHashCode) syncOrders(timeRange)
        Log.d(TAG, "Orders: local = $localOrdersHashCode; remote = $remoteOrdersHashCode")

        val soList = invDao.getSubOrdersByDateRangeL(timeRange)
        val localSubOrdersHashCode = soList.sumOf { it.hashCode() }
        val remoteSubOrdersHashCode = invService.getSubOrdersHashCodeForDatePeriod(timeRange)
        if (localSubOrdersHashCode != remoteSubOrdersHashCode) mList.addAll(syncSubOrders(timeRange))
        Log.d(TAG, "SubOrders: local = $localSubOrdersHashCode; remote = $remoteSubOrdersHashCode")

        val tList = invDao.getTasksByDateRangeL(timeRange)
        val localTasksHashCode = tList.sumOf { it.hashCode() }
        val remoteTasksHashCode = invService.getTasksHashCodeForDatePeriod(timeRange)
        if (localTasksHashCode != remoteTasksHashCode) syncSubOrderTasks(timeRange)
        Log.d(TAG, "Tasks: local = $localTasksHashCode; remote = $remoteTasksHashCode")

        val sList = invDao.getSamplesByDateRange(timeRange)
        val localSamplesHashCode = sList.sumOf { it.hashCode() }
        val remoteSamplesHashCode = invService.getSamplesHashCodeForDatePeriod(timeRange)
        if (localSamplesHashCode != remoteSamplesHashCode) syncSamples(timeRange)
        Log.d(TAG, "Samples: local = $localSamplesHashCode; remote = $remoteSamplesHashCode")

        val rList = invDao.getResultsByDateRange(timeRange)
        val localResultsHashCode = rList.sumOf { it.hashCode() }
        val remoteResultsHashCode = invService.getResultsHashCodeForDatePeriod(timeRange)
        if (localResultsHashCode != remoteResultsHashCode) syncResults(timeRange)
        Log.d(TAG, "Results: local = $localResultsHashCode; remote = $remoteResultsHashCode")

        return mList
    }

    /**
     * Inv deletion operations
     * */
    fun deleteOrder(orderId: Int): Flow<Event<Resource<Boolean>>> = crudeOperations.deleteRecord(
        orderId,
        { id -> invService.deleteOrder(id) },
        { id -> invDao.getOrderById(id) },
        { d -> invDao.deleteOrder(d) }
    )

    fun deleteSubOrder(subOrderId: Int): Flow<Event<Resource<Boolean>>> = crudeOperations.deleteRecord(
        subOrderId,
        { id -> invService.deleteSubOrder(id) },
        { id -> invDao.getSubOrderById(id) },
        { d -> invDao.deleteSubOrder(d) }
    )

    fun deleteSubOrderTask(taskId: Int): Flow<Event<Resource<Boolean>>> = crudeOperations.deleteRecord(
        taskId,
        { id -> invService.deleteSubOrderTask(id) },
        { id -> invDao.getSubOrderTaskById(id) },
        { d -> invDao.deleteSubOrderTask(d) }
    )

    fun deleteSample(sampleId: Int): Flow<Event<Resource<Boolean>>> = crudeOperations.deleteRecord(
        sampleId,
        { id -> invService.deleteSample(id) },
        { id -> invDao.getSampleById(id) },
        { d -> invDao.deleteSample(d) }
    )

    fun deleteResults(taskId: Int) = crudeOperations.deleteRecordsByParent(
        taskId,
        { pId -> invService.deleteResults(pId, 0) },
        { pId -> invDao.getResultsByTaskId(pId) },
        { d -> invDao.deleteResult(d) }
    )

    /**
     * Inv adding operations
     * */
    fun CoroutineScope.insertOrder(record: DomainOrder) = crudeOperations.run {
        insertRecord(
            record,
            { r -> invService.createOrder(r) },
            { r -> invDao.insertOrder(r) }
        )
    }

    fun CoroutineScope.insertSubOrder(record: DomainSubOrder) = crudeOperations.run {
        insertRecord(
            record,
            { r -> invService.createSubOrder(r) },
            { r -> invDao.insertSubOrder(r) }
        )
    }

    fun CoroutineScope.insertTask(record: DomainSubOrderTask) = crudeOperations.run {
        insertRecord(
            record,
            { r -> invService.createSubOrderTask(r) },
            { r -> invDao.insertSubOrderTask(r) }
        )
    }

    fun CoroutineScope.insertSample(record: DomainSample) = crudeOperations.run {
        insertRecord(
            record,
            { r -> invService.createSample(r) },
            { r -> invDao.insertSample(r) }
        )
    }

    fun CoroutineScope.insertResults(records: List<DomainResult>) = crudeOperations.run {
        insertRecords(
            records,
            { list -> invService.createResults(list) },
            { list -> invDao.insertResultsAll(list) },
        )
    }

    fun CoroutineScope.updateOrder(record: DomainOrder) = crudeOperations.run {
        updateRecord(
            record,
            { id, r -> invService.editOrder(id, r) },
            { r -> invDao.updateOrder(r) }
        )
    }

    fun CoroutineScope.updateSubOrder(record: DomainSubOrder) = crudeOperations.run {
        updateRecord(
            record,
            { id, r -> invService.editSubOrder(id, r) },
            { r -> invDao.updateSubOrder(r) }
        )
    }

    fun CoroutineScope.updateTask(record: DomainSubOrderTask) = crudeOperations.run {
        updateRecord(
            record,
            { id, r -> invService.editSubOrderTask(id, r) },
            { r -> invDao.updateSubOrderTask(r) }
        )
    }

    fun CoroutineScope.updateResult(record: DomainResult) = crudeOperations.run {
        updateRecord(
            record,
            { id, r -> invService.editResult(id, r) },
            { r -> invDao.updateResult(r) }
        )
    }

    fun CoroutineScope.getOrder(record: DomainOrder) = crudeOperations.run {
        getRecord(
            record,
            { id -> invService.getOrder(id) },
            { r -> invDao.updateOrder(r) }
        )
    }

    fun CoroutineScope.getSubOrder(record: DomainSubOrder) = crudeOperations.run {
        getRecord(
            record,
            { id -> invService.getSubOrder(id) },
            { r -> invDao.updateSubOrder(r) }
        )
    }

    fun CoroutineScope.getTask(record: DomainSubOrderTask) = crudeOperations.run {
        getRecord(
            record,
            { id -> invService.getSubOrderTask(id) },
            { r -> invDao.updateSubOrderTask(r) }
        )
    }

//    ToDO - change this part to return exactly what is needed

    suspend fun getOrderById(id: Int): DomainOrder =
        invDao.getOrderById(id.toString()).let {
            it?.toDomainModel() ?: throw IOException("no such order in local DB")
        }


    suspend fun getSubOrderById(id: Int): DomainSubOrder =
        invDao.getSubOrderById(id.toString()).let {
            it?.toDomainModel() ?: throw IOException("no such sub order in local DB")
        }


    suspend fun getTasksBySubOrderId(subOrderId: Int): List<DomainSubOrderTask> {
        val list = invDao.getTasksBySubOrderId(subOrderId.toString())
        return list.map { it.toDomainModel() }
    }

    suspend fun getSamplesBySubOrderId(subOrderId: Int): List<DomainSample> {
        val list = invDao.getSamplesBySubOrderId(subOrderId)
        return list.map { it.toDomainModel() }
    }

//    -------------------------------------------------------------

    fun investigationStatuses(): Flow<List<DomainOrdersStatus>> =
        invDao.getOrdersStatusesFlow().map { list ->
            list.map { it.toDomainModel() }
        }.flowOn(Dispatchers.IO).conflate()

    suspend fun latestLocalOrderId(): Int {
        val localLatestOrderDate = invDao
            .getLatestOrderDate() ?: NoRecord.num.toLong()
        return invDao.getLatestOrderId(localLatestOrderDate) ?: NoRecord.num
    }

    suspend fun ordersListByLastVisibleId(lastVisibleId: Int): Flow<List<DomainOrderComplete>> {
        val dbOrder = invDao.getOrderById(lastVisibleId.toString())
        return if (dbOrder != null)
            invDao.ordersListByLastVisibleId(dbOrder.createdDate).map { list ->
                list.map { it.toDomainModel() }
            }
        else flow { emit(listOf()) }
    }

    fun subOrdersRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderComplete>> =
        invDao.getSubOrdersByDateRange(pair).map { list ->
            list.map { it.toDomainModel() }
        }

    fun tasksRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderTaskComplete>> =
        invDao.getTasksDateRange(pair).map { list ->
            list.map { it.toDomainModel() }
        }

    fun samplesRangeList(subOrderId: Int): Flow<List<DomainSampleComplete>> =
        invDao.getSamplesBySubOrder(subOrderId).map { list ->
            list.map { it.toDomainModel() }
        }

    fun resultsRangeList(subOrderId: Int): Flow<List<DomainResultComplete>> =
        invDao.getResultsBySubOrder(subOrderId).map { list ->
            list.map { it.toDomainModel() }
        }


    /**
     * New order related data
     * */
    val inputForOrder: LiveData<List<DomainInputForOrder>> =
        invDao.getInputForOrder().map { list ->
            list.map { it.toDomainModel() }.sortedBy { item -> item.depOrder }
        }

    val investigationTypes: LiveData<List<DomainOrdersType>> =
        invDao.getOrdersTypes().map { list ->
            list.map { it.toDomainModel() }
        }

    val investigationReasons: LiveData<List<DomainReason>> =
        invDao.getMeasurementReasons().map { list ->
            list.map { it.toDomainModel() }
        }

    val orders: LiveData<List<DomainOrder>> =
        invDao.getOrders().map { list ->
            list.map { it.toDomainModel() }
        }
}