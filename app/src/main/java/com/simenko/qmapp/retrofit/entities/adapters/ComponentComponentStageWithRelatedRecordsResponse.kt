package com.simenko.qmapp.retrofit.entities.adapters

import com.simenko.qmapp.retrofit.entities.products.NetworkComponentComponentStage
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentStage
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentStageKindComponentStage
import kotlinx.serialization.Serializable

@Serializable
data class ComponentComponentStageWithRelatedRecordsResponse(
    val componentComponentStage: NetworkComponentComponentStage,
    val stageStageKind: NetworkComponentStageKindComponentStage?,
    val componentInStage: NetworkComponentStage?
)
