package com.simenko.qmapp.retrofit.entities.adapters

import com.simenko.qmapp.retrofit.entities.products.NetworkProduct
import com.simenko.qmapp.retrofit.entities.products.NetworkProductKindProduct
import kotlinx.serialization.Serializable

@Serializable
data class ProductKindProductWithRelatedRecordsResponse(
    val productKindProduct: NetworkProductKindProduct,
    val product: NetworkProduct?
)
