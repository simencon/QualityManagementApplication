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
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.simenko.qmapp.utils.InvestigationsUtils.getOrdersRange
import com.simenko.qmapp.utils.NotificationData
import com.simenko.qmapp.works.SyncPeriods
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "InvestigationsRepository"

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class InvestigationsRepository @Inject constructor(
    private val database: QualityManagementDB,
    private val invService: InvestigationsService,
    private val crudeOperations: CrudeOperations
) {

    /**
     * Investigations meta data sync work
     * */
    suspend fun syncInputForOrder() = crudeOperations.syncRecordsAll(
        { invService.getInputForOrder() },
        database.inputForOrderDao
    )

    suspend fun syncOrdersStatuses() = crudeOperations.syncRecordsAll(
        { invService.getOrdersStatuses() },
        database.orderStatusDao
    )

    suspend fun syncInvestigationReasons() = crudeOperations.syncRecordsAll(
        { invService.getMeasurementReasons() },
        database.measurementReasonDao
    )

    suspend fun syncInvestigationTypes() = crudeOperations.syncRecordsAll(
        { invService.getOrdersTypes() },
        database.investigationTypeDao
    )

    suspend fun syncResultsDecryptions() = crudeOperations.syncRecordsAll(
        { invService.getResultsDecryptions() },
        database.resultDecryptionDao
    )

    /**
     * Investigations sync work
     * */
    suspend fun syncOrders(timeRange: Pair<Long, Long>) = crudeOperations.syncRecordsByTimeRange(
        timeRange,
        { tr -> invService.getOrdersByDateRange(tr) },
        database.orderDao
    )

    suspend fun syncSubOrders(timeRange: Pair<Long, Long>): List<NotificationData> = crudeOperations.syncStatusRecordsByTimeRange(
        timeRange,
        { tr -> invService.getSubOrdersByDateRange(tr) },
        { id -> database.subOrderDao.getRecordByIdComplete(id) },
        database.subOrderDao
    )

    suspend fun syncSubOrderTasks(timeRange: Pair<Long, Long>) = crudeOperations.syncRecordsByTimeRange(
        timeRange,
        { tr -> invService.getTasksDateRange(tr) },
        database.taskDao
    )

    suspend fun syncSamples(timeRange: Pair<Long, Long>) = crudeOperations.syncRecordsByTimeRange(
        timeRange,
        { tr -> invService.getSamplesByDateRange(tr) },
        database.sampleDao
    )

    suspend fun syncResults(timeRange: Pair<Long, Long>) = crudeOperations.syncRecordsByTimeRange(
        timeRange,
        { tr -> invService.getResultsByDateRange(tr) },
        database.resultDao
    )

    /**
     * Investigations sync logic
     * */
    private fun insertInvEntities(ntOrders: List<NetworkOrder>) {
        runBlocking {
            val timeRange = ntOrders.getOrdersRange()
            crudeOperations.run {
                responseHandlerForListOfRecords(
                    taskExecutor = { Response.success(ntOrders) }
                ) { list -> database.orderDao.insertRecords(list) }.consumeEach { }
                responseHandlerForListOfRecords(
                    taskExecutor = { invService.getSubOrdersByDateRange(timeRange) }
                ) { list -> database.subOrderDao.insertRecords(list) }.consumeEach { }
                responseHandlerForListOfRecords(
                    taskExecutor = { invService.getTasksDateRange(timeRange) }
                ) { list -> database.taskDao.insertRecords(list) }.consumeEach { }
                responseHandlerForListOfRecords(
                    taskExecutor = { invService.getSamplesByDateRange(timeRange) }
                ) { list -> database.sampleDao.insertRecords(list) }.consumeEach { }
                responseHandlerForListOfRecords(
                    taskExecutor = { invService.getResultsByDateRange(timeRange) }
                ) { list -> database.resultDao.insertRecords(list) }.consumeEach { }
            }
        }
    }

    fun CoroutineScope.getRemoteLatestOrderDate() = crudeOperations.run {
        responseHandlerForService() { invService.getLatestOrderDate() }
    }

    fun CoroutineScope.uploadNewInvestigations(rmDate: Long): ReceiveChannel<Event<Resource<List<DomainOrder>>>> {
        crudeOperations.run {
            database.orderDao.getLatestOrderDate().also { locDate ->
                val finalLocalDate = locDate ?: (rmDate - SyncPeriods.LAST_DAY.latestMillis)
                return if (rmDate > finalLocalDate) {
                    responseHandlerForListOfRecords(
                        taskExecutor = { invService.getOrdersByDateRange(Pair(finalLocalDate, rmDate)) }
                    ) { list -> insertInvEntities(list.map { it.toNetworkModel() }) }
                } else {
                    produce { send(Event(Resource.success(emptyList()))) }
                }
            }
        }
    }

    fun CoroutineScope.uploadOldInvestigations(earliestOrderDate: Long) = crudeOperations.run {
        if (earliestOrderDate == database.orderDao.getEarliestOrderDate())
            responseHandlerForListOfRecords(
                taskExecutor = { invService.getEarliestOrdersByStartingOrderDate(earliestOrderDate) }
            ) { list -> insertInvEntities(list.map { it.toNetworkModel() }) }
        else {
            produce { send(Event(Resource.success(emptyList()))) }
        }
    }

    fun getCompleteOrdersRange(): Pair<Long, Long> {
        return Pair(
            database.orderDao.getEarliestOrderDate() ?: NoRecord.num.toLong(),
            database.orderDao.getLatestOrderDate() ?: NoRecord.num.toLong()
        )
    }

    suspend fun syncInvEntitiesByTimeRange(timeRange: Pair<Long, Long>): List<NotificationData> {
        val mList = mutableListOf<NotificationData>()

        val oList = database.orderDao.getRecordsByTimeRange(timeRange)
        val localOrdersHashCode = oList.sumOf { it.hashCode() }
        val remoteOrdersHashCode = invService.getOrdersHashCodeForDatePeriod(timeRange)
        if (localOrdersHashCode != remoteOrdersHashCode) syncOrders(timeRange)
        Log.d(TAG, "Orders: local = $localOrdersHashCode; remote = $remoteOrdersHashCode")

        val soList = database.subOrderDao.getRecordsByTimeRange(timeRange)
        val localSubOrdersHashCode = soList.sumOf { it.hashCode() }
        val remoteSubOrdersHashCode = invService.getSubOrdersHashCodeForDatePeriod(timeRange)
        if (localSubOrdersHashCode != remoteSubOrdersHashCode) mList.addAll(syncSubOrders(timeRange))
        Log.d(TAG, "SubOrders: local = $localSubOrdersHashCode; remote = $remoteSubOrdersHashCode")

        val tList = database.taskDao.getRecordsByTimeRange(timeRange)
        val localTasksHashCode = tList.sumOf { it.hashCode() }
        val remoteTasksHashCode = invService.getTasksHashCodeForDatePeriod(timeRange)
        if (localTasksHashCode != remoteTasksHashCode) syncSubOrderTasks(timeRange)
        Log.d(TAG, "Tasks: local = $localTasksHashCode; remote = $remoteTasksHashCode")

        val sList = database.sampleDao.getRecordsByTimeRange(timeRange)
        val localSamplesHashCode = sList.sumOf { it.hashCode() }
        val remoteSamplesHashCode = invService.getSamplesHashCodeForDatePeriod(timeRange)
        if (localSamplesHashCode != remoteSamplesHashCode) syncSamples(timeRange)
        Log.d(TAG, "Samples: local = $localSamplesHashCode; remote = $remoteSamplesHashCode")

        val rList = database.resultDao.getRecordsByTimeRange(timeRange)
        val localResultsHashCode = rList.sumOf { it.hashCode() }
        val remoteResultsHashCode = invService.getResultsHashCodeForDatePeriod(timeRange)
        if (localResultsHashCode != remoteResultsHashCode) syncResults(timeRange)
        Log.d(TAG, "Results: local = $localResultsHashCode; remote = $remoteResultsHashCode")

        return mList
    }

    /**
     * Inv deletion operations
     * */
    fun CoroutineScope.deleteOrder(orderId: Int): ReceiveChannel<Event<Resource<DomainOrder>>> = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.deleteOrder(orderId) },
            resultHandler = { r -> database.orderDao.deleteRecord(r) }
        )
    }

    fun CoroutineScope.deleteSubOrder(subOrderId: Int): ReceiveChannel<Event<Resource<DomainSubOrder>>> = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.deleteSubOrder(subOrderId) },
            resultHandler = { r -> database.subOrderDao.deleteRecord(r) }
        )
    }

    fun CoroutineScope.deleteSubOrderTask(taskId: Int): ReceiveChannel<Event<Resource<DomainSubOrderTask>>> = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.deleteSubOrderTask(taskId) },
            resultHandler = { r -> database.taskDao.deleteRecord(r) }
        )
    }

    fun CoroutineScope.deleteSample(sampleId: Int): ReceiveChannel<Event<Resource<DomainSample>>> = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.deleteSample(sampleId) },
            resultHandler = { r -> database.sampleDao.deleteRecord(r) }
        )
    }

    fun CoroutineScope.deleteResults(taskId: Int) = crudeOperations.run {
        responseHandlerForListOfRecords(
            taskExecutor = { invService.deleteResults(taskId) }
        ) { r -> database.resultDao.deleteRecords(r) }
    }

    /**
     * Inv create operations
     * */
    fun CoroutineScope.insertOrder(record: DomainOrder) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.createOrder(record.toDatabaseModel().toNetworkModel()) },
            resultHandler = { r -> database.orderDao.insertRecord(r) }
        )
    }

    fun CoroutineScope.insertSubOrder(record: DomainSubOrder) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.createSubOrder(record.toDatabaseModel().toNetworkModel()) },
            resultHandler = { r -> database.subOrderDao.insertRecord(r) }
        )
    }

    fun CoroutineScope.insertTask(record: DomainSubOrderTask) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.createSubOrderTask(record.toDatabaseModel().toNetworkModel()) },
            resultHandler = { r -> database.taskDao.insertRecord(r) }
        )
    }

    fun CoroutineScope.insertSample(record: DomainSample) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.createSample(record.toDatabaseModel().toNetworkModel()) },
            resultHandler = { r -> database.sampleDao.insertRecord(r) }
        )
    }

    fun CoroutineScope.insertResults(records: List<DomainResult>) = crudeOperations.run {
        responseHandlerForListOfRecords(
            taskExecutor = { invService.createResults(records.map { it.toDatabaseModel().toNetworkModel() }) }
        ) { r -> database.resultDao.insertRecords(r) }
    }

    /**
     * Inv update operations
     * */
    fun CoroutineScope.updateOrder(record: DomainOrder) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.editOrder(record.id, record.toDatabaseModel().toNetworkModel()) },
            resultHandler = { r -> database.orderDao.updateRecord(r) }
        )
    }

    fun CoroutineScope.updateSubOrder(record: DomainSubOrder) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.editSubOrder(record.id, record.toDatabaseModel().toNetworkModel()) },
            resultHandler = { r -> database.subOrderDao.updateRecord(r) }
        )
    }

    fun CoroutineScope.updateTask(record: DomainSubOrderTask) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.editSubOrderTask(record.id, record.toDatabaseModel().toNetworkModel()) },
            resultHandler = { r -> database.taskDao.updateRecord(r) }
        )
    }

    fun CoroutineScope.updateResult(record: DomainResult) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.editResult(record.id, record.toDatabaseModel().toNetworkModel()) },
            resultHandler = { r -> database.resultDao.updateRecord(r) }
        )
    }

    /**
     * Inv read operations
     * */
    fun CoroutineScope.getOrder(record: DomainOrder) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.getOrder(record.id) },
            resultHandler = { r -> database.orderDao.updateRecord(r) }
        )
    }

    fun CoroutineScope.getSubOrder(record: DomainSubOrder) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.getSubOrder(record.getRecordId().toString().toInt()) },
            resultHandler = { r -> database.subOrderDao.updateRecord(r) }
        )
    }

    fun CoroutineScope.getTask(record: DomainSubOrderTask) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.getSubOrderTask(record.getRecordId().toString().toInt()) },
            resultHandler = { r -> database.taskDao.updateRecord(r) }
        )
    }

