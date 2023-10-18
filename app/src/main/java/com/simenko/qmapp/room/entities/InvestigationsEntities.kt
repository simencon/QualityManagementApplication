package com.simenko.qmapp.room.entities

import androidx.room.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.room.contract.DatabaseBaseModel
import com.simenko.qmapp.room.contract.StatusHolderModel
import com.simenko.qmapp.utils.NotificationData
import com.simenko.qmapp.utils.NotificationReasons
import com.simenko.qmapp.utils.ObjectTransformer
import com.simenko.qmapp.utils.StringUtils

@Entity(
    tableName = "1_1_inputForMeasurementRegister",
    primaryKeys = [
        "lineId",
        "operationId",
        "itemId",
        "itemVersionId",
        "charId",
        "itemPrefix"
    ]
)
data class DatabaseInputForOrder constructor(
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
) : DatabaseBaseModel<NetworkInputForOrder, DomainInputForOrder> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseInputForOrder::class, NetworkInputForOrder::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseInputForOrder::class, DomainInputForOrder::class).transform(this)
}

@Entity(tableName = "0_orders_statuses")
data class DatabaseOrdersStatus constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var statusDescription: String? = null
) : DatabaseBaseModel<NetworkOrdersStatus, DomainOrdersStatus> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseOrdersStatus::class, NetworkOrdersStatus::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseOrdersStatus::class, DomainOrdersStatus::class).transform(this)
}

@Entity(tableName = "0_measurement_reasons")
data class DatabaseReason constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var reasonDescription: String? = null,
    var reasonFormalDescript: String? = null,
    var reasonOrder: Int? = null
) : DatabaseBaseModel<NetworkReason, DomainReason> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseReason::class, NetworkReason::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseReason::class, DomainReason::class).transform(this)
}

@Entity(tableName = "0_orders_types")
data class DatabaseOrdersType constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var typeDescription: String? = null
) : DatabaseBaseModel<NetworkOrdersType, DomainOrdersType> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseOrdersType::class, NetworkOrdersType::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseOrdersType::class, DomainOrdersType::class).transform(this)
}

@Entity(
    tableName = "12_orders",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseOrdersType::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("orderTypeId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseReason::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("reasonId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("customerId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseEmployee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("orderedById"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseOrdersStatus::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("statusId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseOrder constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var orderTypeId: Int,
    @ColumnInfo(index = true)
    var reasonId: Int,
    var orderNumber: Int? = null,
    @ColumnInfo(index = true)
    var customerId: Int,
    @ColumnInfo(index = true)
    var orderedById: Int,
    @ColumnInfo(index = true)
    var statusId: Int,
    @ColumnInfo(index = true)
    var createdDate: Long,//Format : "2023-02-02T15:44:47.028Z"
    var completedDate: Long? = null
) : DatabaseBaseModel<NetworkOrder, DomainOrder> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseOrder::class, NetworkOrder::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseOrder::class, DomainOrder::class).transform(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseOrder

        if (id != other.id) return false
        if (orderTypeId != other.orderTypeId) return false
        if (reasonId != other.reasonId) return false
        if (orderNumber != other.orderNumber) return false
        if (customerId != other.customerId) return false
        if (orderedById != other.orderedById) return false
        if (statusId != other.statusId) return false
        if (createdDate != other.createdDate) return false
        if (completedDate != other.completedDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + orderTypeId
        result = 31 * result + reasonId
        result = 31 * result + (orderNumber ?: 0)
        result = 31 * result + customerId
        result = 31 * result + orderedById
        result = 31 * result + statusId
        result = 31 * result + createdDate.hashCode()
        result = 31 * result + (completedDate?.hashCode() ?: 0)
        return result
    }
}

@DatabaseView(
    viewName = "orders_results",
    value = "SELECT o.id, CAST(MIN(r.isOk) AS bit) AS isOk, SUM(CASE(r.isOk ) WHEN 1 THEN 1 ELSE 0 END) AS good, COUNT(r.isOk) AS total  FROM `12_orders` AS o " +
            "LEFT OUTER JOIN `13_sub_orders` AS so ON o.id = so.orderId " +
            "LEFT OUTER JOIN `13_7_sub_order_tasks` AS t ON so.id = t.subOrderId " +
            "LEFT OUTER JOIN `14_8_results` AS r ON t.id = r.taskId " +
            "GROUP BY o.id;"
)
data class DatabaseOrderResult constructor(
    val id: Int,
    val isOk: Boolean?,
    val good: Int?,
    val total: Int?
) : DatabaseBaseModel<Any?, DomainOrderResult> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseOrderResult::class, DomainOrderResult::class).transform(this)
}

