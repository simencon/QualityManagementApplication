package com.simenko.qmapp.domain

import com.simenko.qmapp.room_entities.*

data class DomainInputForOrder constructor(
    var id: Int,
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
    var recordId: String,
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
    var charDesignation: String?=null,
    var charOrder: Int
)

data class DomainOrdersStatus constructor(
    var id: Int,
    var statusDescription: String? = null
)

data class DomainMeasurementReason constructor(
    var id: Int,
    var reasonDescription: String? = null,
    var reasonFormalDescript: String? = null,
    var reasonOrder: Int? = null
)

data class DomainOrdersType constructor(
    var id: Int,
    var typeDescription: String? = null
)

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
    var samplesCount: Int? = null
)

data class DomainSubOrderTask constructor(
    var id:Int,
    var statusId: Int,
    var createdDate: String? = null,
    var completedDate: String? = null,
    var subOrderId: Int,
    var charId: Int
)

data class DomainSample constructor(
    var id: Int,
    var subOrderId: Int,
    var sampleNumber: Int? = null
)

data class DomainResultsDecryption constructor(
    var id: Int,
    var resultDecryption: String? = null
)

data class DomainResult constructor(
    var id: Int,
    var sampleId: Int,
    var metrixId: Int,
    var result: Double? = null,
    var isOk: Boolean? = null,
    var resultDecryptionId: Int
)

data class DomainOrderComplete constructor(
    var order: DomainOrder,
    var orderType: DomainOrdersType,
    var orderReason: DomainMeasurementReason,
    var customer: DomainDepartment,
    var orderPlacer: DomainTeamMember,
    var orderStatus: DomainOrdersStatus,
    var detailsVisibility: Boolean = false
)

data class DomainSubOrderComplete constructor(
    var subOrder: DomainSubOrder,
    var orderedBy: DomainTeamMember,
    var completedBy: DomainTeamMember?,
    var status: DomainOrdersStatus,
    var department: DomainDepartment,
    var subDepartment: DomainSubDepartment,
    var channel: DomainManufacturingChannel,
    var line: DomainManufacturingLine,
    var operation: DomainManufacturingOperation,
    var detailsVisibility: Boolean = false
)

//ToDo just to test

data class CompleteOrder constructor(
    var order: DomainOrder,
    var subOrders: ArrayList<CompleteSubOrder>,
    var subOrderTasks: ArrayList<DomainSubOrderTask>
){
    fun addSubOrder(order: CompleteSubOrder){
        subOrders.add(order)
    }
    fun addSubOrderTask(subOrderTask: DomainSubOrderTask){
        subOrderTasks.add(subOrderTask)
    }
}

data class CompleteSubOrder constructor(
    var subOrder: DomainSubOrder,
    var samples: ArrayList<CompleteSample>
){
    fun addSample(sample: CompleteSample){
        samples.add(sample)
    }
}

data class CompleteSample constructor(
    var sample: DomainSample,
    var results: ArrayList<DomainResult>
){
    fun addResult(result: DomainResult){
        results.add(result)
    }
}
