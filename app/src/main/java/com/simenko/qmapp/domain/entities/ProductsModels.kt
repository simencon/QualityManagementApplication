package com.simenko.qmapp.domain.entities

import androidx.compose.runtime.Stable
import androidx.room.Embedded
import androidx.room.Relation
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ObjectTransformer

@Stable
data class DomainElementIshModel constructor(
    var id: Int = NoRecord.num,
    var ishElement: String? = null
) : DomainBaseModel<DatabaseElementIshModel>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseElementIshModel {
        return ObjectTransformer(DomainElementIshModel::class, DatabaseElementIshModel::class).transform(this)
    }
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

    override fun toDatabaseModel(): DatabaseIshSubCharacteristic {
        return ObjectTransformer(DomainIshSubCharacteristic::class, DatabaseIshSubCharacteristic::class).transform(this)
    }
}

data class DomainManufacturingProject(
    var id: Int,
    var companyId: Int,
    var factoryLocationDep: Int? = null,
    var factoryLocationDetails: String? = null,
    var customerName: String? = null,
    var team: Int? = null,
    var modelYear: String? = null,
    var projectSubject: String? = null,
    var startDate: String? = null,
    var revisionDate: String? = null,
    var refItem: String? = null,
    var pfmeaNum: String? = null,
    var processOwner: Int? = null,
    var confLevel: Int? = null
) : DomainBaseModel<DatabaseManufacturingProject>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainManufacturingProject::class, DatabaseManufacturingProject::class).transform(this)

    data class DomainManufacturingProjectComplete(
        val manufacturingProject: DomainManufacturingProject,
        val company: DomainCompany
    ) : DomainBaseModel<DatabaseManufacturingProject.DatabaseManufacturingProjectComplete>() {
        override fun getRecordId() = manufacturingProject.id
        override fun getParentId() = manufacturingProject.companyId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel(): DatabaseManufacturingProject.DatabaseManufacturingProjectComplete {
            return DatabaseManufacturingProject.DatabaseManufacturingProjectComplete(
                manufacturingProject = this.manufacturingProject.toDatabaseModel(),
                company = this.company.toDatabaseModel()
            )
        }

    }

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
    override fun hasParentId(pId: Int) =
        when (pId) {
            0 -> false
            else -> true
        }

    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseCharacteristic {
        return ObjectTransformer(DomainCharacteristic::class, DatabaseCharacteristic::class).transform(this)
    }
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

    override fun toDatabaseModel(): DatabaseMetrix {
        return ObjectTransformer(DomainMetrix::class, DatabaseMetrix::class).transform(this)
    }
}

data class DomainKey(
    var id: Int = NoRecord.num,
    var projectId: Int? = null,
    var componentKey: String? = null,
    var componentKeyDescription: String? = null
) : DomainBaseModel<DatabaseKey>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseKey {
        return ObjectTransformer(DomainKey::class, DatabaseKey::class).transform(this)
    }
}

data class DomainProductBase(
    var id: Int,
    var projectId: Int?,
    var componentBaseDesignation: String?
) : DomainBaseModel<DatabaseProductBase>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseProductBase {
        return ObjectTransformer(DomainProductBase::class, DatabaseProductBase::class).transform(this)
    }
}

data class DomainProduct(
    var id: Int,
    var productBaseId: Int?,
    var keyId: Int?,
    var productDesignation: String?
) : DomainBaseModel<DatabaseProduct>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseProduct {
        return ObjectTransformer(DomainProduct::class, DatabaseProduct::class).transform(this)
    }
}

data class DomainComponent(
    var id: Int,
    var keyId: Int?,
    var componentDesignation: String?,
    var ifAny: Int?
) : DomainBaseModel<DatabaseComponent>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseComponent {
        return ObjectTransformer(DomainComponent::class, DatabaseComponent::class).transform(this)
    }
}

data class DomainComponentInStage(
    var id: Int,
    var keyId: Int?,
    var componentInStageDescription: String?,
    var ifAny: Int?
) : DomainBaseModel<DatabaseComponentInStage>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseComponentInStage {
        return ObjectTransformer(DomainComponentInStage::class, DatabaseComponentInStage::class).transform(this)
    }
}

data class DomainVersionStatus(
    var id: Int = NoRecord.num,
    var statusDescription: String? = null
) : DomainBaseModel<DatabaseVersionStatus>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseVersionStatus {
        return ObjectTransformer(DomainVersionStatus::class, DatabaseVersionStatus::class).transform(this)
    }
}

data class DomainProductVersion(
    var id: Int,
    var productId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
) : DomainBaseModel<DatabaseProductVersion>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseProductVersion {
        return ObjectTransformer(DomainProductVersion::class, DatabaseProductVersion::class).transform(this)
    }
}

data class DomainComponentVersion(
    var id: Int,
    var componentId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
) : DomainBaseModel<DatabaseComponentVersion>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseComponentVersion {
        return ObjectTransformer(DomainComponentVersion::class, DatabaseComponentVersion::class).transform(this)
    }
}

