package com.simenko.qmapp.domain.entities.products

import androidx.compose.runtime.Stable
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer
import com.simenko.qmapp.utils.StringUtils

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
    var projectId: ID = NoRecord.num,
    var componentKey: String = EmptyString.str,
    var componentKeyDescription: String? = EmptyString.str,
    var isSelected: Boolean = false,
) : DomainBaseModel<DatabaseKey>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun getIdentityName(): String {
        return componentKey
    }

    override fun getName(): String {
        return componentKeyDescription ?: EmptyString.str
    }

    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }


    override fun toDatabaseModel() = ObjectTransformer(DomainKey::class, DatabaseKey::class).transform(this)

    data class DomainKeyComplete(
        val productLineKey: DomainKey = DomainKey(),
        val productLine: DomainProductLine = DomainProductLine(),
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false,
        var isSelected: Boolean = false,
    ) : DomainBaseModel<DatabaseKey.DatabaseKeyComplete>() {

        override fun getRecordId() = productLineKey.id
        override fun getParentId() = productLineKey.projectId
        override fun getIdentityName(): String {
            return productLineKey.componentKey
        }

        override fun getName(): String {
            return productLineKey.componentKeyDescription ?: EmptyString.str
        }

        override fun setIsSelected(value: Boolean) {
            isSelected = value
        }

        override fun getIsSelected() = isSelected
        override fun toDatabaseModel() = DatabaseKey.DatabaseKeyComplete(
            productLineKey = this.productLineKey.toDatabaseModel(),
            productLine = this.productLine.toDatabaseModel()
        )
    }
}

data class DomainProductBase(
    var id: ID = NoRecord.num,
    var projectId: ID? = NoRecord.num,
    var componentBaseDesignation: String? = EmptyString.str
) : DomainBaseModel<DatabaseProductBase>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductBase::class, DatabaseProductBase::class).transform(this)
}

data class DomainProductKind(
    val id: ID = NoRecord.num,
    val projectId: ID = NoRecord.num,
    val productKindDesignation: String = EmptyString.str,
    val comments: String? = EmptyString.str
) : DomainBaseModel<DatabaseProductKind>() {
    override fun getRecordId() = id
    override fun getParentId() = projectId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductKind::class, DatabaseProductKind::class).transform(this)
    data class DomainProductKindComplete(
        val productKind: DomainProductKind = DomainProductKind(),
        val productLine: DomainProductLine.DomainProductLineComplete = DomainProductLine.DomainProductLineComplete(),
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
    val id: ID = NoRecord.num,
    val productKindId: ID = NoRecord.num,
    val componentKindOrder: Int = NoRecord.num.toInt(),
    val componentKindDescription: String = EmptyString.str
) : DomainBaseModel<DatabaseComponentKind>() {
    override fun getRecordId() = id
    override fun getParentId() = productKindId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentKind::class, DatabaseComponentKind::class).transform(this)
    data class DomainComponentKindComplete(
        val componentKind: DomainComponentKind = DomainComponentKind(),
        val productKind: DomainProductKind.DomainProductKindComplete = DomainProductKind.DomainProductKindComplete(),
        var hasComponents: Boolean = false,
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseComponentKind.DatabaseComponentKindComplete>() {
        override fun getRecordId() = componentKind.id
        override fun getParentId() = componentKind.productKindId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseComponentKind.DatabaseComponentKindComplete(
            componentKind = componentKind.toDatabaseModel(),
            productKind = productKind.toDatabaseModel()
        )
    }
}

