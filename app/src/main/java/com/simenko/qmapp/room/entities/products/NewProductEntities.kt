package com.simenko.qmapp.room.entities.products

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.simenko.qmapp.domain.entities.products.*
import com.simenko.qmapp.retrofit.entities.products.*
import com.simenko.qmapp.room.contract.DatabaseBaseModel
import com.simenko.qmapp.room.entities.DatabaseCharacteristic
import com.simenko.qmapp.room.entities.DatabaseComponent
import com.simenko.qmapp.room.entities.DatabaseComponentInStage
import com.simenko.qmapp.room.entities.DatabaseKey
import com.simenko.qmapp.room.entities.DatabaseManufacturingProject
import com.simenko.qmapp.room.entities.DatabaseProduct
import com.simenko.qmapp.utils.ObjectTransformer

@Entity(
    tableName = "1_product_kinds",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingProject::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("projectId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseProductKind(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val projectId: Long,
    val productKindDesignation: String,
    val comments: String?
) : DatabaseBaseModel<NetworkProductKind, DomainProductKind> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductKind::class, NetworkProductKind::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductKind::class, DomainProductKind::class).transform(this)
}

@Entity(
    tableName = "3_component_kinds",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentKind(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val productKindId: Long,
    val componentKindOrder: Int,
    val componentKindDescription: String
) : DatabaseBaseModel<NetworkComponentKind, DomainComponentKind> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentKind::class, NetworkComponentKind::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentKind::class, DomainComponentKind::class).transform(this)
}

@Entity(
    tableName = "5_component_stage_kinds",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentStageKind(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val componentKindId: Long,
    val componentStageOrder: Int,
    val componentStageDescription: String
) : DatabaseBaseModel<NetworkComponentStageKind, DomainComponentStageKind> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentStageKind::class, NetworkComponentStageKind::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentStageKind::class, DomainComponentStageKind::class).transform(this)
}

@Entity(
    tableName = "1_1_product_kind_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseProductKindKey(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val productKindId: Long,
    @ColumnInfo(index = true)
    val keyId: Long
) : DatabaseBaseModel<NetworkProductKindKey, DomainProductKindKey> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductKindKey::class, NetworkProductKindKey::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductKindKey::class, DomainProductKindKey::class).transform(this)
}

@Entity(
    tableName = "3_1_component_kind_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentKindKey(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val componentKindId: Long,
    @ColumnInfo(index = true)
    val keyId: Long
) : DatabaseBaseModel<NetworkComponentKindKey, DomainComponentKindKey> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentKindKey::class, NetworkComponentKindKey::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentKindKey::class, DomainComponentKindKey::class).transform(this)
}

@Entity(
    tableName = "5_1_component_stage_kind_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentStageKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentStageKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentStageKindKey(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val componentStageKindId: Long,
    @ColumnInfo(index = true)
    val keyId: Long
) : DatabaseBaseModel<NetworkComponentStageKindKey, DomainComponentStageKindKey> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentStageKindKey::class, NetworkComponentStageKindKey::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentStageKindKey::class, DomainComponentStageKindKey::class).transform(this)
}

@Entity(
    tableName = "1_2_product_kinds_products",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseProduct::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseProductKindProduct(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val productKindId: Long,
    @ColumnInfo(index = true)
    val productId: Long
) : DatabaseBaseModel<NetworkProductKindProduct, DomainProductKindProduct> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductKindProduct::class, NetworkProductKindProduct::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductKindProduct::class, DomainProductKindProduct::class).transform(this)
}

@Entity(
    tableName = "3_4_component_kinds_components",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentKindComponent(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val componentKindId: Long,
    @ColumnInfo(index = true)
    val componentId: Long
) : DatabaseBaseModel<NetworkComponentKindComponent, DomainComponentKindComponent> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentKindComponent::class, NetworkComponentKindComponent::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentKindComponent::class, DomainComponentKindComponent::class).transform(this)
}

@Entity(
    tableName = "5_6_component_stage_kinds_component_stages",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentStageKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentStageKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponentInStage::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentStageId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentStageKindComponentStage(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val componentStageKindId: Long,
    @ColumnInfo(index = true)
    val componentStageId: Long
) : DatabaseBaseModel<NetworkComponentStageKindComponentStage, DomainComponentStageKindComponentStage> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentStageKindComponentStage::class, NetworkComponentStageKindComponentStage::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentStageKindComponentStage::class, DomainComponentStageKindComponentStage::class).transform(this)
}

@Entity(
    tableName = "2_4_products_components",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProduct::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseProductComponent(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val countOfComponents: Int,
    @ColumnInfo(index = true)
    val productId: Long,
    @ColumnInfo(index = true)
    val componentId: Long
) : DatabaseBaseModel<NetworkProductComponent, DomainProductComponent> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductComponent::class, NetworkProductComponent::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductComponent::class, DomainProductComponent::class).transform(this)
}

@Entity(
    tableName = "4_6_components_component_stages",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponentInStage::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentStageId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseComponentComponentStage(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val componentId: Long,
    @ColumnInfo(index = true)
    val componentStageId: Long
) : DatabaseBaseModel<NetworkComponentComponentStage, DomainComponentComponentStage> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentComponentStage::class, NetworkComponentComponentStage::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentComponentStage::class, DomainComponentComponentStage::class).transform(this)
}

@Entity(
    tableName = "1_7_characteristics_product_kinds",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCharacteristic::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("charId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseProductKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseCharacteristicProductKind(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val charId: Long,
    @ColumnInfo(index = true)
    val productKindId: Long
) : DatabaseBaseModel<NetworkCharacteristicProductKind, DomainCharacteristicProductKind> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseCharacteristicProductKind::class, NetworkCharacteristicProductKind::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCharacteristicProductKind::class, DomainCharacteristicProductKind::class).transform(this)
}

@Entity(
    tableName = "3_7_characteristics_component_kinds",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCharacteristic::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("charId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponentKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseCharacteristicComponentKind(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val charId: Long,
    @ColumnInfo(index = true)
    val componentKindId: Long
) : DatabaseBaseModel<NetworkCharacteristicComponentKind, DomainCharacteristicComponentKind> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseCharacteristicComponentKind::class, NetworkCharacteristicComponentKind::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCharacteristicComponentKind::class, DomainCharacteristicComponentKind::class).transform(this)
}

@Entity(
    tableName = "5_7_characteristic_component_stage_kinds",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCharacteristic::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("charId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseComponentStageKind::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentStageKindId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseCharacteristicComponentStageKind(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val charId: Long,
    @ColumnInfo(index = true)
    val componentStageKindId: Long
) : DatabaseBaseModel<NetworkCharacteristicComponentStageKind, DomainCharacteristicComponentStageKind> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseCharacteristicComponentStageKind::class, NetworkCharacteristicComponentStageKind::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCharacteristicComponentStageKind::class, DomainCharacteristicComponentStageKind::class).transform(this)
}
