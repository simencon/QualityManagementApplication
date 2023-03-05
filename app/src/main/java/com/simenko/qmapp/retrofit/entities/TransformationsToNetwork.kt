package com.simenko.qmapp.retrofit.entities

import com.simenko.qmapp.domain.DomainOrder
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