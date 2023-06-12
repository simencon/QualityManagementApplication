package com.simenko.qmapp.domain

import androidx.compose.runtime.Stable
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ObjectTransformer

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
) : DomainBaseModel<DatabaseInputForOrder>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}

    override fun toDatabaseModel(): DatabaseInputForOrder {
        return ObjectTransformer(DomainInputForOrder::class, DatabaseInputForOrder::class).transform(this)
    }
}

@Stable
data class DomainOrdersStatus constructor(
    var id: Int,
    var statusDescription: String? = null,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseOrdersStatus>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseOrdersStatus {
        return ObjectTransformer(DomainOrdersStatus::class, DatabaseOrdersStatus::class).transform(this)
    }
}

@Stable
data class DomainOrdersType constructor(
    var id: Int,
    var typeDescription: String? = null,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseOrdersType>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseOrdersType {
        return ObjectTransformer(DomainOrdersType::class, DatabaseOrdersType::class).transform(this)
    }
}

@Stable
data class DomainReason constructor(
    var id: Int,
    var reasonDescription: String? = null,
    var reasonFormalDescript: String? = null,
    var reasonOrder: Int? = null,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseReason>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num // ToDo later will be investigation Type id
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseReason {
        return ObjectTransformer(DomainReason::class, DatabaseReason::class).transform(this)
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
    var createdDate: Long,//Format : "2023-02-02T15:44:47.028Z"
    var completedDate: Long? = null
) : DomainBaseModel<DatabaseOrder>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseOrder {
        return ObjectTransformer(DomainOrder::class, DatabaseOrder::class).transform(this)
    }
}

@Stable
data class DomainOrderResult constructor(
    var id: Int,
    var isOk: Boolean?,
    var good: Int?,
    var total: Int?
) : DomainBaseModel<DatabaseOrderResult>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseOrderResult {
        return ObjectTransformer(DomainOrderResult::class, DatabaseOrderResult::class).transform(this)
    }
}

@Stable
data class DomainSubOrder constructor(
    var id: Int,
    var orderId: Int,
    var subOrderNumber: Int,
    var orderedById: Int,
    var completedById: Int? = null,
    var statusId: Int,
    var createdDate: Long,
    var completedDate: Long? = null,
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
) : DomainBaseModel<DatabaseSubOrder>() {
    override fun getRecordId() = id
    override fun getParentId() = orderId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseSubOrder {
        return ObjectTransformer(DomainSubOrder::class, DatabaseSubOrder::class).transform(this)
    }
}

@Stable
data class DomainSubOrderResult constructor(
    var id: Int,
    var isOk: Boolean?,
    var good: Int?,
    var total: Int?
) : DomainBaseModel<DatabaseSubOrderResult>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseSubOrderResult {
        return ObjectTransformer(DomainSubOrderResult::class, DatabaseSubOrderResult::class).transform(this)
    }
}

@Stable
data class DomainSubOrderTask constructor(
    var id: Int,
    var subOrderId: Int,
    var charId: Int,
    var statusId: Int,
    var createdDate: Long? = null,
    var completedDate: Long? = null,
    var orderedById: Int? = null,
    var completedById: Int? = null,
    var isNewRecord: Boolean = false,
    var toBeDeleted: Boolean = false
) : DomainBaseModel<DatabaseSubOrderTask>() {
    override fun getRecordId() = id
    override fun getParentId() = subOrderId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseSubOrderTask {
        return ObjectTransformer(DomainSubOrderTask::class, DatabaseSubOrderTask::class).transform(this)
    }
}

@Stable
data class DomainTaskResult constructor(
    var id: Int,
    var isOk: Boolean?,
    var good: Int?,
    var total: Int?
) : DomainBaseModel<DatabaseTaskResult>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseTaskResult {
        return ObjectTransformer(DomainTaskResult::class, DatabaseTaskResult::class).transform(this)
    }
}

@Stable
data class DomainSample constructor(
    var id: Int,
    var subOrderId: Int,
    var sampleNumber: Int? = null,
    var isNewRecord: Boolean = false,
    var toBeDeleted: Boolean = false
) : DomainBaseModel<DatabaseSample>() {
    override fun getRecordId() = id
    override fun getParentId() = subOrderId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseSample {
        return ObjectTransformer(DomainSample::class, DatabaseSample::class).transform(this)
    }
}

@Stable
data class DomainSampleResult constructor(
    var id: Int,
    var taskId: Int?,
    var isOk: Boolean?,
    var good: Int?,
    var total: Int?
) : DomainBaseModel<DatabaseSampleResult>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseSampleResult {
        return ObjectTransformer(DomainSampleResult::class, DatabaseSampleResult::class).transform(this)
    }
}

@Stable
data class DomainResultsDecryption constructor(
    var id: Int,
    var resultDecryption: String? = null
) : DomainBaseModel<DatabaseResultsDecryption>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseResultsDecryption {
        return ObjectTransformer(DomainResultsDecryption::class, DatabaseResultsDecryption::class).transform(this)
    }
}

