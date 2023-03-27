package com.simenko.qmapp.retrofit.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkInputForOrder constructor(
    var id: Int,
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
    var recordId: String,
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
)

@JsonClass(generateAdapter = true)
data class NetworkOrdersStatus constructor(
    var id: Int,
    var statusDescription: String? = null
)

@JsonClass(generateAdapter = true)
data class NetworkReason(
    var id: Int,
    var reasonDescription: String? = null,
    var reasonFormalDescript: String? = null,
    var reasonOrder: Int? = null
)

@JsonClass(generateAdapter = true)
data class NetworkOrdersType constructor(
    var id: Int,
    var typeDescription: String? = null
)

@JsonClass(generateAdapter = true)
data class NetworkOrder constructor(
    var orderTypeId: Int,
    var reasonId: Int,
    var orderNumber: Int? = null,
    var customerId: Int,
    var orderedById: Int,
    var statusId: Int,
    var createdDate: String,//Format : "2023-02-02T15:44:47.028Z"
    var completedDate: String? = null
) {
    var id: Int = 0

    constructor(
        id: Int,
        orderTypeId: Int,
        reasonId: Int,
        orderNumber: Int? = null,
        customerId: Int,
        orderedById: Int,
        statusId: Int,
        createdDate: String,//Format : "2023-02-02T15:44:47.028Z"
        completedDate: String? = null
    ) : this(
        orderTypeId,
        reasonId,
        orderNumber,
        customerId,
        orderedById,
        statusId,
        createdDate,
        completedDate
    ) {
        this.id = id
    }
}

@JsonClass(generateAdapter = true)
data class NetworkSubOrder constructor(
    var orderId: Int,
    var subOrderNumber: Int,
    var orderedById: Int,
    var completedById: Int? = null,
    var statusId: Int,
    var createdDate: String,
    var completedDate: String? = null,
    var departmentId: Int,
    var subDepartmentId: Int,
    var channelId: Int,
    var lineId: Int,
    var operationId: Int,
    var itemPreffix: String,
    var itemTypeId: Int,
    var itemVersionId: Int,
    var samplesCount: Int? = null
) {
    var id: Int = 0

    constructor(
        id: Int,
        orderId: Int,
        subOrderNumber: Int,
        orderedById: Int,
        completedById: Int? = null,
        statusId: Int,
        createdDate: String,
        completedDate: String? = null,
        departmentId: Int,
        subDepartmentId: Int,
        channelId: Int,
        lineId: Int,
        operationId: Int,
        itemPreffix: String,
        itemTypeId: Int,
        itemVersionId: Int,
        samplesCount: Int? = null
    ) : this(
        orderId,
        subOrderNumber,
        orderedById,
        completedById,
        statusId,
        createdDate,
        completedDate,
        departmentId,
        subDepartmentId,
        channelId,
        lineId,
        operationId,
        itemPreffix,
        itemTypeId,
        itemVersionId,
        samplesCount
    ) {
        this.id = id
    }
}

@JsonClass(generateAdapter = true)
data class NetworkSubOrderTask constructor(
    var id: Int,
    var subOrderId: Int,
    var charId: Int,
    var statusId: Int,
    var createdDate: String? = null,
    var completedDate: String? = null,
    var orderedById: Int? = null,
    var completedById: Int? = null,
)

@JsonClass(generateAdapter = true)
data class NetworkSample constructor(
    var id: Int,
    var subOrderId: Int,
    var sampleNumber: Int? = null
)

@JsonClass(generateAdapter = true)
data class NetworkResultsDecryption constructor(
    var id: Int,
    var resultDecryption: String? = null
)

@JsonClass(generateAdapter = true)
data class NetworkResult constructor(
    var sampleId: Int,
    var metrixId: Int,
    var result: Float? = null,
    var isOk: Boolean? = null,
    var resultDecryptionId: Int,
    var taskId: Int
) {
    var id: Int = 0

    constructor(
        id: Int,
        sampleId: Int,
        metrixId: Int,
        result: Float?,
        isOk: Boolean?,
        resultDecryptionId: Int,
        taskId: Int
    ) : this(
        sampleId = sampleId,
        metrixId = metrixId,
        result = result,
        isOk = isOk,
        resultDecryptionId = resultDecryptionId,
        taskId = taskId
    ) {
        this.id = id
    }
}