package com.simenko.qmapp.retrofit.entities

import com.simenko.qmapp.domain.*
import com.simenko.qmapp.utils.ObjectTransformer

//KClasses can only take primary constructor
fun DomainOrder.toNetworkOrder() = ObjectTransformer(
    DomainOrder::class, NetworkOrder::class
).transform(this)

fun DomainSubOrder.toNetworkSubOrder() = ObjectTransformer(
    DomainSubOrder::class, NetworkSubOrder::class
).transform(this)

fun DomainSubOrderTask.toNetworkSubOrderTask() = ObjectTransformer(
    DomainSubOrderTask::class, NetworkSubOrderTask::class
).transform(this)

fun DomainSample.toNetworkSample() = ObjectTransformer(
    DomainSample::class, NetworkSample::class
).transform(this)

fun DomainResult.toNetworkResult() = ObjectTransformer(
    DomainResult::class, NetworkResult::class
).transform(this)

fun DomainTeamMember.toNetworkTeamMember() = ObjectTransformer(
    DomainTeamMember::class, NetworkTeamMember::class
).transform(this)
