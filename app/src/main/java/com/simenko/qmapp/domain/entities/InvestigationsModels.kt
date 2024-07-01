package com.simenko.qmapp.domain.entities

import androidx.compose.runtime.Stable
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
import com.simenko.qmapp.domain.entities.products.DomainItemVersionComplete
import com.simenko.qmapp.domain.entities.products.DomainMetrix
import com.simenko.qmapp.domain.entities.products.DomainResultTolerance
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ObjectTransformer

data class DomainInputForOrder constructor(
    var depId: ID,
    var depAbbr: String,
    var depOrder: Int,
    var subDepId: ID,
    var subDepAbbr: String,
    var subDepOrder: Int,
    var chId: ID,
    var channelAbbr: String,
    var channelOrder: Int,
    var lineId: ID,
    var lineAbbr: String,
    var lineOrder: Int,
    var id: String,
    var itemPrefix: String,
    var itemId: ID,
    var itemVersionId: ID,
    var isDefault: Boolean,
    var itemKey: String,
    var itemDesignation: String,
    var operationId: ID,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int,
    var charId: ID,
    var ishSubChar: ID,
    var charDescription: String,
    var charDesignation: String? = null,
    var charOrder: Int
) : DomainBaseModel<DatabaseInputForOrder>() {
    fun getItemVersionPid(): String = itemPrefix + itemVersionId
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainInputForOrder::class, DatabaseInputForOrder::class).transform(this)
}

@Stable
data class DomainOrdersStatus constructor(
    var id: ID = NoRecord.num,
    var statusDescription: String? = null,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseOrdersStatus>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainOrdersStatus::class, DatabaseOrdersStatus::class).transform(this)
}

@Stable
data class DomainOrdersType constructor(
    var id: ID = NoRecord.num,
    var typeDescription: String? = null,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseOrdersType>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainOrdersType::class, DatabaseOrdersType::class).transform(this)
}

@Stable
data class DomainReason constructor(
    var id: ID = NoRecord.num,
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

    override fun toDatabaseModel() = ObjectTransformer(DomainReason::class, DatabaseReason::class).transform(this)
}

@Stable
data class DomainOrder constructor(
    var id: ID = NoRecord.num,
    var orderTypeId: ID = NoRecord.num,
    var reasonId: ID = NoRecord.num,
    var orderNumber: Long? = null,
    var customerId: ID = NoRecord.num,
    var orderedById: ID = NoRecord.num,
    var statusId: ID = NoRecord.num,
    var createdDate: Long = NoRecord.num.toLong(),//Format : "2023-02-02T15:44:47.028Z"
    var completedDate: Long? = null
) : DomainBaseModel<DatabaseOrder>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainOrder::class, DatabaseOrder::class).transform(this)
}

@Stable
data class DomainOrderResult constructor(
    var id: ID = NoRecord.num,
    var isOk: Boolean? = null,
    var good: Int? = null,
    var total: Int? = null
) : DomainBaseModel<DatabaseOrderResult>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainOrderResult::class, DatabaseOrderResult::class).transform(this)
}

@Stable
data class DomainSubOrder constructor(
    var id: ID = NoRecord.num,
    var orderId: ID = NoRecord.num,
    var subOrderNumber: Long = NoRecord.num,
    var orderedById: ID = NoRecord.num,
    var completedById: ID? = null,
    var statusId: ID = NoRecord.num,
    var createdDate: Long = NoRecord.num,
    var completedDate: Long? = null,
    var departmentId: ID = NoRecord.num,
    var subDepartmentId: ID = NoRecord.num,
    var channelId: ID = NoRecord.num,
    var lineId: ID = NoRecord.num,
    var operationId: ID = NoRecord.num,
    var itemPreffix: String = NoString.str,
    var itemTypeId: ID = NoRecord.num,
    var itemVersionId: ID = NoRecord.num,
    var samplesCount: Int? = ZeroValue.num.toInt(),
    var remarkId: ID = 1 //means no remark
) : DomainBaseModel<DatabaseSubOrder>() {
    fun getItemIds(): Triple<String, ID, ID> = Triple(itemPreffix[0].toString(), itemTypeId, itemVersionId)
    fun setItemIds(id: Triple<String, ID, ID>) {
        itemPreffix = id.first
        itemTypeId = id.second
        itemVersionId = id.third
    }

    fun getItemVersionPid(): String = itemPreffix[0].toString() + itemVersionId
    override fun getRecordId() = id
    override fun getParentId() = orderId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainSubOrder::class, DatabaseSubOrder::class).transform(this)
}

