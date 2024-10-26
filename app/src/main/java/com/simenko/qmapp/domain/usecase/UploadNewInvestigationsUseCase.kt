package com.simenko.qmapp.domain.usecase

import com.simenko.qmapp.data.repository.InvestigationsRepository
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.domain.entities.DomainOrder
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class UploadNewInvestigationsUseCase @Inject constructor(private val repository: InvestigationsRepository) {
    fun execute(scope: CoroutineScope): ReceiveChannel<Event<Resource<List<DomainOrder>>>> {
        return with(scope) {
            produce {
                with(repository) { getRemoteLatestOrderDate() }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> send(Event(Resource.loading(null)))
                            Status.SUCCESS -> resource.data?.let { latestOrder ->
                                repository.run { uploadNewInvestigations(latestOrder.toLong()) }.consumeEach { event ->
                                    event.getContentIfNotHandled()?.let { resource ->
                                        when (resource.status) {
                                            Status.LOADING -> send(Event(Resource.loading(null)))
                                            Status.SUCCESS -> resource.data?.let { send(Event(Resource.success(it))) } ?: run { send(Event(Resource.success(emptyList()))) }
                                            Status.ERROR -> send(Event(Resource.error(resource.message ?: EmptyString.str, null)))
                                        }
                                    }
                                }
                            } ?: run { send(Event(Resource.success(emptyList()))) }

                            Status.ERROR -> send(Event(Resource.error(resource.message ?: EmptyString.str, null)))
                        }
                    }
                }
            }
        }
    }
}