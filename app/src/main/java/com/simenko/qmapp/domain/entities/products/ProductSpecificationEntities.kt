package com.simenko.qmapp.domain.entities.products

import androidx.compose.runtime.Stable
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer

data class DomainProductLine(
    var id: ID = NoRecord.num,
    var companyId: ID = NoRecord.num,
    var factoryLocationDep: ID = NoRecord.num,
    var factoryLocationDetails: String? = null,
    var customerName: String? = null,
    var team: ID? = null,
    var modelYear: String? = null,
    var projectSubject: String? = EmptyString.str,
    var startDate: String? = EmptyString.str,
    var revisionDate: String? = EmptyString.str,
    var refItem: String? = null,
    var pfmeaNum: String? = EmptyString.str,
    var processOwner: ID = NoRecord.num,
    var confLevel: ID? = null
) : DomainBaseModel<DatabaseProductLine>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductLine::class, DatabaseProductLine::class).transform(this)

    data class DomainProductLineComplete(
        val manufacturingProject: DomainProductLine = DomainProductLine(),
        val company: DomainCompany = DomainCompany(),
        val designDepartment: DomainDepartment = DomainDepartment(),
        val designManager: DomainEmployee = DomainEmployee(),
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseProductLine.DatabaseProductLineComplete>() {
        override fun getRecordId() = manufacturingProject.id
        override fun getParentId() = manufacturingProject.companyId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel() = DatabaseProductLine.DatabaseProductLineComplete(
            manufacturingProject = this.manufacturingProject.toDatabaseModel(),
            company = this.company.toDatabaseModel(),
            designDepartment = this.designDepartment.toDatabaseModel(),
            designManager = this.designManager.toDatabaseModel()
        )
    }
}

data class DomainKey(
    var id: ID = NoRecord.num,
    var projectId: ID? = NoRecord.num,
    var componentKey: String? = EmptyString.str,
    var componentKeyDescription: String? = EmptyString.str
) : DomainBaseModel<DatabaseKey>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainKey::class, DatabaseKey::class).transform(this)

    data class DomainKeyComplete(
        val productLineKey: DomainKey = DomainKey(),
        val productLine: DomainProductLine = DomainProductLine(),
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseKey.DatabaseKeyComplete>() {

        override fun getRecordId() = productLineKey.id
        override fun getParentId() = productLineKey.projectId ?: NoRecord.num
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseKey.DatabaseKeyComplete(
            productLineKey = this.productLineKey.toDatabaseModel(),
            productLine = this.productLine.toDatabaseModel()
        )
    }
}

data class DomainProductBase(
    var id: ID,
    var projectId: ID?,
    var componentBaseDesignation: String?
) : DomainBaseModel<DatabaseProductBase>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductBase::class, DatabaseProductBase::class).transform(this)
}

data class DomainProductKind(
    val id: ID,
    val projectId: ID,
    val productKindDesignation: String,
    val comments: String?
) : DomainBaseModel<DatabaseProductKind>() {
    override fun getRecordId() = id
    override fun getParentId() = projectId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductKind::class, DatabaseProductKind::class).transform(this)
    data class DomainProductKindComplete(
        val productKind: DomainProductKind,
        val productLine: DomainProductLine.DomainProductLineComplete,
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseProductKind.DatabaseProductKindComplete>() {
        override fun getRecordId() = productKind.id
        override fun getParentId() = productKind.projectId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseProductKind.DatabaseProductKindComplete(
            productKind = productKind.toDatabaseModel(),
            productLine = productLine.toDatabaseModel()
        )
    }
}

data class DomainComponentKind(
    val id: ID,
    val productKindId: ID,
    val componentKindOrder: Int,
    val componentKindDescription: String
) : DomainBaseModel<DatabaseComponentKind>() {
    override fun getRecordId() = id
    override fun getParentId() = productKindId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentKind::class, DatabaseComponentKind::class).transform(this)
}

data class DomainComponentStageKind(
    val id: ID,
    val componentKindId: ID,
    val componentStageOrder: Int,
    val componentStageDescription: String
) : DomainBaseModel<DatabaseComponentStageKind>() {
    override fun getRecordId() = id
    override fun getParentId() = componentKindId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentStageKind::class, DatabaseComponentStageKind::class).transform(this)
}

data class DomainProductKindKey(
    val id: ID,
    val productKindId: ID,
    val keyId: ID
) : DomainBaseModel<DatabaseProductKindKey>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductKindKey::class, DatabaseProductKindKey::class).transform(this)
}

data class DomainComponentKindKey(
    val id: ID,
    val componentKindId: ID,
    val keyId: ID
) : DomainBaseModel<DatabaseComponentKindKey>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentKindKey::class, DatabaseComponentKindKey::class).transform(this)
}

