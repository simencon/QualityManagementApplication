package com.simenko.qmapp.domain.entities.products

import androidx.compose.runtime.Stable
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.room.entities.DatabaseResultTolerance
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer

@Stable
data class DomainCharGroup constructor(
    var id: ID = NoRecord.num,
    val productLineId: ID = NoRecord.num,
    var ishElement: String? = EmptyString.str
) : DomainBaseModel<DatabaseCharGroup>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainCharGroup::class, DatabaseCharGroup::class).transform(this)
    data class DomainCharGroupComplete(
        val charGroup: DomainCharGroup = DomainCharGroup(),
        val productLine: DomainProductLine.DomainProductLineComplete = DomainProductLine.DomainProductLineComplete()
    ): DomainBaseModel<DatabaseCharGroup.DatabaseCharGroupComplete>() {
        override fun getRecordId() = charGroup.id
        override fun getParentId() = charGroup.productLineId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel() = DatabaseCharGroup.DatabaseCharGroupComplete(
            charGroup = charGroup.toDatabaseModel(),
            productLine = productLine.toDatabaseModel()
        )
    }
}

@Stable
data class DomainCharSubGroup constructor(
    var id: ID = NoRecord.num,
    val charGroupId: ID = NoRecord.num,
    var ishElement: String? = EmptyString.str,
    var measurementGroupRelatedTime: Double? = null
) : DomainBaseModel<DatabaseCharSubGroup>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainCharSubGroup::class, DatabaseCharSubGroup::class).transform(this)
    data class DomainCharSubGroupComplete(
        val charSubGroup: DomainCharSubGroup = DomainCharSubGroup(),
        val charGroup: DomainCharGroup.DomainCharGroupComplete = DomainCharGroup.DomainCharGroupComplete()
    ) : DomainBaseModel<DatabaseCharSubGroup.DatabaseCharSubGroupComplete>() {
        override fun getRecordId() = charSubGroup.id
        override fun getParentId() = charSubGroup.charGroupId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseCharSubGroup.DatabaseCharSubGroupComplete (
            charSubGroup = charSubGroup.toDatabaseModel(),
            charGroup = charGroup.toDatabaseModel()
        )
    }
}


@Stable
data class DomainCharacteristic constructor(
    var id: ID = NoRecord.num,
    var ishSubCharId: ID = NoRecord.num,
    var charOrder: Int? = null,
    var charDesignation: String? = null,
    var charDescription: String? = null,
    var sampleRelatedTime: Double? = null,
    var measurementRelatedTime: Double? = null,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseCharacteristic>() {
    override fun getRecordId() = id
    override fun getParentId() = ishSubCharId
    override fun hasParentId(pId: ID) = pId != 0L

    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainCharacteristic::class, DatabaseCharacteristic::class).transform(this)

    @Stable
    data class DomainCharacteristicComplete(
        val characteristic: DomainCharacteristic = DomainCharacteristic(),
        val characteristicSubGroup: DomainCharSubGroup.DomainCharSubGroupComplete = DomainCharSubGroup.DomainCharSubGroupComplete(),
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseCharacteristic.DatabaseCharacteristicComplete>() {
        override fun getRecordId() = characteristic.id
        override fun getParentId() = characteristic.ishSubCharId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseCharacteristic.DatabaseCharacteristicComplete(
            characteristic = this.characteristic.toDatabaseModel(),
            characteristicSubGroup = this.characteristicSubGroup.toDatabaseModel()
        )
    }
}

@Stable
data class DomainMetrix constructor(
    var id: ID = NoRecord.num,
    var charId: ID = NoRecord.num,
    var metrixOrder: Int? = NoRecord.num.toInt(),
    var metrixDesignation: String? = EmptyString.str,
    var metrixDescription: String? = EmptyString.str,
    var units: String? = EmptyString.str,
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseMetrix>() {
    override fun getRecordId() = id
    override fun getParentId() = charId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainMetrix::class, DatabaseMetrix::class).transform(this)
}

data class DomainCharacteristicProductKind(
    val id: ID,
    val charId: ID,
    val productKindId: ID
) : DomainBaseModel<DatabaseCharacteristicProductKind>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainCharacteristicProductKind::class, DatabaseCharacteristicProductKind::class).transform(this)
}

data class DomainCharacteristicComponentKind(
    val id: ID,
    val charId: ID,
    val componentKindId: ID
) : DomainBaseModel<DatabaseCharacteristicComponentKind>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainCharacteristicComponentKind::class, DatabaseCharacteristicComponentKind::class).transform(this)
}

data class DomainCharacteristicComponentStageKind(
    val id: ID,
    val charId: ID,
    val componentStageKindId: ID
) : DomainBaseModel<DatabaseCharacteristicComponentStageKind>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainCharacteristicComponentStageKind::class, DatabaseCharacteristicComponentStageKind::class).transform(this)
}

@Stable
data class DomainResultTolerance(
    var id: ID = NoRecord.num,
    var nominal: Float? = null,
    var lsl: Float? = null,
    var usl: Float? = null
) : DomainBaseModel<DatabaseResultTolerance>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainResultTolerance::class, DatabaseResultTolerance::class).transform(this)
}

data class DomainProductTolerance(
    var id: ID,
    var metrixId: ID?,
    var versionId: ID?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : DomainBaseModel<DatabaseProductTolerance>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductTolerance::class, DatabaseProductTolerance::class).transform(this)
}

data class DomainComponentTolerance(
    var id: ID,
    var metrixId: ID?,
    var versionId: ID?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : DomainBaseModel<DatabaseComponentTolerance>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentTolerance::class, DatabaseComponentTolerance::class).transform(this)
}

data class DomainComponentInStageTolerance(
    var id: ID,
    var metrixId: ID?,
    var versionId: ID?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : DomainBaseModel<DatabaseComponentInStageTolerance>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentInStageTolerance::class, DatabaseComponentInStageTolerance::class).transform(this)
}

data class DomainItemTolerance(
    var id: String,
    var fId: String,
    var metrixId: ID,
    var versionId: ID,
    var fVersionId: String,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : DomainBaseModel<DatabaseItemTolerance>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainItemTolerance::class, DatabaseItemTolerance::class).transform(this)
}

