package com.simenko.qmapp.domain.entities.products

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer

data class DomainProductToLine(
    var id: ID,
    var lineId: ID,
    var productId: ID
) : DomainBaseModel<DatabaseProductToLine>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductToLine::class, DatabaseProductToLine::class).transform(this)
}

data class DomainComponentToLine(
    var id: ID,
    var lineId: ID,
    var componentId: ID
) : DomainBaseModel<DatabaseComponentToLine>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentToLine::class, DatabaseComponentToLine::class).transform(this)
}

data class DomainComponentInStageToLine(
    var id: ID,
    var lineId: ID,
    var componentInStageId: ID
) : DomainBaseModel<DatabaseComponentInStageToLine>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentInStageToLine::class, DatabaseComponentInStageToLine::class).transform(this)
}

data class DomainItemToLine(
    var id: ID = NoRecord.num,
    var fId: String = NoString.str,
    var lineId: ID = NoRecord.num,
    var itemId: ID = NoRecord.num,
    val fItemId: String = NoString.str
) : DomainBaseModel<DatabaseItemToLine>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainItemToLine::class, DatabaseItemToLine::class).transform(this)
}
