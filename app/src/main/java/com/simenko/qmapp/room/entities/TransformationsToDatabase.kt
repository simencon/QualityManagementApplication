package com.simenko.qmapp.room.entities

import com.simenko.qmapp.domain.*
import com.simenko.qmapp.retrofit.entities.*
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

fun DomainResult.toDatabaseResult() =
    ObjectTransformer(DomainResult::class, DatabaseResult::class).transform(this)

fun NetworkResult.toDatabaseResult() =
    ObjectTransformer(NetworkResult::class, DatabaseResult::class).transform(this)