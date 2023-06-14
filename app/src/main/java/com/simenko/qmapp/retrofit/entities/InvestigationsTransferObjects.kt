package com.simenko.qmapp.retrofit.entities

import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ObjectTransformer
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkInputForOrder constructor(
    var depId: Int,
    var depAbbr: String,
    var depOrder: Int,
    var subDepId: Int,
    var subDepAbbr: String,
    var subDepOrder: Int,
    var chId: Int,
    var channelAbbr: String,
    var channelOrder: Int,
    var lineId: Int,
    var lineAbbr: String,
    var lineOrder: Int,
    var id: String,
    @Json(name = "itemPreffix")
    var itemPrefix: String,
    var itemId: Int,
    var itemVersionId: Int,
    var isDefault: Boolean,
    var itemKey: String,
    var itemDesignation: String,
    var operationId: Int,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int,
    var charId: Int,
    var ishCharId: Int,
    var ishSubChar: Int,
    var charDescription: String,
    var charDesignation: String? = null,
    var charOrder: Int
) : NetworkBaseModel<DatabaseInputForOrder> {
    override fun toDatabaseModel(): DatabaseInputForOrder {
        return ObjectTransformer(NetworkInputForOrder::class, DatabaseInputForOrder::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkOrdersStatus constructor(
    var id: Int,
    var statusDescription: String? = null
) : NetworkBaseModel<DatabaseOrdersStatus> {
    override fun toDatabaseModel(): DatabaseOrdersStatus {
        return ObjectTransformer(NetworkOrdersStatus::class, DatabaseOrdersStatus::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkReason(
    var id: Int,
    var reasonDescription: String? = null,
    var reasonFormalDescript: String? = null,
    var reasonOrder: Int? = null
): NetworkBaseModel<DatabaseReason> {
    override fun toDatabaseModel(): DatabaseReason {
        return ObjectTransformer(NetworkReason::class, DatabaseReason::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkOrdersType constructor(
    var id: Int,
    var typeDescription: String? = null
) : NetworkBaseModel<DatabaseOrdersType> {
    override fun toDatabaseModel(): DatabaseOrdersType {
        return ObjectTransformer(NetworkOrdersType::class, DatabaseOrdersType::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkOrder constructor(
    var id: Int = 0,
    var orderTypeId: Int,
    var reasonId: Int,
    var orderNumber: Int? = null,
    var customerId: Int,
    var orderedById: Int,
    var statusId: Int,
    var createdDate: Long,//Format : "2023-02-02T15:44:47.028Z"
    var completedDate: Long? = null
) : NetworkBaseModel<DatabaseOrder> {
    override fun toDatabaseModel(): DatabaseOrder {
        return ObjectTransformer(NetworkOrder::class, DatabaseOrder::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkSubOrder constructor(
    var id: Int = 0,
    var orderId: Int,
    var subOrderNumber: Int,
    var orderedById: Int,
    var completedById: Int? = null,
    var statusId: Int,
    var createdDate: Long,
    var completedDate: Long? = null,
    var departmentId: Int,
    var subDepartmentId: Int,
    var channelId: Int,
    var lineId: Int,
    var operationId: Int,
    var itemPreffix: String,
    var itemTypeId: Int,
    var itemVersionId: Int,
    var samplesCount: Int? = null,
    var remarkId: Int
) : NetworkBaseModel<DatabaseSubOrder> {
    override fun toDatabaseModel(): DatabaseSubOrder {
        return ObjectTransformer(NetworkSubOrder::class, DatabaseSubOrder::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkSubOrderTask constructor(
    var id: Int,
    var subOrderId: Int,
    var charId: Int,
    var statusId: Int,
    var createdDate: Long? = null,
    var completedDate: Long? = null,
    var orderedById: Int? = null,
    var completedById: Int? = null,
) : NetworkBaseModel<DatabaseSubOrderTask> {
    override fun toDatabaseModel(): DatabaseSubOrderTask {
        return ObjectTransformer(NetworkSubOrderTask::class, DatabaseSubOrderTask::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkSample constructor(
    var id: Int,
    var subOrderId: Int,
    var sampleNumber: Int? = null
) : NetworkBaseModel<DatabaseSample> {
    override fun toDatabaseModel(): DatabaseSample {
        return ObjectTransformer(NetworkSample::class, DatabaseSample::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkResultsDecryption constructor(
    var id: Int,
    var resultDecryption: String? = null
) : NetworkBaseModel<DatabaseResultsDecryption> {
    override fun toDatabaseModel(): DatabaseResultsDecryption {
        return ObjectTransformer(NetworkResultsDecryption::class, DatabaseResultsDecryption::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkResult constructor(
    var id: Int = 0,
    var sampleId: Int,
    var metrixId: Int,
    var result: Float? = null,
    var isOk: Boolean? = null,
    var resultDecryptionId: Int,
    var taskId: Int
) : NetworkBaseModel<DatabaseResult> {
    override fun toDatabaseModel(): DatabaseResult {
        return ObjectTransformer(NetworkResult::class, DatabaseResult::class).transform(this)
    }
}