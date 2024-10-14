package com.simenko.qmapp.data.repository.contract

import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.contract.DaoTimeDependentModel
import com.simenko.qmapp.data.cache.db.contract.DatabaseBaseModel
import com.simenko.qmapp.data.cache.db.contract.StatusHolderModel
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.data.remote.NetworkBaseModel
import com.simenko.qmapp.data.remote.entities.NetworkErrorBody
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
class CrudeOperations @Inject constructor(private val errorConverter: Converter<ResponseBody, NetworkErrorBody>) {
    fun <N : Number> CoroutineScope.responseHandlerForService(
        taskExecutor: suspend () -> Response<N>
    ): ReceiveChannel<Event<Resource<Number>>> = produce {
        runCatching {
            send(Event(Resource.loading(null)))
            taskExecutor().let { response ->
                if (response.isSuccessful) {
                    response.body()?.also {
                        send(Event(Resource.success(response.body()!!.toLong())))
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

    fun <ID, PID, N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM, ID, PID>, DM : DomainBaseModel<DB>> CoroutineScope.responseHandlerForSingleRecord(
        taskExecutor: suspend () -> Response<N>,
        resultHandler: suspend (DB) -> Unit
    ): ReceiveChannel<Event<Resource<DM>>> = produce {
        try {
            send(Event(Resource.loading(null)))
            taskExecutor().let { response ->
                if (response.isSuccessful) {
                    response.body()?.also {
                        resultHandler(it.toDatabaseModel())
                        send(Event(Resource.success(it.toDatabaseModel().toDomainModel())))
                    } ?: run {
                        send(Event(Resource.error("No response body", null)))
                    }
                } else {
                    send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", null)))
                }
            }
        } catch (e: Throwable) {
            send(Event(Resource.error(e.message ?: "Network Error", null)))
        }
    }

    fun <ID, PID, N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM, ID, PID>, DM : DomainBaseModel<DB>> CoroutineScope.responseHandlerForListOfRecords(
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

    suspend fun <N, DB, DBC, DM, DAO, ID, PID> syncStatusRecordsByTimeRange(
        timeRange: Pair<Long, Long>,
        dao: DAO,
        daoReadDetailedRecordById: suspend (ID) -> DBC?,
        serviceGetRecordsByTimeRange: suspend (Pair<Long, Long>) -> Response<List<N>>,
    ): List<NotificationData> where
            N : NetworkBaseModel<DB>,
            DB : DatabaseBaseModel<N, DM, ID, PID>,
            DBC : StatusHolderModel,
            DM : DomainBaseModel<DB>,
            DAO : DaoBaseModel<ID, PID, DB>,
            DAO : DaoTimeDependentModel<ID, PID, DB> {
        val result = mutableListOf<NotificationData>()
        withContext(Dispatchers.IO) {
            val ntOrders = serviceGetRecordsByTimeRange(timeRange).run {
                if (isSuccessful) body()?.map { it.toDatabaseModel() } ?: listOf() else throw IOException("Network error, sub orders not available.")
            }
            val dbOrders = dao.getRecordsByTimeRange(timeRange)

            ntOrders.subtract(dbOrders.toSet()).let { it1 ->
                it1.filter { it2 -> it2.getRecordId() !in dbOrders.map { it3 -> it3.getRecordId() } }.let { listToInsert ->
                    dao.insertRecords(listToInsert)
                    listToInsert.forEach { record ->
                        daoReadDetailedRecordById(record.getRecordId())?.let { recordComplete ->
                            result.add(recordComplete.toNotificationData(NotificationReasons.CREATED))
                        }
                    }
                }
                it1.filter { it2 -> it2.getRecordId() in dbOrders.map { it3 -> it3.getRecordId() } }.let { listToUpdate ->
                    dao.updateRecords(listToUpdate)
                    listToUpdate.forEach { record ->
                        daoReadDetailedRecordById(record.getRecordId())?.let { recordComplete ->
                            result.add(recordComplete.toNotificationData(NotificationReasons.CHANGED))
                        }
                    }
                }
            }

            dbOrders.subtract(ntOrders.toSet()).filter { it1 -> it1.getRecordId() !in ntOrders.map { it2 -> it2.getRecordId() } }.let { listToDelete ->
                dao.deleteRecords(listToDelete)
                listToDelete.forEach { record ->
                    daoReadDetailedRecordById(record.getRecordId())?.let { recordComplete ->
                        result.add(recordComplete.toNotificationData(NotificationReasons.DELETED))
                    }
                }
            }
        }
        return result
    }

    suspend fun <N, DB, DM, DAO, ID, PID> syncRecordsByTimeRange(
        timeRange: Pair<Long, Long>,
        dao: DAO,
        serviceGetRecordsByTimeRange: suspend (Pair<Long, Long>) -> Response<List<N>>,
    ) where N : NetworkBaseModel<DB>,
            DB : DatabaseBaseModel<N, DM, ID, PID>,
            DM : DomainBaseModel<DB>,
            DAO : DaoBaseModel<ID, PID, DB>,
            DAO : DaoTimeDependentModel<ID, PID, DB> {
        withContext(Dispatchers.IO) {
            val ntOrders = serviceGetRecordsByTimeRange(timeRange).run {
                if (isSuccessful) body()?.map { it.toDatabaseModel() } ?: listOf() else throw IOException("Network error, orders not available.")
            }
            val dbOrders = dao.getRecordsByTimeRange(timeRange)

            ntOrders.subtract(dbOrders.toSet()).let { it1 ->
                it1.filter { it2 -> it2.getRecordId() !in dbOrders.map { it3 -> it3.getRecordId() } }.let { listToInsert ->
                    dao.insertRecords(listToInsert)
                }
                it1.filter { it2 -> it2.getRecordId() in dbOrders.map { it3 -> it3.getRecordId() } }.let { listToUpdate ->
                    dao.updateRecords(listToUpdate)
                }
            }

            dbOrders.subtract(ntOrders.toSet()).filter { it1 -> it1.getRecordId() !in ntOrders.map { it2 -> it2.getRecordId() } }.let { listToDelete ->
                dao.deleteRecords(listToDelete)
            }
        }
    }

    suspend fun <N, DB, DM, DAO, ID, PID> syncRecordsAll(
        dao: DAO,
        serviceGetRecords: suspend () -> Response<List<N>>,
    ) where
            N : NetworkBaseModel<DB>,
            DB : DatabaseBaseModel<N, DM, ID, PID>,
            DM : DomainBaseModel<DB>,
            DAO : DaoBaseModel<ID, PID, DB> {
        withContext(Dispatchers.IO) {
            val ntOrders = serviceGetRecords().run {
                if (isSuccessful) body()?.map { it.toDatabaseModel() } ?: listOf() else throw IOException("Network error, orders not available.")
            }
            val dbOrders = dao.getRecords()

            ntOrders.subtract(dbOrders.toSet()).let { it1 ->
                it1.filter { it2 -> it2.getRecordId() !in dbOrders.map { it3 -> it3.getRecordId() } }.let { listToInsert ->
                    dao.insertRecords(listToInsert)
                }
                it1.filter { it2 -> it2.getRecordId() in dbOrders.map { it3 -> it3.getRecordId() } }.let { listToUpdate ->
                    dao.updateRecords(listToUpdate)
                }
            }

            dbOrders.subtract(ntOrders.toSet()).filter { it1 -> it1.getRecordId() !in ntOrders.map { it2 -> it2.getRecordId() } }.let { listToDelete ->
                dao.deleteRecords(listToDelete)
            }
        }
    }

    fun <P_N, C_N, P_DB, C_DB, P_DM, C_DM, P_DAO, C_DAO, P_ID, C_ID, P_PID, C_PID> CoroutineScope.syncParentWithChildren(
        parentDao: P_DAO,
        childrenDao: C_DAO,
        taskExecutor: suspend () -> Response<Pair<P_N, List<C_N>>>,
        resultHandler: suspend (P_DB) -> Unit
    ): ReceiveChannel<Event<Resource<P_ID>>> where
            P_N : NetworkBaseModel<P_DB>,
            P_DB : DatabaseBaseModel<P_N, P_DM, P_ID, P_PID>,
            P_DM : DomainBaseModel<P_DB>,
            P_DAO : DaoBaseModel<P_ID, P_PID, P_DB>,

            C_N : NetworkBaseModel<C_DB>,
            C_DB : DatabaseBaseModel<C_N, C_DM, C_ID, C_PID>,
            C_DM : DomainBaseModel<C_DB>,
            C_DAO : DaoBaseModel<C_ID, C_PID, C_DB> = produce {
        try {
            send(Event(Resource.loading(null)))
            taskExecutor().let { response ->
                if (response.isSuccessful) {
                    response.body()?.also { body ->
                        val (ntParent, ntChildren) = Pair(
                            body.first.toDatabaseModel(),
                            body.second.map { it.toDatabaseModel() }
                        )

                        // sync parent ----------------------------------------------------------------------------------------------------------------------------------
                        parentDao.getRecords().find { it.getRecordId() == ntParent.getRecordId() }?.let {
                            parentDao.updateRecord(ntParent)
                        } ?: run {
                            parentDao.insertRecord(ntParent)
                        }
                        // sync children ----------------------------------------------------------------------------------------------------------------------------------
                        val dbChildren = childrenDao.getRecords().map { it.toDomainModel() }.filter { it.getParentId() == ntParent.getRecordId() }.map { it.toDatabaseModel() }

                        ntChildren.subtract(dbChildren.toSet()).let { it1 ->
                            it1.filter { it2 -> it2.getRecordId() !in dbChildren.map { it3 -> it3.getRecordId() } }.let { listToInsert ->
                                childrenDao.insertRecords(listToInsert)
                            }
                            it1.filter { it2 -> it2.getRecordId() in dbChildren.map { it3 -> it3.getRecordId() } }.let { listToUpdate ->
                                childrenDao.updateRecords(listToUpdate)
                            }
                        }

                        dbChildren.subtract(ntChildren.toSet()).filter { it1 -> it1.getRecordId() !in ntChildren.map { it2 -> it2.getRecordId() } }.let { listToDelete ->
                            childrenDao.deleteRecords(listToDelete)
                        }
                        // finish with success
                        resultHandler.invoke(ntParent)
                        send(Event(Resource.success(ntParent.getRecordId())))
                    } ?: run {
                        send(Event(Resource.error("No response body", null)))
                    }
                } else {
                    send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", null)))
                }
            }
        } catch (e: Throwable) {
            send(Event(Resource.error(e.message ?: "Network Error", null)))
        }
    }
}