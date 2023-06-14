package com.simenko.qmapp.repository.contract

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.entities.DomainSubOrder
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.retrofit.entities.NetworkErrorBody
import com.simenko.qmapp.retrofit.entities.NetworkSubOrder
import com.simenko.qmapp.retrofit.implementation.InvestigationsService
import com.simenko.qmapp.room.DatabaseBaseModel
import com.simenko.qmapp.room.entities.DatabaseSubOrder
import com.simenko.qmapp.room.implementation.InvestigationsDao
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class CrudeOperations<out DB : DatabaseBaseModel<N, DM>, DM : DomainBaseModel<DB>, N : NetworkBaseModel<DB>> @Inject constructor(
    private val invDao: InvestigationsDao,
    private val invService: InvestigationsService,
    private val errorConverter: Converter<ResponseBody, NetworkErrorBody>
) {
    /*private lateinit var serviceInsert: suspend (N) -> Response<N>
    private lateinit var daoInsert: (DB) -> Unit
    private lateinit var dagGetById: (String) -> DM

    private suspend fun assignFunction(data: DM) {
        when (data) {
            is DomainSubOrder -> {
                serviceInsert = { n -> invService.createSubOrder(n as NetworkSubOrder) }
                daoInsert = { d -> invDao.insertSubOrder(d as DatabaseSubOrder) }
            }
        }
    }*/


    fun CoroutineScope.insertRecord(
        data: DM,
        serviceInsert: suspend (N) -> Response<N>,
        daoInsert: (DB) -> Unit,
        dagGetById: (String) -> DM
    ) = produce {
        runCatching {
            send(Event(Resource.loading(null)))
            serviceInsert(data.toDatabaseModel().toNetworkModel()).let { response ->
                if (response.isSuccessful) {
                    response.body().let { newRecord ->
                        if (newRecord != null) {
                            daoInsert(newRecord.toDatabaseModel())
                            send(Event(Resource.success(dagGetById(newRecord.getId().toString()))))
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
}