//    ToDO - change this part to return exactly what is needed

    fun getOrderById(id: Int): DomainOrder =
        database.orderDao.getRecordById(id.toString()).let {
            it?.toDomainModel() ?: throw IOException("no such order in local DB")
        }


    fun getSubOrderById(id: Int): DomainSubOrder =
        database.subOrderDao.getRecordById(id.toString()).let {
            it?.toDomainModel() ?: throw IOException("no such sub order in local DB")
        }


    fun getTasksBySubOrderId(subOrderId: Int): List<DomainSubOrderTask> {
        val list = database.taskDao.getRecordsByParentId(subOrderId)
        return list.map { it.toDomainModel() }
    }

    fun getSamplesBySubOrderId(subOrderId: Int): List<DomainSample> {
        val list = database.sampleDao.getRecordsByParentId(subOrderId)
        return list.map { it.toDomainModel() }
    }

//    -------------------------------------------------------------

    fun investigationStatuses(): Flow<List<DomainOrdersStatus>> =
        database.orderStatusDao.getRecordsFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }.flowOn(Dispatchers.IO).conflate()

    fun latestLocalOrderId(): Int {
        val localLatestOrderDate = database.orderDao.getLatestOrderDate() ?: NoRecord.num.toLong()
        return database.orderDao.getLatestOrderId(localLatestOrderDate) ?: NoRecord.num
    }

    suspend fun ordersListByLastVisibleId(lastVisibleId: Int): Flow<List<DomainOrderComplete>> {
        val dbOrder = database.orderDao.getRecordById(lastVisibleId.toString())
        return if (dbOrder != null)
            database.orderDao.ordersListByLastVisibleId(dbOrder.createdDate).map { list ->
                list.map { it.toDomainModel() }
            }
        else flow { emit(listOf()) }
    }

    fun subOrdersRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderComplete>> =
        database.subOrderDao.getRecordsByTimeRangeForUI(pair).map { list ->
            list.map { it.toDomainModel() }
        }

    fun tasksRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderTaskComplete>> =
        database.taskDao.getRecordsByTimeRangeForUI(pair).map { list ->
            list.map { it.toDomainModel() }
        }

    fun samplesRangeList(subOrderId: Int): Flow<List<DomainSampleComplete>> =
        database.sampleDao.getRecordsByParentIdForUI(subOrderId).map { list ->
            list.map { it.toDomainModel() }
        }

    fun resultsRangeList(subOrderId: Int): Flow<List<DomainResultComplete>> =
        database.resultDao.getRecordsByParentIdForUI(subOrderId).map { list ->
            list.map { it.toDomainModel() }
        }


    /**
     * New order related data
     * */
    val inputForOrder: LiveData<List<DomainInputForOrder>> =
        database.inputForOrderDao.getRecordsForUI().map { list ->
            list.map { it.toDomainModel() }.sortedBy { item -> item.depOrder }
        }

    val investigationTypes: LiveData<List<DomainOrdersType>> =
        database.investigationTypeDao.getRecordsForUI().map { list ->
            list.map { it.toDomainModel() }
        }

    val investigationReasons: LiveData<List<DomainReason>> =
        database.measurementReasonDao.getRecordsForUI().map { list ->
            list.map { it.toDomainModel() }
        }

    val orders: LiveData<List<DomainOrder>> =
        database.orderDao.getRecordsForUI().map { list ->
            list.map { it.toDomainModel() }
        }
}