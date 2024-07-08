package com.simenko.qmapp.retrofit.entities.adapters

import com.simenko.qmapp.retrofit.entities.products.NetworkComponentComponentStage
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentStage
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ComponentComponentStageWithRelatedRecordsResponse(
    val componentComponentStage: NetworkComponentComponentStage,
    val componentStage: NetworkComponentStage?
)
