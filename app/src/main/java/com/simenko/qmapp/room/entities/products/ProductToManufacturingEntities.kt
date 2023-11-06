package com.simenko.qmapp.room.entities.products

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.simenko.qmapp.domain.entities.products.*
import com.simenko.qmapp.retrofit.entities.products.*
import com.simenko.qmapp.room.contract.DatabaseBaseModel
import com.simenko.qmapp.room.entities.DatabaseManufacturingLine
import com.simenko.qmapp.utils.ObjectTransformer

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
    var id: Int,
    @ColumnInfo(index = true)
    var lineId: Int,
    @ColumnInfo(index = true)
    var productId: Int
) : DatabaseBaseModel<NetworkProductToLine, DomainProductToLine> {
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
    var id: Int,
    @ColumnInfo(index = true)
    var lineId: Int,
    @ColumnInfo(index = true)
    var componentId: Int
) : DatabaseBaseModel<NetworkComponentToLine, DomainComponentToLine> {
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
            entity = DatabaseComponentInStage::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentInStageId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)

data class DatabaseComponentInStageToLine(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var lineId: Int,
    @ColumnInfo(index = true)
    var componentInStageId: Int
) : DatabaseBaseModel<NetworkComponentInStageToLine, DomainComponentInStageToLine> {
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
    val id: Int,
    val fId: String,
    val lineId: Int,
    val itemId: Int,
    val fItemId: String
) : DatabaseBaseModel<Any?, DomainItemToLine> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseItemToLine::class, DomainItemToLine::class).transform(this)
}