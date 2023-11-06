package com.simenko.qmapp.domain.entities.products

import androidx.compose.runtime.Stable
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.room.entities.DatabaseResultTolerance
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer

@Stable
data class DomainElementIshModel constructor(
    var id: Int = NoRecord.num,
    var ishElement: String? = null
) : DomainBaseModel<DatabaseElementIshModel>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainElementIshModel::class, DatabaseElementIshModel::class).transform(this)
}

@Stable
data class DomainIshSubCharacteristic constructor(
    var id: Int = NoRecord.num,
    var ishElement: String? = null,
    var measurementGroupRelatedTime: Double? = null
) : DomainBaseModel<DatabaseIshSubCharacteristic>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainIshSubCharacteristic::class, DatabaseIshSubCharacteristic::class).transform(this)
}


@Stable
data class DomainCharacteristic constructor(
    var id: Int = NoRecord.num,
    var ishCharId: Int = NoRecord.num,
    var charOrder: Int? = null,
    var charDesignation: String? = null,
    var charDescription: String? = null,
    var ishSubChar: Int = NoRecord.num,
    var projectId: Int = NoRecord.num,
    var sampleRelatedTime: Double? = null,
    var measurementRelatedTime: Double? = null,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseCharacteristic>() {
    override fun getRecordId() = id
    override fun getParentId() = projectId
    override fun hasParentId(pId: Int) = pId != 0

    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainCharacteristic::class, DatabaseCharacteristic::class).transform(this)
}

@Stable
data class DomainCharacteristicComplete(
    val characteristic: DomainCharacteristic = DomainCharacteristic(),
    val characteristicGroup: DomainElementIshModel = DomainElementIshModel(),
    val characteristicSubGroup: DomainIshSubCharacteristic = DomainIshSubCharacteristic()
) : DomainBaseModel<DatabaseCharacteristicComplete>() {
    override fun getRecordId() = characteristic.id
    override fun getParentId() = characteristic.projectId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseCharacteristicComplete {
        return DatabaseCharacteristicComplete(
            characteristic = characteristic.toDatabaseModel(),
            characteristicGroup = characteristicGroup.toDatabaseModel(),
            characteristicSubGroup = characteristicSubGroup.toDatabaseModel()
        )
    }
}

@Stable
data class DomainMetrix constructor(
    var id: Int = NoRecord.num,
    var charId: Int = NoRecord.num,
    var metrixOrder: Int? = null,
    var metrixDesignation: String? = null,
    var metrixDescription: String? = null,
    var units: String? = null
) : DomainBaseModel<DatabaseMetrix>() {
    override fun getRecordId() = id
    override fun getParentId() = charId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainMetrix::class, DatabaseMetrix::class).transform(this)
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

@Stable
data class DomainResultTolerance(
    var id: Int = NoRecord.num,
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
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
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
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
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
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
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
    var metrixId: Int,
    var versionId: Int,
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

