package com.simenko.qmapp.repository.contract

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.retrofit.entities.NetworkErrorBody
import com.simenko.qmapp.room.contract.DatabaseBaseModel
import com.simenko.qmapp.room.contract.StatusHolderModel
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.contract.DaoTimeDependentModel
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
    private val errorConverter: Converter<ResponseBody, NetworkErrorBody>,
    private val userRepository: UserRepository
) {
    fun <N : Number> CoroutineScope.responseHandlerForService(
        taskExecutor: suspend () -> Response<N>
    ): ReceiveChannel<Event<Resource<Number>>> = produce {
        runCatching {
            userRepository.refreshTokenIfNecessary()
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

    fun <N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB>> CoroutineScope.responseHandlerForSingleRecord(
        taskExecutor: suspend () -> Response<N>,
        resultHandler: (DB) -> Unit
    ): ReceiveChannel<Event<Resource<DM>>> = produce {
        runCatching {
            userRepository.refreshTokenIfNecessary()
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
            userRepository.refreshTokenIfNecessary()
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

    suspend fun <N, DB, DBC, DM, DAO> syncStatusRecordsByTimeRange(
        timeRange: Pair<Long, Long>,
        dao: DAO,
        daoReadDetailedRecordById: suspend (Int) -> DBC?,
        serviceGetRecordsByTimeRange: suspend (Pair<Long, Long>) -> Response<List<N>>,
    ): List<NotificationData> where
            N : NetworkBaseModel<DB>,
            DB : DatabaseBaseModel<N, DM>,
            DBC : StatusHolderModel,
            DM : DomainBaseModel<DB>,
            DAO : DaoBaseModel<DB>,
            DAO : DaoTimeDependentModel<DB> {
        val result = mutableListOf<NotificationData>()
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val ntOrders = serviceGetRecordsByTimeRange(timeRange).run {
                if (isSuccessful) body()?.map { it.toDatabaseModel() } ?: listOf() else throw IOException("Network error, sub orders not available.")
            }
            val dbOrders = dao.getRecordsByTimeRange(timeRange)

            ntOrders.subtract(dbOrders.toSet()).let { it1 ->
                it1.filter { it2 -> it2.getRecordId() !in dbOrders.map { it3 -> it3.getRecordId() } }.let { listToInsert ->
                    dao.insertRecords(listToInsert)
                    listToInsert.forEach { record ->
                        daoReadDetailedRecordById(record.getRecordId().toString().toInt())?.let { recordComplete ->
                            result.add(recordComplete.toNotificationData(NotificationReasons.CREATED))
                        }
                    }
                }
                it1.filter { it2 -> it2.getRecordId() in dbOrders.map { it3 -> it3.getRecordId() } }.let { listToUpdate ->
                    dao.updateRecords(listToUpdate)
                    listToUpdate.forEach { record ->
                        daoReadDetailedRecordById(record.getRecordId().toString().toInt())?.let { recordComplete ->
                            result.add(recordComplete.toNotificationData(NotificationReasons.CHANGED))
                        }
                    }
                }
            }

            dbOrders.subtract(ntOrders.toSet()).filter { it1 -> it1.getRecordId() !in ntOrders.map { it2 -> it2.getRecordId() } }.let { listToDelete ->
                dao.deleteRecords(listToDelete)
                listToDelete.forEach { record ->
                    daoReadDetailedRecordById(record.getRecordId().toString().toInt())?.let { recordComplete ->
                        result.add(recordComplete.toNotificationData(NotificationReasons.DELETED))
                    }
                }
            }
        }
        return result
    }

    suspend fun <N, DB, DM, DAO> syncRecordsByTimeRange(
        timeRange: Pair<Long, Long>,
        dao: DAO,
        serviceGetRecordsByTimeRange: suspend (Pair<Long, Long>) -> Response<List<N>>,
    ) where N : NetworkBaseModel<DB>,
            DB : DatabaseBaseModel<N, DM>,
            DM : DomainBaseModel<DB>,
            DAO : DaoBaseModel<DB>,
            DAO : DaoTimeDependentModel<DB> {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
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

    suspend fun <N, DB, DM, DAO> syncRecordsAll(
        dao: DAO,
        serviceGetRecords: suspend () -> Response<List<N>>,
    ) where
            N : NetworkBaseModel<DB>,
            DB : DatabaseBaseModel<N, DM>,
            DM : DomainBaseModel<DB>,
            DAO : DaoBaseModel<DB> {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
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
}