package com.simenko.qmapp.data.remote.entities.products

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.remote.NetworkBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCharGroup (
    var id: ID,
    val productLineId: ID,
    var ishElement: String?
) : NetworkBaseModel<DatabaseCharGroup> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharGroup::class, DatabaseCharGroup::class).transform(this)
}

@Serializable
data class NetworkCharSubGroup (
    var id: ID,
    val charGroupId: ID,
    var ishElement: String?,
    var measurementGroupRelatedTime: Double?
) : NetworkBaseModel<DatabaseCharSubGroup> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharSubGroup::class, DatabaseCharSubGroup::class).transform(this)
}


@Serializable
data class NetworkCharacteristic (
    var id: ID,
    @SerialName("ishSubChar") var ishSubCharId: ID,
    var charOrder: Int? = null,
    var charDesignation: String? = null,
    var charDescription: String? = null,
    var sampleRelatedTime: Double? = null,
    var measurementRelatedTime: Double? = null
) : NetworkBaseModel<DatabaseCharacteristic> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharacteristic::class, DatabaseCharacteristic::class).transform(this)
}

@Serializable
data class NetworkMetrix (
    var id: ID,
    var charId: ID,
    var metrixOrder: Int? = null,
    var metrixDesignation: String? = null,
    var metrixDescription: String? = null,
    var units: String? = null
) : NetworkBaseModel<DatabaseMetrix> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkMetrix::class, DatabaseMetrix::class).transform(this)
}

@Serializable
data class NetworkCharacteristicProductKind(
    val id: ID,
    val charId: ID,
    val productKindId: ID
) : NetworkBaseModel<DatabaseCharacteristicProductKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharacteristicProductKind::class, DatabaseCharacteristicProductKind::class).transform(this)
}

@Serializable
data class NetworkCharacteristicComponentKind(
    val id: ID,
    val charId: ID,
    val componentKindId: ID
) : NetworkBaseModel<DatabaseCharacteristicComponentKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharacteristicComponentKind::class, DatabaseCharacteristicComponentKind::class).transform(this)
}

@Serializable
data class NetworkCharacteristicComponentStageKind(
    val id: ID,
    val charId: ID,
    val componentStageKindId: ID
) : NetworkBaseModel<DatabaseCharacteristicComponentStageKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharacteristicComponentStageKind::class, DatabaseCharacteristicComponentStageKind::class).transform(this)
}

@Serializable
data class NetworkProductTolerance(
    var id: ID,
    var metrixId: ID,
    var versionId: ID,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : NetworkBaseModel<DatabaseProductTolerance> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductTolerance::class, DatabaseProductTolerance::class).transform(this)
}

@Serializable
data class NetworkComponentTolerance(
    var id: ID,
    var metrixId: ID,
    var versionId: ID,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : NetworkBaseModel<DatabaseComponentTolerance> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentTolerance::class, DatabaseComponentTolerance::class).transform(this)
}

@Serializable
data class NetworkComponentInStageTolerance(
    var id: ID,
    var metrixId: ID,
    var versionId: ID,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : NetworkBaseModel<DatabaseComponentInStageTolerance> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentInStageTolerance::class, DatabaseComponentInStageTolerance::class).transform(this)
}