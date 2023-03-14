package com.simenko.qmapp.room.entities

import com.simenko.qmapp.domain.DomainOrder
import com.simenko.qmapp.domain.DomainSample
import com.simenko.qmapp.domain.DomainSubOrder
import com.simenko.qmapp.domain.DomainSubOrderTask
import com.simenko.qmapp.retrofit.entities.NetworkOrder
import com.simenko.qmapp.retrofit.entities.NetworkSample
import com.simenko.qmapp.retrofit.entities.NetworkSubOrder
import com.simenko.qmapp.retrofit.entities.NetworkSubOrderTask
import com.simenko.qmapp.utils.ObjectTransformer

fun DomainOrder.toDatabaseOrder() =
    ObjectTransformer(DomainOrder::class, DatabaseOrder::class).transform(this)

fun NetworkOrder.toDatabaseOrder() =
    ObjectTransformer(NetworkOrder::class, DatabaseOrder::class).transform(this)

fun DomainSubOrder.toDatabaseSubOrder() =
    ObjectTransformer(DomainSubOrder::class, DatabaseSubOrder::class).transform(this)

fun NetworkSubOrder.toDatabaseSubOrder() =
    ObjectTransformer(NetworkSubOrder::class, DatabaseSubOrder::class).transform(this)

fun DomainSubOrderTask.toDatabaseSubOrderTask() =
    ObjectTransformer(DomainSubOrderTask::class, DatabaseSubOrderTask::class).transform(this)

fun NetworkSubOrderTask.toDatabaseSubOrderTask() =
    ObjectTransformer(NetworkSubOrderTask::class, DatabaseSubOrderTask::class).transform(this)

fun DomainSample.toDatabaseSample() =
    ObjectTransformer(DomainSample::class, DatabaseSample::class).transform(this)

fun NetworkSample.toDatabaseSample() =
    ObjectTransformer(NetworkSample::class, DatabaseSample::class).transform(this)