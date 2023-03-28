package com.simenko.qmapp.room.entities

import androidx.room.*

//    @ColumnInfo(name = "nameInTable")

@Entity(
    tableName = "1_1_inputForMeasurementRegister",
    primaryKeys = [
        "itemPrefix",
        "itemVersionId",
        "operationId",
        "charId"
    ]
)
data class DatabaseInputForOrder constructor(
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
    var charDesignation: String? = null,
    var charOrder: Int
)

@Entity(tableName = "0_orders_statuses")
data class DatabaseOrdersStatus constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var statusDescription: String? = null
)

@Entity(tableName = "0_measurement_reasons")
data class DatabaseReason constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var reasonDescription: String? = null,
    var reasonFormalDescript: String? = null,
    var reasonOrder: Int? = null
)

@Entity(tableName = "0_orders_types")
data class DatabaseOrdersType constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var typeDescription: String? = null
)

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
            entity = DatabaseTeamMember::class,
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
    var createdDate: String,//Format : "2023-02-02T15:44:47.028Z"
    var completedDate: String? = null
)

@DatabaseView(
    viewName = "orders_results",
    value = "SELECT o.id, CAST(MIN(r.isOk) AS bit) AS isOk, SUM(IIF(r.isOk = 1, 1, 0)) AS good, COUNT(r.isOk) AS total  FROM `12_orders` AS o " +
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
)

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
            entity = DatabaseTeamMember::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("orderedById"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseTeamMember::class,
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
    var createdDate: String,
    var completedDate: String? = null,
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
    var samplesCount: Int? = null
)

@DatabaseView(
    viewName = "sub_orders_results",
    value = "SELECT so.id, CAST(MIN(r.isOk) AS bit) AS isOk, SUM(IIF(r.isOk = 1, 1, 0)) AS good, COUNT(r.isOk) AS total  FROM `13_sub_orders` AS so " +
            "LEFT OUTER JOIN `13_7_sub_order_tasks` AS t ON so.id = t.subOrderId " +
            "LEFT OUTER JOIN `14_8_results` AS r ON t.id = r.taskId " +
            "GROUP BY so.id;"
)
data class DatabaseSubOrderResult constructor(
    val id: Int,
    val isOk: Boolean?,
    val good: Int?,
    val total: Int?
)

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
            entity = DatabaseTeamMember::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("orderedById"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseTeamMember::class,
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
    var createdDate: String? = null,
    var completedDate: String? = null,
    @ColumnInfo(index = true)
    var orderedById: Int? = null,
    @ColumnInfo(index = true)
    var completedById: Int? = null,
)

@DatabaseView(
    viewName = "tasks_results",
    value = "SELECT t.id, CAST(MIN(r.isOk) AS bit) AS isOk, SUM(IIF(r.isOk = 1, 1, 0)) AS good, COUNT(r.isOk) AS total  FROM `13_7_sub_order_tasks` AS t " +
            "LEFT OUTER JOIN `14_8_results` AS r ON t.ID = r.taskId " +
            "GROUP BY t.id;"
)
data class DatabaseTaskResult constructor(
    val id: Int,
    val isOk: Boolean?,
    val good: Int?,
    val total: Int?
)

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
)

@DatabaseView(
    viewName = "samples_results",
    value = "SELECT s.id, r.taskId, CAST(MIN(r.isOk) AS bit) AS isOk, SUM(IIF(r.isOk = 1, 1, 0)) AS good, COUNT(r.isOk) AS total  FROM `14_samples` AS s " +
            "LEFT OUTER JOIN `14_8_results` AS r ON s.id = r.sampleId " +
            "GROUP BY s.id, r.taskId;"
)
data class DatabaseSampleResult constructor(
    val id: Int,
    val taskId: Int?,
    val isOk: Boolean?,
    val good: Int?,
    val total: Int?
)

data class DatabaseSampleComplete constructor(
    @Embedded
    val sampleResult: DatabaseSampleResult,
    @Relation(
        entity = DatabaseSample::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val sample: DatabaseSample
)

@Entity(tableName = "0_results_decryptions")
data class DatabaseResultsDecryption constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var resultDecryption: String? = null
)

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
)
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
)

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
        entity = DatabaseTeamMember::class,
        parentColumn = "orderedById",
        entityColumn = "id"
    )
    val orderPlacer: DatabaseTeamMember,

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
)

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
        entity = DatabaseTeamMember::class,
        parentColumn = "orderedById",
        entityColumn = "id"
    )
    val orderedBy: DatabaseTeamMember,
    @Relation(
        entity = DatabaseTeamMember::class,
        parentColumn = "completedById",
        entityColumn = "id"
    )
    val completedBy: DatabaseTeamMember?,
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
)

data class DatabaseSubOrderWithChildren constructor(
    @Embedded
    var subOrder: DatabaseSubOrder,
    @Relation(
        entity = DatabaseSample::class,
        parentColumn = "id",
        entityColumn = "subOrderId"
    )
    var samples: List<DatabaseSample>,
    @Relation(
        entity = DatabaseSubOrderTask::class,
        parentColumn = "id",
        entityColumn = "subOrderId"
    )
    var subOrderTasks: List<DatabaseSubOrderTask>
)

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
)

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
)

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
)
