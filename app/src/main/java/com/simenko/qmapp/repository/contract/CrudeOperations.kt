package com.simenko.qmapp.repository.contract

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.retrofit.entities.NetworkErrorBody
import com.simenko.qmapp.room.DatabaseBaseModel
import com.simenko.qmapp.room.StatusHolderModel
import com.simenko.qmapp.room.implementation.DaoBase
import com.simenko.qmapp.utils.NotificationData
import com.simenko.qmapp.utils.NotificationReasons
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class CrudeOperations @Inject constructor(
    private val errorConverter: Converter<ResponseBody, NetworkErrorBody>
) {
    fun <N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB>> CoroutineScope.responseHandlerForSingleRecord(
        taskExecutor: suspend () -> Response<N>,
        resultHandler: (DB) -> Unit
    ): ReceiveChannel<Event<Resource<DM>>> = produce {
        runCatching {
            send(Event(Resource.loading(null)))
            taskExecutor().let { response ->
                if (response.isSuccessful) {
                    response.body()?.also {
                        resultHandler(it.toDatabaseModel())
                        send(Event(Resource.success(it.toDatabaseModel().toDomainModel())))
                    } ?: send(Event(Resource.error("No response body", null)))
                } else {
                    send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", null)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null) {
                send(Event(Resource.error(it.message ?: "Network Error", null)))
            }
        }
    }

    fun <N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB>> CoroutineScope.responseHandlerForListOfRecords(
        taskExecutor: suspend () -> Response<List<N>>,
        resultHandler: (List<DB>) -> Unit
    ): ReceiveChannel<Event<Resource<List<DM>>>> = produce {
        runCatching {
            send(Event(Resource.loading(null)))
            taskExecutor().let { response ->
                if (response.isSuccessful) {
                    response.body()?.also { records ->
                        resultHandler(records.map { it.toDatabaseModel() })
                        send(Event(Resource.success(records.map { it.toDatabaseModel().toDomainModel() })))
                    } ?: send(Event(Resource.error("No response body", null)))
                } else {
                    send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", null)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null) {
                send(Event(Resource.error(it.message ?: "Network Error", null)))
            }
        }
    }

    suspend fun <N, DB, DBC, DM> syncStatusRecordsByTimeRange(
        timeRange: Pair<Long, Long>,
        serviceGetRecordsByTimeRange: suspend (Pair<Long, Long>) -> Response<List<N>>,
        daoGetRecordsByTimeRange: suspend (Pair<Long, Long>) -> List<DB>,
        daoCreateRecord: (DB) -> Unit,
        daoReadDetailedRecordById: suspend (Int) -> DBC?,
        daoUpdateRecord: (DB) -> Unit,
        daoDeleteRecord: (DB) -> Unit
    ): List<NotificationData> where
            N : NetworkBaseModel<DB>,
            DB : DatabaseBaseModel<N, DM>,
            DBC : StatusHolderModel,
            DM : DomainBaseModel<DB> {
        val result = mutableListOf<NotificationData>()
        withContext(Dispatchers.IO) {
            val ntSubOrders = serviceGetRecordsByTimeRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, sub orders not available.")
            }
            val dbSubOrders = daoGetRecordsByTimeRange(timeRange)
            ntSubOrders.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbSubOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    daoCreateRecord(ntIt.toDatabaseModel())
                    daoReadDetailedRecordById(ntIt.getRecordId().toString().toInt())?.let {
                        result.add(
                            it.toNotificationData(NotificationReasons.CREATED)
                        )
                    }
                }
            }
            ntSubOrders.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbSubOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        if (dbIt != ntIt.toDatabaseModel())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    daoUpdateRecord(ntIt.toDatabaseModel())
                    daoReadDetailedRecordById(ntIt.getRecordId().toString().toInt())?.let {
                        result.add(
                            it.toNotificationData(NotificationReasons.CHANGED)
                        )
                    }
                }
            }
            dbSubOrders.forEach byBlock1@{ dbIt ->
                var recordExists = false
                ntSubOrders.forEach byBlock2@{ ntIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    daoReadDetailedRecordById(dbIt.getRecordId().toString().toInt())?.let {
                        result.add(
                            it.toNotificationData(NotificationReasons.DELETED)
                        )
                    }
                    daoDeleteRecord(dbIt)
                }
            }
        }
        return result
    }

    suspend fun <N, DB, DM> syncRecordsByTimeRange(
        timeRange: Pair<Long, Long>,
        serviceGetRecordsByTimeRange: suspend (Pair<Long, Long>) -> Response<List<N>>,
        daoGetRecordsByTimeRange: suspend (Pair<Long, Long>) -> List<DB>,
        daoCreateRecord: (DB) -> Unit,
        daoUpdateRecord: (DB) -> Unit,
        daoDeleteRecord: (DB) -> Unit
    ) where N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB> {
        withContext(Dispatchers.IO) {
            val ntOrders = serviceGetRecordsByTimeRange(timeRange).run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, orders not available.")
            }
            val dbOrders = daoGetRecordsByTimeRange(timeRange)
            ntOrders.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    daoCreateRecord(ntIt.toDatabaseModel())
                }
            }
            ntOrders.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        if (dbIt != ntIt.toDatabaseModel())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    daoUpdateRecord(ntIt.toDatabaseModel())
                }
            }
            dbOrders.forEach byBlock1@{ dbIt ->
                var recordExists = false
                ntOrders.forEach byBlock2@{ ntIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    daoDeleteRecord(dbIt)
                }
            }
        }
    }

    suspend fun <N, DB, DM> syncRecordsAll(
        serviceGetRecords: suspend () -> Response<List<N>>,
        daoGetRecords: suspend () -> List<DB>,
        daoCreateRecord: suspend (DB) -> Unit,
        daoUpdateRecord: suspend (DB) -> Unit,
        daoDeleteRecord: suspend (DB) -> Unit
    ) where N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB> {
        withContext(Dispatchers.IO) {
            val ntOrders = serviceGetRecords().run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, orders not available.")
            }
            val dbOrders = daoGetRecords()
            ntOrders.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    daoCreateRecord(ntIt.toDatabaseModel())
                }
            }
            ntOrders.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        if (dbIt != ntIt.toDatabaseModel())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    daoUpdateRecord(ntIt.toDatabaseModel())
                }
            }
            dbOrders.forEach byBlock1@{ dbIt ->
                var recordExists = false
                ntOrders.forEach byBlock2@{ ntIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    daoDeleteRecord(dbIt)
                }
            }
        }
    }

    suspend fun <N, DB, DM, DAO> syncRecordsAll(
        serviceGetRecords: suspend () -> Response<List<N>>,
        dao: DAO
    ) where
            N : NetworkBaseModel<DB>,
            DB : DatabaseBaseModel<N, DM>,
            DM : DomainBaseModel<DB>,
            DAO : DaoBase<DB>
    {
        withContext(Dispatchers.IO) {
            val ntOrders = serviceGetRecords().run {
                if (isSuccessful) body() ?: listOf() else throw IOException("Network error, orders not available.")
            }
            val dbOrders = dao.getRecords()
            ntOrders.forEach byBlock1@{ ntIt ->
                var recordExists = false
                dbOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    dao.insertRecord(ntIt.toDatabaseModel())
                }
            }
            ntOrders.forEach byBlock1@{ ntIt ->
                var recordStatusChanged = false
                dbOrders.forEach byBlock2@{ dbIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        if (dbIt != ntIt.toDatabaseModel())
                            recordStatusChanged = true
                        return@byBlock2
                    }
                }
                if (recordStatusChanged) {
                    dao.updateRecord(ntIt.toDatabaseModel())
                }
            }
            dbOrders.forEach byBlock1@{ dbIt ->
                var recordExists = false
                ntOrders.forEach byBlock2@{ ntIt ->
                    if (ntIt.getRecordId() == dbIt.getRecordId()) {
                        recordExists = true
                        return@byBlock2
                    }
                }
                if (!recordExists) {
                    dao.deleteRecord(dbIt)
                }
            }
        }
    }
}