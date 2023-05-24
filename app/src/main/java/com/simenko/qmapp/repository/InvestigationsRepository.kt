package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.other.Constants.BTN_ORDER_ID
import com.simenko.qmapp.other.Constants.TOP_ORDER_ID
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.InvestigationsService
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.InvestigationsDao
import com.simenko.qmapp.utils.InvestigationsUtils.getOrdersRange
import com.simenko.qmapp.utils.ListTransformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "InvestigationsRepository"

@OptIn(ExperimentalCoroutinesApi::class)
class InvestigationsRepository @Inject constructor(
    private val investigationsDao: InvestigationsDao,
    private val investigationsService: InvestigationsService
) {
    private var localLatestOrderDate = NoSelectedRecord.num.toLong()
    private var remoteLatestOrderDate = NoSelectedRecord.num.toLong()
    suspend fun checkAndUploadNew() {
        localLatestOrderDate = investigationsDao.getLatestOrderDateEpoch() ?: NoSelectedRecord.num.toLong()
        investigationsService.getLatestOrderDateEpoch().apply {
            if (isSuccessful)
                remoteLatestOrderDate = this.body() ?: NoSelectedRecord.num.toLong()
        }
        val oneDayEgo =  Instant.now().minusMillis(1000L*60*60*24).toEpochMilli()
        if (remoteLatestOrderDate > localLatestOrderDate)
            uploadNewOrders(if (oneDayEgo > localLatestOrderDate) oneDayEgo else localLatestOrderDate)
    }

    companion object {
        suspend fun syncOrders(
            ntOrders: List<NetworkOrder>,
            investigationsDao: InvestigationsDao
        ) {
            val dbOrders: List<DatabaseOrder> = investigationsDao.getOrdersList()
            ntOrders.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    investigationsDao.insertOrder(ntIt.toDatabaseOrder())
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
                    investigationsDao.updateOrder(ntIt.toDatabaseOrder())
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
                    investigationsDao.deleteOrder(dbIt)
                }
            }
        }

        suspend fun syncSubOrders(
            ntSubOrders: List<NetworkSubOrder>,
            investigationsDao: InvestigationsDao
        ) {
            val dbSubOrders: List<DatabaseSubOrder> = investigationsDao.getSubOrdersList()
            ntSubOrders.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbSubOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    investigationsDao.insertSubOrder(ntIt.toDatabaseSubOrder())
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
                    investigationsDao.updateSubOrder(ntIt.toDatabaseSubOrder())
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
                    investigationsDao.deleteSubOrder(dbIt)
                }
            }
        }

        suspend fun syncSubOrderTasks(
            ntSubOrderTasks: List<NetworkSubOrderTask>,
            investigationsDao: InvestigationsDao
        ) {
            val dbSubOrderTasks: List<DatabaseSubOrderTask> = investigationsDao.getTasksList()
            ntSubOrderTasks.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbSubOrderTasks.forEach byBlock2@{ dbIt ->
                    if (ntIt.id == dbIt.id) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    investigationsDao.insertSubOrderTask(ntIt.toDatabaseSubOrderTask())
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
                    investigationsDao.updateSubOrderTask(ntIt.toDatabaseSubOrderTask())
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
                    investigationsDao.deleteSubOrderTask(dbIt)
                }
            }
        }

        suspend fun syncSamples(
            ntSamples: List<NetworkSample>,
            investigationsDao: InvestigationsDao
        ) {
            val dbSamples: List<DatabaseSample> = investigationsDao.getSamplesList()
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
            ntResults: List<NetworkResult>,
            investigationsDao: InvestigationsDao
        ) {
            val dbResults: List<DatabaseResult> = investigationsDao.getResultsList()
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
                investigationsService.getInputForOrder()
            investigationsDao.insertInputForOrderAll(
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
            val records = investigationsService.getOrdersStatuses()
            investigationsDao.insertOrdersStatusesAll(
                ListTransformer(
                    records,
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
            val records =
                investigationsService.getMeasurementReasons()
            investigationsDao.insertMeasurementReasonsAll(
                ListTransformer(
                    records,
                    NetworkReason::class, DatabaseReason::class
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
            val records = investigationsService.getOrdersTypes()
            investigationsDao.insertOrdersTypesAll(
                ListTransformer(
                    records,
                    NetworkOrdersType::class,
                    DatabaseOrdersType::class
                ).generateList()
            )
            Log.d(TAG, "refreshOrdersTypes: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    private suspend fun uploadNewOrders(lastOrderDateEpoch: Long) {
        val ntOrders = investigationsService.getLatestOrdersByStartingOrderDate(lastOrderDateEpoch)
        val newOrdersRangeDateEpoch = ntOrders.getOrdersRange()
        Log.d(TAG, "uploadNewOrders: ntOrders is uploaded")
        val ntSubOrders = investigationsService.getSubOrdersByOrderDateEpochRange(
            newOrdersRangeDateEpoch.first,
            newOrdersRangeDateEpoch.second
        )
        Log.d(TAG, "uploadNewOrders: ntSubOrders is uploaded")
        val ntTasks = investigationsService.getTasksByOrderDateEpochRange(
            newOrdersRangeDateEpoch.first,
            newOrdersRangeDateEpoch.second
        )
        Log.d(TAG, "uploadNewOrders: ntTasks is uploaded")
        val ntSamples = investigationsService.getSamplesByOrderDateEpochRange(
            newOrdersRangeDateEpoch.first,
            newOrdersRangeDateEpoch.second
        )
        Log.d(TAG, "uploadNewOrders: ntSamples is uploaded")
        val ntResults = investigationsService.getResultsByOrderDateEpochRange(
            newOrdersRangeDateEpoch.first,
            newOrdersRangeDateEpoch.second
        )
        Log.d(TAG, "uploadNewOrders: ntResults is uploaded")
//        syncOrders(ntOrders, investigationsDao)
        investigationsDao.insertOrdersAll(ntOrders.map { it.toDatabaseOrder() })
        Log.d(TAG, "uploadNewOrders: ntOrders is saved")
//        syncSubOrders(ntSubOrders, investigationsDao)
        investigationsDao.insertSubOrdersAll(ntSubOrders.map { it.toDatabaseSubOrder() })
        Log.d(TAG, "uploadNewOrders: ntSubOrders is saved")
//        syncSubOrderTasks(ntTasks, investigationsDao)
        investigationsDao.insertSubOrderTasksAll(ntTasks.map { it.toDatabaseSubOrderTask() })
        Log.d(TAG, "uploadNewOrders: ntTasks is saved")
//        syncSamples(ntSamples, investigationsDao)
        investigationsDao.insertSamplesAll(ntSamples.map { it.toDatabaseSample() })
        Log.d(TAG, "uploadNewOrders: ntSamples is saved")
//        syncResults(ntResults, investigationsDao)
        investigationsDao.insertResultsAll(ntResults.map { it.toDatabaseResult() })
        Log.d(TAG, "uploadNewOrders: ntResults is saved")
    }

    suspend fun refreshOrders() {
        withContext(Dispatchers.IO) {
            val ntOrders = investigationsService.getOrdersByNumberRange(BTN_ORDER_ID, TOP_ORDER_ID)

            syncOrders(ntOrders, investigationsDao)
            Log.d(TAG, "refreshOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun deleteOrder(orderId: Int) {
        withContext(Dispatchers.IO) {
            investigationsService.deleteOrder(orderId)
        }
    }

    suspend fun deleteSubOrder(subOrderId: Int) {
        withContext(Dispatchers.IO) {
            investigationsService.deleteSubOrder(subOrderId)
        }
    }

    suspend fun deleteSubOrderTask(taskId: Int) {
        withContext(Dispatchers.IO) {
            investigationsService.deleteSubOrderTask(taskId)
        }
    }

    suspend fun deleteSample(sample: DomainSample) {
        withContext(Dispatchers.IO) {
            investigationsService.deleteSample(sample.id)
        }
    }

    suspend fun deleteResults(charId: Int = 0, id: Int = 0) {
        withContext(Dispatchers.IO) {
            investigationsService.deleteResults(charId, id)
        }
    }

    suspend fun refreshSubOrders() {
        withContext(Dispatchers.IO) {
            val ntSubOrder = investigationsService.getSubOrdersByOrderDateEpochRange(
                BTN_ORDER_ID,
                TOP_ORDER_ID
            )

            syncSubOrders(ntSubOrder, investigationsDao)

            Log.d(TAG, "refreshSubOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSubOrderTasks() {
        withContext(Dispatchers.IO) {
            val ntSubOrderTasks = investigationsService.getTasksByOrderDateEpochRange(
                BTN_ORDER_ID,
                TOP_ORDER_ID
            )
            syncSubOrderTasks(ntSubOrderTasks, investigationsDao)
            Log.d(
                TAG,
                "refreshSubOrderTasks: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshSamples() {
        withContext(Dispatchers.IO) {
            val ntSamples = investigationsService.getSamplesByOrderDateEpochRange(
                BTN_ORDER_ID,
                TOP_ORDER_ID
            )
            syncSamples(ntSamples, investigationsDao)
            Log.d(TAG, "refreshSamples: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshResultsDecryptions() {
        withContext(Dispatchers.IO) {
            val resultsDecryptions =
                investigationsService.getResultsDecryptions()
            investigationsDao.insertResultsDecryptionsAll(
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
            val ntResults = investigationsService.getResultsByOrderDateEpochRange(
                BTN_ORDER_ID,
                TOP_ORDER_ID
            )
            syncResults(ntResults, investigationsDao)
            Log.d(TAG, "refreshResults: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainOrder) =
        coroutineScope.produce {
            val newOrder = investigationsService.createOrder(
                record.toNetworkOrderWithoutId()
            ).toDatabaseOrder()
            investigationsDao.insertOrder(newOrder)
            send(newOrder.toDomainOrder()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) =
        coroutineScope.produce {
            val newRecord = investigationsService.createSubOrder(
                record.toNetworkSubOrderWithoutId()
            ).toDatabaseSubOrder()
            investigationsDao.insertSubOrder(newRecord)
            send(newRecord.toDomainSubOrder()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) =
        coroutineScope.produce {
            val newRecord = investigationsService.createSubOrderTask(
                record.toNetworkSubOrderTaskWithoutId()
            ).toDatabaseSubOrderTask()
            investigationsDao.insertSubOrderTask(newRecord)
            send(newRecord.toDomainSubOrderTask()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainResult) =
        coroutineScope.produce {
            val newRecord = investigationsService.createResult(
                record.toNetworkResultWithoutId()
            ).toDatabaseResult()
            investigationsDao.insertResult(newRecord)
            send(newRecord.toDomainResult()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

    suspend fun getCreatedRecords(coroutineScope: CoroutineScope, records: List<DomainResult>) =
        coroutineScope.produce {
            val newRecords = investigationsService.createResults(
                records.map {
                    it.toNetworkResultWithoutId()
                }
            ).map { it.toDatabaseResult() }
            investigationsDao.insertResultsAll(newRecords)
            send(newRecords.map { it.toDomainResult() }) //cold send, can be this.trySend(l).isSuccess //hot send
        }


    fun updateRecord(coroutineScope: CoroutineScope, record: DomainOrder) = coroutineScope.produce {
        val nOrder = record.toNetworkOrderWithId()
        investigationsService.editOrder(record.id, nOrder)
        investigationsDao.updateOrder(record.toDatabaseOrder())
        send(record)
    }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) =
        coroutineScope.produce {
            val nSubOrder = record.toNetworkSubOrderWithId()
            investigationsService.editSubOrder(record.id, nSubOrder)
            investigationsDao.updateSubOrder(record.toDatabaseSubOrder())
            send(record)
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) =
        coroutineScope.produce {
            val nSubOrderTask = record.toNetworkSubOrderTaskWithId()
            investigationsService.editSubOrderTask(
                record.id,
                nSubOrderTask
            )

            val dSubOrderTask =
                investigationsService.getSubOrderTask(record.id)
                    .toDatabaseSubOrderTask()
            investigationsDao.updateSubOrderTask(dSubOrderTask)

            send(dSubOrderTask.toDomainSubOrderTask())
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainResult) =
        coroutineScope.produce {
            val nNetwork = record.toNetworkWithId()
            investigationsService.editResult(record.id, nNetwork)
            investigationsDao.updateResult(record.toDatabaseResult())
            send(record)
        }

    fun getRecord(coroutineScope: CoroutineScope, record: DomainOrder) = coroutineScope.produce {
        val nOrder = investigationsService.getOrder(record.id)
        investigationsDao.updateOrder(nOrder.toDatabaseOrder())
        send(nOrder.toDomainOrder())
    }

    fun getRecord(coroutineScope: CoroutineScope, record: DomainSubOrder) = coroutineScope.produce {
        val nSubOrder = investigationsService.getSubOrder(record.id)
        investigationsDao.updateSubOrder(nSubOrder.toDatabaseSubOrder())
        send(nSubOrder.toDomainSubOrder())
    }

    fun getRecord(coroutineScope: CoroutineScope, record: DomainSubOrderTask) =
        coroutineScope.produce {
            val nSubOrderTask = investigationsService.getSubOrderTask(record.id)
            investigationsDao.updateSubOrderTask(nSubOrderTask.toDatabaseSubOrderTask())
            send(nSubOrderTask.toDomainSubOrderTask())
        }

    fun getCreatedRecord(coroutineScope: CoroutineScope, record: DomainSample) =
        coroutineScope.produce {
            val newRecord = investigationsService.createSample(
                record.toNetworkSampleWithoutId()
            ).toDatabaseSample()
            investigationsDao.insertSample(newRecord)
            send(newRecord.toDomainSample()) //cold send, can be this.trySend(l).isSuccess //hot send
        }

//    ToDO - change this part to return exactly what is needed

    suspend fun getOrderById(id: Int): DomainOrder? {
        return investigationsDao.getOrderById(id.toString())?.toDomainOrder()
    }

    suspend fun getSubOrderById(id: Int): DomainSubOrder {
        return investigationsDao.getSubOrderById(id.toString()).toDomainSubOrder()
    }

    suspend fun getTasksBySubOrderId(subOrderId: Int): List<DomainSubOrderTask> {
        val list = investigationsDao.getTasksBySubOrderId(subOrderId.toString())
        return ListTransformer(
            list,
            DatabaseSubOrderTask::class, DomainSubOrderTask::class
        ).generateList()
    }

    suspend fun getSamplesBySubOrderId(subOrderId: Int): List<DomainSample> {
        val list = investigationsDao.getSamplesBySubOrderId(subOrderId)
        return ListTransformer(
            list,
            DatabaseSample::class, DomainSample::class
        ).generateList()
    }

//    -------------------------------------------------------------


    val inputForOrder: LiveData<List<DomainInputForOrder>> =
        investigationsDao.getInputForOrder().map {
            ListTransformer(
                it,
                DatabaseInputForOrder::class,
                DomainInputForOrder::class
            ).generateList().sortedBy { item -> item.depOrder }
        }

    val investigationTypes: LiveData<List<DomainOrdersType>> =
        investigationsDao.getOrdersTypes().map {
            ListTransformer(it, DatabaseOrdersType::class, DomainOrdersType::class).generateList()
        }

    val investigationReasons: LiveData<List<DomainReason>> =
        investigationsDao.getMeasurementReasons().map {
            ListTransformer(
                it,
                DatabaseReason::class,
                DomainReason::class
            ).generateList()
        }

    val investigationStatuses: LiveData<List<DomainOrdersStatus>> =
        investigationsDao.getOrdersStatuses().map {
            ListTransformer(
                it,
                DatabaseOrdersStatus::class,
                DomainOrdersStatus::class
            ).generateList()
        }

    fun investigationStatuses(): Flow<List<DomainOrdersStatus>> =
        investigationsDao.getOrdersStatusesFlow().map {
            ListTransformer(
                it,
                DatabaseOrdersStatus::class,
                DomainOrdersStatus::class
            ).generateList()
        }.flowOn(Dispatchers.IO).conflate()

    val orders: LiveData<List<DomainOrder>> =
        investigationsDao.getOrders().map {
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
        val localLatestOrderDate = investigationsDao
            .getLatestOrderDateEpoch() ?: NoSelectedRecord.num.toLong()
        return investigationsDao.getLatestOrderId(localLatestOrderDate) ?: NoSelectedRecord.num
    }

    suspend fun ordersListByLastVisibleId(lastVisibleId: Int): Flow<List<DomainOrderComplete>> {
        val dbOrder = investigationsDao.getOrderById(lastVisibleId.toString())
        return if (dbOrder != null)
            investigationsDao.ordersListByLastVisibleId(dbOrder.createdDate).map {
                it.asDomainOrdersComplete(currentOrder)
            }
        else flow { emit(listOf()) }
    }

    private var currentSubOrder = -1
    fun setCurrentSubOrder(id: Int) {
        currentSubOrder = id
    }

    fun subOrdersRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderComplete>> =
        investigationsDao.subOrdersRangeList(pair.first, pair.second).map {
            it.asDomainSubOrderDetailed(currentSubOrder)
        }

    private var currentTask = -1
    fun setCurrentTask(id: Int) {
        currentTask = id
    }

    fun tasksRangeList(pair: Pair<Long, Long>): Flow<List<DomainSubOrderTaskComplete>> =
        investigationsDao.tasksRangeList(pair.first, pair.second).mapLatest {
            it.asDomainSubOrderTask(currentTask)
        }

    private var currentSample = -1
    fun setCurrentSample(id: Int) {
        currentSample = id
    }

    fun samplesRangeList(subOrderId: Int): Flow<List<DomainSampleComplete>> =
        investigationsDao.samplesRangeList(subOrderId).map {
            it.asDomainSamples(currentSample)
        }

    val subOrdersWithChildren: LiveData<List<DomainSubOrderShort>> =
        investigationsDao.getSubOrderWithChildren().map {
            it.toDomainSubOrderShort()
        }

    private var currentResult = 0
    fun setCurrentResult(id: Int) {
        currentResult = id
    }

    fun resultsRangeList(subOrderId: Int): Flow<List<DomainResultComplete>> =
        investigationsDao.resultsRangeList(subOrderId).map {
            it.asDomainResults(currentResult)
        }
}