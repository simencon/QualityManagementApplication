package com.simenko.qmapp.repository.contract

import android.util.Log
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.entities.DomainOrder
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.retrofit.entities.NetworkErrorBody
import com.simenko.qmapp.room.DatabaseBaseModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CrudeOperations"

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class CrudeOperations @Inject constructor(
    private val errorConverter: Converter<ResponseBody, NetworkErrorBody>
) {
    fun <N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB>> CoroutineScope.insertRecords(
        records: List<DM>,
        serviceInsert: suspend (List<N>) -> Response<List<N>>,
        daoInsert: (List<DB>) -> Unit
    ) = produce {
        runCatching {
            send(Event(Resource.loading(null)))
            serviceInsert(records.map { it.toDatabaseModel().toNetworkModel() }).let { response ->
                if (response.isSuccessful) {
                    response.body()?.also { ntRecords ->
                        daoInsert(ntRecords.map { it.toDatabaseModel() })
                        send(Event(Resource.success(ntRecords.map { it.toDatabaseModel().toDomainModel() })))
                    } ?: send(Event(Resource.error("No response body", null)))
                } else {
                    send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", null)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null)
                send(Event(Resource.error("Network error", null)))
        }
    }

    fun <N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB>> CoroutineScope.insertRecord(
        data: DM,
        serviceInsert: suspend (N) -> Response<N>,
        daoInsert: (DB) -> Unit
    ) = produce {
        runCatching {
            send(Event(Resource.loading(null)))
            serviceInsert(data.toDatabaseModel().toNetworkModel()).let { response ->
                if (response.isSuccessful) {
                    response.body()?.also { newRecord ->
                        daoInsert(newRecord.toDatabaseModel())
                        send(Event(Resource.success(newRecord.toDatabaseModel().toDomainModel())))
                    } ?: send(Event(Resource.error("No response body", null)))
                } else {
                    send(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", null)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null)
                send(Event(Resource.error("Network error", null)))
        }
    }

    fun <DB : DatabaseBaseModel<*, *>> deleteRecord(
        recordId: Int = 0,
        serviceDelete: suspend (Int) -> Response<Unit>,
        daoGetRecordById: suspend (String) -> DB?,
        daoDeleteRecord: (DB) -> Unit

    ): Flow<Event<Resource<Boolean>>> = flow {
        runCatching {
            emit(Event(Resource.loading(false)))
            serviceDelete(recordId).let { response ->
                if (response.isSuccessful) {
                    daoGetRecordById(recordId.toString())?.let { it ->
                        daoDeleteRecord(it)
                    }
                    emit(Event(Resource.success(true)))
                } else {
                    emit(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", false)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null) {
                emit(Event(Resource.error("Network error", false)))
            }
        }
    }

    fun <DB : DatabaseBaseModel<*, *>> deleteRecordsByParent(
        parentId: Int = 0,
        serviceDelete: suspend (Int) -> Response<Unit>,
        daoGetRecordsById: suspend (String) -> List<DB>,
        daoDeleteRecord: (DB) -> Unit
    ) = flow {
        runCatching {
            emit(Event(Resource.loading(false)))
            serviceDelete(parentId).let { response ->
                if (response.isSuccessful) {
                    daoGetRecordsById(parentId.toString()).forEach {
                        daoDeleteRecord(it)
                    }
                    emit(Event(Resource.success(true)))
                } else {
                    emit(Event(Resource.error(response.errorBody()?.run { errorConverter.convert(this)?.message } ?: "Undefined error", false)))
                }
            }
        }.exceptionOrNull().also {
            if (it != null)
                emit(Event(Resource.error("Network error", false)))
        }
    }

    fun <N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB>> CoroutineScope.updateRecord(
        record: DM,
        serviceEdit: suspend (Int, N) -> Response<N>,
        daoUpdate: (DB) -> Unit
    ) = produce {
        runCatching {
            val id = record.getRecordId().toString().toInt()
            send(Event(Resource.loading(null)))
            serviceEdit(id, record.toDatabaseModel().toNetworkModel()).let { response ->
                if (response.isSuccessful) {
                    response.body()?.also { ntRecord ->
                        daoUpdate(ntRecord.toDatabaseModel())
                        send(Event(Resource.success(ntRecord.toDatabaseModel().toDomainModel())))
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

    fun <N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB>> CoroutineScope.getRecord(
        record: DM,
        serviceGetRecord: suspend (Int) -> Response<N>,
        daoUpdateRecord: (DB) -> Unit
    ) = produce {
        runCatching {
            send(Event(Resource.loading(null)))
            serviceGetRecord(record.getRecordId().toString().toInt()).let { response ->
                if (response.isSuccessful) {
                    response.body()?.also {
                        daoUpdateRecord(it.toDatabaseModel())
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
}