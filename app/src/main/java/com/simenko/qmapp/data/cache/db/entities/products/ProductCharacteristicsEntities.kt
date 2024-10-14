package com.simenko.qmapp.data.cache.db.entities.products

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.simenko.qmapp.data.cache.db.contract.DatabaseBaseModel
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.products.*
import com.simenko.qmapp.data.remote.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer

@Entity(
    tableName = "10_1_d_element_ish_model",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productLineId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseCharGroup(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val productLineId: ID,
    val ishElement: String?
) : DatabaseBaseModel<NetworkCharGroup, DomainCharGroup, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseCharGroup::class, NetworkCharGroup::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCharGroup::class, DomainCharGroup::class).transform(this)

    @DatabaseView(
        viewName = "characteristic_group_complete",
        value = "SELECT * FROM `10_1_d_element_ish_model` ORDER BY id;"
    )
    data class DatabaseCharGroupComplete(
        @Embedded
        val charGroup: DatabaseCharGroup,
        @Relation(
            entity = DatabaseProductLine.DatabaseProductLineComplete::class,
            parentColumn = "productLineId",
            entityColumn = "id"
        )
        val productLine: DatabaseProductLine.DatabaseProductLineComplete
    ) : DatabaseBaseModel<Any?, DomainCharGroup.DomainCharGroupComplete, ID, ID> {
        override fun getRecordId() = charGroup.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainCharGroup.DomainCharGroupComplete(
            charGroup = charGroup.toDomainModel(),
            productLine = productLine.toDomainModel()
        )
    }
}

@Entity(
    tableName = "0_ish_sub_characteristics",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCharGroup::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("charGroupId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseCharSubGroup(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val charGroupId: ID,
    val ishElement: String? = null,
    val measurementGroupRelatedTime: Double? = null
) : DatabaseBaseModel<NetworkCharSubGroup, DomainCharSubGroup, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseCharSubGroup::class, NetworkCharSubGroup::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCharSubGroup::class, DomainCharSubGroup::class).transform(this)

    @DatabaseView(
        viewName = "characteristic_sub_group_complete",
        value = "SELECT * FROM `0_ish_sub_characteristics` ORDER BY id;"
    )
    data class DatabaseCharSubGroupComplete(
        @Embedded
        val charSubGroup: DatabaseCharSubGroup,
        @Relation(
            entity = DatabaseCharGroup.DatabaseCharGroupComplete::class,
            parentColumn = "charGroupId",
            entityColumn = "id"
        )
        val charGroup: DatabaseCharGroup.DatabaseCharGroupComplete
    ) : DatabaseBaseModel<Any?, DomainCharSubGroup.DomainCharSubGroupComplete, ID, ID> {
        override fun getRecordId() = charSubGroup.id
        override fun toNetworkModel() = charSubGroup.charGroupId
        override fun toDomainModel() = DomainCharSubGroup.DomainCharSubGroupComplete(
            charSubGroup = charSubGroup.toDomainModel(),
            charGroup = charGroup.toDomainModel()
        )
    }
}


