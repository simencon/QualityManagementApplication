package com.simenko.qmapp.data.remote.entities.adapters

import com.simenko.qmapp.data.remote.entities.products.NetworkComponent
import com.simenko.qmapp.data.remote.entities.products.NetworkComponentKindComponent
import com.simenko.qmapp.data.remote.entities.products.NetworkProductComponent
import kotlinx.serialization.Serializable

@Serializable
data class ProductComponentWithRelatedRecordsResponse(
    val productComponent: NetworkProductComponent,
    val componentKindComponent: NetworkComponentKindComponent?,
    val component: NetworkComponent?
)
