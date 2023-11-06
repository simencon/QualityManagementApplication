package com.simenko.qmapp.retrofit.entities.products

import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer

data class NetworkProductKind(
    val id: Long,
    val projectId: Long,
    val productKindDesignation: String,
    val comments: String?
) : NetworkBaseModel<DatabaseProductKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKind::class, DatabaseProductKind::class).transform(this)
}

data class NetworkComponentKind(
    val id: Long,
    val productKindId: Long,
    val componentKindOrder: Int,
    val componentKindDescription: String
) : NetworkBaseModel<DatabaseComponentKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKind::class, DatabaseComponentKind::class).transform(this)
}

data class NetworkComponentStageKind(
    val id: Long,
    val componentKindId: Long,
    val componentStageOrder: Int,
    val componentStageDescription: String
) : NetworkBaseModel<DatabaseComponentStageKind> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentStageKind::class, DatabaseComponentStageKind::class).transform(this)
}

data class NetworkProductKindKey(
    val id: Long,
    val productKindId: Long,
    val keyId: Long
) : NetworkBaseModel<DatabaseProductKindKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKindKey::class, DatabaseProductKindKey::class).transform(this)
}

data class NetworkComponentKindKey(
    val id: Long,
    val componentKindId: Long,
    val keyId: Long
) : NetworkBaseModel<DatabaseComponentKindKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKindKey::class, DatabaseComponentKindKey::class).transform(this)
}

data class NetworkComponentStageKindKey(
    val id: Long,
    val componentStageKindId: Long,
    val keyId: Long
) : NetworkBaseModel<DatabaseComponentStageKindKey> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentStageKindKey::class, DatabaseComponentStageKindKey::class).transform(this)
}

data class NetworkProductKindProduct(
    val id: Long,
    val productKindId: Long,
    val productId: Long
) : NetworkBaseModel<DatabaseProductKindProduct> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKindProduct::class, DatabaseProductKindProduct::class).transform(this)
}

data class NetworkComponentKindComponent(
    val id: Long,
    val componentKindId: Long,
    val componentId: Long
) : NetworkBaseModel<DatabaseComponentKindComponent> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKindComponent::class, DatabaseComponentKindComponent::class).transform(this)
}

data class NetworkComponentStageKindComponentStage(
    val id: Long,
    val componentStageKindId: Long,
    val componentStageId: Long
) : NetworkBaseModel<DatabaseComponentStageKindComponentStage> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentStageKindComponentStage::class, DatabaseComponentStageKindComponentStage::class).transform(this)
}

data class NetworkProductComponent(
    val id: Long,
    val countOfComponents: Int,
    val productId: Long,
    val componentId: Long
) : NetworkBaseModel<DatabaseProductComponent> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductComponent::class, DatabaseProductComponent::class).transform(this)
}

data class NetworkComponentComponentStage(
    val id: Long,
    val componentId: Long,
    val componentStageId: Long
) : NetworkBaseModel<DatabaseComponentComponentStage> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentComponentStage::class, DatabaseComponentComponentStage::class).transform(this)
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