@Entity(
    tableName = "7_characteristics",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCharSubGroup::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("ishSubCharId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class DatabaseCharacteristic(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val ishSubCharId: ID,
    val charOrder: Int? = null,
    val charDesignation: String? = null,
    val charDescription: String? = null,
    val sampleRelatedTime: Double? = null,
    val measurementRelatedTime: Double? = null
) : DatabaseBaseModel<NetworkCharacteristic, DomainCharacteristic, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseCharacteristic::class, NetworkCharacteristic::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCharacteristic::class, DomainCharacteristic::class).transform(this)

    @DatabaseView(
        viewName = "characteristicWithParents",
        value = """
        select cg.productLineId as productLineId, cg.id as groupId, cg.ishElement as groupDescription,
        csg.id as subGroupId, csg.ishElement as subGroupDescription, csg.measurementGroupRelatedTime as subGroupRelatedTime, 
        c.id as charId, c.charOrder as charOrder, c.charDesignation as charDesignation, c.charDescription as charDescription, c.sampleRelatedTime, c.measurementRelatedTime
        from `7_characteristics` as c
        inner join `0_ish_sub_characteristics` as csg on c.ishSubCharId = csg.id
        inner join `10_1_d_element_ish_model` as cg on csg.charGroupId = cg.id
        order by cg.id, csg.id, c.charOrder
        """
    )
    data class DatabaseCharacteristicWithParents(
        val productLineId: ID,
        val groupId: ID,
        val groupDescription: String,
        val subGroupId: ID,
        val subGroupDescription: String,
        val subGroupRelatedTime: Double,
        val charId: ID,
        val charOrder: Int,
        val charDesignation: String?,
        val charDescription: String,
        val sampleRelatedTime: Double,
        val measurementRelatedTime: Double
    ) : DatabaseBaseModel<Any?, DomainCharacteristic.DomainCharacteristicWithParents, ID, ID> {
        override fun getRecordId() = charId
        override fun toNetworkModel() = null
        override fun toDomainModel() = ObjectTransformer(DatabaseCharacteristicWithParents::class, DomainCharacteristic.DomainCharacteristicWithParents::class).transform(this)
    }

    @DatabaseView(
        viewName = "characteristic_complete",
        value = "SELECT * FROM `7_characteristics` ORDER BY charOrder;"
    )
    data class DatabaseCharacteristicComplete(
        @Embedded
        val characteristic: DatabaseCharacteristic,
        @Relation(
            entity = DatabaseCharSubGroup.DatabaseCharSubGroupComplete::class,
            parentColumn = "ishSubCharId",
            entityColumn = "id"
        )
        val characteristicSubGroup: DatabaseCharSubGroup.DatabaseCharSubGroupComplete
    ) : DatabaseBaseModel<Any?, DomainCharacteristic.DomainCharacteristicComplete, ID, ID> {
        override fun getRecordId() = characteristic.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainCharacteristic.DomainCharacteristicComplete(
            characteristic = this.characteristic.toDomainModel(),
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
data class DatabaseMetrix(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val charId: ID,
    val metrixOrder: Int? = null,
    val metrixDesignation: String? = null,
    val metrixDescription: String? = null,
    val units: String? = null
) : DatabaseBaseModel<NetworkMetrix, DomainMetrix, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseMetrix::class, NetworkMetrix::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseMetrix::class, DomainMetrix::class).transform(this)

    @DatabaseView(
        viewName = "metricWithParents",
        value = """
        select cg.id as groupId, cg.ishElement as groupDescription,
        csg.id as subGroupId, csg.ishElement as subGroupDescription, csg.measurementGroupRelatedTime as subGroupRelatedTime,
        c.id as charId, c.charOrder as charOrder, c.charDesignation as charDesignation, c.charDescription as charDescription, c.sampleRelatedTime, c.measurementRelatedTime,
        m.id as metricId, m.metrixOrder as metricOrder, m.metrixDesignation as metricDesignation, m.metrixDescription as metricDescription, m.units as metricUnits
        from `8_metrixes` as m
        inner join `7_characteristics` as c on m.charId = c.id
        inner join `0_ish_sub_characteristics` as csg on c.ishSubCharId = csg.id
        inner join `10_1_d_element_ish_model` as cg on csg.charGroupId = cg.id
        order by cg.id, csg.id, c.charOrder, m.metrixOrder
        """
    )
    data class DatabaseMetricWithParents(
        val groupId: ID,
        val groupDescription: String,
        val subGroupId: ID,
        val subGroupDescription: String,
        val subGroupRelatedTime: Double,
        val charId: ID,
        val charOrder: Int,
        val charDesignation: String?,
        val charDescription: String,
        val sampleRelatedTime: Double,
        val measurementRelatedTime: Double,
        val metricId: ID,
        val metricOrder: Int,
        val metricDesignation: String?,
        val metricDescription: String?,
        val metricUnits: String,
    ) : DatabaseBaseModel<Any?, DomainMetrix.DomainMetricWithParents, ID, ID> {
        override fun getRecordId() = charId
        override fun toNetworkModel() = null
        override fun toDomainModel() = ObjectTransformer(DatabaseMetricWithParents::class, DomainMetrix.DomainMetricWithParents::class).transform(this)
    }
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
    val id: ID,
    @ColumnInfo(index = true)
    val charId: ID,
    @ColumnInfo(index = true)
    val productKindId: ID
) : DatabaseBaseModel<NetworkCharacteristicProductKind, DomainCharacteristicProductKind, ID, ID> {
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
    val id: ID,
    @ColumnInfo(index = true)
    val charId: ID,
    @ColumnInfo(index = true)
    val componentKindId: ID
) : DatabaseBaseModel<NetworkCharacteristicComponentKind, DomainCharacteristicComponentKind, ID, ID> {
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
    val id: ID,
    @ColumnInfo(index = true)
    val charId: ID,
    @ColumnInfo(index = true)
    val componentStageKindId: ID
) : DatabaseBaseModel<NetworkCharacteristicComponentStageKind, DomainCharacteristicComponentStageKind, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseCharacteristicComponentStageKind::class, NetworkCharacteristicComponentStageKind::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCharacteristicComponentStageKind::class, DomainCharacteristicComponentStageKind::class).transform(this)
}

