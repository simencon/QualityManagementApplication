package com.simenko.qmapp.retrofit.entities.products

import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkCharGroup constructor(
    var id: Int,
    val productLineId: Long,
    var ishElement: String?
) : NetworkBaseModel<DatabaseCharGroup> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharGroup::class, DatabaseCharGroup::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkCharSubGroup constructor(
    var id: Int,
    val charGroupId: Long,
    var ishElement: String?,
    var measurementGroupRelatedTime: Double?
) : NetworkBaseModel<DatabaseCharSubGroup> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharSubGroup::class, DatabaseCharSubGroup::class).transform(this)
}



@JsonClass(generateAdapter = true)
data class NetworkCharacteristic constructor(
    var id: Int,
    @Json(name="ishSubChar")
    var ishSubCharId: Int,
    var charOrder: Int? = null,
    var charDesignation: String? = null,
    var charDescription: String? = null,
    var sampleRelatedTime: Double? = null,
    var measurementRelatedTime: Double? = null
) : NetworkBaseModel<DatabaseCharacteristic> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharacteristic::class, DatabaseCharacteristic::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkMetrix constructor(
    var id: Int,
    var charId: Int,
    var metrixOrder: Int? = null,
    var metrixDesignation: String? = null,
    var metrixDescription: String? = null,
    var units: String? = null
) : NetworkBaseModel<DatabaseMetrix> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkMetrix::class, DatabaseMetrix::class).transform(this)
}

data class NetworkCharacteristicProductKind(
    val id: Long,
    val charId: Long,
    val productKindId: Long
) : NetworkBaseModel<DatabaseCharacteristicProductKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharacteristicProductKind::class, DatabaseCharacteristicProductKind::class).transform(this)
}

data class NetworkCharacteristicComponentKind(
    val id: Long,
    val charId: Long,
    val componentKindId: Long
) : NetworkBaseModel<DatabaseCharacteristicComponentKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharacteristicComponentKind::class, DatabaseCharacteristicComponentKind::class).transform(this)
}

data class NetworkCharacteristicComponentStageKind(
    val id: Long,
    val charId: Long,
    val componentStageKindId: Long
) : NetworkBaseModel<DatabaseCharacteristicComponentStageKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharacteristicComponentStageKind::class, DatabaseCharacteristicComponentStageKind::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductTolerance(
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : NetworkBaseModel<DatabaseProductTolerance> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductTolerance::class, DatabaseProductTolerance::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentTolerance(
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : NetworkBaseModel<DatabaseComponentTolerance> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentTolerance::class, DatabaseComponentTolerance::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentInStageTolerance(
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : NetworkBaseModel<DatabaseComponentInStageTolerance> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentInStageTolerance::class, DatabaseComponentInStageTolerance::class).transform(this)
}