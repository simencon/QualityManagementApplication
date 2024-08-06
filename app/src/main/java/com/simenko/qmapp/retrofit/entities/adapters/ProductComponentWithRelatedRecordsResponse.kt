package com.simenko.qmapp.retrofit.entities.adapters

import com.simenko.qmapp.retrofit.entities.products.NetworkComponent
import com.simenko.qmapp.retrofit.entities.products.NetworkProductComponent
import kotlinx.serialization.Serializable

@Serializable
data class ProductComponentWithRelatedRecordsResponse(
    val productComponent: NetworkProductComponent,
    val component: NetworkComponent?
)