@Stable
data class DomainSubOrderResult constructor(
    var id: ID = NoRecord.num,
    var isOk: Boolean? = null,
    var good: Int? = null,
    var total: Int? = null
) : DomainBaseModel<DatabaseSubOrderResult>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainSubOrderResult::class, DatabaseSubOrderResult::class).transform(this)
}

@Stable
data class DomainSubOrderTask constructor(
    var id: ID = NoRecord.num,
    var subOrderId: ID = NoRecord.num,
    var charId: ID = NoRecord.num,
    var statusId: ID = NoRecord.num,
    var createdDate: Long? = null,
    var completedDate: Long? = null,
    var orderedById: ID? = null,
    var completedById: ID? = null,
    var isNewRecord: Boolean = false,
    var toBeDeleted: Boolean = false
) : DomainBaseModel<DatabaseSubOrderTask>() {
    override fun getRecordId() = id
    override fun getParentId() = subOrderId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainSubOrderTask::class, DatabaseSubOrderTask::class).transform(this)
}

@Stable
data class DomainTaskResult constructor(
    var id: ID = NoRecord.num,
    var isOk: Boolean? = null,
    var good: Int? = null,
    var total: Int? = null
) : DomainBaseModel<DatabaseTaskResult>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainTaskResult::class, DatabaseTaskResult::class).transform(this)
}

@Stable
data class DomainSample constructor(
    var id: ID = NoRecord.num,
    var subOrderId: ID = NoRecord.num,
    var sampleNumber: Int? = null,
    var isNewRecord: Boolean = false,
    var toBeDeleted: Boolean = false
) : DomainBaseModel<DatabaseSample>() {
    override fun getRecordId() = id
    override fun getParentId() = subOrderId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainSample::class, DatabaseSample::class).transform(this)
}

@Stable
data class DomainSampleResult constructor(
    var id: ID = NoRecord.num,
    var taskId: ID? = null,
    var isOk: Boolean? = null,
    var good: Int? = null,
    var total: Int? = null
) : DomainBaseModel<DatabaseSampleResult>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainSampleResult::class, DatabaseSampleResult::class).transform(this)
}

@Stable
data class DomainResultsDecryption constructor(
    var id: ID = NoRecord.num,
    var resultDecryption: String? = null
) : DomainBaseModel<DatabaseResultsDecryption>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainResultsDecryption::class, DatabaseResultsDecryption::class).transform(this)
}

@Stable
data class DomainResult constructor(
    var id: ID = NoRecord.num,
    var sampleId: ID = NoRecord.num,
    var metrixId: ID = NoRecord.num,
    var result: Float? = null,
    var isOk: Boolean? = null,
    var resultDecryptionId: ID = NoRecord.num,
    var taskId: ID = NoRecord.num
) : DomainBaseModel<DatabaseResult>() {
    override fun getRecordId() = id
    override fun getParentId() = 31 * sampleId + taskId
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainResult::class, DatabaseResult::class).transform(this)
}

@Stable
data class DomainOrderShort constructor(
    val order: DomainOrder = DomainOrder(),
    val orderType: DomainOrdersType = DomainOrdersType(),
    val orderReason: DomainReason = DomainReason()
) : DomainBaseModel<DatabaseOrderShort>() {
    override fun getRecordId() = order.getRecordId()
    override fun getParentId() = order.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = DatabaseOrderShort(
        order = order.toDatabaseModel(),
        orderType = orderType.toDatabaseModel(),
        orderReason = orderReason.toDatabaseModel()
    )
}

data class DomainSubOrderShort constructor(
    var subOrder: DomainSubOrder,
    var order: DomainOrder,
    var samples: MutableList<DomainSample> = mutableListOf(),
    var subOrderTasks: MutableList<DomainSubOrderTask> = mutableListOf(),
    var extraTrigger: Boolean = false
) : DomainBaseModel<Any?>() {
    override fun getRecordId() = subOrder.getRecordId()
    override fun getParentId() = subOrder.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = null
}