@Entity(
    tableName = "13_sub_orders",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseOrder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("orderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseEmployee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("orderedById"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseEmployee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("completedById"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseOrdersStatus::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("statusId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("departmentId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseSubDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subDepartmentId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseManufacturingChannel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("channelId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseManufacturingLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lineId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseManufacturingOperation::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("operationId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class DatabaseSubOrder constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var orderId: Int,
    var subOrderNumber: Int,
    @ColumnInfo(index = true)
    var orderedById: Int,
    @ColumnInfo(index = true)
    var completedById: Int? = null,
    @ColumnInfo(index = true)
    var statusId: Int,
    var createdDate: Long,
    var completedDate: Long? = null,
    @ColumnInfo(index = true)
    var departmentId: Int,
    @ColumnInfo(index = true)
    var subDepartmentId: Int,
    @ColumnInfo(index = true)
    var channelId: Int,
    @ColumnInfo(index = true)
    var lineId: Int,
    @ColumnInfo(index = true)
    var operationId: Int,
    @ColumnInfo(index = true)
    var itemPreffix: String,
    var itemTypeId: Int,
    var itemVersionId: Int,
    var samplesCount: Int? = null,
    var remarkId: Int
) : DatabaseBaseModel<NetworkSubOrder, DomainSubOrder> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseSubOrder::class, NetworkSubOrder::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseSubOrder::class, DomainSubOrder::class).transform(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseSubOrder

        if (id != other.id) return false
        if (orderId != other.orderId) return false
        if (subOrderNumber != other.subOrderNumber) return false
        if (orderedById != other.orderedById) return false
        if (completedById != other.completedById) return false
        if (statusId != other.statusId) return false
        if (createdDate != other.createdDate) return false
        if (completedDate != other.completedDate) return false
        if (departmentId != other.departmentId) return false
        if (subDepartmentId != other.subDepartmentId) return false
        if (channelId != other.channelId) return false
        if (lineId != other.lineId) return false
        if (operationId != other.operationId) return false
        if (itemPreffix != other.itemPreffix) return false
        if (itemTypeId != other.itemTypeId) return false
        if (itemVersionId != other.itemVersionId) return false
        if (samplesCount != other.samplesCount) return false
        if (remarkId != other.remarkId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + orderId
        result = 31 * result + subOrderNumber
        result = 31 * result + orderedById
        result = 31 * result + (completedById ?: 0)
        result = 31 * result + statusId
        result = 31 * result + createdDate.hashCode()
        result = 31 * result + (completedDate?.hashCode() ?: 0)
        result = 31 * result + departmentId
        result = 31 * result + subDepartmentId
        result = 31 * result + channelId
        result = 31 * result + lineId
        result = 31 * result + operationId
        result = 31 * result + itemPreffix.hashCode()
        result = 31 * result + itemTypeId
        result = 31 * result + itemVersionId
        result = 31 * result + (samplesCount ?: 0)
        result = 31 * result + remarkId
        return result
    }
}

@DatabaseView(
    viewName = "sub_orders_results",
    value = "SELECT so.id, CAST(MIN(r.isOk) AS bit) AS isOk, SUM(CASE(r.isOk ) WHEN 1 THEN 1 ELSE 0 END) AS good, COUNT(r.isOk) AS total  FROM `13_sub_orders` AS so " +
            "LEFT OUTER JOIN `13_7_sub_order_tasks` AS t ON so.id = t.subOrderId " +
            "LEFT OUTER JOIN `14_8_results` AS r ON t.id = r.taskId " +
            "GROUP BY so.id;"
)
data class DatabaseSubOrderResult constructor(
    val id: Int,
    val isOk: Boolean?,
    val good: Int?,
    val total: Int?
) : DatabaseBaseModel<Any?, DomainSubOrderResult> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseSubOrderResult::class, DomainSubOrderResult::class).transform(this)
}

