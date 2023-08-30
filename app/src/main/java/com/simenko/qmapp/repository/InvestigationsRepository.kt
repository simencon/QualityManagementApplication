package com.simenko.qmapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.other.Status
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
        database.inputForOrderDao
    ) { invService.getInputForOrder() }

    suspend fun syncOrdersStatuses() = crudeOperations.syncRecordsAll(
        database.orderStatusDao
    ) { invService.getOrdersStatuses() }

    suspend fun syncInvestigationReasons() = crudeOperations.syncRecordsAll(
        database.measurementReasonDao
    ) { invService.getMeasurementReasons() }

    suspend fun syncInvestigationTypes() = crudeOperations.syncRecordsAll(
        database.investigationTypeDao
    ) { invService.getOrdersTypes() }

    suspend fun syncResultsDecryptions() = crudeOperations.syncRecordsAll(
        database.resultDecryptionDao
    ) { invService.getResultsDecryptions() }

    /**
     * Investigations sync work
     * */
    suspend fun syncOrders(timeRange: Pair<Long, Long>) = crudeOperations.syncRecordsByTimeRange(
        timeRange,
        database.orderDao
    ) { tr -> invService.getOrdersByDateRange(tr) }

    suspend fun syncSubOrders(timeRange: Pair<Long, Long>): List<NotificationData> = crudeOperations.syncStatusRecordsByTimeRange(
        timeRange,
        database.subOrderDao,
        { id -> database.subOrderDao.getRecordByIdComplete(id) },
    ) { tr -> invService.getSubOrdersByDateRange(tr) }

    suspend fun syncSubOrderTasks(timeRange: Pair<Long, Long>) = crudeOperations.syncRecordsByTimeRange(
        timeRange,
        database.taskDao
    ) { tr -> invService.getTasksDateRange(tr) }

    suspend fun syncSamples(timeRange: Pair<Long, Long>) = crudeOperations.syncRecordsByTimeRange(
        timeRange,
        database.sampleDao
    ) { tr -> invService.getSamplesByDateRange(tr) }

    suspend fun syncResults(timeRange: Pair<Long, Long>) = crudeOperations.syncRecordsByTimeRange(
        timeRange,
        database.resultDao,
    ) { tr -> invService.getResultsByDateRange(tr) }

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
        responseHandlerForService { invService.getLatestOrderDate() }
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
        runBlocking {
            crudeOperations.run {
                responseHandlerForService { invService.getOrdersHashCodeForDatePeriod(timeRange) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        if (resource.status == Status.SUCCESS)
                            if (database.orderDao.getRecordsByTimeRange(timeRange).sumOf { it.hashCode() } != resource.data?.toInt())
                                syncOrders(timeRange)
                    }
                }
                responseHandlerForService { invService.getSubOrdersHashCodeForDatePeriod(timeRange) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        if (resource.status == Status.SUCCESS)
                            if (database.subOrderDao.getRecordsByTimeRange(timeRange).sumOf { it.hashCode() } != resource.data?.toInt())
                                mList.addAll(syncSubOrders(timeRange))
                    }
                }
                responseHandlerForService { invService.getTasksHashCodeForDatePeriod(timeRange) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        if (resource.status == Status.SUCCESS)
                            if (database.taskDao.getRecordsByTimeRange(timeRange).sumOf { it.hashCode() } != resource.data?.toInt())
                                syncSubOrderTasks(timeRange)
                    }
                }
                responseHandlerForService { invService.getSamplesHashCodeForDatePeriod(timeRange) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        if (resource.status == Status.SUCCESS)
                            if (database.sampleDao.getRecordsByTimeRange(timeRange).sumOf { it.hashCode() } != resource.data?.toInt())
                                syncSamples(timeRange)
                    }
                }
                responseHandlerForService { invService.getResultsHashCodeForDatePeriod(timeRange) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        if (resource.status == Status.SUCCESS)
                            if (database.resultDao.getRecordsByTimeRange(timeRange).sumOf { it.hashCode() } != resource.data?.toInt())
                                syncResults(timeRange)
                    }
                }
            }
        }
        return mList
    }

    /**
     * Inv deletion operations
     * */
    fun CoroutineScope.deleteOrder(orderId: Int): ReceiveChannel<Event<Resource<DomainOrder>>> = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.deleteOrder(orderId) }
        ) { r -> database.orderDao.deleteRecord(r) }
    }

    fun CoroutineScope.deleteSubOrder(subOrderId: Int): ReceiveChannel<Event<Resource<DomainSubOrder>>> = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.deleteSubOrder(subOrderId) }
        ) { r -> database.subOrderDao.deleteRecord(r) }
    }

    fun CoroutineScope.deleteSubOrderTask(taskId: Int): ReceiveChannel<Event<Resource<DomainSubOrderTask>>> = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.deleteSubOrderTask(taskId) }
        ) { r -> database.taskDao.deleteRecord(r) }
    }

    fun CoroutineScope.deleteSample(sampleId: Int): ReceiveChannel<Event<Resource<DomainSample>>> = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.deleteSample(sampleId) }
        ) { r -> database.sampleDao.deleteRecord(r) }
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
            taskExecutor = { invService.createOrder(record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.orderDao.insertRecord(r) }
    }

    fun CoroutineScope.insertSubOrder(record: DomainSubOrder) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.createSubOrder(record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.subOrderDao.insertRecord(r) }
    }

    fun CoroutineScope.insertTask(record: DomainSubOrderTask) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.createSubOrderTask(record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.taskDao.insertRecord(r) }
    }

    fun CoroutineScope.insertSample(record: DomainSample) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.createSample(record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.sampleDao.insertRecord(r) }
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
            taskExecutor = { invService.editOrder(record.id, record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.orderDao.updateRecord(r) }
    }

    fun CoroutineScope.updateSubOrder(record: DomainSubOrder) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.editSubOrder(record.id, record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.subOrderDao.updateRecord(r) }
    }

    fun CoroutineScope.updateTask(record: DomainSubOrderTask) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.editSubOrderTask(record.id, record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.taskDao.updateRecord(r) }
    }

    fun CoroutineScope.updateResult(record: DomainResult) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { invService.editResult(record.id, record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.resultDao.updateRecord(r) }
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
            database.orderDao.ordersListByLastVisibleIdForUI(dbOrder.createdDate).map { list ->
                list.map { it.toDomainModel() }
            }
        else flow { emit(listOf()) }
    }

    fun subOrdersRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderComplete>> =
        database.subOrderDao.getRecordsByTimeRangeForUI(pair).map { list ->
            list.map { it.toDomainModel() }
        }

    fun tasksRangeList(subOrderId: Int): Flow<List<DomainSubOrderTaskComplete>> =
        database.taskDao.getRecordsByParentIdForUI(subOrderId).map { list ->
            list.map { it.toDomainModel() }
        }

    fun samplesRangeList(subOrderId: Int): Flow<List<DomainSampleComplete>> =
        database.sampleDao.getRecordsByParentIdForUI(subOrderId).map { list ->
            list.map { it.toDomainModel() }
        }

    fun resultsRangeList(taskId: Int, sampleId: Int): Flow<List<DomainResultComplete>> =
        database.resultDao.getRecordsByParentIdForUI(taskId, sampleId).map { list ->
            list.map { it.toDomainModel() }
        }

    /**
     * New order related data
     * */
    val inputForOrder: Flow<List<DomainInputForOrder>> =
        database.inputForOrderDao.getRecordsFlowForUI().map { list ->
            list.map { it.toDomainModel() }.sortedBy { item -> item.depOrder }
        }

    val getOrderTypes: Flow<List<DomainOrdersType>> =
        database.investigationTypeDao.getRecordsFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }

    val getOrderReasons: Flow<List<DomainReason>> =
        database.measurementReasonDao.getRecordsFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }

    val orders: LiveData<List<DomainOrder>> =
        database.orderDao.getRecordsForUI().map { list ->
            list.map { it.toDomainModel() }
        }
}