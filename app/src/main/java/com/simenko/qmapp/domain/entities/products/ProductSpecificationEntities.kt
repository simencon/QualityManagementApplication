package com.simenko.qmapp.domain.entities.products

import androidx.compose.runtime.Stable
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.DomainCompany
import com.simenko.qmapp.domain.entities.DomainDepartment
import com.simenko.qmapp.domain.entities.DomainEmployee
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer

data class DomainManufacturingProject(
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
) : DomainBaseModel<DatabaseManufacturingProject>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainManufacturingProject::class, DatabaseManufacturingProject::class).transform(this)

    data class DomainManufacturingProjectComplete(
        val manufacturingProject: DomainManufacturingProject,
        val company: DomainCompany,
        val designDepartment: DomainDepartment,
        val designManager: DomainEmployee,
        var detailsVisibility: Boolean = false,
        var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseManufacturingProject.DatabaseManufacturingProjectComplete>() {
        override fun getRecordId() = manufacturingProject.id
        override fun getParentId() = manufacturingProject.companyId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel(): DatabaseManufacturingProject.DatabaseManufacturingProjectComplete {
            return DatabaseManufacturingProject.DatabaseManufacturingProjectComplete(
                manufacturingProject = this.manufacturingProject.toDatabaseModel(),
                company = this.company.toDatabaseModel(),
                designDepartment = this.designDepartment.toDatabaseModel(),
                designManager = this.designManager.toDatabaseModel()
            )
        }

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
    override fun toDatabaseModel() = ObjectTransformer(DomainKey::class, DatabaseKey::class).transform(this)
}

data class DomainProductBase(
    var id: Int,
    var projectId: Int?,
    var componentBaseDesignation: String?
) : DomainBaseModel<DatabaseProductBase>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductBase::class, DatabaseProductBase::class).transform(this)
}

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

data class DomainProduct(
    var id: Int,
    var productBaseId: Int?,
    var keyId: Int?,
    var productDesignation: String?
) : DomainBaseModel<DatabaseProduct>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProduct::class, DatabaseProduct::class).transform(this)
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
    override fun toDatabaseModel() = ObjectTransformer(DomainComponent::class, DatabaseComponent::class).transform(this)
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
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentInStage::class, DatabaseComponentInStage::class).transform(this)
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

data class DomainVersionStatus(
    var id: Int = NoRecord.num,
    var statusDescription: String? = null
) : DomainBaseModel<DatabaseVersionStatus>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainVersionStatus::class, DatabaseVersionStatus::class).transform(this)
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
    override fun toDatabaseModel() = ObjectTransformer(DomainProductVersion::class, DatabaseProductVersion::class).transform(this)
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
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentVersion::class, DatabaseComponentVersion::class).transform(this)
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
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentInStageVersion::class, DatabaseComponentInStageVersion::class).transform(this)
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
    override fun toDatabaseModel() = ObjectTransformer(DomainItem::class, DatabaseItem::class).transform(this)
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
    override fun toDatabaseModel() = ObjectTransformer(DomainItemVersion::class, DatabaseItemVersion::class).transform(this)
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
    override fun hasParentId(pId: Int) = itemComplete.itemToLines.find { it.lineId == pId } != null
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