@Entity(
    tableName = "13_7_sub_order_tasks",
    indices = [
        Index(
            value = ["subOrderId", "charId"],
            unique = true
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = DatabaseOrdersStatus::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("statusId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseSubOrder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subOrderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseCharacteristic::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("charId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseEmployee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("orderedById"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseEmployee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("completedById"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseSubOrderTask constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var subOrderId: Int,
    @ColumnInfo(index = true)
    var charId: Int,
    @ColumnInfo(index = true)
    var statusId: Int,
    var createdDate: Long? = null,
    var completedDate: Long? = null,
    @ColumnInfo(index = true)
    var orderedById: Int? = null,
    @ColumnInfo(index = true)
    var completedById: Int? = null,
) : DatabaseBaseModel<NetworkSubOrderTask, DomainSubOrderTask> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseSubOrderTask::class, NetworkSubOrderTask::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseSubOrderTask::class, DomainSubOrderTask::class).transform(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseSubOrderTask

        if (id != other.id) return false
        if (subOrderId != other.subOrderId) return false
        if (charId != other.charId) return false
        if (statusId != other.statusId) return false
        if (createdDate != other.createdDate) return false
        if (completedDate != other.completedDate) return false
        if (orderedById != other.orderedById) return false
        if (completedById != other.completedById) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + subOrderId
        result = 31 * result + charId
        result = 31 * result + statusId
        result = 31 * result + (createdDate?.hashCode() ?: 0)
        result = 31 * result + (completedDate?.hashCode() ?: 0)
        result = 31 * result + (orderedById ?: 0)
        result = 31 * result + (completedById ?: 0)
        return result
    }
}

@DatabaseView(
    viewName = "tasks_results",
    value = "SELECT t.id, CAST(MIN(r.isOk) AS bit) AS isOk, SUM(CASE(r.isOk ) WHEN 1 THEN 1 ELSE 0 END) AS good, COUNT(r.isOk) AS total  FROM `13_7_sub_order_tasks` AS t " +
            "LEFT OUTER JOIN `14_8_results` AS r ON t.ID = r.taskId " +
            "GROUP BY t.id;"
)
data class DatabaseTaskResult constructor(
    val id: Int,
    val isOk: Boolean?,
    val good: Int?,
    val total: Int?
) : DatabaseBaseModel<Any?, DomainTaskResult> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseTaskResult::class, DomainTaskResult::class).transform(this)
}

@Entity(
    tableName = "14_samples",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseSubOrder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subOrderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseSample constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var subOrderId: Int,
    var sampleNumber: Int? = null
) : DatabaseBaseModel<NetworkSample, DomainSample> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseSample::class, NetworkSample::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseSample::class, DomainSample::class).transform(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseSample

        if (id != other.id) return false
        if (subOrderId != other.subOrderId) return false
        if (sampleNumber != other.sampleNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + subOrderId
        result = 31 * result + (sampleNumber ?: 0)
        return result
    }
}

@DatabaseView(
    viewName = "samples_results",
    value = "SELECT s.id, r.taskId, s.subOrderId, CAST(MIN(r.isOk) AS bit) AS isOk, SUM(CASE(r.isOk ) WHEN 1 THEN 1 ELSE 0 END) AS good, COUNT(r.isOk) AS total  FROM `14_samples` AS s " +
            "LEFT OUTER JOIN `14_8_results` AS r ON s.id = r.sampleId " +
            "GROUP BY s.id, r.taskId;"
)
data class DatabaseSampleResult constructor(
    val id: Int,
    val taskId: Int?,
    val subOrderId: Int,
    val isOk: Boolean?,
    val good: Int?,
    val total: Int?
) : DatabaseBaseModel<Any?, DomainSampleResult> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseSampleResult::class, DomainSampleResult::class).transform(this)
}

