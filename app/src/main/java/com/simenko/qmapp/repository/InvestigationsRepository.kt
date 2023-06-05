package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.other.Constants.INITIAL_UPDATE_PERIOD_H
import com.simenko.qmapp.repository.contract.InvRepository
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.InvestigationsService
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.InvestigationsDao
import com.simenko.qmapp.utils.InvestigationsUtils.getOrdersRange
import com.simenko.qmapp.utils.ListTransformer
import com.simenko.qmapp.utils.NotificationData
import com.simenko.qmapp.utils.NotificationReasons
import com.simenko.qmapp.utils.StringUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "InvestigationsRepository"

@OptIn(ExperimentalCoroutinesApi::class)
class InvestigationsRepository @Inject constructor(
    private val invDao: InvestigationsDao,
    private val invService: InvestigationsService
) : InvRepository {
    private val timeFormatter = DateTimeFormatter.ISO_INSTANT

    suspend fun checkAndUploadNew() {
        var remoteLatestDate = NoSelectedRecord.num.toLong()
        val localLatestDate = invDao.getLatestOrderDateEpoch() ?: NoSelectedRecord.num.toLong()
        invService.getLatestOrderDateEpoch().apply {
            if (isSuccessful)
                remoteLatestDate = this.body() ?: NoSelectedRecord.num.toLong()
        }
        val oneDayEgo = Instant.now()
            .minusMillis(1000L * 60L * 60L * INITIAL_UPDATE_PERIOD_H)
            .toEpochMilli()
        if (remoteLatestDate > localLatestDate)
            uploadNewOrders(if (oneDayEgo > localLatestDate) oneDayEgo else localLatestDate)
    }

    suspend fun checkAndUploadPrevious(earliestOrderDate: Long): Boolean =
        earliestOrderDate == invDao.getEarliestOrderDateEpoch()

    override suspend fun getCompleteOrdersRange(): Pair<Long, Long> {
        return Pair(
            invDao.getEarliestOrderDateEpoch() ?: NoSelectedRecord.num.toLong(),
            invDao.getLatestOrderDateEpoch() ?: NoSelectedRecord.num.toLong()
        )
    }

    /**
     * Update Investigations from the network
     */
    suspend fun refreshInputForOrder() {
        withContext(Dispatchers.IO) {
            val inputForOrder =
                invService.getInputForOrder()
            invDao.insertInputForOrderAll(
                ListTransformer(
                    inputForOrder,
                    NetworkInputForOrder::class,
                    DatabaseInputForOrder::class
                ).generateList()
            )
            Log.d(TAG, "refreshInputForOrder: ${timeFormatter.format(Instant.now())}")
        }
    }

    suspend fun refreshOrdersStatuses() {
        withContext(Dispatchers.IO) {
            val records = invService.getOrdersStatuses()
            invDao.insertOrdersStatusesAll(
                ListTransformer(
                    records,
                    NetworkOrdersStatus::class,
                    DatabaseOrdersStatus::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshOrdersStatuses: ${timeFormatter.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshInvestigationReasons() {
        withContext(Dispatchers.IO) {
            val records =
                invService.getMeasurementReasons()
            invDao.insertMeasurementReasonsAll(
                ListTransformer(
                    records,
                    NetworkReason::class, DatabaseReason::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshMeasurementReasons: ${timeFormatter.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshInvestigationTypes() {
        withContext(Dispatchers.IO) {
            val records = invService.getOrdersTypes()
            invDao.insertOrdersTypesAll(
                ListTransformer(
                    records,
                    NetworkOrdersType::class,
                    DatabaseOrdersType::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshOrdersTypes: ${timeFormatter.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshResultsDecryptions() {
        withContext(Dispatchers.IO) {
            val resultsDecryptions =
                invService.getResultsDecryptions()
            invDao.insertResultsDecryptionsAll(
                ListTransformer(
                    resultsDecryptions,
                    NetworkResultsDecryption::class, DatabaseResultsDecryption::class
                ).generateList()
            )
            Log.d(TAG, "refreshResultsDecryptions: ${timeFormatter.format(Instant.now())}")
        }
    }

    //    ToDo - get latest local order date and update with sync latest-now
    suspend fun uploadNewOrders(lastOrderDateEpoch: Long, uploadNewOrders: Boolean = true) {

        runBlocking {
            val ntOrders = if (uploadNewOrders)
                invService.getLatestOrdersByStartingOrderDate(lastOrderDateEpoch)
            else
                invService.getEarliestOrdersByStartingOrderDate(lastOrderDateEpoch)

            val newOrdersRangeDateEpoch = ntOrders.getOrdersRange()
            Log.d(TAG, "uploadNewOrders: ntOrders is uploaded")
            val ntSubOrders = invService.getSubOrdersByDateRange(newOrdersRangeDateEpoch)

            Log.d(TAG, "uploadNewOrders: ntSubOrders is uploaded")
            val ntTasks = invService.getTasksDateRange(newOrdersRangeDateEpoch)

            Log.d(TAG, "uploadNewOrders: ntTasks is uploaded")
            val ntSamples = invService.getSamplesByDateRange(newOrdersRangeDateEpoch)

            Log.d(TAG, "uploadNewOrders: ntSamples is uploaded")
            val ntResults = invService.getResultsByDateRange(newOrdersRangeDateEpoch)

            Log.d(TAG, "uploadNewOrders: ntResults is uploaded")
//        syncOrders(ntOrders, investigationsDao)
            invDao.insertOrdersAll(ntOrders.map { it.toDatabaseOrder() })
            Log.d(TAG, "uploadNewOrders: ntOrders is saved")
//        syncSubOrders(ntSubOrders, investigationsDao)
            invDao.insertSubOrdersAll(ntSubOrders.map { it.toDatabaseSubOrder() })
            Log.d(TAG, "uploadNewOrders: ntSubOrders is saved")
//        syncSubOrderTasks(ntTasks, investigationsDao)
            invDao.insertSubOrderTasksAll(ntTasks.map { it.toDatabaseSubOrderTask() })
            Log.d(TAG, "uploadNewOrders: ntTasks is saved")
//        syncSamples(ntSamples, investigationsDao)
            invDao.insertSamplesAll(ntSamples.map { it.toDatabaseSample() })
            Log.d(TAG, "uploadNewOrders: ntSamples is saved")
//        syncResults(ntResults, investigationsDao)
            invDao.insertResultsAll(ntResults.map { it.toDatabaseResult() })
            Log.d(TAG, "uploadNewOrders: ntResults is saved")
        }
    }

    /**
     * Investigations sync work
     * */
    suspend fun refreshOrders(timeRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            val ntOrders = invService.getOrdersByDateRange(timeRange)
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
                    invDao.insertOrder(ntIt.toDatabaseOrder())
                }
            }
            ntOrders.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        if (dbIt != ntIt.toDatabaseOrder())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateOrder(ntIt.toDatabaseOrder())
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

    private fun DatabaseSubOrderComplete.toNotificationData(reason: NotificationReasons): NotificationData {
        return NotificationData(
            orderId = subOrder.orderId,
            subOrderId = subOrder.id,
            orderNumber = orderShort.order.orderNumber,
            subOrderStatus = status.statusDescription,
            departmentAbbr = department.depAbbr,
            channelAbbr = channel.channelAbbr,
            itemTypeCompleteDesignation = StringUtils.concatTwoStrings1(
                StringUtils.concatTwoStrings3(
                    itemVersionComplete.itemComplete.key.componentKey,
                    itemVersionComplete.itemComplete.item.itemDesignation
                ),
                itemVersionComplete.itemVersion.versionDescription
            ),
            notificationReason = reason
        )
    }

    suspend fun refreshSubOrders(timeRange: Pair<Long, Long>): List<NotificationData> {
        val result = mutableListOf<NotificationData>()
        withContext(Dispatchers.IO) {
            val ntSubOrders = invService.getSubOrdersByDateRange(timeRange)
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
                    invDao.insertSubOrder(ntIt.toDatabaseSubOrder())
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
                        if (dbIt != ntIt.toDatabaseSubOrder())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateSubOrder(ntIt.toDatabaseSubOrder())
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

    suspend fun refreshSubOrderTasks(timeRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            val ntTasks = invService.getTasksDateRange(timeRange)
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
                    invDao.insertSubOrderTask(ntIt.toDatabaseSubOrderTask())
                }
            }
            ntTasks.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbTasks.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        if (dbIt != ntIt.toDatabaseSubOrderTask())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateSubOrderTask(ntIt.toDatabaseSubOrderTask())
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

    suspend fun refreshSamples(timeRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            val ntSamples = invService.getSamplesByDateRange(timeRange)
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
                    invDao.insertSample(ntIt.toDatabaseSample())
                }
            }
            ntSamples.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbSamples.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        if (dbIt != ntIt.toDatabaseSample())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateSample(ntIt.toDatabaseSample())
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

    suspend fun refreshResults(timeRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            val ntResults = invService.getResultsByDateRange(timeRange)
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
                    invDao.insertResult(ntIt.toDatabaseResult())
                }
            }
            ntResults.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbResults.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        if (dbIt != ntIt.toDatabaseResult())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateResult(ntIt.toDatabaseResult())
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

    //    ToDo - the main sync function - refactor it after deleting useless code
    override suspend fun refreshInvestigationsIfNecessary(timeRange: Pair<Long, Long>): List<NotificationData> {
        val mList = mutableListOf<NotificationData>()

        val oList = invDao.getOrdersByDateRange(timeRange)
        val localOrdersHashCode = oList.sumOf { it.hashCode() }
        val remoteOrdersHashCode = invService.getOrdersHashCodeForDatePeriod(timeRange)
        if (localOrdersHashCode != remoteOrdersHashCode) refreshOrders(timeRange)
        Log.d(TAG, "Orders: local = $localOrdersHashCode; remote = $remoteOrdersHashCode")

        val soList = invDao.getSubOrdersByDateRangeL(timeRange)
        val localSubOrdersHashCode = soList.sumOf { it.hashCode() }
        val remoteSubOrdersHashCode = invService.getSubOrdersHashCodeForDatePeriod(timeRange)
        if (localSubOrdersHashCode != remoteSubOrdersHashCode) mList.addAll(
            refreshSubOrders(
                timeRange
            )
        )
        Log.d(TAG, "SubOrders: local = $localSubOrdersHashCode; remote = $remoteSubOrdersHashCode")

        val tList = invDao.getTasksByDateRangeL(timeRange)
        val localTasksHashCode = tList.sumOf { it.hashCode() }
        val remoteTasksHashCode = invService.getTasksHashCodeForDatePeriod(timeRange)
        if (localTasksHashCode != remoteTasksHashCode) refreshSubOrderTasks(timeRange)
        Log.d(TAG, "Tasks: local = $localTasksHashCode; remote = $remoteTasksHashCode")

        val sList = invDao.getSamplesByDateRange(timeRange)
        val localSamplesHashCode = sList.sumOf { it.hashCode() }
        val remoteSamplesHashCode = invService.getSamplesHashCodeForDatePeriod(timeRange)
        if (localSamplesHashCode != remoteSamplesHashCode) refreshSamples(timeRange)
        Log.d(TAG, "Samples: local = $localSamplesHashCode; remote = $remoteSamplesHashCode")

        val rList = invDao.getResultsByDateRange(timeRange)
        val localResultsHashCode = rList.sumOf { it.hashCode() }
        val remoteResultsHashCode = invService.getResultsHashCodeForDatePeriod(timeRange)
        if (localResultsHashCode != remoteResultsHashCode) refreshResults(timeRange)
        Log.d(TAG, "Results: local = $localResultsHashCode; remote = $remoteResultsHashCode")

        return mList
    }

    //    ToDo - must delete in local after remote deletion
    suspend fun deleteOrder(orderId: Int) {
        withContext(Dispatchers.IO) {
            invService.deleteOrder(orderId)
        }
    }

    suspend fun deleteSubOrder(subOrderId: Int) {
        withContext(Dispatchers.IO) {
            invService.deleteSubOrder(subOrderId)
        }
    }

    suspend fun deleteSubOrderTask(taskId: Int) {
        withContext(Dispatchers.IO) {
            invService.deleteSubOrderTask(taskId)
        }
    }

    suspend fun deleteSample(sample: DomainSample) {
        withContext(Dispatchers.IO) {
            invService.deleteSample(sample.id)
        }
    }

    suspend fun deleteResults(charId: Int = 0, id: Int = 0) {
        withContext(Dispatchers.IO) {
            invService.deleteResults(charId, id)
        }
    }

//    ToDo - stay with single constructor, with Spring "withoutId" - is useless
    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainOrder) =
        coroutineScope.produce {
            val newOrder = invService.createOrder(record.toNetworkOrder()).toDatabaseOrder()
            invDao.insertOrder(newOrder)
            send(newOrder.toDomainOrder()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

//    ToDo - stay with single constructor, with Spring "withoutId" - is useless
    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) =
        coroutineScope.produce {
            val newRecord = invService.createSubOrder(record.toNetworkSubOrder()).toDatabaseSubOrder()
            invDao.insertSubOrder(newRecord)
            send(newRecord.toDomainSubOrder()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

//    ToDo - stay with single constructor, with Spring "withoutId" - is useless
    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) =
        coroutineScope.produce {
            val newRecord = invService.createSubOrderTask(record.toNetworkSubOrderTask())
                .toDatabaseSubOrderTask()
            invDao.insertSubOrderTask(newRecord)
            send(newRecord.toDomainSubOrderTask()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

//    ToDo - stay with single constructor, with Spring "withoutId" - is useless
    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSample) =
        coroutineScope.produce {
            val newRecord = invService.createSample(record.toNetworkSample()).toDatabaseSample()
            invDao.insertSample(newRecord)
            send(newRecord.toDomainSample()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

//    ToDo - stay with single constructor, with Spring "withoutId" - is useless
    suspend fun getCreatedRecords(coroutineScope: CoroutineScope, records: List<DomainResult>) =
        coroutineScope.produce {
            val newRecords = invService.createResults(records.map { it.toNetworkResult() })
                .map { it.toDatabaseResult() }
            invDao.insertResultsAll(newRecords)
            send(newRecords.map { it.toDomainResult() }) //cold send, can be this.trySend(l).isSuccess //hot send
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainOrder) =
        coroutineScope.produce {
            val nOrder = record.toNetworkOrder()
            invService.editOrder(record.id, nOrder)
            invDao.updateOrder(record.toDatabaseOrder())
            send(record)
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) =
        coroutineScope.produce {
            val nSubOrder = record.toNetworkSubOrder()
            invService.editSubOrder(record.id, nSubOrder)
            invDao.updateSubOrder(record.toDatabaseSubOrder())
            send(record)
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) =
        coroutineScope.produce {
            val nSubOrderTask = record.toNetworkSubOrderTask()
            invService.editSubOrderTask(record.id, nSubOrderTask)

            val dSubOrderTask = invService.getSubOrderTask(record.id).toDatabaseSubOrderTask()
            invDao.updateSubOrderTask(dSubOrderTask)

            send(dSubOrderTask.toDomainSubOrderTask())
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainResult) =
        coroutineScope.produce {
            val nNetwork = record.toNetworkResult()
            invService.editResult(record.id, nNetwork)
            invDao.updateResult(record.toDatabaseResult())
            send(record)
        }

    fun getRecord(coroutineScope: CoroutineScope, record: DomainOrder) =
        coroutineScope.produce {
            val nOrder = invService.getOrder(record.id)
            invDao.updateOrder(nOrder.toDatabaseOrder())
            send(nOrder.toDomainOrder())
        }

    fun getRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) =
        coroutineScope.produce {
            val nSubOrder = invService.getSubOrder(record.id)
            invDao.updateSubOrder(nSubOrder.toDatabaseSubOrder())
            send(nSubOrder.toDomainSubOrder())
        }

    fun getRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) =
        coroutineScope.produce {
            val nSubOrderTask = invService.getSubOrderTask(record.id)
            invDao.updateSubOrderTask(nSubOrderTask.toDatabaseSubOrderTask())
            send(nSubOrderTask.toDomainSubOrderTask())
        }

//    ToDO - change this part to return exactly what is needed

    suspend fun getOrderById(id: Int): DomainOrder? {
        return invDao.getOrderById(id.toString())?.toDomainOrder()
    }

    suspend fun getSubOrderById(id: Int): DomainSubOrder {
        return invDao.getSubOrderById(id.toString()).toDomainSubOrder()
    }

    suspend fun getTasksBySubOrderId(subOrderId: Int): List<DomainSubOrderTask> {
        val list = invDao.getTasksBySubOrderId(subOrderId.toString())
        return ListTransformer(
            list,
            DatabaseSubOrderTask::class, DomainSubOrderTask::class
        ).generateList()
    }

    suspend fun getSamplesBySubOrderId(subOrderId: Int): List<DomainSample> {
        val list = invDao.getSamplesBySubOrderId(subOrderId)
        return ListTransformer(
            list,
            DatabaseSample::class, DomainSample::class
        ).generateList()
    }

//    -------------------------------------------------------------

    fun investigationStatuses(): Flow<List<DomainOrdersStatus>> =
        invDao.getOrdersStatusesFlow().map {
            ListTransformer(
                it,
                DatabaseOrdersStatus::class,
                DomainOrdersStatus::class
            ).generateList()
        }.flowOn(Dispatchers.IO).conflate()

    suspend fun latestLocalOrderId(): Int {
        val localLatestOrderDate = invDao
            .getLatestOrderDateEpoch() ?: NoSelectedRecord.num.toLong()
        return invDao.getLatestOrderId(localLatestOrderDate) ?: NoSelectedRecord.num
    }

    suspend fun ordersListByLastVisibleId(lastVisibleId: Int): Flow<List<DomainOrderComplete>> {
        val dbOrder = invDao.getOrderById(lastVisibleId.toString())
        return if (dbOrder != null)
            invDao.ordersListByLastVisibleId(dbOrder.createdDate).map {
                it.asDomainOrdersComplete()
            }
        else flow { emit(listOf()) }
    }

    fun subOrdersRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderComplete>> =
        invDao.getSubOrdersByDateRange(pair).map {
            it.asDomainSubOrderDetailed()
        }

    fun tasksRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderTaskComplete>> =
        invDao.getTasksDateRange(pair).map {
            it.asDomainSubOrderTask()
        }

    fun samplesRangeList(subOrderId: Int): Flow<List<DomainSampleComplete>> =
        invDao.getSamplesBySubOrder(subOrderId).map {
            it.asDomainSamples()
        }

    fun resultsRangeList(subOrderId: Int): Flow<List<DomainResultComplete>> =
        invDao.getResultsBySubOrder(subOrderId).map {
            it.asDomainResults()
        }


    /**
     * New order related data
     * */
    val inputForOrder: LiveData<List<DomainInputForOrder>> =
        invDao.getInputForOrder().map {
            ListTransformer(
                it,
                DatabaseInputForOrder::class,
                DomainInputForOrder::class
            ).generateList().sortedBy { item -> item.depOrder }
        }

    val investigationTypes: LiveData<List<DomainOrdersType>> =
        invDao.getOrdersTypes().map {
            ListTransformer(
                it,
                DatabaseOrdersType::class,
                DomainOrdersType::class
            ).generateList()
        }

    val investigationReasons: LiveData<List<DomainReason>> =
        invDao.getMeasurementReasons().map {
            ListTransformer(
                it,
                DatabaseReason::class,
                DomainReason::class
            ).generateList()
        }

    val orders: LiveData<List<DomainOrder>> =
        invDao.getOrders().map {
            ListTransformer(
                it,
                DatabaseOrder::class,
                DomainOrder::class
            ).generateList()
        }

    val subOrdersWithChildren: LiveData<List<DomainSubOrderShort>> =
        invDao.getSubOrderWithChildren().map {
            it.toDomainSubOrderShort()
        }
}