data class DomainComponentStageKind(
    val id: ID = NoRecord.num,
    val componentKindId: ID = NoRecord.num,
    val componentStageOrder: Int = NoRecord.num.toInt(),
    val componentStageDescription: String = EmptyString.str
) : DomainBaseModel<DatabaseComponentStageKind>() {
    override fun getRecordId() = id
    override fun getParentId() = componentKindId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentStageKind::class, DatabaseComponentStageKind::class).transform(this)
    data class DomainComponentStageKindComplete(
        val componentStageKind: DomainComponentStageKind = DomainComponentStageKind(),
        val componentKind: DomainComponentKind.DomainComponentKindComplete = DomainComponentKind.DomainComponentKindComplete(),
        val hasComponentStages: Boolean = false,
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseComponentStageKind.DatabaseComponentStageKindComplete>() {
        override fun getRecordId() = componentStageKind.id
        override fun getParentId() = componentStageKind.componentKindId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseComponentStageKind.DatabaseComponentStageKindComplete(
            componentStageKind = componentStageKind.toDatabaseModel(),
            componentKind = componentKind.toDatabaseModel()
        )
    }
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
    data class DomainProductKindKeyComplete(
        val productKindKey: DomainProductKindKey,
        val productKind: DomainProductKind.DomainProductKindComplete,
        val key: DomainKey.DomainKeyComplete
    ) : DomainBaseModel<DatabaseProductKindKey.DatabaseProductKindKeyComplete>() {
        override fun getRecordId() = productKindKey.id
        override fun getParentId() = productKind.productKind.projectId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel() = DatabaseProductKindKey.DatabaseProductKindKeyComplete(
            productKindKey = productKindKey.toDatabaseModel(),
            productKind = productKind.toDatabaseModel(),
            key = key.toDatabaseModel()
        )

    }
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
    data class DomainComponentKindKeyComplete(
        val componentKindKey: DomainComponentKindKey,
        val componentKind: DomainComponentKind.DomainComponentKindComplete,
        val key: DomainKey.DomainKeyComplete
    ) : DomainBaseModel<DatabaseComponentKindKey.DatabaseComponentKindKeyComplete>() {
        override fun getRecordId() = componentKindKey.id
        override fun getParentId() = componentKind.componentKind.productKindId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseComponentKindKey.DatabaseComponentKindKeyComplete(
            componentKindKey = componentKindKey.toDatabaseModel(),
            componentKind = componentKind.toDatabaseModel(),
            key = key.toDatabaseModel()
        )
    }
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
    data class DomainComponentStageKindKeyComplete(
        val componentStageKindKey: DomainComponentStageKindKey,
        val componentStageKind: DomainComponentStageKind.DomainComponentStageKindComplete,
        val key: DomainKey.DomainKeyComplete
    ) : DomainBaseModel<DatabaseComponentStageKindKey.DatabaseComponentStageKindKeyComplete>() {
        override fun getRecordId() = componentStageKindKey.id
        override fun getParentId() = componentStageKind.componentStageKind.componentKindId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseComponentStageKindKey.DatabaseComponentStageKindKeyComplete(
            componentStageKindKey = componentStageKindKey.toDatabaseModel(),
            componentStageKind = componentStageKind.toDatabaseModel(),
            key = key.toDatabaseModel()
        )
    }
}

data class DomainProduct(
    var id: ID = NoRecord.num,
    var productBaseId: ID = NoRecord.num,
    var keyId: ID = NoRecord.num,
    var productDesignation: String = EmptyString.str
) : DomainBaseModel<DatabaseProduct>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProduct::class, DatabaseProduct::class).transform(this)
    data class DomainProductComplete(
        val product: DomainProduct = DomainProduct(),
        val productBase: DomainProductBase = DomainProductBase(),
        val key: DomainKey = DomainKey(),
        var isSelected: Boolean = false,
    ) : DomainBaseModel<DatabaseProduct.DatabaseProductComplete>() {
        override fun getRecordId() = product.id
        override fun getParentId() = key.projectId
        override fun getIdentityName() = product.productDesignation
        override fun getName() = productBase.componentBaseDesignation ?: NoString.str

        override fun setIsSelected(value: Boolean) {
            isSelected = value
        }

        override fun getIsSelected() = isSelected
        override fun toDatabaseModel() = DatabaseProduct.DatabaseProductComplete(
            product = this.product.toDatabaseModel(),
            productBase = this.productBase.toDatabaseModel(),
            key = this.key.toDatabaseModel()
        )
    }
}

data class DomainComponent(
    var id: ID = NoRecord.num,
    var keyId: ID = NoRecord.num,
    var componentDesignation: String = EmptyString.str,
    var ifAny: Int? = null
) : DomainBaseModel<DatabaseComponent>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponent::class, DatabaseComponent::class).transform(this)
    data class DomainComponentComplete(
        val component: DomainComponent = DomainComponent(),
        val key: DomainKey = DomainKey(),
        var isSelected: Boolean = false
    ) : DomainBaseModel<DatabaseComponent.DatabaseComponentComplete>() {
        override fun getRecordId() = component.id
        override fun getParentId() = key.projectId

        override fun getIdentityName(): String {
            return NoString.str
        }

        override fun getName(): String {
            return StringUtils.concatTwoStrings3(key.componentKey, component.componentDesignation)
        }

        override fun setIsSelected(value: Boolean) {
            isSelected = value
        }

        override fun getIsSelected() = isSelected

        override fun toDatabaseModel() = DatabaseComponent.DatabaseComponentComplete(
            component = this.component.toDatabaseModel(),
            key = this.key.toDatabaseModel()
        )
    }
}

