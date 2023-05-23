package com.simenko.qmapp.domain

import android.util.Log
import androidx.compose.runtime.Stable

private const val TAG = "InvestigationsModels"

data class DomainInputForOrder constructor(
    var depId: Int,
    var depAbbr: String,
    var depOrder: Int,
    var subDepId: Int,
    var subDepAbbr: String,
    var subDepOrder: Int,
    var chId: Int,
    var channelAbbr: String,
    var channelOrder: Int,
    var lineId: Int,
    var lineAbbr: String,
    var lineOrder: Int,
    var id: String,
    var itemPrefix: String,
    var itemId: Int,
    var itemVersionId: Int,
    var isDefault: Boolean,
    var itemKey: String,
    var itemDesignation: String,
    var operationId: Int,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int,
    var charId: Int,
    var ishCharId: Int,
    var ishSubChar: Int,
    var charDescription: String,
    var charDesignation: String? = null,
    var charOrder: Int
)

@Stable
data class DomainOrdersStatus constructor(
    var id: Int,
    var statusDescription: String? = null,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId(): Any {
        return id
    }

    override fun getParentOneId(): Int {
        return 0
    }

    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }
}

@Stable
data class DomainOrdersType constructor(
    var id: Int,
    var typeDescription: String? = null,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId() = id
    override fun getParentOneId() = 0
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }
}

@Stable
data class DomainReason constructor(
    var id: Int,
    var reasonDescription: String? = null,
    var reasonFormalDescript: String? = null,
    var reasonOrder: Int? = null,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId() = id
    override fun getParentOneId() = 0 // ToDo later will be investigation Type id
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }
}

@Stable
data class DomainOrder constructor(
    var id: Int,
    var orderTypeId: Int,
    var reasonId: Int,
    var orderNumber: Int? = null,
    var customerId: Int,
    var orderedById: Int,
    var statusId: Int,
    var createdDate: String,//Format : "2023-02-02T15:44:47.028Z"
    var completedDate: String? = null
)

@Stable
data class DomainOrderResult constructor(
    var id: Int,
    var isOk: Boolean?,
    var good: Int?,
    var total: Int?
)

@Stable
data class DomainSubOrder constructor(
    var id: Int,
    var orderId: Int,
    var subOrderNumber: Int,
    var orderedById: Int,
    var completedById: Int? = null,
    var statusId: Int,
    var createdDate: String,
    var completedDate: String? = null,
    var departmentId: Int,
    var subDepartmentId: Int,
    var channelId: Int,
    var lineId: Int,
    var operationId: Int,
    var itemPreffix: String,
    var itemTypeId: Int,
    var itemVersionId: Int,
    var samplesCount: Int? = null,
    var remarkId: Int
)

@Stable
data class DomainSubOrderResult constructor(
    var id: Int,
    var isOk: Boolean?,
    var good: Int?,
    var total: Int?
)

@Stable
data class DomainSubOrderTask constructor(
    var id: Int,
    var subOrderId: Int,
    var charId: Int,
    var statusId: Int,
    var createdDate: String? = null,
    var completedDate: String? = null,
    var orderedById: Int? = null,
    var completedById: Int? = null,
    var isNewRecord: Boolean = false,
    var toBeDeleted: Boolean = false
)

@Stable
data class DomainTaskResult constructor(
    var id: Int,
    var isOk: Boolean?,
    var good: Int?,
    var total: Int?
)

@Stable
data class DomainSample constructor(
    var id: Int,
    var subOrderId: Int,
    var sampleNumber: Int? = null,
    var isNewRecord: Boolean = false,
    var toBeDeleted: Boolean = false
)

@Stable
data class DomainSampleResult constructor(
    var id: Int,
    var taskId: Int?,
    var isOk: Boolean?,
    var good: Int?,
    var total: Int?
)

@Stable
data class DomainResultsDecryption constructor(
    var id: Int,
    var resultDecryption: String? = null
)

@Stable
data class DomainResult constructor(
    var id: Int,
    var sampleId: Int,
    var metrixId: Int,
    var result: Float? = null,
    var isOk: Boolean? = null,
    var resultDecryptionId: Int,
    var taskId: Int
)

@Stable
data class DomainOrderShort constructor(
    val order: DomainOrder,
    val orderType: DomainOrdersType,
    val orderReason: DomainReason
)

data class DomainSubOrderShort constructor(
    var subOrder: DomainSubOrder,
    var order: DomainOrder,
    var samples: MutableList<DomainSample> = mutableListOf(),
    var subOrderTasks: MutableList<DomainSubOrderTask> = mutableListOf()
)

@Stable
data class DomainOrderComplete constructor(
    var order: DomainOrder,
    var orderType: DomainOrdersType,
    var orderReason: DomainReason,
    var customer: DomainDepartment,
    var orderPlacer: DomainTeamMember,
    var orderStatus: DomainOrdersStatus,
    var orderResult: DomainOrderResult,
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false
)

@Stable
data class DomainSubOrderComplete constructor(
    var subOrder: DomainSubOrder,
    var orderShort: DomainOrderShort,
    var orderedBy: DomainTeamMember,
    var completedBy: DomainTeamMember?,
    var status: DomainOrdersStatus,
    var department: DomainDepartment,
    var subDepartment: DomainSubDepartment,
    var channel: DomainManufacturingChannel,
    var line: DomainManufacturingLine,
    var operation: DomainManufacturingOperation,
    var itemVersionComplete: DomainItemVersionComplete,
    var subOrderResult: DomainSubOrderResult,
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false
)

@Stable
data class DomainSubOrderTaskComplete constructor(
    var subOrderTask: DomainSubOrderTask,
    var characteristic: DomainCharacteristicComplete,
    var status: DomainOrdersStatus,
    var subOrder: DomainSubOrder,
    var taskResult: DomainTaskResult,
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false
)

@Stable
data class DomainSampleComplete constructor(
    var sampleResult: DomainSampleResult,
    var sample: DomainSample,
    var detailsVisibility: Boolean = false
)

@Stable
data class DomainResultComplete(
    var result: DomainResult,
    var resultsDecryption: DomainResultsDecryption,
    var metrix: DomainMetrix,
    var resultTolerance: DomainResultTolerance,
    var detailsVisibility: Boolean = false
)
