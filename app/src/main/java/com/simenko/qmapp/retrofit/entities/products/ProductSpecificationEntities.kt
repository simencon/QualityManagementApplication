package com.simenko.qmapp.retrofit.entities.products

import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkManufacturingProject(
    var id: Int,
    var companyId: Int,
    var factoryLocationDep: Long,
    var factoryLocationDetails: String? = null,
    var customerName: String? = null,
    var team: Int? = null,
    var modelYear: String? = null,
    var projectSubject: String? = null,
    var startDate: String? = null,
    var revisionDate: String? = null,
    var refItem: String? = null,
    var pfmeaNum: String? = null,
    var processOwner: Long,
    var confLevel: Int? = null
) : NetworkBaseModel<DatabaseManufacturingProject> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkManufacturingProject::class, DatabaseManufacturingProject::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkKey(
    var id: Int,
    var projectId: Int?,
    var componentKey: String?,
    var componentKeyDescription: String?
) : NetworkBaseModel<DatabaseKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkKey::class, DatabaseKey::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductBase(
    var id: Int,
    var projectId: Int?,
    var componentBaseDesignation: String?
) : NetworkBaseModel<DatabaseProductBase> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseProductBase {
        return ObjectTransformer(NetworkProductBase::class, DatabaseProductBase::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkProductKind(
    val id: Long,
    val projectId: Long,
    val productKindDesignation: String,
    val comments: String?
) : NetworkBaseModel<DatabaseProductKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKind::class, DatabaseProductKind::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentKind(
    val id: Long,
    val productKindId: Long,
    val componentKindOrder: Int,
    val componentKindDescription: String
) : NetworkBaseModel<DatabaseComponentKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKind::class, DatabaseComponentKind::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentStageKind(
    val id: Long,
    val componentKindId: Long,
    val componentStageOrder: Int,
    val componentStageDescription: String
) : NetworkBaseModel<DatabaseComponentStageKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentStageKind::class, DatabaseComponentStageKind::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductKindKey(
    val id: Long,
    val productKindId: Long,
    val keyId: Long
) : NetworkBaseModel<DatabaseProductKindKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKindKey::class, DatabaseProductKindKey::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentKindKey(
    val id: Long,
    val componentKindId: Long,
    val keyId: Long
) : NetworkBaseModel<DatabaseComponentKindKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKindKey::class, DatabaseComponentKindKey::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentStageKindKey(
    val id: Long,
    val componentStageKindId: Long,
    val keyId: Long
) : NetworkBaseModel<DatabaseComponentStageKindKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentStageKindKey::class, DatabaseComponentStageKindKey::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProduct(
    var id: Int,
    var productBaseId: Int?,
    var keyId: Int?,
    var productDesignation: String?
) : NetworkBaseModel<DatabaseProduct> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProduct::class, DatabaseProduct::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponent(
    var id: Int,
    var keyId: Int?,
    var componentDesignation: String?,
    var ifAny: Int?
) : NetworkBaseModel<DatabaseComponent> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponent::class, DatabaseComponent::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentInStage(
    var id: Int,
    var keyId: Int?,
    var componentInStageDescription: String?,
    var ifAny: Int?
) : NetworkBaseModel<DatabaseComponentInStage> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentInStage::class, DatabaseComponentInStage::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductKindProduct(
    val id: Long,
    val productKindId: Long,
    val productId: Long
) : NetworkBaseModel<DatabaseProductKindProduct> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKindProduct::class, DatabaseProductKindProduct::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentKindComponent(
    val id: Long,
    val componentKindId: Long,
    val componentId: Long
) : NetworkBaseModel<DatabaseComponentKindComponent> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKindComponent::class, DatabaseComponentKindComponent::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentStageKindComponentStage(
    val id: Long,
    val componentStageKindId: Long,
    val componentStageId: Long
) : NetworkBaseModel<DatabaseComponentStageKindComponentStage> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentStageKindComponentStage::class, DatabaseComponentStageKindComponentStage::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductComponent(
    val id: Long,
    val countOfComponents: Int,
    val productId: Long,
    val componentId: Long
) : NetworkBaseModel<DatabaseProductComponent> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductComponent::class, DatabaseProductComponent::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentComponentStage(
    val id: Long,
    val componentId: Long,
    val componentStageId: Long
) : NetworkBaseModel<DatabaseComponentComponentStage> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentComponentStage::class, DatabaseComponentComponentStage::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkVersionStatus(
    var id: Int,
    var statusDescription: String?
) : NetworkBaseModel<DatabaseVersionStatus> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkVersionStatus::class, DatabaseVersionStatus::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductVersion(
    var id: Int,
    var productId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
) : NetworkBaseModel<DatabaseProductVersion> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductVersion::class, DatabaseProductVersion::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentVersion(
    var id: Int,
    var componentId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
) : NetworkBaseModel<DatabaseComponentVersion> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentVersion::class, DatabaseComponentVersion::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentInStageVersion(
    var id: Int,
    var componentInStageId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
) : NetworkBaseModel<DatabaseComponentInStageVersion> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentInStageVersion::class, DatabaseComponentInStageVersion::class).transform(this)
}
