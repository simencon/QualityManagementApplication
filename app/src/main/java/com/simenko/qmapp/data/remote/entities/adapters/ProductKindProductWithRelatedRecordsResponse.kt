package com.simenko.qmapp.data.remote.entities.adapters

import com.simenko.qmapp.data.remote.entities.products.NetworkProduct
import com.simenko.qmapp.data.remote.entities.products.NetworkProductKindProduct
import kotlinx.serialization.Serializable

@Serializable
data class ProductKindProductWithRelatedRecordsResponse(
    val productKindProduct: NetworkProductKindProduct,
    val product: NetworkProduct?
)
