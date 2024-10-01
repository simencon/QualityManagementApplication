package com.simenko.qmapp.domain.usecase.products

import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.entities.products.DomainItemTolerance
import com.simenko.qmapp.domain.entities.products.DomainItemVersion
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.repository.ProductsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class MakeItemVersionUseCase @Inject constructor(private val repository: ProductsRepository) {
    fun execute(scope: CoroutineScope, version: DomainItemVersion, tolerances: List<DomainItemTolerance>): ReceiveChannel<Event<Resource<String>>> {
        return with(scope) {
            produce {
                send(Event(Resource.loading(null)))
                delay(3000L)
                send(Event(Resource.success(NoRecordStr.str)))
            }
        }
    }
}