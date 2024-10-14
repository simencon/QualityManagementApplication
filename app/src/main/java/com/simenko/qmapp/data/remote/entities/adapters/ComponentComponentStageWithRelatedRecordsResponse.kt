package com.simenko.qmapp.data.remote.entities.adapters

import com.simenko.qmapp.data.remote.entities.products.NetworkComponentComponentStage
import com.simenko.qmapp.data.remote.entities.products.NetworkComponentStage
import com.simenko.qmapp.data.remote.entities.products.NetworkComponentStageKindComponentStage
import kotlinx.serialization.Serializable

@Serializable
data class ComponentComponentStageWithRelatedRecordsResponse(
    val componentComponentStage: NetworkComponentComponentStage,
    val stageStageKind: NetworkComponentStageKindComponentStage?,
    val componentInStage: NetworkComponentStage?
)
