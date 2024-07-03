package com.simenko.qmapp.retrofit.entities.adapters

import com.simenko.qmapp.retrofit.entities.products.NetworkProduct
import com.simenko.qmapp.retrofit.entities.products.NetworkProductKindProduct
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductKindProductWithRelatedRecordsResponse(
    val productKindProduct: NetworkProductKindProduct,
    val product: NetworkProduct?
)
