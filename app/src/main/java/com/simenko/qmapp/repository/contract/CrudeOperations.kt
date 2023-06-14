package com.simenko.qmapp.repository.contract

import com.simenko.qmapp.domain.DomainBaseModel
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

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class CrudeOperations @Inject constructor(
    private val errorConverter: Converter<ResponseBody, NetworkErrorBody>
) {
    fun <N : NetworkBaseModel<DB>, DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB>> CoroutineScope.insertRecord(
        data: DM,
        serviceInsert: suspend (N) -> Response<N>,
        daoInsert: (DB) -> Unit,
        daoGetRecordById: suspend (String) -> DB?
    ) = produce {
        runCatching {
            send(Event(Resource.loading(null)))
            serviceInsert(data.toDatabaseModel().toNetworkModel()).let { response ->
                if (response.isSuccessful) {
                    response.body().let { newRecord ->
                        if (newRecord != null) {
                            daoInsert(newRecord.toDatabaseModel())
                            send(Event(Resource.success(daoGetRecordById(newRecord.getRecordId().toString())?.toDomainModel())))
                        } else {
                            send(Event(Resource.error("Response body is empty.", null)))
                        }
                    }
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
}