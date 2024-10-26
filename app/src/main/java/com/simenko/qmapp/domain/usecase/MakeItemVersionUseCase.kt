package com.simenko.qmapp.domain.usecase

import com.simenko.qmapp.domain.ComponentPref
import com.simenko.qmapp.domain.ComponentStagePref
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ProductPref
import com.simenko.qmapp.domain.entities.products.DomainComponentInStageTolerance
import com.simenko.qmapp.domain.entities.products.DomainComponentStageVersion
import com.simenko.qmapp.domain.entities.products.DomainComponentTolerance
import com.simenko.qmapp.domain.entities.products.DomainComponentVersion
import com.simenko.qmapp.domain.entities.products.DomainItemTolerance
import com.simenko.qmapp.domain.entities.products.DomainItemVersion
import com.simenko.qmapp.domain.entities.products.DomainProductTolerance
import com.simenko.qmapp.domain.entities.products.DomainProductVersion
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ProductsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class MakeItemVersionUseCase @Inject constructor(private val repository: ProductsRepository) {
    fun execute(scope: CoroutineScope, version: DomainItemVersion, tolerances: List<DomainItemTolerance>): ReceiveChannel<Event<Resource<String>>> {
        return with(scope) {
            produce {
                val itemPref = version.fItemId.firstOrNull() ?: ProductPref.char
                with(repository) {
                    when (itemPref) {
                        ProductPref.char -> makeProductVersion(prepareProductVersion(version, tolerances))
                        ComponentPref.char -> makeComponentVersion(prepareComponentVersion(version, tolerances))
                        ComponentStagePref.char -> makeStageVersion(prepareStageVersion(version, tolerances))
                        else -> produce { send(Event(Resource.error("Not defined item", null))) }
                    }
                }.consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> send(Event(Resource.loading(null)))

                            Status.SUCCESS -> resource.data?.let {
                                send(Event(Resource.success(itemPref.toString() + it)))
                            }

                            Status.ERROR -> send(Event(Resource.error(resource.message ?: EmptyString.str, null)))
                        }
                    }
                }
            }
        }
    }

    private fun prepareProductVersion(version: DomainItemVersion, tolerances: List<DomainItemTolerance>): Pair<DomainProductVersion, List<DomainProductTolerance>> {
        return DomainProductVersion(
            id = version.id,
            productId = version.itemId,
            versionDescription = version.versionDescription ?: EmptyString.str,
            versionDate = version.versionDate,
            statusId = version.statusId ?: NoRecord.num,
            isDefault = version.isDefault
        ) to tolerances.map {
            DomainProductTolerance(
                id = it.id,
                metrixId = it.metrixId,
                versionId = it.versionId,
                nominal = it.nominal,
                lsl = it.lsl,
                usl = it.usl,
                isActual = it.isActual,
            )
        }
    }

    private fun prepareComponentVersion(version: DomainItemVersion, tolerances: List<DomainItemTolerance>): Pair<DomainComponentVersion, List<DomainComponentTolerance>> {
        return DomainComponentVersion(
            id = version.id,
            componentId = version.itemId,
            versionDescription = version.versionDescription ?: EmptyString.str,
            versionDate = version.versionDate,
            statusId = version.statusId ?: NoRecord.num,
            isDefault = version.isDefault
        ) to tolerances.map {
            DomainComponentTolerance(
                id = it.id,
                metrixId = it.metrixId,
                versionId = it.versionId,
                nominal = it.nominal,
                lsl = it.lsl,
                usl = it.usl,
                isActual = it.isActual,
            )
        }
    }

    private fun prepareStageVersion(version: DomainItemVersion, tolerances: List<DomainItemTolerance>): Pair<DomainComponentStageVersion, List<DomainComponentInStageTolerance>> {
        return DomainComponentStageVersion(
            id = version.id,
            componentInStageId = version.itemId,
            versionDescription = version.versionDescription ?: EmptyString.str,
            versionDate = version.versionDate,
            statusId = version.statusId ?: NoRecord.num,
            isDefault = version.isDefault
        ) to tolerances.map {
            DomainComponentInStageTolerance(
                id = it.id,
                metrixId = it.metrixId,
                versionId = it.versionId,
                nominal = it.nominal,
                lsl = it.lsl,
                usl = it.usl,
                isActual = it.isActual,
            )
        }
    }
}