data class DatabaseSampleComplete constructor(
    @Embedded
    val sampleResult: DatabaseSampleResult,
    @Relation(
        entity = DatabaseSample::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val sample: DatabaseSample
) : DatabaseBaseModel<Any?, DomainSampleComplete> {
    override fun getRecordId() = sample.id
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainSampleComplete(
        sampleResult = sampleResult.toDomainModel(),
        sample = sample.toDomainModel(),
        detailsVisibility = false
    )
}

@Entity(tableName = "0_results_decryptions")
data class DatabaseResultsDecryption constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var resultDecryption: String? = null
) : DatabaseBaseModel<NetworkResultsDecryption, DomainResultsDecryption> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseResultsDecryption::class, NetworkResultsDecryption::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseResultsDecryption::class, DomainResultsDecryption::class).transform(this)
}

@Entity(
    tableName = "14_8_results",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseSample::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sampleId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseMetrix::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("metrixId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseResultsDecryption::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("resultDecryptionId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseSubOrderTask::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("taskId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseResult constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var sampleId: Int,
    @ColumnInfo(index = true)
    var metrixId: Int,
    var result: Float? = null,
    var isOk: Boolean? = null,
    @ColumnInfo(index = true)
    var resultDecryptionId: Int,
    @ColumnInfo(index = true)
    var taskId: Int
) : DatabaseBaseModel<NetworkResult, DomainResult> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseResult::class, NetworkResult::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseResult::class, DomainResult::class).transform(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseResult

        if (id != other.id) return false
        if (sampleId != other.sampleId) return false
        if (metrixId != other.metrixId) return false
        if (result != other.result) return false
        if (isOk != other.isOk) return false
        if (resultDecryptionId != other.resultDecryptionId) return false
        if (taskId != other.taskId) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = id
        result1 = 31 * result1 + sampleId
        result1 = 31 * result1 + metrixId
        result1 = 31 * result1 + (result?.hashCode() ?: 0)
        result1 = 31 * result1 + (isOk?.hashCode() ?: 0)
        result1 = 31 * result1 + resultDecryptionId
        result1 = 31 * result1 + taskId
        return result1
    }
}

@DatabaseView(
    viewName = "orders_short",
    value = "SELECT * FROM `12_orders` ORDER BY orderNumber;"
)
data class DatabaseOrderShort constructor(
    @Embedded
    val order: DatabaseOrder,

    @Relation(
        entity = DatabaseOrdersType::class,
        parentColumn = "orderTypeId",
        entityColumn = "id"
    )
    val orderType: DatabaseOrdersType,

    @Relation(
        entity = DatabaseReason::class,
        parentColumn = "reasonId",
        entityColumn = "id"
    )
    val orderReason: DatabaseReason
) : DatabaseBaseModel<Any?, DomainOrderShort> {
    override fun getRecordId() = order.id
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainOrderShort(
        order = order.toDomainModel(),
        orderType = orderType.toDomainModel(),
        orderReason = orderReason.toDomainModel()
    )
}

