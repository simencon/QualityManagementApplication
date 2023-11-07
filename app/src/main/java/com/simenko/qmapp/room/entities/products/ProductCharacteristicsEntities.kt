package com.simenko.qmapp.room.entities.products

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.simenko.qmapp.domain.entities.products.*
import com.simenko.qmapp.retrofit.entities.products.*
import com.simenko.qmapp.room.contract.DatabaseBaseModel
import com.simenko.qmapp.utils.ObjectTransformer

@Entity(tableName = "10_1_d_element_ish_model")
data class DatabaseElementIshModel constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var ishElement: String? = null
) : DatabaseBaseModel<NetworkElementIshModel, DomainElementIshModel> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseElementIshModel::class, NetworkElementIshModel::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseElementIshModel::class, DomainElementIshModel::class).transform(this)
}

@Entity(tableName = "0_ish_sub_characteristics")
data class DatabaseIshSubCharacteristic constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var ishElement: String? = null,
    var measurementGroupRelatedTime: Double? = null
) : DatabaseBaseModel<NetworkIshSubCharacteristic, DomainIshSubCharacteristic> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseIshSubCharacteristic::class, NetworkIshSubCharacteristic::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseIshSubCharacteristic::class, DomainIshSubCharacteristic::class).transform(this)
}


@Entity(
    tableName = "7_characteristics",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseElementIshModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("ishCharId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseIshSubCharacteristic::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("ishSubChar"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseManufacturingProject::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("projectId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseCharacteristic constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var ishCharId: Int,
    var charOrder: Int? = null,
    var charDesignation: String? = null,
    var charDescription: String? = null,
    @ColumnInfo(index = true)
    var ishSubChar: Int,
    @ColumnInfo(index = true)
    var projectId: Int,
    var sampleRelatedTime: Double? = null,
    var measurementRelatedTime: Double? = null
) : DatabaseBaseModel<NetworkCharacteristic, DomainCharacteristic> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseCharacteristic::class, NetworkCharacteristic::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCharacteristic::class, DomainCharacteristic::class).transform(this)

    @DatabaseView(
        viewName = "characteristic_complete",
        value = "SELECT * FROM `7_characteristics` ORDER BY charOrder;"
    )
    data class DatabaseCharacteristicComplete(
        @Embedded
        val characteristic: DatabaseCharacteristic,
        @Relation(
            entity = DatabaseManufacturingProject::class,
            parentColumn = "projectId",
            entityColumn = "id"
        )
        val productLine: DatabaseManufacturingProject,
        @Relation(
            entity = DatabaseElementIshModel::class,
            parentColumn = "ishCharId",
            entityColumn = "id"
        )
        val characteristicGroup: DatabaseElementIshModel,
        @Relation(
            entity = DatabaseIshSubCharacteristic::class,
            parentColumn = "ishSubChar",
            entityColumn = "id"
        )
        val characteristicSubGroup: DatabaseIshSubCharacteristic
    ) : DatabaseBaseModel<Any?, DomainCharacteristic.DomainCharacteristicComplete> {
        override fun getRecordId() = characteristic.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainCharacteristic.DomainCharacteristicComplete(
            characteristic = this.characteristic.toDomainModel(),
            productLine = this.productLine.toDomainModel(),
            characteristicGroup = this.characteristicGroup.toDomainModel(),
            characteristicSubGroup = this.characteristicSubGroup.toDomainModel()
        )
    }
}

@Entity(
    tableName = "8_metrixes",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCharacteristic::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("charId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseMetrix constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var charId: Int,
    var metrixOrder: Int? = null,
    var metrixDesignation: String? = null,
    var metrixDescription: String? = null,
    var units: String? = null
) : DatabaseBaseModel<NetworkMetrix, DomainMetrix> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseMetrix::class, NetworkMetrix::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseMetrix::class, DomainMetrix::class).transform(this)
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

@Entity(
    tableName = "9_8_product_tolerances",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseMetrix::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("metrixId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseProductVersion::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("versionId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseProductTolerance(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var metrixId: Int?,
    @ColumnInfo(index = true)
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : DatabaseBaseModel<NetworkProductTolerance, DomainProductTolerance> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductTolerance::class, NetworkProductTolerance::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductTolerance::class, DomainProductTolerance::class).transform(this)
}

@Entity(
    tableName = "10_8_component_tolerances",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseMetrix::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("metrixId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseComponentVersion::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("versionId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseComponentTolerance(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var metrixId: Int?,
    @ColumnInfo(index = true)
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : DatabaseBaseModel<NetworkComponentTolerance, DomainComponentTolerance> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentTolerance::class, NetworkComponentTolerance::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentTolerance::class, DomainComponentTolerance::class).transform(this)
}

@Entity(
    tableName = "11_8_component_in_stage_tolerances",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseMetrix::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("metrixId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseComponentInStageVersion::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("versionId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseComponentInStageTolerance(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var metrixId: Int?,
    @ColumnInfo(index = true)
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : DatabaseBaseModel<NetworkComponentInStageTolerance, DomainComponentInStageTolerance> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentInStageTolerance::class, NetworkComponentInStageTolerance::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentInStageTolerance::class, DomainComponentInStageTolerance::class).transform(this)
}

@DatabaseView(
    viewName = "items_tolerances",
    value = "SELECT pt.id, ('p' || pt.id) as fId, pt.metrixId, pt.versionId, ('p' || pt.versionId) as fVersionId, pt.nominal, pt.LSL, pt.USL, pt.isActual FROM `9_8_product_tolerances` AS pt " +
            "UNION ALL " +
            "SELECT ct.id, ('c' || ct.id) as fId, ct.metrixId, ct.versionId, ('c' || ct.versionId) as fVersionId, ct.nominal, ct.LSL, ct.USL, ct.isActual FROM `10_8_component_tolerances` AS ct " +
            "UNION ALL " +
            "SELECT st.id, ('s' || st.id) as fId, st.metrixId, st.versionId, ('s' || st.versionId) as fVersionId, st.nominal, st.LSL, st.USL, st.isActual FROM `11_8_component_in_stage_tolerances` AS st;"
)
data class DatabaseItemTolerance(
    val id: String,
    val fId: String,
    val metrixId: Int,
    val versionId: Int,
    val fVersionId: String,
    val nominal: Float?,
    val lsl: Float?,
    val usl: Float?,
    val isActual: Boolean
) : DatabaseBaseModel<Any?, DomainItemTolerance> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseItemTolerance::class, DomainItemTolerance::class).transform(this)
}