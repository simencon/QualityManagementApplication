package com.simenko.qmapp.domain

import com.simenko.qmapp.utils.StringUtils

data class DomainElementIshModel constructor(
    var id: Int,
    var ishElement: String? = null
)

data class DomainIshSubCharacteristic constructor(
    var id: Int,
    var ishElement: String? = null,
    var measurementGroupRelatedTime: Double? = null
)

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
)

data class DomainCharacteristic constructor(
    var id: Int,
    var ishCharId: Int,
    var charOrder: Int? = null,
    var charDesignation: String? = null,
    var charDescription: String? = null,
    var ishSubChar: Int,
    var projectId: Int,
    var sampleRelatedTime: Double? = null,
    var measurementRelatedTime: Double? = null,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId() = id
    override fun getParentOneId() = -1
    override fun hasParentOneId(qnt: Int) =
        when (qnt) {
            0 -> false
            else -> true
        }
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }

    override fun changeCheckedState(): Boolean {
        isSelected = !isSelected
        return isSelected
    }
}

data class DomainCharacteristicComplete(
    val characteristic: DomainCharacteristic,
    val characteristicGroup: DomainElementIshModel,
    val characteristicSubGroup: DomainIshSubCharacteristic
)

data class DomainMetrix constructor(
    var id: Int,
    var charId: Int,
    var metrixOrder: Int? = null,
    var metrixDesignation: String? = null,
    var metrixDescription: String? = null,
    var units: String? = null
)

data class DomainKey(
    var id: Int,
    var projectId: Int?,
    var componentKey: String?,
    var componentKeyDescription: String?
)

data class DomainProductBase(
    var id: Int,
    var projectId: Int?,
    var componentBaseDesignation: String?
)

data class DomainProduct(
    var id: Int,
    var productBaseId: Int?,
    var keyId: Int?,
    var productDesignation: String?
)

data class DomainComponent(
    var id: Int,
    var keyId: Int?,
    var componentDesignation: String?,
    var ifAny: Int?
)

data class DomainComponentInStage(
    var id: Int,
    var keyId: Int?,
    var componentInStageDescription: String?,
    var ifAny: Int?
)

data class DomainVersionStatus(
    var id: Int,
    var statusDescription: String?
)

data class DomainProductVersion(
    var id: Int,
    var productId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
)

data class DomainComponentVersion(
    var id: Int,
    var componentId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
)

data class DomainComponentInStageVersion(
    var id: Int,
    var componentInStageId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
)

data class DomainProductTolerance(
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
)

data class DomainComponentTolerance(
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
)

data class DomainComponentInStageTolerance(
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
)

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
)

data class DomainProductToLine(
    var id: Int,
    var lineId: Int,
    var productId: Int
)

data class DomainComponentToLine(
    var id: Int,
    var lineId: Int,
    var componentId: Int
)

data class DomainComponentInStageToLine(
    var id: Int,
    var lineId: Int,
    var componentInStageId: Int
)

data class DomainItem(
    var id: Int,
    val fId: String,
    var keyId: Int?,
    var itemDesignation: String?
)

data class DomainItemToLine(
    var id: Int,
    var fId: String,
    var lineId: Int,
    var itemId: Int,
    val fItemId: String
)

data class DomainItemVersion(
    var id: Int,
    var fId: String,
    var itemId: Int,
    var fItemId: String,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
)

enum class ItemType {
    PRODUCT,
    COMPONENT,
    COMPONENT_IN_STAGE
}

data class DomainItemComplete(
    val item: DomainItem,
    val key: DomainKey,
    val itemToLines: List<DomainItemToLine>
)

data class DomainItemVersionComplete(
    val itemVersion: DomainItemVersion,
    val versionStatus: DomainVersionStatus,
    val itemComplete: DomainItemComplete,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId() = itemVersion.fId
    override fun getParentOneId() = 0//is not the case with itemsVersions
    override fun hasParentOneId(pId: Int): Boolean {
        var result = false
        itemComplete.itemToLines.forEach runByBlock@{ it ->
            if (it.lineId == pId) {
                result = true
                return@runByBlock
            }
        }
        return result
    }

    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }
}