data class DomainComponentInStageVersion(
    var id: Int,
    var componentInStageId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
) : DomainBaseModel<DatabaseComponentInStageVersion>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseComponentInStageVersion {
        return ObjectTransformer(DomainComponentInStageVersion::class, DatabaseComponentInStageVersion::class).transform(this)
    }
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

    override fun toDatabaseModel(): DatabaseResultTolerance {
        return ObjectTransformer(DomainResultTolerance::class, DatabaseResultTolerance::class).transform(this)
    }
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

    override fun toDatabaseModel(): DatabaseProductTolerance {
        return ObjectTransformer(DomainProductTolerance::class, DatabaseProductTolerance::class).transform(this)
    }
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

    override fun toDatabaseModel(): DatabaseComponentTolerance {
        return ObjectTransformer(DomainComponentTolerance::class, DatabaseComponentTolerance::class).transform(this)
    }
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

    override fun toDatabaseModel(): DatabaseComponentInStageTolerance {
        return ObjectTransformer(DomainComponentInStageTolerance::class, DatabaseComponentInStageTolerance::class).transform(this)
    }
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

    override fun toDatabaseModel(): DatabaseItemTolerance {
        return ObjectTransformer(DomainItemTolerance::class, DatabaseItemTolerance::class).transform(this)
    }
}

data class DomainProductToLine(
    var id: Int,
    var lineId: Int,
    var productId: Int
) : DomainBaseModel<DatabaseProductToLine>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseProductToLine {
        return ObjectTransformer(DomainProductToLine::class, DatabaseProductToLine::class).transform(this)
    }
}

data class DomainComponentToLine(
    var id: Int,
    var lineId: Int,
    var componentId: Int
) : DomainBaseModel<DatabaseComponentToLine>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseComponentToLine {
        return ObjectTransformer(DomainComponentToLine::class, DatabaseComponentToLine::class).transform(this)
    }
}

data class DomainComponentInStageToLine(
    var id: Int,
    var lineId: Int,
    var componentInStageId: Int
) : DomainBaseModel<DatabaseComponentInStageToLine>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseComponentInStageToLine {
        return ObjectTransformer(DomainComponentInStageToLine::class, DatabaseComponentInStageToLine::class).transform(this)
    }
}

data class DomainItem(
    var id: Int = NoRecord.num,
    val fId: String = NoString.str,
    var keyId: Int? = null,
    var itemDesignation: String? = null
) : DomainBaseModel<DatabaseItem>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseItem {
        return ObjectTransformer(DomainItem::class, DatabaseItem::class).transform(this)
    }
}

data class DomainItemToLine(
    var id: Int = NoRecord.num,
    var fId: String = NoString.str,
    var lineId: Int = NoRecord.num,
    var itemId: Int = NoRecord.num,
    val fItemId: String = NoString.str
) : DomainBaseModel<DatabaseItemToLine>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseItemToLine {
        return ObjectTransformer(DomainItemToLine::class, DatabaseItemToLine::class).transform(this)
    }
}

data class DomainItemVersion(
    var id: Int = NoRecord.num,
    var fId: String = NoString.str,
    var itemId: Int = NoRecord.num,
    var fItemId: String = NoString.str,
    var versionDescription: String? = null,
    var versionDate: String? = null,
    var statusId: Int? = null,
    var isDefault: Boolean = false
) : DomainBaseModel<DatabaseItemVersion>() {
    override fun getRecordId() = id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseItemVersion {
        return ObjectTransformer(DomainItemVersion::class, DatabaseItemVersion::class).transform(this)
    }
}

data class DomainItemComplete(
    val item: DomainItem = DomainItem(),
    val key: DomainKey = DomainKey(),
    val itemToLines: List<DomainItemToLine> = List(2) { DomainItemToLine() }
) : DomainBaseModel<DatabaseItemComplete>() {
    override fun getRecordId() = item.id

    override fun getParentId() = NoRecord.num

    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseItemComplete {
        return DatabaseItemComplete(
            item = item.toDatabaseModel(),
            key = key.toDatabaseModel(),
            itemToLines = itemToLines.map { it.toDatabaseModel() }
        )
    }
}

@Stable
data class DomainItemVersionComplete(
    val itemVersion: DomainItemVersion = DomainItemVersion(),
    val versionStatus: DomainVersionStatus = DomainVersionStatus(),
    val itemComplete: DomainItemComplete = DomainItemComplete(),
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseItemVersionComplete>() {
    override fun getRecordId() = itemVersion.fId
    override fun getParentId() = 0//is not the case with itemsVersions
    override fun hasParentId(pId: Int): Boolean {
        var result = false
        itemComplete.itemToLines.forEach runByBlock@{ it ->
            if (it.lineId == pId) {
                result = true
                return@runByBlock
            }
        }
        return result
    }

    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseItemVersionComplete {
        return DatabaseItemVersionComplete(
            itemVersion = itemVersion.toDatabaseModel(),
            versionStatus = versionStatus.toDatabaseModel(),
            itemComplete = itemComplete.toDatabaseModel()
        )
    }
}
