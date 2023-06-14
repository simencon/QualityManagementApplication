package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.repository.contract.InvRepository
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.InvestigationsService
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.InvestigationsDao
import com.simenko.qmapp.utils.InvestigationsUtils.getOrdersRange
import com.simenko.qmapp.utils.NotificationData
import com.simenko.qmapp.utils.NotificationReasons
import com.simenko.qmapp.works.SyncPeriods
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "InvestigationsRepository"

@OptIn(ExperimentalCoroutinesApi::class)
class InvestigationsRepository @Inject constructor(
    private val invDao: InvestigationsDao,
    private val invService: InvestigationsService,
    private val errorConverter: Converter<ResponseBody, NetworkErrorBody>
) : InvRepository {
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

    override suspend fun getCompleteOrdersRange(): Pair<Long, Long> {
        return Pair(invDao.getEarliestOrderDate() ?: NoRecord.num.toLong(), invDao.getLatestOrderDate() ?: NoRecord.num.toLong())
    }

    override suspend fun syncInvEntitiesByTimeRange(timeRange: Pair<Long, Long>): List<NotificationData> {
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
    fun deleteOrder(orderId: Int): Flow<Event<Resource<Boolean>>> = flow {
        runCatching {
            emit(Event(Resource.loading(true)))
            invService.deleteOrder(orderId).let { response ->
                if (response.isSuccessful) {
                    invDao.getOrderById(orderId.toString())?.let { it ->
                        invDao.deleteOrder(it)
                    }
                    emit(Event(Resource.success(true)))
                } else {
                    emit(Event(Resource.error("Order is not deleted.", true)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null)
                emit(Event(Resource.error("Network error", true)))
        }
    }

    fun deleteSubOrder(subOrderId: Int): Flow<Event<Resource<Boolean>>> = flow {
        runCatching {
            emit(Event(Resource.loading(true)))
            invService.deleteSubOrder(subOrderId).let { response ->
                if (response.isSuccessful) {
                    invDao.getSubOrderById(subOrderId.toString())?.let { it ->
                        invDao.deleteSubOrder(it)
                    }
                    emit(Event(Resource.success(true)))
                } else {
                    emit(Event(Resource.error("Sub order is not deleted.", true)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null)
                emit(Event(Resource.error("Network error", true)))
        }
    }

    fun deleteSubOrderTask(taskId: Int): Flow<Event<Resource<Boolean>>> = flow {
        runCatching {
            emit(Event(Resource.loading(true)))
            invService.deleteSubOrderTask(taskId).let { response ->
                if (response.isSuccessful) {
                    invDao.getSubOrderTaskById(taskId.toString())?.let { it ->
                        invDao.deleteSubOrderTask(it)
                        Log.d(TAG, "deleteSubOrderTask: $it")
                    }
                    emit(Event(Resource.success(true)))
                } else {
                    emit(Event(Resource.error("Sub order is not deleted.", true)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null) {
                emit(Event(Resource.error("Network error", true)))
            }
        }
    }

    fun deleteSample(sampleId: Int): Flow<Event<Resource<Boolean>>> = flow {
        runCatching {
            emit(Event(Resource.loading(true)))
            invService.deleteSample(sampleId).let { response ->
                if (response.isSuccessful) {
                    invDao.getSampleById(sampleId.toString())?.let { it ->
                        invDao.deleteSample(it)
                        Log.d(TAG, "deleteSample: $it")
                    }
                    emit(Event(Resource.success(true)))
                } else {
                    emit(Event(Resource.error("Sub order is not deleted.", true)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null)
                emit(Event(Resource.error("Network error", true)))
        }
    }

    fun deleteResults(taskId: Int = 0, id: Int = 0) = flow {
        runCatching {
            emit(Event(Resource.loading(true)))
            invService.deleteResults(taskId, id).let { response ->
                if (response.isSuccessful) {
                    if (taskId == 0 && id != 0)
                        invDao.getResultById(id.toString())?.let { it ->
                            invDao.deleteResult(it)
                        }
                    else if (taskId != 0 && id == 0)
                        invDao.getResultsByTaskId(taskId.toString()).forEach { it ->
                            invDao.deleteResult(it)
                        }
                    else
                        emit(Event(Resource.error("You mast select either taskId or resultId", true)))
                    emit(Event(Resource.success(true)))
                } else {
                    emit(Event(Resource.error("Sub order is not deleted.", true)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null)
                emit(Event(Resource.error("Network error", true)))
        }
    }

    /**
     * Inv adding operations
     * */
    fun CoroutineScope.insertOrder(record: DomainOrder) = produce {
        val newOrder = invService.createOrder(record.toDatabaseModel().toNetworkModel()).toDatabaseModel()
        invDao.insertOrder(newOrder)
        send(newOrder.toDomainModel()) //cold send, can be this.trySend(l).isSuccess //hot send
    }

    @Inject lateinit var crudeOperations: CrudeOperations<DatabaseSubOrder, DomainSubOrder, NetworkSubOrder>

    fun CoroutineScope.insertSubOrder(record: DomainSubOrder) = produce {
        runCatching {
            send(Event(Resource.loading(null)))
            invService.createSubOrder(record.toDatabaseModel().toNetworkModel()).let { response ->
                if (response.isSuccessful) {
                    response.body().let { newRecord ->
                        if (newRecord != null) {
                            invDao.insertSubOrder(newRecord.toDatabaseModel())
                            send(Event(Resource.success(invDao.getSubOrderById(newRecord.id.toString()))))
                        } else {
                            send(Event(Resource.error("Response body is empty.",null)))
                        }
                    }
                } else {
                    send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error",null)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null)
                send(Event(Resource.error("Network error", null)))
        }
    }

    fun CoroutineScope.insertTask(record: DomainSubOrderTask) = produce {
        val newRecord = invService.createSubOrderTask(record.toDatabaseModel().toNetworkModel()).toDatabaseModel()
        invDao.insertSubOrderTask(newRecord)
        send(newRecord.toDomainModel()) //cold send, can be this.trySend(l).isSuccess //hot send
    }

    fun CoroutineScope.insertSample(record: DomainSample) = produce {
        val newRecord = invService.createSample(record.toDatabaseModel().toNetworkModel()).toDatabaseModel()
        invDao.insertSample(newRecord)
        send(newRecord.toDomainModel()) //cold send, can be this.trySend(l).isSuccess //hot send
    }

    fun CoroutineScope.insertResults(records: List<DomainResult>) = produce {
        runCatching {
            send(Event(Resource.loading(true)))
            invService.createResults(records.map { it.toDatabaseModel().toNetworkModel() }).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { ntResults ->
                        invDao.insertResultsAll(ntResults.map { ntResult ->
                            ntResult.toDatabaseModel()
                        })
                    }
                    send(Event(Resource.success(true)))
                } else {
                    send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", true)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null)
                send(Event(Resource.error("Network error", true)))
        }
    }


    fun CoroutineScope.updateOrder(record: DomainOrder) = produce {
        val nOrder = record.toDatabaseModel().toNetworkModel()
        invService.editOrder(record.id, nOrder)
        invDao.updateOrder(record.toDatabaseModel())
        send(record)
    }

    fun CoroutineScope.updateSubOrder(record: DomainSubOrder) = produce {
        val nSubOrder = record.toDatabaseModel().toNetworkModel()
        invService.editSubOrder(record.id, nSubOrder)
        invDao.updateSubOrder(record.toDatabaseModel())
        send(record)
    }

    fun CoroutineScope.updateTask(record: DomainSubOrderTask) = produce {
        runCatching {
            send(Event(Resource.loading(true)))
            invService.editSubOrderTask(record.id, record.toDatabaseModel().toNetworkModel()).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { ntResult ->
                        val dSubOrderTask = invService.getSubOrderTask(ntResult.id).toDatabaseModel()
                        invDao.updateSubOrderTask(dSubOrderTask)
                    }
                    send(Event(Resource.success(true)))
                } else {
                    send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", true)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null)
                send(Event(Resource.error("Network error", true)))
        }
    }

    fun CoroutineScope.updateResult(record: DomainResult) = produce {
        val nNetwork = record.toDatabaseModel().toNetworkModel()
        invService.editResult(record.id, nNetwork)
        invDao.updateResult(record.toDatabaseModel())
        send(record)
    }


    fun CoroutineScope.syncOrder(record: DomainOrder) = produce {
        val nOrder = invService.getOrder(record.id)
        invDao.updateOrder(nOrder.toDatabaseModel())
        send(nOrder.toDatabaseModel().toDomainModel())
    }

    fun CoroutineScope.syncSubOrder(record: DomainSubOrder) = produce {
        val nSubOrder = invService.getSubOrder(record.id)
        invDao.updateSubOrder(nSubOrder.toDatabaseModel())
        send(nSubOrder.toDatabaseModel().toDomainModel())
    }

    fun CoroutineScope.syncTask(record: DomainSubOrderTask) = produce {
        Log.d(TAG, "syncTask: $record")
        val nSubOrderTask = invService.getSubOrderTask(record.id)
        Log.d(TAG, "syncTask: $nSubOrderTask")
        invDao.updateSubOrderTask(nSubOrderTask.toDatabaseModel())
        send(nSubOrderTask.toDatabaseModel().toDomainModel())
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