@Stable
data class DomainOrderComplete constructor(
    var order: DomainOrder = DomainOrder(),
    var orderType: DomainOrdersType = DomainOrdersType(),
    var orderReason: DomainReason = DomainReason(),
    var customer: DomainDepartment = DomainDepartment(),
    var orderPlacer: DomainEmployee = DomainEmployee(),
    var orderStatus: DomainOrdersStatus = DomainOrdersStatus(),
    var orderResult: DomainOrderResult = DomainOrderResult(),
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseOrderComplete>() {
    override fun getRecordId() = order.getRecordId()
    override fun getParentId() = order.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = DatabaseOrderComplete(
        order = order.toDatabaseModel(),
        orderType = orderType.toDatabaseModel(),
        orderReason = orderReason.toDatabaseModel(),
        customer = customer.toDatabaseModel(),
        orderPlacer = orderPlacer.toDatabaseModel(),
        orderStatus = orderStatus.toDatabaseModel(),
        orderResult = orderResult.toDatabaseModel(),
    )
}

@Stable
data class DomainSubOrderComplete constructor(
    var subOrder: DomainSubOrder = DomainSubOrder(),
    var orderShort: DomainOrderShort = DomainOrderShort(),
    var orderedBy: DomainEmployee = DomainEmployee(),
    var completedBy: DomainEmployee? = null,
    var status: DomainOrdersStatus = DomainOrdersStatus(),
    var department: DomainDepartment = DomainDepartment(),
    var subDepartment: DomainSubDepartment = DomainSubDepartment(),
    var channel: DomainManufacturingChannel = DomainManufacturingChannel(),
    var line: DomainManufacturingLine = DomainManufacturingLine(),
    var operation: DomainManufacturingOperation = DomainManufacturingOperation(),
    var itemVersionComplete: DomainItemVersionComplete = DomainItemVersionComplete(),
    var subOrderResult: DomainSubOrderResult = DomainSubOrderResult(),
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseSubOrderComplete>() {
    override fun getRecordId() = subOrder.getRecordId()
    override fun getParentId() = subOrder.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = DatabaseSubOrderComplete(
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

@Stable
data class DomainSubOrderTaskComplete constructor(
    var subOrderTask: DomainSubOrderTask = DomainSubOrderTask(),
    var characteristic: DomainCharacteristic.DomainCharacteristicComplete = DomainCharacteristic.DomainCharacteristicComplete(),
    var status: DomainOrdersStatus = DomainOrdersStatus(),
    var subOrder: DomainSubOrder = DomainSubOrder(),
    var taskResult: DomainTaskResult = DomainTaskResult(),
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseSubOrderTaskComplete>() {
    override fun getRecordId() = subOrderTask.getRecordId()
    override fun getParentId() = subOrderTask.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = DatabaseSubOrderTaskComplete(
        subOrderTask = subOrderTask.toDatabaseModel(),
        characteristic = characteristic.toDatabaseModel(),
        status = status.toDatabaseModel(),
        subOrder = subOrder.toDatabaseModel(),
        taskResult = taskResult.toDatabaseModel(),
    )
}

@Stable
data class DomainSampleComplete constructor(
    var sampleResult: DomainSampleResult = DomainSampleResult(),
    var sample: DomainSample = DomainSample(),
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseSampleComplete>() {
    override fun getRecordId() = sample.getRecordId()
    override fun getParentId() = sample.getRecordId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = DatabaseSampleComplete(
        sampleResult = sampleResult.toDatabaseModel(),
        sample = sample.toDatabaseModel()
    )
}

@Stable
data class DomainResultComplete(
    var result: DomainResult = DomainResult(),
    var resultsDecryption: DomainResultsDecryption = DomainResultsDecryption(),
    var metrix: DomainMetrix = DomainMetrix(),
    var resultTolerance: DomainResultTolerance = DomainResultTolerance(),
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseResultComplete>() {
    override fun getRecordId() = result.getRecordId()
    override fun getParentId() = result.getParentId()
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = DatabaseResultComplete(
        result = result.toDatabaseModel(),
        resultsDecryption = resultsDecryption.toDatabaseModel(),
        metrix = metrix.toDatabaseModel(),
        resultTolerance = resultTolerance.toDatabaseModel()
    )
}