data class DomainComponentStage(
    var id: ID = NoRecord.num,
    var keyId: ID = NoRecord.num,
    var componentInStageDescription: String = EmptyString.str,
    var ifAny: Int? = null
) : DomainBaseModel<DatabaseComponentStage>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentStage::class, DatabaseComponentStage::class).transform(this)
    data class DomainComponentStageComplete(
        val componentStage: DomainComponentStage = DomainComponentStage(),
        val key: DomainKey = DomainKey(),
        var isSelected: Boolean = false,
    ) : DomainBaseModel<DatabaseComponentStage.DatabaseComponentStageComplete>() {
        override fun getRecordId() = componentStage.id
        override fun getParentId() = key.projectId
        override fun getIdentityName(): String {
            return NoString.str
        }

        override fun getName(): String {
            return StringUtils.concatTwoStrings3(key.componentKey, componentStage.componentInStageDescription)
        }

        override fun setIsSelected(value: Boolean) {
            isSelected = value
        }

        override fun getIsSelected() = isSelected
        override fun toDatabaseModel() = DatabaseComponentStage.DatabaseComponentStageComplete(
            componentStage = this.componentStage.toDatabaseModel(),
            key = this.key.toDatabaseModel()
        )
    }
}

data class DomainProductKindProduct(
    val id: ID = NoRecord.num,
    val productKindId: ID = NoRecord.num,
    val productId: ID = NoRecord.num
) : DomainBaseModel<DatabaseProductKindProduct>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductKindProduct::class, DatabaseProductKindProduct::class).transform(this)
    data class DomainProductKindProductComplete(
        val productKindProduct: DomainProductKindProduct = DomainProductKindProduct(),
        val productKind: DomainProductKind = DomainProductKind(),
        val product: DomainProduct.DomainProductComplete = DomainProduct.DomainProductComplete(),
        val versions: List<DomainProductVersion> = emptyList(),
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseProductKindProduct.DatabaseProductKindProductComplete>() {
        override fun getRecordId() = productKindProduct.productId
        override fun getParentId() = productKindProduct.productKindId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseProductKindProduct.DatabaseProductKindProductComplete(
            productKindProduct = this.productKindProduct.toDatabaseModel(),
            productKind = this.productKind.toDatabaseModel(),
            product = this.product.toDatabaseModel(),
            versions = this.versions.map { it.toDatabaseModel() }
        )
    }
}

data class DomainComponentKindComponent(
    val id: ID = NoRecord.num,
    val componentKindId: ID = NoRecord.num,
    val componentId: ID = NoRecord.num
) : DomainBaseModel<DatabaseComponentKindComponent>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentKindComponent::class, DatabaseComponentKindComponent::class).transform(this)
    data class DomainComponentKindComponentComplete(
        val componentKindComponent: DomainComponentKindComponent = DomainComponentKindComponent(),
        val componentKind: DomainComponentKind = DomainComponentKind(),
        val component: DomainComponent.DomainComponentComplete = DomainComponent.DomainComponentComplete(),
        val versions: List<DomainComponentVersion> = emptyList()
    ) : DomainBaseModel<DatabaseComponentKindComponent.DatabaseComponentKindComponentComplete>() {
        override fun getRecordId() = componentKindComponent.id
        override fun getParentId() = componentKindComponent.componentKindId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel() = DatabaseComponentKindComponent.DatabaseComponentKindComponentComplete(
            componentKindComponent = this.componentKindComponent.toDatabaseModel(),
            componentKind = this.componentKind.toDatabaseModel(),
            component = this.component.toDatabaseModel(),
            versions = this.versions.map { it.toDatabaseModel() }
        )
    }
}

