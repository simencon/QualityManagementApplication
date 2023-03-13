package com.simenko.qmapp.retrofit.entities

import com.simenko.qmapp.domain.DomainOrder
import com.simenko.qmapp.domain.DomainSample
import com.simenko.qmapp.domain.DomainSubOrder
import com.simenko.qmapp.domain.DomainSubOrderTask
import com.simenko.qmapp.utils.ObjectTransformer

//KClasses can only take primary constructor
fun DomainOrder.toNetworkOrderWithoutId() = ObjectTransformer(
    DomainOrder::class, NetworkOrder::class
).transform(this)

fun DomainOrder.toNetworkOrderWithId() = NetworkOrder(
    id = id,
    orderTypeId = orderTypeId,
    reasonId = reasonId,
    orderNumber = orderNumber,
    customerId = customerId,
    orderedById = orderedById,
    statusId = statusId,
    createdDate = createdDate,
    completedDate = completedDate
)

fun DomainSubOrder.toNetworkSubOrderWithoutId() = ObjectTransformer(
    DomainSubOrder::class, NetworkSubOrder::class
).transform(this)

fun DomainSubOrder.toNetworkSubOrderWithId() = NetworkSubOrder(
    id = id,
    orderId = orderId,//maybe currentOrder.id?
    subOrderNumber = subOrderNumber,
    orderedById = orderedById,
    completedById = completedById,
    statusId = statusId,
    createdDate = createdDate,
    completedDate = completedDate,
    departmentId = departmentId,
    subDepartmentId = subDepartmentId,
    channelId = channelId,
    lineId = lineId,
    operationId = operationId,
    itemPreffix = itemPreffix,
    itemTypeId = itemTypeId,
    itemVersionId = itemVersionId,
    samplesCount = samplesCount
)

fun DomainSubOrderTask.toNetworkSubOrderTaskWithoutId() = ObjectTransformer(
    DomainSubOrderTask::class, NetworkSubOrderTask::class
).transform(this)

fun DomainSample.toNetworkSampleWithoutId() = ObjectTransformer(
    DomainSample::class, NetworkSample::class
).transform(this)