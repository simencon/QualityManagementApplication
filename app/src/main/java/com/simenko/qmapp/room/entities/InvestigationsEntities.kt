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
data class DatabaseMeasurementReason constructor(
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
            entity = DatabaseMeasurementReason::class,
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
    var itemPreffix: String,
    var itemTypeId: Int,
    var itemVersionId: Int,
    var samplesCount: Int? = null
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
        )]
)
data class DatabaseSubOrderTask constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var statusId: Int,
    var createdDate: String? = null,
    var completedDate: String? = null,
    @ColumnInfo(index = true)
    var subOrderId: Int,
    @ColumnInfo(index = true)
    var charId: Int
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
    var result: Double? = null,
    var isOk: Boolean? = null,
    @ColumnInfo(index = true)
    var resultDecryptionId: Int,
    @ColumnInfo(index = true)
    var taskId: Int
)

data class DatabaseOrderComplete constructor(
    @Embedded
    var order: DatabaseOrder,

    @Relation(
        entity = DatabaseOrdersType::class,
        parentColumn = "orderTypeId",
        entityColumn = "id"
    )
    var orderType: DatabaseOrdersType,

    @Relation(
        entity = DatabaseMeasurementReason::class,
        parentColumn = "reasonId",
        entityColumn = "id"
    )
    var orderReason: DatabaseMeasurementReason,

    @Relation(
        entity = DatabaseDepartment::class,
        parentColumn = "customerId",
        entityColumn = "id"
    )
    var customer: DatabaseDepartment,

    @Relation(
        entity = DatabaseTeamMember::class,
        parentColumn = "orderedById",
        entityColumn = "id"
    )
    var orderPlacer: DatabaseTeamMember,

    @Relation(
        entity = DatabaseOrdersStatus::class,
        parentColumn = "statusId",
        entityColumn = "id"
    )
    var orderStatus: DatabaseOrdersStatus
)

data class DatabaseCompleteSubOrder constructor(
    @Embedded
    var subOrder: DatabaseSubOrder,
    @Relation(
        entity = DatabaseTeamMember::class,
        parentColumn = "orderedById",
        entityColumn = "id"
    )
    var orderedBy: DatabaseTeamMember,
    @Relation(
        entity = DatabaseTeamMember::class,
        parentColumn = "completedById",
        entityColumn = "id"
    )
    var completedBy: DatabaseTeamMember?,
    @Relation(
        entity = DatabaseOrdersStatus::class,
        parentColumn = "statusId",
        entityColumn = "id"
    )
    var status: DatabaseOrdersStatus,
    @Relation(
        entity = DatabaseDepartment::class,
        parentColumn = "departmentId",
        entityColumn = "id"
    )
    var department: DatabaseDepartment,
    @Relation(
        entity = DatabaseSubDepartment::class,
        parentColumn = "subDepartmentId",
        entityColumn = "id"
    )
    var subDepartment: DatabaseSubDepartment,
    @Relation(
        entity = DatabaseManufacturingChannel::class,
        parentColumn = "channelId",
        entityColumn = "id"
    )
    var channel: DatabaseManufacturingChannel,
    @Relation(
        entity = DatabaseManufacturingLine::class,
        parentColumn = "lineId",
        entityColumn = "id"
    )
    var line: DatabaseManufacturingLine,
    @Relation(
        entity = DatabaseManufacturingOperation::class,
        parentColumn = "operationId",
        entityColumn = "id"
    )
    var operation: DatabaseManufacturingOperation
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
        entity = DatabaseCharacteristic::class,
        parentColumn = "charId",
        entityColumn = "id"
    )
    var characteristic: DatabaseCharacteristic,
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
    var status: DatabaseOrdersStatus
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
        entity = DatabaseSubOrderTaskComplete::class,
        parentColumn = "taskId",
        entityColumn = "id"
    )
    val subOrderTask: DatabaseSubOrderTaskComplete
)
