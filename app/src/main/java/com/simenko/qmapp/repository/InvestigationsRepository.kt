package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.other.Constants.INITIAL_UPDATE_PERIOD_H
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.InvestigationsService
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.InvestigationsDao
import com.simenko.qmapp.utils.InvestigationsUtils.getOrdersRange
import com.simenko.qmapp.utils.ListTransformer
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
) {
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

    suspend fun getCompleteOrdersRange(): Pair<Long, Long> {
        return Pair(
            invDao.getEarliestOrderDateEpoch() ?: NoSelectedRecord.num.toLong(),
            invDao.getLatestOrderDateEpoch() ?: NoSelectedRecord.num.toLong()
        )
    }

    companion object {
        suspend fun syncOrders(
            oRange: Pair<Long, Long>,
            invService: InvestigationsService,
            invDao: InvestigationsDao
        ) {
            val ntOrders = invService.getOrdersByDateRange(oRange.first, oRange.second)
            val dbOrders = invDao.getOrdersByDateRange(oRange.first, oRange.second)
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
                        if (ntIt.statusId != dbIt.statusId)
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
        }

        suspend fun syncSubOrders(
            oRange: Pair<Long, Long>,
            invService: InvestigationsService,
            invDao: InvestigationsDao
        ) {
            val ntSubOrders = invService.getSubOrdersByDateRange(oRange.first, oRange.second)
            val dbSubOrders = invDao.getSubOrdersByDateRangeL(oRange.first, oRange.second)
                .map { it.subOrder }
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
                    invDao.updateSubOrder(ntIt.toDatabaseSubOrder())
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
                    invDao.deleteSubOrder(dbIt)
                }
            }
        }

        suspend fun syncSubOrderTasks(
            oRange: Pair<Long, Long>,
            invService: InvestigationsService,
            invDao: InvestigationsDao
        ) {
            val ntTasks = invService.getTasksDateRange(oRange.first, oRange.second)
            val dbSubOrderTasks = invDao.getTasksDateRangeL(oRange.first, oRange.second)
                .map { it.subOrderTask }
            ntTasks.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbSubOrderTasks.forEach byBlock2@{ dbIt ->
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
                dbSubOrderTasks.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        if (ntIt.statusId != dbIt.statusId)
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    invDao.updateSubOrderTask(ntIt.toDatabaseSubOrderTask())
                }
            }
            dbSubOrderTasks.forEach byBlock1@{ dbIt ->
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
        }

        suspend fun syncSamples(
            oRange: Pair<Long, Long>,
            invService: InvestigationsService,
            investigationsDao: InvestigationsDao
        ) {
            val ntSamples = invService.getSamplesByDateRange(oRange.first, oRange.second)
            val dbSamples = investigationsDao.getSamplesByDateRange(oRange.first, oRange.second)
            ntSamples.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbSamples.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    investigationsDao.insertSample(ntIt.toDatabaseSample())
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
                    investigationsDao.deleteSample(dbIt)
                }
            }
        }

        suspend fun syncResults(
            oRange: Pair<Long, Long>,
            invService: InvestigationsService,
            investigationsDao: InvestigationsDao
        ) {
            val ntResults = invService.getResultsByDateRange(oRange.first, oRange.second)
            val dbResults = investigationsDao.getResultsByDateRange(oRange.first, oRange.second)
            ntResults.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbResults.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    investigationsDao.insertResult(ntIt.toDatabaseResult())
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
                    investigationsDao.deleteResult(dbIt)
                }
            }
        }
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

    suspend fun uploadNewOrders(lastOrderDateEpoch: Long, uploadNewOrders: Boolean = true) {

        runBlocking {
            val ntOrders = if (uploadNewOrders)
                invService.getLatestOrdersByStartingOrderDate(lastOrderDateEpoch)
            else
                invService.getEarliestOrdersByStartingOrderDate(lastOrderDateEpoch)

            val newOrdersRangeDateEpoch = ntOrders.getOrdersRange()
            Log.d(TAG, "uploadNewOrders: ntOrders is uploaded")
            val ntSubOrders = invService.getSubOrdersByDateRange(
                newOrdersRangeDateEpoch.first,
                newOrdersRangeDateEpoch.second
            )
            Log.d(TAG, "uploadNewOrders: ntSubOrders is uploaded")
            val ntTasks = invService.getTasksDateRange(
                newOrdersRangeDateEpoch.first,
                newOrdersRangeDateEpoch.second
            )
            Log.d(TAG, "uploadNewOrders: ntTasks is uploaded")
            val ntSamples = invService.getSamplesByDateRange(
                newOrdersRangeDateEpoch.first,
                newOrdersRangeDateEpoch.second
            )
            Log.d(TAG, "uploadNewOrders: ntSamples is uploaded")
            val ntResults = invService.getResultsByDateRange(
                newOrdersRangeDateEpoch.first,
                newOrdersRangeDateEpoch.second
            )
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

    suspend fun refreshOrders(uiOrdersRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            syncOrders(uiOrdersRange, invService, invDao)
            Log.d(TAG, "refreshOrders: ${timeFormatter.format(Instant.now())}")
        }
    }

    suspend fun refreshOrdersIfNecessary(ordersTimeRange: Pair<Long, Long>) {
        val list = invDao.getOrdersByDateRange(ordersTimeRange.first, ordersTimeRange.second)
        val localListHashCode = list.sumOf { it.hashCode() }
        val remoteListHashCode = invService.getOrdersHashCodeForDatePeriod(ordersTimeRange.first, ordersTimeRange.second)
        Log.d(TAG, "refreshOrdersIfNecessary: localListHashCode = $localListHashCode; remoteListHashCode = $remoteListHashCode")
    }

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

    suspend fun refreshSubOrders(uiOrdersRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            syncSubOrders(uiOrdersRange, invService, invDao)
            Log.d(TAG, "refreshSubOrders: ${timeFormatter.format(Instant.now())}")
        }
    }

    suspend fun refreshSubOrderTasks(uiOrdersRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            syncSubOrderTasks(uiOrdersRange, invService, invDao)
            Log.d(TAG, "refreshSubOrderTasks: ${timeFormatter.format(Instant.now())}")
        }
    }

    suspend fun refreshSamples(uiOrdersRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            syncSamples(uiOrdersRange, invService, invDao)
            Log.d(TAG, "refreshSamples: ${timeFormatter.format(Instant.now())}")
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

    suspend fun refreshResults(uiOrdersRange: Pair<Long, Long>) {
        withContext(Dispatchers.IO) {
            syncResults(uiOrdersRange, invService, invDao)
            Log.d(TAG, "refreshResults: ${timeFormatter.format(Instant.now())}")
        }
    }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainOrder) =
        coroutineScope.produce {
            val newOrder = invService.createOrder(
                record.toNetworkOrderWithoutId()
            ).toDatabaseOrder()
            invDao.insertOrder(newOrder)
            send(newOrder.toDomainOrder()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) =
        coroutineScope.produce {
            val newRecord = invService.createSubOrder(
                record.toNetworkSubOrderWithoutId()
            ).toDatabaseSubOrder()
            invDao.insertSubOrder(newRecord)
            send(newRecord.toDomainSubOrder()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) =
        coroutineScope.produce {
            val newRecord = invService.createSubOrderTask(
                record.toNetworkSubOrderTaskWithoutId()
            ).toDatabaseSubOrderTask()
            invDao.insertSubOrderTask(newRecord)
            send(newRecord.toDomainSubOrderTask()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainResult) =
        coroutineScope.produce {
            val newRecord = invService.createResult(
                record.toNetworkResultWithoutId()
            ).toDatabaseResult()
            invDao.insertResult(newRecord)
            send(newRecord.toDomainResult()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

    suspend fun getCreatedRecords(coroutineScope: CoroutineScope, records: List<DomainResult>) =
        coroutineScope.produce {
            val newRecords = invService.createResults(
                records.map {
                    it.toNetworkResultWithoutId()
                }
            ).map { it.toDatabaseResult() }
            invDao.insertResultsAll(newRecords)
            send(newRecords.map { it.toDomainResult() }) //cold send, can be this.trySend(l).isSuccess //hot send
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainOrder) =
        coroutineScope.produce {
            val nOrder = record.toNetworkOrderWithId()
            invService.editOrder(record.id, nOrder)
            invDao.updateOrder(record.toDatabaseOrder())
            send(record)
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) =
        coroutineScope.produce {
            val nSubOrder = record.toNetworkSubOrderWithId()
            invService.editSubOrder(record.id, nSubOrder)
            invDao.updateSubOrder(record.toDatabaseSubOrder())
            send(record)
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) =
        coroutineScope.produce {
            val nSubOrderTask = record.toNetworkSubOrderTaskWithId()
            invService.editSubOrderTask(
                record.id,
                nSubOrderTask
            )

            val dSubOrderTask =
                invService.getSubOrderTask(record.id)
                    .toDatabaseSubOrderTask()
            invDao.updateSubOrderTask(dSubOrderTask)

            send(dSubOrderTask.toDomainSubOrderTask())
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainResult) =
        coroutineScope.produce {
            val nNetwork = record.toNetworkWithId()
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

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSample) =
        coroutineScope.produce {
            val newRecord = invService.createSample(
                record.toNetworkSampleWithoutId()
            ).toDatabaseSample()
            invDao.insertSample(newRecord)
            send(newRecord.toDomainSample()) //cold send, can be this.trySend(l).isSuccess //hot send
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

    val investigationStatuses: LiveData<List<DomainOrdersStatus>> =
        invDao.getOrdersStatuses().map {
            ListTransformer(
                it,
                DatabaseOrdersStatus::class,
                DomainOrdersStatus::class
            ).generateList()
        }

    fun investigationStatuses(): Flow<List<DomainOrdersStatus>> =
        invDao.getOrdersStatusesFlow().map {
            ListTransformer(
                it,
                DatabaseOrdersStatus::class,
                DomainOrdersStatus::class
            ).generateList()
        }.flowOn(Dispatchers.IO).conflate()

    val orders: LiveData<List<DomainOrder>> =
        invDao.getOrders().map {
            ListTransformer(
                it,
                DatabaseOrder::class,
                DomainOrder::class
            ).generateList()
        }

    private var currentOrder = -1
    fun setCurrentOrder(id: Int) {
        currentOrder = id
    }

    suspend fun latestLocalOrderId(): Int {
        val localLatestOrderDate = invDao
            .getLatestOrderDateEpoch() ?: NoSelectedRecord.num.toLong()
        return invDao.getLatestOrderId(localLatestOrderDate) ?: NoSelectedRecord.num
    }

    suspend fun ordersListByLastVisibleId(lastVisibleId: Int): Flow<List<DomainOrderComplete>> {
        val dbOrder = invDao.getOrderById(lastVisibleId.toString())
        return if (dbOrder != null)
            invDao.ordersListByLastVisibleId(dbOrder.createdDate).map {
                it.asDomainOrdersComplete(currentOrder)
            }
        else flow { emit(listOf()) }
    }

    private var currentSubOrder = -1
    fun setCurrentSubOrder(id: Int) {
        currentSubOrder = id
    }

    fun subOrdersRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderComplete>> =
        invDao.getSubOrdersByDateRange(pair.first, pair.second).map {
            it.asDomainSubOrderDetailed(currentSubOrder)
        }

    private var currentTask = -1
    fun setCurrentTask(id: Int) {
        currentTask = id
    }

    fun tasksRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderTaskComplete>> =
        invDao.getTasksDateRange(pair.first, pair.second).map {
            it.asDomainSubOrderTask(currentTask)
        }

    private var currentSample = -1
    fun setCurrentSample(id: Int) {
        currentSample = id
    }

    fun samplesRangeList(subOrderId: Int): Flow<List<DomainSampleComplete>> =
        invDao.getSamplesBySubOrder(subOrderId).map {
            it.asDomainSamples(currentSample)
        }

    val subOrdersWithChildren: LiveData<List<DomainSubOrderShort>> =
        invDao.getSubOrderWithChildren().map {
            it.toDomainSubOrderShort()
        }

    private var currentResult = 0
    fun setCurrentResult(id: Int) {
        currentResult = id
    }

    fun resultsRangeList(subOrderId: Int): Flow<List<DomainResultComplete>> =
        invDao.getResultsBySubOrder(subOrderId).map {
            it.asDomainResults(currentResult)
        }
}