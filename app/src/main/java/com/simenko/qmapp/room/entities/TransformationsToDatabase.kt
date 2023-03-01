package com.simenko.qmapp.room.entities

import com.simenko.qmapp.domain.DomainOrder
import com.simenko.qmapp.retrofit.entities.NetworkOrder
import com.simenko.qmapp.utils.ObjectTransformer

fun DomainOrder.toDatabaseOrder() =
    ObjectTransformer(DomainOrder::class, DatabaseOrder::class).transform(this)

fun NetworkOrder.toDatabaseOrder() =
    ObjectTransformer(NetworkOrder::class, DatabaseOrder::class).transform(this)