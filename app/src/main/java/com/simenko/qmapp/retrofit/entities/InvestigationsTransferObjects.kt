package com.simenko.qmapp.retrofit.entities

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ObjectTransformer
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkInputForOrder constructor(
    var depId: ID,
    var depAbbr: String,
    var depOrder: Int,
    var subDepId: ID,
    var subDepAbbr: String,
    var subDepOrder: Int,
    var chId: ID,
    var channelAbbr: String,
    var channelOrder: Int,
    var lineId: ID,
    var lineAbbr: String,
    var lineOrder: Int,
    var id: String,
    @Json(name = "itemPreffix")
    var itemPrefix: String,
    var itemId: ID,
    var itemVersionId: ID,
    var isDefault: Boolean,
    var itemKey: String,
    var itemDesignation: String,
    var operationId: ID,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int,
    var charId: ID,
    var ishSubChar: ID,
    var charDescription: String,
    var charDesignation: String? = null,
    var charOrder: Int
) : NetworkBaseModel<DatabaseInputForOrder> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkInputForOrder::class, DatabaseInputForOrder::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkOrdersStatus constructor(
    var id: ID,
    var statusDescription: String? = null
) : NetworkBaseModel<DatabaseOrdersStatus> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseOrdersStatus {
        return ObjectTransformer(NetworkOrdersStatus::class, DatabaseOrdersStatus::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkReason(
    var id: ID,
    var reasonDescription: String? = null,
    var reasonFormalDescript: String? = null,
    var reasonOrder: Int? = null
): NetworkBaseModel<DatabaseReason> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseReason {
        return ObjectTransformer(NetworkReason::class, DatabaseReason::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkOrdersType constructor(
    var id: ID,
    var typeDescription: String? = null
) : NetworkBaseModel<DatabaseOrdersType> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseOrdersType {
        return ObjectTransformer(NetworkOrdersType::class, DatabaseOrdersType::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkOrder constructor(
    var id: ID,
    var orderTypeId: ID,
    var reasonId: ID,
    var orderNumber: Long? = null,
    var customerId: ID,
    var orderedById: ID,
    var statusId: ID,
    var createdDate: Long,//Format : "2023-02-02T15:44:47.028Z"
    var completedDate: Long? = null
) : NetworkBaseModel<DatabaseOrder> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseOrder {
        return ObjectTransformer(NetworkOrder::class, DatabaseOrder::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkSubOrder constructor(
    var id: ID = 0,
    var orderId: ID,
    var subOrderNumber: Long,
    var orderedById: ID,
    var completedById: ID? = null,
    var statusId: ID,
    var createdDate: Long,
    var completedDate: Long? = null,
    var departmentId: ID,
    var subDepartmentId: ID,
    var channelId: ID,
    var lineId: ID,
    var operationId: ID,
    var itemPreffix: String,
    var itemTypeId: ID,
    var itemVersionId: ID,
    var samplesCount: Int? = null,
    var remarkId: ID
) : NetworkBaseModel<DatabaseSubOrder> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseSubOrder {
        return ObjectTransformer(NetworkSubOrder::class, DatabaseSubOrder::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkSubOrderTask constructor(
    var id: ID,
    var subOrderId: ID,
    var charId: ID,
    var statusId: ID,
    var createdDate: Long? = null,
    var completedDate: Long? = null,
    var orderedById: ID? = null,
    var completedById: ID? = null,
) : NetworkBaseModel<DatabaseSubOrderTask> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseSubOrderTask {
        return ObjectTransformer(NetworkSubOrderTask::class, DatabaseSubOrderTask::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkSample constructor(
    var id: ID,
    var subOrderId: ID,
    var sampleNumber: Int? = null
) : NetworkBaseModel<DatabaseSample> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseSample {
        return ObjectTransformer(NetworkSample::class, DatabaseSample::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkResultsDecryption constructor(
    var id: ID,
    var resultDecryption: String? = null
) : NetworkBaseModel<DatabaseResultsDecryption> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseResultsDecryption {
        return ObjectTransformer(NetworkResultsDecryption::class, DatabaseResultsDecryption::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkResult constructor(
    var id: ID = 0,
    var sampleId: ID,
    var metrixId: ID,
    var result: Float? = null,
    var isOk: Boolean? = null,
    var resultDecryptionId: ID,
    var taskId: ID
) : NetworkBaseModel<DatabaseResult> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseResult {
        return ObjectTransformer(NetworkResult::class, DatabaseResult::class).transform(this)
    }
}