data class DomainComponentStageKindKey(
    val id: ID,
    val componentStageKindId: ID,
    val keyId: ID
) : DomainBaseModel<DatabaseComponentStageKindKey>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentStageKindKey::class, DatabaseComponentStageKindKey::class).transform(this)
}

data class DomainProduct(
    var id: ID,
    var productBaseId: ID?,
    var keyId: ID?,
    var productDesignation: String?
) : DomainBaseModel<DatabaseProduct>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProduct::class, DatabaseProduct::class).transform(this)
}

data class DomainComponent(
    var id: ID,
    var keyId: ID?,
    var componentDesignation: String?,
    var ifAny: Int?
) : DomainBaseModel<DatabaseComponent>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponent::class, DatabaseComponent::class).transform(this)
}

data class DomainComponentInStage(
    var id: ID,
    var keyId: ID?,
    var componentInStageDescription: String?,
    var ifAny: Int?
) : DomainBaseModel<DatabaseComponentInStage>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentInStage::class, DatabaseComponentInStage::class).transform(this)
}

data class DomainProductKindProduct(
    val id: ID,
    val productKindId: ID,
    val productId: ID
) : DomainBaseModel<DatabaseProductKindProduct>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductKindProduct::class, DatabaseProductKindProduct::class).transform(this)
}

data class DomainComponentKindComponent(
    val id: ID,
    val componentKindId: ID,
    val componentId: ID
) : DomainBaseModel<DatabaseComponentKindComponent>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentKindComponent::class, DatabaseComponentKindComponent::class).transform(this)
}

data class DomainComponentStageKindComponentStage(
    val id: ID,
    val componentStageKindId: ID,
    val componentStageId: ID
) : DomainBaseModel<DatabaseComponentStageKindComponentStage>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentStageKindComponentStage::class, DatabaseComponentStageKindComponentStage::class).transform(this)
}

data class DomainProductComponent(
    val id: ID,
    val countOfComponents: Int,
    val productId: ID,
    val componentId: ID
) : DomainBaseModel<DatabaseProductComponent>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductComponent::class, DatabaseProductComponent::class).transform(this)
}

data class DomainComponentComponentStage(
    val id: ID,
    val componentId: ID,
    val componentStageId: ID
) : DomainBaseModel<DatabaseComponentComponentStage>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentComponentStage::class, DatabaseComponentComponentStage::class).transform(this)
}

data class DomainVersionStatus(
    var id: ID = NoRecord.num,
    var statusDescription: String? = null
) : DomainBaseModel<DatabaseVersionStatus>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainVersionStatus::class, DatabaseVersionStatus::class).transform(this)
}

data class DomainProductVersion(
    var id: ID,
    var productId: ID,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: ID?,
    var isDefault: Boolean
) : DomainBaseModel<DatabaseProductVersion>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductVersion::class, DatabaseProductVersion::class).transform(this)
}

data class DomainComponentVersion(
    var id: ID,
    var componentId: ID,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: ID?,
    var isDefault: Boolean
) : DomainBaseModel<DatabaseComponentVersion>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentVersion::class, DatabaseComponentVersion::class).transform(this)
}

data class DomainComponentInStageVersion(
    var id: ID,
    var componentInStageId: ID,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: ID?,
    var isDefault: Boolean
) : DomainBaseModel<DatabaseComponentInStageVersion>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentInStageVersion::class, DatabaseComponentInStageVersion::class).transform(this)
}

data class DomainItem(
    var id: ID = NoRecord.num,
    val fId: String = NoString.str,
    var keyId: ID? = null,
    var itemDesignation: String? = null
) : DomainBaseModel<DatabaseItem>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainItem::class, DatabaseItem::class).transform(this)
}

data class DomainItemVersion(
    var id: ID = NoRecord.num,
    var fId: String = NoString.str,
    var itemId: ID = NoRecord.num,
    var fItemId: String = NoString.str,
    var versionDescription: String? = null,
    var versionDate: String? = null,
    var statusId: ID? = null,
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
    override fun toDatabaseModel() = DatabaseItemComplete(
        item = item.toDatabaseModel(),
        key = key.toDatabaseModel(),
        itemToLines = itemToLines.map { it.toDatabaseModel() }
    )
}

@Stable
data class DomainItemVersionComplete(
    val itemVersion: DomainItemVersion = DomainItemVersion(),
    val versionStatus: DomainVersionStatus = DomainVersionStatus(),
    val itemComplete: DomainItemComplete = DomainItemComplete(),
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseItemVersionComplete>() {
    override fun getRecordId() = itemVersion.fId
    override fun getParentId() = 0L//is not the case with itemsVersions
    override fun hasParentId(pId: ID) = itemComplete.itemToLines.find { it.lineId == pId } != null
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = DatabaseItemVersionComplete(
        itemVersion = itemVersion.toDatabaseModel(),
        versionStatus = versionStatus.toDatabaseModel(),
        itemComplete = itemComplete.toDatabaseModel()
    )
}