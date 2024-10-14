package com.simenko.qmapp.domain.entities.products

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.data.cache.db.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer

data class DomainProductLineToDepartment(
    val id: ID = NoRecord.num,
    val depId: ID,
    val productLineId: ID
) : DomainBaseModel<DatabaseProductLineToDepartment>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductLineToDepartment::class, DatabaseProductLineToDepartment::class).transform(this)
}

data class DomainProductKindToSubDepartment(
    val id: ID = NoRecord.num,
    val subDepId: ID,
    val prodKindId: ID
) : DomainBaseModel<DatabaseProductKindToSubDepartment>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductKindToSubDepartment::class, DatabaseProductKindToSubDepartment::class).transform(this)
}

data class DomainComponentKindToSubDepartment(
    val id: ID = NoRecord.num,
    val subDepId: ID,
    val compKindId: ID
) : DomainBaseModel<DatabaseComponentKindToSubDepartment>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentKindToSubDepartment::class, DatabaseComponentKindToSubDepartment::class).transform(this)
}

data class DomainStageKindToSubDepartment(
    val id: ID = NoRecord.num,
    val subDepId: ID,
    val stageKindId: ID
) : DomainBaseModel<DatabaseStageKindToSubDepartment>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainStageKindToSubDepartment::class, DatabaseStageKindToSubDepartment::class).transform(this)
}

data class DomainProductKeyToChannel(
    val id: ID = NoRecord.num,
    val chId: ID,
    val keyId: ID
) : DomainBaseModel<DatabaseProductKeyToChannel>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductKeyToChannel::class, DatabaseProductKeyToChannel::class).transform(this)
}

data class DomainComponentKeyToChannel(
    val id: ID = NoRecord.num,
    val chId: ID,
    val keyId: ID
) : DomainBaseModel<DatabaseComponentKeyToChannel>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentKeyToChannel::class, DatabaseComponentKeyToChannel::class).transform(this)
}

data class DomainStageKeyToChannel(
    val id: ID = NoRecord.num,
    val chId: ID,
    val keyId: ID
) : DomainBaseModel<DatabaseStageKeyToChannel>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainStageKeyToChannel::class, DatabaseStageKeyToChannel::class).transform(this)
}

data class DomainProductToLine(
    val id: ID = NoRecord.num,
    val lineId: ID,
    val productId: ID
) : DomainBaseModel<DatabaseProductToLine>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductToLine::class, DatabaseProductToLine::class).transform(this)
}

data class DomainComponentToLine(
    val id: ID = NoRecord.num,
    val lineId: ID,
    val componentId: ID
) : DomainBaseModel<DatabaseComponentToLine>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentToLine::class, DatabaseComponentToLine::class).transform(this)
}

data class DomainComponentInStageToLine(
    val id: ID = NoRecord.num,
    val lineId: ID,
    val componentInStageId: ID
) : DomainBaseModel<DatabaseComponentInStageToLine>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentInStageToLine::class, DatabaseComponentInStageToLine::class).transform(this)
}

data class DomainItemToLine(
    val id: ID = NoRecord.num,
    val fId: String = NoString.str,
    val lineId: ID = NoRecord.num,
    val itemId: ID = NoRecord.num,
    val fItemId: String = NoString.str
) : DomainBaseModel<DatabaseItemToLine>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainItemToLine::class, DatabaseItemToLine::class).transform(this)
}

data class DomainCharacteristicToOperation(
    val id: ID = NoRecord.num,
    val charId: ID,
    val operationId: ID
) : DomainBaseModel<DatabaseCharacteristicToOperation>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainCharacteristicToOperation::class, DatabaseCharacteristicToOperation::class).transform(this)
}
