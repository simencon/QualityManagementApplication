package com.simenko.qmapp.domain.usecase.products

import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainComponentComponentStage
import com.simenko.qmapp.domain.entities.products.DomainComponentStage
import com.simenko.qmapp.domain.entities.products.DomainComponentStageKindComponentStage
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ProductsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@ViewModelScoped
class MakeComponentStageUseCase @Inject constructor(private val repository: ProductsRepository) {
    fun execute(scope: CoroutineScope, stage: DomainComponentStage, stageKindId: ID, componentId: ID, quantity: Float): ReceiveChannel<Event<Resource<ID>>> {
        return with(scope) {
            produce {
                with(repository) { if (stage.id == NoRecord.num) insertComponentStage(stage) else updateComponentStage(stage) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> send(Event(Resource.loading(null)))
                            Status.SUCCESS -> resource.data?.id?.let {
                                makeStageKindStage(
                                    stageId = it,
                                    stageKindId = stageKindId,
                                    componentId = componentId,
                                    quantity = quantity,
                                    producer = this@produce
                                )
                            }

                            Status.ERROR -> send(Event(Resource.error(resource.message ?: EmptyString.str, null)))
                        }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.makeStageKindStage(stageId: ID, stageKindId: ID, componentId: ID, quantity: Float, producer: ProducerScope<Event<Resource<ID>>>) = launch {
        val stageKindStage = DomainComponentStageKindComponentStage(componentStageId = stageId, componentStageKindId = stageKindId)
        with(repository) { insertComponentStageKindComponentStage(stageKindStage) }.consumeEach { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> producer.send(Event(Resource.loading(null)))
                    Status.SUCCESS -> resource.data?.id?.let {
                        makeStageComponent(
                            stageId = stageId,
                            componentId = componentId,
                            stageKindStageId = it,
                            quantity = quantity,
                            producer = producer
                        )
                    }

                    Status.ERROR -> producer.send(Event(Resource.error(resource.message ?: EmptyString.str, null)))
                }
            }
        }
    }

    private fun CoroutineScope.makeStageComponent(stageId: ID, componentId: ID, stageKindStageId: ID, quantity: Float, producer: ProducerScope<Event<Resource<ID>>>) = launch {
        val productComponent = DomainComponentComponentStage(componentId = componentId, stageKindStageId = stageKindStageId, quantity = quantity)
        with(repository) { insertComponentComponentStage(productComponent) }.consumeEach { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> producer.send(Event(Resource.loading(null)))
                    Status.SUCCESS -> producer.send(Event(Resource.success(stageId)))
                    Status.ERROR -> producer.send(Event(Resource.error(resource.message ?: EmptyString.str, null)))
                }
            }
        }
    }
}