data class DomainComponentStageKindComponentStage(
    val id: ID = NoRecord.num,
    val componentStageKindId: ID = NoRecord.num,
    val componentStageId: ID = NoRecord.num
) : DomainBaseModel<DatabaseComponentStageKindComponentStage>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentStageKindComponentStage::class, DatabaseComponentStageKindComponentStage::class).transform(this)
    data class DomainComponentStageKindComponentStageComplete(
        val componentStageKindComponentStage: DomainComponentStageKindComponentStage = DomainComponentStageKindComponentStage(),
        val componentStageKind: DomainComponentStageKind = DomainComponentStageKind(),
        val componentStage: DomainComponentStage.DomainComponentStageComplete = DomainComponentStage.DomainComponentStageComplete(),
        val versions: List<DomainComponentStageVersion> = emptyList(),
    ) : DomainBaseModel<DatabaseComponentStageKindComponentStage.DatabaseComponentStageKindComponentStageComplete>() {
        override fun getRecordId() = componentStageKindComponentStage.componentStageId
        override fun getParentId() = componentStageKindComponentStage.componentStageKindId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseComponentStageKindComponentStage.DatabaseComponentStageKindComponentStageComplete(
            componentStageKindComponentStage = this.componentStageKindComponentStage.toDatabaseModel(),
            componentStageKind = this.componentStageKind.toDatabaseModel(),
            componentStage = this.componentStage.toDatabaseModel(),
            versions = this.versions.map { it.toDatabaseModel() }
        )
    }
}

data class DomainProductComponent(
    val id: ID = NoRecord.num,
    val countOfComponents: Int = ZeroValue.num.toInt(),
    val productId: ID = NoRecord.num,
    val componentKindComponentId: ID = NoRecord.num
) : DomainBaseModel<DatabaseProductComponent>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainProductComponent::class, DatabaseProductComponent::class).transform(this)
    data class DomainProductComponentComplete(
        val productComponent: DomainProductComponent = DomainProductComponent(),
        val product: DomainProduct.DomainProductComplete = DomainProduct.DomainProductComplete(),
        val component: DomainComponentKindComponent.DomainComponentKindComponentComplete = DomainComponentKindComponent.DomainComponentKindComponentComplete(),
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseProductComponent.DatabaseProductComponentComplete>() {
        override fun getRecordId() = component.component.component.id
        override fun getParentId() = productComponent.productId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseProductComponent.DatabaseProductComponentComplete(
            productComponent = this.productComponent.toDatabaseModel(),
            product = this.product.toDatabaseModel(),
            component = this.component.toDatabaseModel(),
        )
    }
}

data class DomainComponentComponentStage(
    val id: ID = NoRecord.num,
    val componentId: ID = NoRecord.num,
    val stageKindStageId: ID = NoRecord.num
) : DomainBaseModel<DatabaseComponentComponentStage>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentComponentStage::class, DatabaseComponentComponentStage::class).transform(this)
    data class DomainComponentComponentStageComplete(
        val componentComponentStage: DomainComponentComponentStage = DomainComponentComponentStage(),
        val component: DomainComponent.DomainComponentComplete = DomainComponent.DomainComponentComplete(),
        val componentStage: DomainComponentStageKindComponentStage.DomainComponentStageKindComponentStageComplete = DomainComponentStageKindComponentStage.DomainComponentStageKindComponentStageComplete(),
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseComponentComponentStage.DatabaseComponentComponentStageComplete>() {
        override fun getRecordId() = componentStage.componentStage.componentStage.id
        override fun getParentId() = componentComponentStage.componentId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseComponentComponentStage.DatabaseComponentComponentStageComplete(
            componentComponentStage = this.componentComponentStage.toDatabaseModel(),
            component = this.component.toDatabaseModel(),
            componentStage = this.componentStage.toDatabaseModel(),
        )
    }
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
    var versionDescription: String,
    var versionDate: Long,
    var statusId: ID,
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
    var versionDescription: String,
    var versionDate: Long,
    var statusId: ID,
    var isDefault: Boolean
) : DomainBaseModel<DatabaseComponentVersion>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentVersion::class, DatabaseComponentVersion::class).transform(this)
}

data class DomainComponentStageVersion(
    var id: ID,
    var componentInStageId: ID,
    var versionDescription: String,
    var versionDate: Long,
    var statusId: ID,
    var isDefault: Boolean
) : DomainBaseModel<DatabaseComponentStageVersion>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainComponentStageVersion::class, DatabaseComponentStageVersion::class).transform(this)
}

data class DomainItemKind(
    val fId: String,
    val id: ID,
    val pId: ID,
    val itemKindOrder: Int,
    val itemKindDesignation: String,
    val comments: String?
) : DomainBaseModel<DatabaseItemKind>() {
    override fun getRecordId() = fId
    override fun getParentId() = pId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainItemKind::class, DatabaseItemKind::class).transform(this)
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
    var versionDate: Long = ZeroValue.num,
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
    var isSelected: Boolean = false,
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseItemVersionComplete>() {
    override fun getRecordId() = itemVersion.fId
    override fun getParentId() = NoRecord.num
    override fun getParentIdStr() = itemVersion.fItemId
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