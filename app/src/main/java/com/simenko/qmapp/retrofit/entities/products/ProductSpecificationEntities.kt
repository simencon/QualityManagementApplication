package com.simenko.qmapp.retrofit.entities.products

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkProductLine(
    var id: ID,
    var companyId: ID,
    var factoryLocationDep: ID,
    var factoryLocationDetails: String? = null,
    var customerName: String? = null,
    var team: ID? = null,
    var modelYear: String? = null,
    var projectSubject: String? = null,
    var startDate: String? = null,
    var revisionDate: String? = null,
    var refItem: String? = null,
    var pfmeaNum: String? = null,
    var processOwner: ID,
    var confLevel: ID? = null
) : NetworkBaseModel<DatabaseProductLine> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductLine::class, DatabaseProductLine::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkKey(
    var id: ID,
    var projectId: ID,
    var componentKey: String,
    var componentKeyDescription: String?
) : NetworkBaseModel<DatabaseKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkKey::class, DatabaseKey::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductBase(
    var id: ID,
    var projectId: ID?,
    var componentBaseDesignation: String?
) : NetworkBaseModel<DatabaseProductBase> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseProductBase {
        return ObjectTransformer(NetworkProductBase::class, DatabaseProductBase::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkProductKind(
    val id: ID,
    val projectId: ID,
    val productKindDesignation: String,
    val comments: String?
) : NetworkBaseModel<DatabaseProductKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKind::class, DatabaseProductKind::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentKind(
    val id: ID,
    val productKindId: ID,
    val componentKindOrder: Int,
    val componentKindDescription: String
) : NetworkBaseModel<DatabaseComponentKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKind::class, DatabaseComponentKind::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentStageKind(
    val id: ID,
    val componentKindId: ID,
    val componentStageOrder: Int,
    val componentStageDescription: String
) : NetworkBaseModel<DatabaseComponentStageKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentStageKind::class, DatabaseComponentStageKind::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductKindKey(
    val id: ID,
    val productKindId: ID,
    val keyId: ID
) : NetworkBaseModel<DatabaseProductKindKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKindKey::class, DatabaseProductKindKey::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentKindKey(
    val id: ID,
    val componentKindId: ID,
    val keyId: ID
) : NetworkBaseModel<DatabaseComponentKindKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKindKey::class, DatabaseComponentKindKey::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentStageKindKey(
    val id: ID,
    val componentStageKindId: ID,
    val keyId: ID
) : NetworkBaseModel<DatabaseComponentStageKindKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentStageKindKey::class, DatabaseComponentStageKindKey::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProduct(
    var id: ID,
    var productBaseId: ID,
    var keyId: ID,
    var productDesignation: String
) : NetworkBaseModel<DatabaseProduct> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProduct::class, DatabaseProduct::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponent(
    var id: ID,
    var keyId: ID?,
    var componentDesignation: String?,
    var ifAny: Int?
) : NetworkBaseModel<DatabaseComponent> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponent::class, DatabaseComponent::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentInStage(
    var id: ID,
    var keyId: ID?,
    var componentInStageDescription: String?,
    var ifAny: Int?
) : NetworkBaseModel<DatabaseComponentInStage> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentInStage::class, DatabaseComponentInStage::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductKindProduct(
    val id: ID,
    val productKindId: ID,
    val productId: ID
) : NetworkBaseModel<DatabaseProductKindProduct> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKindProduct::class, DatabaseProductKindProduct::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentKindComponent(
    val id: ID,
    val componentKindId: ID,
    val componentId: ID
) : NetworkBaseModel<DatabaseComponentKindComponent> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKindComponent::class, DatabaseComponentKindComponent::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentStageKindComponentStage(
    val id: ID,
    val componentStageKindId: ID,
    val componentStageId: ID
) : NetworkBaseModel<DatabaseComponentStageKindComponentStage> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentStageKindComponentStage::class, DatabaseComponentStageKindComponentStage::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductComponent(
    val id: ID,
    val countOfComponents: Int,
    val productId: ID,
    val componentId: ID
) : NetworkBaseModel<DatabaseProductComponent> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductComponent::class, DatabaseProductComponent::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentComponentStage(
    val id: ID,
    val componentId: ID,
    val componentStageId: ID
) : NetworkBaseModel<DatabaseComponentComponentStage> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentComponentStage::class, DatabaseComponentComponentStage::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkVersionStatus(
    var id: ID,
    var statusDescription: String?
) : NetworkBaseModel<DatabaseVersionStatus> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkVersionStatus::class, DatabaseVersionStatus::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkProductVersion(
    var id: ID,
    var productId: ID,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: ID?,
    var isDefault: Boolean
) : NetworkBaseModel<DatabaseProductVersion> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductVersion::class, DatabaseProductVersion::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentVersion(
    var id: ID,
    var componentId: ID,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: ID?,
    var isDefault: Boolean
) : NetworkBaseModel<DatabaseComponentVersion> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentVersion::class, DatabaseComponentVersion::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentInStageVersion(
    var id: ID,
    var componentInStageId: ID,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: ID?,
    var isDefault: Boolean
) : NetworkBaseModel<DatabaseComponentInStageVersion> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentInStageVersion::class, DatabaseComponentInStageVersion::class).transform(this)
}
