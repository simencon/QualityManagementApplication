package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.InvestigationsService
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.InvestigationsDao
import com.simenko.qmapp.room.implementation.ProductsDao
import com.simenko.qmapp.utils.ListTransformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "InvestigationsRepository"

@OptIn(ExperimentalCoroutinesApi::class)
class InvestigationsRepository @Inject constructor(
    private val investigationsDao: InvestigationsDao,
    private val productsDao: ProductsDao,
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
            val ntOrders = investigationsService.getOrders()
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

    suspend fun deleteSubOrder(subOrder: DomainSubOrder) {
        withContext(Dispatchers.IO) {
            investigationsService.deleteSubOrder(subOrder.id)
        }
    }

    suspend fun deleteSubOrderTask(subOrderTask: DomainSubOrderTask) {
        withContext(Dispatchers.IO) {
            investigationsService.deleteSubOrderTask(
                subOrderTask.id
            )
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
            val ntSubOrder = investigationsService.getSubOrders()
            val dbSubOrders = investigationsDao.getSubOrdersByList()

            syncSubOrders(dbSubOrders, ntSubOrder, investigationsDao)

            Log.d(TAG, "refreshSubOrders: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSubOrderTasks() {
        withContext(Dispatchers.IO) {
            val ntSubOrderTasks =
                investigationsService.getSubOrderTasks()
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
            val ntSamples = investigationsService.getSamples()
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
            val ntResults = investigationsService.getResults()
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

    fun getCreatedRecords(coroutineScope: CoroutineScope, records: List<DomainResult>) =
        coroutineScope.produce {
            val newRecords = investigationsService.createResults(
                records.map {
                    it.toNetworkResultWithoutId()
                }
            )

            newRecords.forEach { nIt ->
                investigationsDao.insertResult(nIt.toDatabaseResult())
            }

            send(newRecords) //cold send, can be this.trySend(l).isSuccess //hot send
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
            val nSubOrderTask =
                investigationsService.getSubOrderTask(record.id)
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

    suspend fun getMetricsByPrefixVersionIdActualityCharId(
        prefix: String,
        versionId: Int,
        actual: Boolean,
        charId: Int
    ): List<DomainMetrix> {
        val list = productsDao.getMetricsByPrefixVersionIdActualityCharId(
            prefix, versionId.toString(), if (actual) "1" else "0", charId.toString()
        )
        return ListTransformer(
            list,
            DatabaseMetrix::class, DomainMetrix::class
        ).generateList()
    }

    suspend fun getAllSamples(): List<DomainSampleComplete> {
        return investigationsDao.getAllSamplesDetailed().asDomainSamples()
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

    val completeOrders: LiveData<List<DomainOrderComplete>> =
        investigationsDao.getOrdersDetailed().map {
            it.asDomainOrdersComplete(currentOrder)
        }

    fun completeOrders(): Flow<List<DomainOrderComplete>> =
        investigationsDao.getOrdersDetailedFlow().map {
            it.asDomainOrdersComplete(currentOrder)
        }
            .flowOn(Dispatchers.IO).conflate()

    private var currentSubOrder = -1
    fun setCurrentSubOrder(id: Int) {
        currentSubOrder = id
    }

    val completeSubOrders: LiveData<List<DomainSubOrderComplete>> =
        investigationsDao.getSubOrdersDetailed().map {
            it.asDomainSubOrderDetailed(currentSubOrder)
        }

    fun completeSubOrders(): Flow<List<DomainSubOrderComplete>> =
        investigationsDao.getSubOrdersDetailedFlow().map {
            it.asDomainSubOrderDetailed(currentSubOrder)
        }
            .flowOn(Dispatchers.IO).conflate()

    private var currentTask = -1
    fun setCurrentTask(id: Int) {
        currentTask = id
    }

    val completeSubOrderTasks: LiveData<List<DomainSubOrderTaskComplete>> =
        investigationsDao.getSubOrderTasksDetailed().map {
            it.asDomainSubOrderTask(currentTask)
        }

    fun completeSubOrderTasks(): Flow<List<DomainSubOrderTaskComplete>> =
        investigationsDao.getSubOrderTasksDetailedFlow().map {
            it.asDomainSubOrderTask(currentTask)
        }
            .flowOn(Dispatchers.IO).conflate()

    private var currentSample = -1
    fun setCurrentSample(id: Int) {
        currentSample = id
    }

    val completeSamples: LiveData<List<DomainSampleComplete>> =
        investigationsDao.getSamplesDetailed().map {
            it.asDomainSamples(currentSample)
        }

    fun completeSamples(): Flow<List<DomainSampleComplete>> =
        investigationsDao.getSamplesDetailedFlow().map {
            it.asDomainSamples(currentSample)
        }
            .flowOn(Dispatchers.IO).conflate()

    val subOrdersWithChildren: LiveData<List<DomainSubOrderShort>> =
        investigationsDao.getSubOrderWithChildren().map {
            it.toDomainSubOrderShort()
        }

    private var currentResult = 0
    fun setCurrentResult(id: Int) {
        currentResult = id
    }

    val completeResults: LiveData<List<DomainResultComplete>> =
        investigationsDao.getResultsDetailed().map {
            it.asDomainResults(currentResult)
        }

    fun completeResults(): Flow<List<DomainResultComplete>> =
        investigationsDao.getResultsDetailedFlow().map {
            it.asDomainResults(currentResult)
        }
            .flowOn(Dispatchers.IO).conflate()

}