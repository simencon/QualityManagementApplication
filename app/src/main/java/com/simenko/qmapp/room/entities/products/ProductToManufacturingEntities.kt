package com.simenko.qmapp.room.entities.products

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.products.*
import com.simenko.qmapp.retrofit.entities.products.*
import com.simenko.qmapp.room.contract.DatabaseBaseModel
import com.simenko.qmapp.room.entities.DatabaseDepartment
import com.simenko.qmapp.room.entities.DatabaseManufacturingChannel
import com.simenko.qmapp.room.entities.DatabaseManufacturingLine
import com.simenko.qmapp.room.entities.DatabaseManufacturingOperation
import com.simenko.qmapp.room.entities.DatabaseSubDepartment
import com.simenko.qmapp.utils.ObjectTransformer

@Entity(
    tableName = "10_0_prod_projects_to_departments",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("depId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseProductLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productLineId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseProductLineToDepartment(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val depId: ID,
    @ColumnInfo(index = true)
    val productLineId: ID
) : DatabaseBaseModel<NetworkProductLineToDepartment, DomainProductLineToDepartment, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductLineToDepartment::class, NetworkProductLineToDepartment::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductLineToDepartment::class, DomainProductLineToDepartment::class).transform(this)
}

@Entity(
    tableName = "11_1_prod_kinds_to_s_departments",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseSubDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subDepId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseProductKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("prodKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseProductKindToSubDepartment(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val subDepId: ID,
    @ColumnInfo(index = true)
    val prodKindId: ID
) : DatabaseBaseModel<NetworkProductKindToSubDepartment, DomainProductKindToSubDepartment, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductKindToSubDepartment::class, NetworkProductKindToSubDepartment::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductKindToSubDepartment::class, DomainProductKindToSubDepartment::class).transform(this)
}

@Entity(
    tableName = "11_3_comp_kinds_to_s_departments",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseSubDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subDepId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponentKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("compKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseComponentKindToSubDepartment(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val subDepId: ID,
    @ColumnInfo(index = true)
    val compKindId: ID
) : DatabaseBaseModel<NetworkComponentKindToSubDepartment, DomainComponentKindToSubDepartment, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentKindToSubDepartment::class, NetworkComponentKindToSubDepartment::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentKindToSubDepartment::class, DomainComponentKindToSubDepartment::class).transform(this)
}

@Entity(
    tableName = "11_5_comp_stages_to_s_departments",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseSubDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subDepId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponentStageKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("stageKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseStageKindToSubDepartment(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val subDepId: ID,
    @ColumnInfo(index = true)
    val stageKindId: ID
) : DatabaseBaseModel<NetworkStageKindToSubDepartment, DomainStageKindToSubDepartment, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseStageKindToSubDepartment::class, NetworkStageKindToSubDepartment::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseStageKindToSubDepartment::class, DomainStageKindToSubDepartment::class).transform(this)
}

@Entity(
    tableName = "12_1_products_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingChannel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseProductKeyToChannel(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val chId: ID,
    @ColumnInfo(index = true)
    val keyId: ID
) : DatabaseBaseModel<NetworkProductKeyToChannel, DomainProductKeyToChannel, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductKeyToChannel::class, NetworkProductKeyToChannel::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductKeyToChannel::class, DomainProductKeyToChannel::class).transform(this)
}

@Entity(
    tableName = "12_3_components_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingChannel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseComponentKeyToChannel(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val chId: ID,
    @ColumnInfo(index = true)
    val keyId: ID
) : DatabaseBaseModel<NetworkComponentKeyToChannel, DomainComponentKeyToChannel, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentKeyToChannel::class, NetworkComponentKeyToChannel::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentKeyToChannel::class, DomainComponentKeyToChannel::class).transform(this)
}

@Entity(
    tableName = "12_5_stages_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingChannel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseStageKeyToChannel(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val chId: ID,
    @ColumnInfo(index = true)
    val keyId: ID
) : DatabaseBaseModel<NetworkStageKeyToChannel, DomainStageKeyToChannel, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseStageKeyToChannel::class, NetworkStageKeyToChannel::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseStageKeyToChannel::class, DomainStageKeyToChannel::class).transform(this)
}

@Entity(
    tableName = "13_1_products_to_lines",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lineId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseProduct::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseProductToLine(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val lineId: ID,
    @ColumnInfo(index = true)
    val productId: ID
) : DatabaseBaseModel<NetworkProductToLine, DomainProductToLine, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductToLine::class, NetworkProductToLine::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductToLine::class, DomainProductToLine::class).transform(this)
}

@Entity(
    tableName = "13_3_components_to_lines",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lineId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseComponentToLine(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val lineId: ID,
    @ColumnInfo(index = true)
    val componentId: ID
) : DatabaseBaseModel<NetworkComponentToLine, DomainComponentToLine, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentToLine::class, NetworkComponentToLine::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentToLine::class, DomainComponentToLine::class).transform(this)
}

@Entity(
    tableName = "13_5_component_in_stages_to_lines",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lineId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponentStage::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentInStageId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)

data class DatabaseComponentInStageToLine(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val lineId: ID,
    @ColumnInfo(index = true)
    val componentInStageId: ID
) : DatabaseBaseModel<NetworkComponentInStageToLine, DomainComponentInStageToLine, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentInStageToLine::class, NetworkComponentInStageToLine::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentInStageToLine::class, DomainComponentInStageToLine::class).transform(this)
}

@DatabaseView(
    viewName = "items_to_lines",
    value = "SELECT ptl.id as id, ('p'|| ptl.id) as fId, ptl.lineId, ptl.productId as itemId, ('p'|| ptl.productId) as fItemId FROM `13_1_products_to_lines` AS ptl " +
            "UNION ALL " +
            "SELECT ctl.id as id, ('c'|| ctl.id) as fId, ctl.lineId, ctl.componentId as itemId, ('c'|| ctl.componentId) as fItemId FROM `13_3_components_to_lines` AS ctl " +
            "UNION ALL " +
            "SELECT stl.id as id, ('s'|| stl.id) as fId, stl.lineId, stl.componentInStageId as itemId, ('s'|| stl.componentInStageId) as fItemId FROM `13_5_component_in_stages_to_lines` AS stl;"
)
data class DatabaseItemToLine(
    val id: ID,
    val fId: String,
    val lineId: ID,
    val itemId: ID,
    val fItemId: String
) : DatabaseBaseModel<Any?, DomainItemToLine, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseItemToLine::class, DomainItemToLine::class).transform(this)
}

@Entity(
    tableName = "14_7_operations_to_chars",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCharacteristic::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("charId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseManufacturingOperation::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("operationId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseCharacteristicToOperation(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val charId: ID,
    @ColumnInfo(index = true)
    val operationId: ID
) : DatabaseBaseModel<NetworkCharacteristicToOperation, DomainCharacteristicToOperation, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseCharacteristicToOperation::class, NetworkCharacteristicToOperation::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCharacteristicToOperation::class, DomainCharacteristicToOperation::class).transform(this)
}