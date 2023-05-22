package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.other.Constants.BTN_NUMBER_TO_TEST
import com.simenko.qmapp.other.Constants.TOP_NUMBER_TO_TEST
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.InvestigationsService
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.InvestigationsDao
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

    companion object {
        fun syncOrders(
            dbOrders: List<DatabaseOrder>,
            ntOrders: List<NetworkOrder>,
            investigationsDao: InvestigationsDao
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
                    investigationsDao.insertOrder(ntIt.toDatabaseOrder())
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
                    investigationsDao.updateOrder(ntIt.toDatabaseOrder())
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
                    investigationsDao.deleteOrder(dbIt)
//            Log.d(TAG, "syncOrders: Order deleted from SQLite / id = ${dbIt.id}")
                }
            }
        }

        fun syncSubOrders(
            dbSubOrders: List<DatabaseSubOrder>,
            ntSubOrders: List<NetworkSubOrder>,
            investigationsDao: InvestigationsDao
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
                    investigationsDao.insertSubOrder(ntIt.toDatabaseSubOrder())
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
                    investigationsDao.updateSubOrder(ntIt.toDatabaseSubOrder())
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
                    investigationsDao.deleteSubOrder(dbIt)
//            Log.d(TAG, "syncSubOrders: Sub order deleted from SQLite / id = ${dbIt.id}")
                }
            }
        }

        fun syncSubOrderTasks(
            dbSubOrderTasks: List<DatabaseSubOrderTask>,
            ntSubOrderTasks: List<NetworkSubOrderTask>,
            investigationsDao: InvestigationsDao
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
                    investigationsDao.insertSubOrderTask(ntIt.toDatabaseSubOrderTask())
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
                    investigationsDao.updateSubOrderTask(ntIt.toDatabaseSubOrderTask())
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
                    investigationsDao.deleteSubOrderTask(dbIt)
//            Log.d(TAG, "syncSubOrders: Sub order deleted from SQLite / id = ${dbIt.id}")
                }
            }
        }

        fun syncSamples(
            dbSamples: List<DatabaseSample>,
            ntSamples: List<NetworkSample>,
            investigationsDao: InvestigationsDao
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
                    investigationsDao.insertSample(ntIt.toDatabaseSample())
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
                    investigationsDao.deleteSample(dbIt)
//            Log.d(TAG, "syncSamples: Sample deleted from SQLite / id = ${dbIt.id}")
                }
            }
        }

        fun syncResults(
            dbResults: List<DatabaseResult>,
            ntResults: List<NetworkResult>,
            investigationsDao: InvestigationsDao
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
                    investigationsDao.insertResult(ntIt.toDatabaseResult())
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
                    investigationsDao.deleteResult(dbIt)
//            Log.d(TAG, "syncSamples: Result deleted from SQLite / id = ${dbIt.id}")
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

    suspend fun refreshOrders() {
        withContext(Dispatchers.IO) {
//            val ntOrders = investigationsService.getOrders()
            val ntOrders =
                investigationsService.getOrdersByNumberRange(BTN_NUMBER_TO_TEST, TOP_NUMBER_TO_TEST)
            val dbOrders = investigationsDao.getOrdersByList()

            syncOrders(dbOrders, ntOrders, investigationsDao)
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
//            val ntSubOrder = investigationsService.getSubOrders()
            val ntSubOrder = investigationsService.getSubOrdersByNumberRange(
                BTN_NUMBER_TO_TEST,
                TOP_NUMBER_TO_TEST
            )
            val dbSubOrders = investigationsDao.getSubOrdersByList()

            syncSubOrders(dbSubOrders, ntSubOrder, investigationsDao)

            Log.d(TAG, "refreshSubOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSubOrderTasks() {
        withContext(Dispatchers.IO) {
//            val ntSubOrderTasks = investigationsService.getSubOrderTasks()
            val ntSubOrderTasks = investigationsService.getSubOrderTasksByNumberRange(
                BTN_NUMBER_TO_TEST,
                TOP_NUMBER_TO_TEST
            )

            val dbSubOrderTasks = investigationsDao.getSubOrderTasksByList()
            syncSubOrderTasks(dbSubOrderTasks, ntSubOrderTasks, investigationsDao)

            Log.d(
                TAG,
                "refreshSubOrderTasks: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshSamples() {
        withContext(Dispatchers.IO) {
//            val ntSamples = investigationsService.getSamples()
            val ntSamples = investigationsService.getSamplesByNumberRange(
                BTN_NUMBER_TO_TEST,
                TOP_NUMBER_TO_TEST
            )

            val dbSamples = investigationsDao.getSamplesByList()
            syncSamples(dbSamples, ntSamples, investigationsDao)

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
//            val ntResults = investigationsService.getResults()
            val ntResults = investigationsService.getResultsByNumberRange(
                BTN_NUMBER_TO_TEST,
                TOP_NUMBER_TO_TEST
            )

            val dbResults = investigationsDao.getResultsByList()
            syncResults(dbResults, ntResults, investigationsDao)

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

    suspend fun getOrderById(id: Int): DomainOrder {
        return investigationsDao.getOrderById(id.toString()).toDomainOrder()
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

    suspend fun latestLocalOrderId(): Int = investigationsDao.getLatestOrderId() ?: 0

    fun ordersListByLastVisibleId(lastVisibleId: Int): Flow<List<DomainOrderComplete>> =
        investigationsDao.ordersListByLastVisibleId(lastVisibleId).map {
            it.asDomainOrdersComplete(currentOrder)
        }

    private var currentSubOrder = -1
    fun setCurrentSubOrder(id: Int) {
        currentSubOrder = id
    }

    fun subOrdersRangeList(pair: Pair<Int, Int>): Flow<List<DomainSubOrderComplete>> =
        investigationsDao.subOrdersRangeList(pair.first, pair.second).map {
            it.asDomainSubOrderDetailed(currentSubOrder)
        }

    private var currentTask = -1
    fun setCurrentTask(id: Int) {
        currentTask = id
    }

    fun tasksRangeList(pair: Pair<Int, Int>): Flow<List<DomainSubOrderTaskComplete>> =
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