data class DatabaseOrderComplete constructor(
    @Embedded
    val order: DatabaseOrder,

    @Relation(
        entity = DatabaseOrdersType::class,
        parentColumn = "orderTypeId",
        entityColumn = "id"
    )
    val orderType: DatabaseOrdersType,

    @Relation(
        entity = DatabaseReason::class,
        parentColumn = "reasonId",
        entityColumn = "id"
    )
    val orderReason: DatabaseReason,

    @Relation(
        entity = DatabaseDepartment::class,
        parentColumn = "customerId",
        entityColumn = "id",
    )
    val customer: DatabaseDepartment,

    @Relation(
        entity = DatabaseEmployee::class,
        parentColumn = "orderedById",
        entityColumn = "id"
    )
    val orderPlacer: DatabaseEmployee,

    @Relation(
        entity = DatabaseOrdersStatus::class,
        parentColumn = "statusId",
        entityColumn = "id"
    )
    val orderStatus: DatabaseOrdersStatus,

    @Relation(
        entity = DatabaseOrderResult::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val orderResult: DatabaseOrderResult
) : DatabaseBaseModel<Any?, DomainOrderComplete> {
    override fun getRecordId() = order.id
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainOrderComplete(
        order = order.toDomainModel(),
        orderType = orderType.toDomainModel(),
        orderReason = orderReason.toDomainModel(),
        customer = customer.toDomainModel(),
        orderPlacer = orderPlacer.toDomainModel(),
        orderStatus = orderStatus.toDomainModel(),
        orderResult = orderResult.toDomainModel(),
        detailsVisibility = false
    )
}

data class DatabaseSubOrderComplete constructor(
    @Embedded
    val subOrder: DatabaseSubOrder,

    @Relation(
        entity = DatabaseOrderShort::class,
        parentColumn = "orderId",
        entityColumn = "id"
    )
    val orderShort: DatabaseOrderShort,

    @Relation(
        entity = DatabaseEmployee::class,
        parentColumn = "orderedById",
        entityColumn = "id"
    )
    val orderedBy: DatabaseEmployee,
    @Relation(
        entity = DatabaseEmployee::class,
        parentColumn = "completedById",
        entityColumn = "id"
    )
    val completedBy: DatabaseEmployee?,
    @Relation(
        entity = DatabaseOrdersStatus::class,
        parentColumn = "statusId",
        entityColumn = "id"
    )
    val status: DatabaseOrdersStatus,
    @Relation(
        entity = DatabaseDepartment::class,
        parentColumn = "departmentId",
        entityColumn = "id"
    )
    val department: DatabaseDepartment,
    @Relation(
        entity = DatabaseSubDepartment::class,
        parentColumn = "subDepartmentId",
        entityColumn = "id"
    )
    val subDepartment: DatabaseSubDepartment,
    @Relation(
        entity = DatabaseManufacturingChannel::class,
        parentColumn = "channelId",
        entityColumn = "id"
    )
    val channel: DatabaseManufacturingChannel,
    @Relation(
        entity = DatabaseManufacturingLine::class,
        parentColumn = "lineId",
        entityColumn = "id"
    )
    val line: DatabaseManufacturingLine,
    @Relation(
        entity = DatabaseManufacturingOperation::class,
        parentColumn = "operationId",
        entityColumn = "id"
    )
    val operation: DatabaseManufacturingOperation,
    @Relation(
        entity = DatabaseItemVersionComplete::class,
        parentColumn = "itemPreffix",
        entityColumn = "fId"
    )
    val itemVersionComplete: DatabaseItemVersionComplete,

    @Relation(
        entity = DatabaseSubOrderResult::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val subOrderResult: DatabaseSubOrderResult
) : DatabaseBaseModel<Any?, DomainSubOrderComplete>, StatusHolderModel {
    override fun getRecordId() = subOrder.id
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainSubOrderComplete(
        subOrder = subOrder.toDomainModel(),
        orderShort = orderShort.toDomainModel(),
        orderedBy = orderedBy.toDomainModel(),
        completedBy = completedBy?.toDomainModel(),
        status = status.toDomainModel(),
        department = department.toDomainModel(),
        subDepartment = subDepartment.toDomainModel(),
        channel = channel.toDomainModel(),
        line = line.toDomainModel(),
        operation = operation.toDomainModel(),
        itemVersionComplete = itemVersionComplete.toDomainModel(),
        subOrderResult = subOrderResult.toDomainModel(),
        detailsVisibility = false
    )

    override fun toNotificationData(reason: NotificationReasons) = NotificationData(
        orderId = subOrder.orderId,
        subOrderId = subOrder.id,
        orderNumber = orderShort.order.orderNumber,
        subOrderStatus = status.statusDescription,
        departmentAbbr = department.depAbbr,
        channelAbbr = channel.channelAbbr,
        itemTypeCompleteDesignation = StringUtils.concatTwoStrings1(
            StringUtils.concatTwoStrings3(itemVersionComplete.itemComplete.key.componentKey, itemVersionComplete.itemComplete.item.itemDesignation),
            itemVersionComplete.itemVersion.versionDescription
        ),
        notificationReason = reason
    )
}

@DatabaseView(
    viewName = "sub_order_task_complete",
    value = "SELECT * FROM `13_7_sub_order_tasks` ORDER BY id;"
)
data class DatabaseSubOrderTaskComplete constructor(
    @Embedded
    var subOrderTask: DatabaseSubOrderTask,
    @Relation(
        entity = DatabaseCharacteristicComplete::class,
        parentColumn = "charId",
        entityColumn = "id"
    )
    var characteristic: DatabaseCharacteristicComplete,
    @Relation(
        entity = DatabaseSubOrder::class,
        parentColumn = "subOrderId",
        entityColumn = "id"
    )
    var subOrder: DatabaseSubOrder,
    @Relation(
        entity = DatabaseOrdersStatus::class,
        parentColumn = "statusId",
        entityColumn = "id"
    )
    var status: DatabaseOrdersStatus,
    @Relation(
        entity = DatabaseTaskResult::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val taskResult: DatabaseTaskResult,
) : DatabaseBaseModel<Any?, DomainSubOrderTaskComplete> {
    override fun getRecordId() = subOrderTask.id
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainSubOrderTaskComplete(
        subOrderTask = subOrderTask.toDomainModel(),
        characteristic = characteristic.toDomainModel(),
        subOrder = subOrder.toDomainModel(),
        status = status.toDomainModel(),
        taskResult = taskResult.toDomainModel(),
        detailsVisibility = false
    )
}

@DatabaseView(
    viewName = "results_tolerances",
    value = "SELECT r.id, it.nominal, it.lsl, it.usl FROM `14_8_results` AS r " +
            "INNER JOIN `13_7_sub_order_tasks` AS t ON t.ID = r.taskID " +
            "INNER JOIN `13_sub_orders` AS so ON t.subOrderID = so.ID " +
            "INNER JOIN items_tolerances AS it ON r.metrixID = it.metrixID AND so.itemPreffix = it.fVersionID;"
)
data class DatabaseResultTolerance(
    val id: Int,
    val nominal: Float?,
    val lsl: Float?,
    val usl: Float?
) : DatabaseBaseModel<Any?, DomainResultTolerance> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseResultTolerance::class, DomainResultTolerance::class).transform(this)
}

@DatabaseView(
    viewName = "result_complete",
    value = "SELECT * FROM `14_8_results` ORDER BY id;"
)
data class DatabaseResultComplete(
    @Embedded
    val result: DatabaseResult,
    @Relation(
        entity = DatabaseResultsDecryption::class,
        parentColumn = "resultDecryptionId",
        entityColumn = "id"
    )
    val resultsDecryption: DatabaseResultsDecryption,
    @Relation(
        entity = DatabaseMetrix::class,
        parentColumn = "metrixId",
        entityColumn = "id"
    )
    val metrix: DatabaseMetrix,
    @Relation(
        entity = DatabaseResultTolerance::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val resultTolerance: DatabaseResultTolerance
) : DatabaseBaseModel<Any?, DomainResultComplete> {
    override fun getRecordId() = result.id
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainResultComplete(
        result = result.toDomainModel(),
        resultsDecryption = resultsDecryption.toDomainModel(),
        metrix = metrix.toDomainModel(),
        resultTolerance = resultTolerance.toDomainModel(),
        detailsVisibility = false
    )
}
