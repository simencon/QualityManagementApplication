package com.simenko.qmapp.domain.entities.products

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer

data class DomainProductKind(
    val id: Long,
    val projectId: Long,
    val productKindDesignation: String,
    val comments: String?
) : DomainBaseModel<DatabaseProductKind>() {
    override fun getRecordId() = id
    override fun getParentId() = projectId.toInt()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductKind::class, DatabaseProductKind::class).transform(this)
}

data class DomainComponentKind(
    val id: Long,
    val productKindId: Long,
    val componentKindOrder: Int,
    val componentKindDescription: String
) : DomainBaseModel<DatabaseComponentKind>() {
    override fun getRecordId() = id
    override fun getParentId() = productKindId.toInt()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentKind::class, DatabaseComponentKind::class).transform(this)
}

data class DomainComponentStageKind(
    val id: Long,
    val componentKindId: Long,
    val componentStageOrder: Int,
    val componentStageDescription: String
) : DomainBaseModel<DatabaseComponentStageKind>() {
    override fun getRecordId() = id
    override fun getParentId() = componentKindId.toInt()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentStageKind::class, DatabaseComponentStageKind::class).transform(this)
}

data class DomainProductKindKey(
    val id: Long,
    val productKindId: Long,
    val keyId: Long
) : DomainBaseModel<DatabaseProductKindKey>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductKindKey::class, DatabaseProductKindKey::class).transform(this)
}

data class DomainComponentKindKey(
    val id: Long,
    val componentKindId: Long,
    val keyId: Long
) : DomainBaseModel<DatabaseComponentKindKey>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentKindKey::class, DatabaseComponentKindKey::class).transform(this)
}

data class DomainComponentStageKindKey(
    val id: Long,
    val componentStageKindId: Long,
    val keyId: Long
) : DomainBaseModel<DatabaseComponentStageKindKey>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentStageKindKey::class, DatabaseComponentStageKindKey::class).transform(this)
}

data class DomainProductKindProduct(
    val id: Long,
    val productKindId: Long,
    val productId: Long
) : DomainBaseModel<DatabaseProductKindProduct>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductKindProduct::class, DatabaseProductKindProduct::class).transform(this)
}

data class DomainComponentKindComponent(
    val id: Long,
    val componentKindId: Long,
    val componentId: Long
) : DomainBaseModel<DatabaseComponentKindComponent>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentKindComponent::class, DatabaseComponentKindComponent::class).transform(this)
}

data class DomainComponentStageKindComponentStage(
    val id: Long,
    val componentStageKindId: Long,
    val componentStageId: Long
) : DomainBaseModel<DatabaseComponentStageKindComponentStage>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentStageKindComponentStage::class, DatabaseComponentStageKindComponentStage::class).transform(this)
}

data class DomainProductComponent(
    val id: Long,
    val countOfComponents: Int,
    val productId: Long,
    val componentId: Long
) : DomainBaseModel<DatabaseProductComponent>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductComponent::class, DatabaseProductComponent::class).transform(this)
}

data class DomainComponentComponentStage(
    val id: Long,
    val componentId: Long,
    val componentStageId: Long
) : DomainBaseModel<DatabaseComponentComponentStage>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentComponentStage::class, DatabaseComponentComponentStage::class).transform(this)
}

data class DomainCharacteristicProductKind(
    val id: Long,
    val charId: Long,
    val productKindId: Long
) : DomainBaseModel<DatabaseCharacteristicProductKind>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainCharacteristicProductKind::class, DatabaseCharacteristicProductKind::class).transform(this)
}

data class DomainCharacteristicComponentKind(
    val id: Long,
    val charId: Long,
    val componentKindId: Long
) : DomainBaseModel<DatabaseCharacteristicComponentKind>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainCharacteristicComponentKind::class, DatabaseCharacteristicComponentKind::class).transform(this)
}

data class DomainCharacteristicComponentStageKind(
    val id: Long,
    val charId: Long,
    val componentStageKindId: Long
) : DomainBaseModel<DatabaseCharacteristicComponentStageKind>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainCharacteristicComponentStageKind::class, DatabaseCharacteristicComponentStageKind::class).transform(this)
}