@DatabaseView(
    viewName = "item_kind_characteristic",
    value = """
        select ('p'||cpk.id) as fId, cpk.id as id, cpk.charId as charId, ('p'||cpk.productKindId) as itemKindFId, cpk.productKindId as itemKindId from `1_7_characteristics_product_kinds` as cpk
        union all
        select ('c'||cck.id) as fId, cck.id as id, cck.charId as charId, ('c'||cck.componentKindId) as itemKindFId, cck.componentKindId as itemKindId from `3_7_characteristics_component_kinds` as cck
        union all
        select ('s'||csk.id) as fId, csk.id as id, csk.charId as charId, ('s'||csk.componentStageKindId) as itemKindFId, csk.componentStageKindId as itemKindId from `5_7_characteristic_component_stage_kinds` as csk
        """
)
data class DatabaseCharacteristicItemKind(
    val fId: String,
    val id: ID,
    val charId: ID,
    val itemKindFId: String,
    val itemKindId: ID
) : DatabaseBaseModel<Any?, DomainCharacteristicItemKind, String, String> {
    override fun getRecordId() = fId
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseCharacteristicItemKind::class, DomainCharacteristicItemKind::class).transform(this)

    data class DatabaseCharacteristicItemKindComplete(
        @Embedded
        val characteristicItemKind: DatabaseCharacteristicItemKind,
        @Relation(
            entity = DatabaseCharacteristic.DatabaseCharacteristicWithParents::class,
            parentColumn = "charId",
            entityColumn = "charId"
        )
        val characteristicWithParents: DatabaseCharacteristic.DatabaseCharacteristicWithParents
    ) : DatabaseBaseModel<Any?, DomainCharacteristicItemKind.DomainCharacteristicItemKindComplete, String, String> {
        override fun getRecordId() = characteristicItemKind.fId
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainCharacteristicItemKind.DomainCharacteristicItemKindComplete(
            characteristicItemKind = this.characteristicItemKind.toDomainModel(),
            characteristicWithParents = this.characteristicWithParents.toDomainModel()
        )
    }
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
    val id: ID,
    @ColumnInfo(index = true)
    val metrixId: ID,
    @ColumnInfo(index = true)
    val versionId: ID,
    val nominal: Float?,
    val lsl: Float?,
    val usl: Float?,
    val isActual: Boolean
) : DatabaseBaseModel<NetworkProductTolerance, DomainProductTolerance, ID, ID> {
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
    val id: ID,
    @ColumnInfo(index = true)
    val metrixId: ID,
    @ColumnInfo(index = true)
    val versionId: ID,
    val nominal: Float?,
    val lsl: Float?,
    val usl: Float?,
    val isActual: Boolean
) : DatabaseBaseModel<NetworkComponentTolerance, DomainComponentTolerance, ID, ID> {
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
            entity = DatabaseComponentStageVersion::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("versionId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class DatabaseComponentInStageTolerance(
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    @ColumnInfo(index = true)
    val metrixId: ID,
    @ColumnInfo(index = true)
    val versionId: ID,
    val nominal: Float?,
    val lsl: Float?,
    val usl: Float?,
    val isActual: Boolean
) : DatabaseBaseModel<NetworkComponentInStageTolerance, DomainComponentInStageTolerance, ID, ID> {
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
    val id: ID,
    val fId: String,
    val metrixId: ID,
    val versionId: ID,
    val fVersionId: String,
    val nominal: Float?,
    val lsl: Float?,
    val usl: Float?,
    val isActual: Boolean
) : DatabaseBaseModel<Any?, DomainItemTolerance, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseItemTolerance::class, DomainItemTolerance::class).transform(this)

    data class DatabaseItemToleranceComplete(
        @Embedded
        val itemTolerance: DatabaseItemTolerance,
        @Relation(
            entity = DatabaseMetrix.DatabaseMetricWithParents::class,
            parentColumn = "metrixId",
            entityColumn = "metricId"
        )
        val metricWithParents: DatabaseMetrix.DatabaseMetricWithParents
    ) : DatabaseBaseModel<Any?, DomainItemTolerance.DomainItemToleranceComplete, ID, ID> {
        override fun getRecordId() = itemTolerance.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainItemTolerance.DomainItemToleranceComplete(
            itemTolerance = itemTolerance.toDomainModel(),
            metricWithParents = metricWithParents.toDomainModel()
        )
    }
}