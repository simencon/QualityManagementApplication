package com.simenko.qmapp.room_entities

import androidx.room.*
import com.simenko.qmapp.domain.CompleteSubOrder
import com.simenko.qmapp.domain.DomainOrder
import com.simenko.qmapp.domain.DomainSubOrderTask

//    @ColumnInfo(name = "nameInTable")

@Entity(tableName = "1_1_inputForMeasurementRegister")
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
    @PrimaryKey(autoGenerate = false)
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
        ),
        ForeignKey(
            entity = DatabaseOrdersStatus::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("statusId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
        //    ToDo declare the rest of ForeignKeys when necessary
    ]
)
data class DatabaseSubOrder constructor(
    @PrimaryKey(autoGenerate = true)
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
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
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
    var statusId: Int,
    var createdDate: String? = null,
    var completedDate: String? = null,
    var subOrderId: Int,
    var charId: Int
)

@Entity(
    tableName = "14_samples",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseSubOrder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subOrderId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseSample constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
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
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseMetrix::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("metrixId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseResultsDecryption::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("resultDecryptionId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseResult constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var sampleId: Int,
    var metrixId: Int,
    var result: Double? = null,
    var isOk: Boolean? = null,
    var resultDecryptionId: Int
)

data class DatabaseCompleteOrder constructor(
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
    var orderStatus: DatabaseOrdersStatus,

    @Relation(
        entity = DatabaseSubOrder::class,
        parentColumn = "id",
        entityColumn = "orderId"
    )
    var subOrders: List<DatabaseSubOrder>

){
    fun selectedRecord(): String {
        return "${orderReason.reasonDescription} (${order.orderNumber})"
    }
}
