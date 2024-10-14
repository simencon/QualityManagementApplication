package com.simenko.qmapp.domain.usecase.products

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainComponent
import com.simenko.qmapp.domain.entities.products.DomainComponentKindComponent
import com.simenko.qmapp.domain.entities.products.DomainProductComponent
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ProductsRepository
import com.simenko.qmapp.domain.EmptyString
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@ViewModelScoped
class MakeProductComponentUseCase @Inject constructor(private val repository: ProductsRepository) {
    fun execute(scope: CoroutineScope, component: DomainComponent, componentKindId: ID, productId: ID, quantity: Float): ReceiveChannel<Event<Resource<ID>>> {
        return with(scope) {
            produce {
                with(repository) { if (component.id == NoRecord.num) insertComponent(component) else updateComponent(component) }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> send(Event(Resource.loading(null)))
                            Status.SUCCESS -> resource.data?.id?.let {
                                makeComponentKindComponent(
                                    componentId = it,
                                    componentKindId = componentKindId,
                                    productId = productId,
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

    private fun CoroutineScope.makeComponentKindComponent(componentId: ID, componentKindId: ID, productId: ID, quantity: Float, producer: ProducerScope<Event<Resource<ID>>>) = launch(Dispatchers.IO) {
        val componentKindComponent = DomainComponentKindComponent(componentKindId = componentKindId, componentId = componentId)
        with(repository) { insertComponentKindComponent(componentKindComponent) }.consumeEach { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> producer.send(Event(Resource.loading(null)))
                    Status.SUCCESS -> resource.data?.id?.let {
                        makeProductComponent(
                            componentId = componentId,
                            productId = productId,
                            componentKindComponentId = it,
                            quantity = quantity,
                            producer = producer
                        )
                    }

                    Status.ERROR -> producer.send(Event(Resource.error(resource.message ?: EmptyString.str, null)))
                }
            }
        }
    }

    private fun CoroutineScope.makeProductComponent(componentId: ID, productId: ID, componentKindComponentId: ID, quantity: Float, producer: ProducerScope<Event<Resource<ID>>>) =
        launch(Dispatchers.IO) {
            val productComponent = DomainProductComponent(quantity = quantity, productId = productId, componentKindComponentId = componentKindComponentId)
            with(repository) { insertProductComponent(productComponent) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> producer.send(Event(Resource.loading(null)))
                        Status.SUCCESS -> producer.send(Event(Resource.success(componentId)))
                        Status.ERROR -> producer.send(Event(Resource.error(resource.message ?: EmptyString.str, null)))
                    }
                }
            }
        }
}