@Stable
data class DomainResult constructor(
    var id: Int,
    var sampleId: Int,
    var metrixId: Int,
    var result: Float? = null,
    var isOk: Boolean? = null,
    var resultDecryptionId: Int,
    var taskId: Int
) : DomainBaseModel<DatabaseResult>() {
    override fun getRecordId() = id
    override fun getParentId() = 31 * sampleId + taskId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseResult {
        return ObjectTransformer(DomainResult::class, DatabaseResult::class).transform(this)
    }
}

@Stable
data class DomainOrderShort constructor(
    val order: DomainOrder,
    val orderType: DomainOrdersType,
    val orderReason: DomainReason
) : DomainBaseModel<DatabaseOrderShort>() {
    override fun getRecordId() = order.getRecordId()
    override fun getParentId() = order.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseOrderShort {
        return DatabaseOrderShort(
            order = order.toDatabaseModel(),
            orderType = orderType.toDatabaseModel(),
            orderReason = orderReason.toDatabaseModel()
        )
    }
}

data class DomainSubOrderShort constructor(
    var subOrder: DomainSubOrder,
    var order: DomainOrder,
    var samples: MutableList<DomainSample> = mutableListOf(),
    var subOrderTasks: MutableList<DomainSubOrderTask> = mutableListOf()
) : DomainBaseModel<DatabaseSubOrderShort>() {
    override fun getRecordId() = subOrder.getRecordId()
    override fun getParentId() = subOrder.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseSubOrderShort {
        return DatabaseSubOrderShort(
            subOrder = subOrder.toDatabaseModel(),
            order = order.toDatabaseModel(),
            samples = samples.map { it.toDatabaseModel() },
            subOrderTasks = subOrderTasks.map { it.toDatabaseModel() }
        )
    }
}

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
) : DomainBaseModel<DatabaseOrderComplete>() {
    override fun getRecordId() = order.getRecordId()
    override fun getParentId() = order.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseOrderComplete {
        return DatabaseOrderComplete(
            order = order.toDatabaseModel(),
            orderType = orderType.toDatabaseModel(),
            orderReason = orderReason.toDatabaseModel(),
            customer = customer.toDatabaseModel(),
            orderPlacer = orderPlacer.toDatabaseModel(),
            orderStatus = orderStatus.toDatabaseModel(),
            orderResult = orderResult.toDatabaseModel(),
        )
    }
}

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
) : DomainBaseModel<DatabaseSubOrderComplete>() {
    override fun getRecordId() = subOrder.getRecordId()
    override fun getParentId() = subOrder.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseSubOrderComplete {
        return DatabaseSubOrderComplete(
            subOrder = subOrder.toDatabaseModel(),
            orderShort = orderShort.toDatabaseModel(),
            orderedBy = orderedBy.toDatabaseModel(),
            completedBy = completedBy?.toDatabaseModel(),
            status = status.toDatabaseModel(),
            department = department.toDatabaseModel(),
            subDepartment = subDepartment.toDatabaseModel(),
            channel = channel.toDatabaseModel(),
            line = line.toDatabaseModel(),
            operation = operation.toDatabaseModel(),
            itemVersionComplete = itemVersionComplete.toDatabaseModel(),
            subOrderResult = subOrderResult.toDatabaseModel(),
        )
    }
}

@Stable
data class DomainSubOrderTaskComplete constructor(
    var subOrderTask: DomainSubOrderTask,
    var characteristic: DomainCharacteristicComplete,
    var status: DomainOrdersStatus,
    var subOrder: DomainSubOrder,
    var taskResult: DomainTaskResult,
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseSubOrderTaskComplete>() {
    override fun getRecordId() = subOrderTask.getRecordId()
    override fun getParentId() = subOrderTask.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseSubOrderTaskComplete {
        return DatabaseSubOrderTaskComplete(
            subOrderTask = subOrderTask.toDatabaseModel(),
            characteristic = characteristic.toDatabaseModel(),
            status = status.toDatabaseModel(),
            subOrder = subOrder.toDatabaseModel(),
            taskResult = taskResult.toDatabaseModel(),
        )
    }
}

@Stable
data class DomainSampleComplete constructor(
    var sampleResult: DomainSampleResult,
    var sample: DomainSample,
    var detailsVisibility: Boolean = false
) : DomainBaseModel<DatabaseSampleComplete>() {
    override fun getRecordId() = sample.getRecordId()
    override fun getParentId() = sample.getRecordId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseSampleComplete {
        return DatabaseSampleComplete(
            sampleResult = sampleResult.toDatabaseModel(),
            sample = sample.toDatabaseModel()
        )
    }
}

@Stable
data class DomainResultComplete(
    var result: DomainResult,
    var resultsDecryption: DomainResultsDecryption,
    var metrix: DomainMetrix,
    var resultTolerance: DomainResultTolerance,
    var detailsVisibility: Boolean = false
) : DomainBaseModel<DatabaseResultComplete>() {
    override fun getRecordId() = result.getRecordId()
    override fun getParentId() = result.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseResultComplete {
        return DatabaseResultComplete(
            result = result.toDatabaseModel(),
            resultsDecryption = resultsDecryption.toDatabaseModel(),
            metrix = metrix.toDatabaseModel(),
            resultTolerance = resultTolerance.toDatabaseModel()
        )
    }
}
