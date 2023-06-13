package com.simenko.qmapp.room.entities

import androidx.room.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.room.DatabaseBaseModel
import com.simenko.qmapp.utils.ObjectTransformer

@Entity(tableName = "10_1_d_element_ish_model")
data class DatabaseElementIshModel constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var ishElement: String? = null
) : DatabaseBaseModel<NetworkElementIshModel, DomainElementIshModel> {
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
    override fun toNetworkModel() = ObjectTransformer(DatabaseIshSubCharacteristic::class, NetworkIshSubCharacteristic::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseIshSubCharacteristic::class, DomainIshSubCharacteristic::class).transform(this)
}

@Entity(
    tableName = "0_manufacturing_project",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCompany::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("companyId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseManufacturingProject(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var companyId: Int,
    var factoryLocationDep: Int? = null,
    var factoryLocationDetails: String? = null,
    var customerName: String? = null,
    var team: Int? = null,
    var modelYear: String? = null,
    var projectSubject: String? = null,
    var startDate: String? = null,
    var revisionDate: String? = null,
    var refItem: String? = null,
    var pfmeaNum: String? = null,
    var processOwner: Int? = null,
    var confLevel: Int? = null
) : DatabaseBaseModel<NetworkManufacturingProject, DomainManufacturingProject> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseManufacturingProject::class, NetworkManufacturingProject::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseManufacturingProject::class, DomainManufacturingProject::class).transform(this)
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
    override fun toNetworkModel() = ObjectTransformer(DatabaseCharacteristic::class, NetworkCharacteristic::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCharacteristic::class, DomainCharacteristic::class).transform(this)
}

@DatabaseView(
    viewName = "characteristic_complete",
    value = "SELECT * FROM `7_characteristics` ORDER BY charOrder;"
)
data class DatabaseCharacteristicComplete(
    @Embedded
    val characteristic: DatabaseCharacteristic,
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
) : DatabaseBaseModel<Any?, DomainCharacteristicComplete> {
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainCharacteristicComplete(
        characteristic = characteristic.toDomainModel(),
        characteristicGroup = characteristicGroup.toDomainModel(),
        characteristicSubGroup = characteristicSubGroup.toDomainModel()
    )
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
    override fun toNetworkModel() = ObjectTransformer(DatabaseMetrix::class, NetworkMetrix::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseMetrix::class, DomainMetrix::class).transform(this)
}

@Entity(
    tableName = "0_keys",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingProject::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("projectId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseKey(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var projectId: Int?,
    var componentKey: String?,
    var componentKeyDescription: String?
) : DatabaseBaseModel<NetworkKey, DomainKey> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseKey::class, NetworkKey::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseKey::class, DomainKey::class).transform(this)
}

@Entity(
    tableName = "0_products_bases",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingProject::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("projectId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseProductBase(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var projectId: Int?,
    var componentBaseDesignation: String?
) : DatabaseBaseModel<NetworkProductBase, DomainProductBase> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductBase::class, NetworkProductBase::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductBase::class, DomainProductBase::class).transform(this)
}


@Entity(
    tableName = "2_products",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProductBase::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productBaseId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseProduct(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var productBaseId: Int?,
    @ColumnInfo(index = true)
    var keyId: Int?,
    var productDesignation: String?
) : DatabaseBaseModel<NetworkProduct, DomainProduct> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseProduct::class, NetworkProduct::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProduct::class, DomainProduct::class).transform(this)
}

@Entity(
    tableName = "4_components",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseComponent(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var keyId: Int?,
    var componentDesignation: String?,
    var ifAny: Int?
) : DatabaseBaseModel<NetworkComponent, DomainComponent> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponent::class, NetworkComponent::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponent::class, DomainComponent::class).transform(this)
}

@Entity(
    tableName = "6_components_in_stages",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseKey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("keyId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseComponentInStage(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var keyId: Int?,
    var componentInStageDescription: String?,
    var ifAny: Int?
) : DatabaseBaseModel<NetworkComponentInStage, DomainComponentInStage> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentInStage::class, NetworkComponentInStage::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentInStage::class, DomainComponentInStage::class).transform(this)
}

@Entity(tableName = "0_versions_status")
data class DatabaseVersionStatus(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var statusDescription: String?
) : DatabaseBaseModel<NetworkVersionStatus, DomainVersionStatus> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseVersionStatus::class, NetworkVersionStatus::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseVersionStatus::class, DomainVersionStatus::class).transform(this)
}

@Entity(
    tableName = "9_products_versions",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseProduct::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseVersionStatus::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("statusId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseProductVersion(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var productId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    @ColumnInfo(index = true)
    var statusId: Int?,
    var isDefault: Boolean
) : DatabaseBaseModel<NetworkProductVersion, DomainProductVersion> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseProductVersion::class, NetworkProductVersion::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseProductVersion::class, DomainProductVersion::class).transform(this)
}


@Entity(
    tableName = "10_components_versions",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponent::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseVersionStatus::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("statusId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseComponentVersion(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var componentId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    @ColumnInfo(index = true)
    var statusId: Int?,
    var isDefault: Boolean
) : DatabaseBaseModel<NetworkComponentVersion, DomainComponentVersion> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentVersion::class, NetworkComponentVersion::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentVersion::class, DomainComponentVersion::class).transform(this)
}

@Entity(
    tableName = "11_component_in_stage_versions",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseComponentInStage::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("componentInStageId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseVersionStatus::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("statusId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseComponentInStageVersion(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var componentInStageId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    @ColumnInfo(index = true)
    var statusId: Int?,
    var isDefault: Boolean
) : DatabaseBaseModel<NetworkComponentInStageVersion, DomainComponentInStageVersion> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentInStageVersion::class, NetworkComponentInStageVersion::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentInStageVersion::class, DomainComponentInStageVersion::class).transform(this)
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
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseItemTolerance::class, DomainItemTolerance::class).transform(this)
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
    var id: Int,
    @ColumnInfo(index = true)
    var lineId: Int,
    @ColumnInfo(index = true)
    var productId: Int
) : DatabaseBaseModel<NetworkProductToLine, DomainProductToLine> {
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
    override fun toNetworkModel() = ObjectTransformer(DatabaseComponentInStageToLine::class, NetworkComponentInStageToLine::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseComponentInStageToLine::class, DomainComponentInStageToLine::class).transform(this)
}

@DatabaseView(
    viewName = "items",
    value = "SELECT p.id as id, ('p'|| p.Id) as fId, p.keyId, p.productDesignation as itemDesignation FROM `2_products` AS p " +
            "UNION ALL " +
            "SELECT c.id as id, ('c'|| c.Id) as fId, c.keyId, c.componentDesignation as itemDesignation FROM `4_components` AS c " +
            "UNION ALL " +
            "SELECT s.id as id, ('s'|| s.Id) as fId, s.keyId, s.componentInStageDescription as itemDesignation FROM `6_components_in_stages` AS s;"
)
data class DatabaseItem(
    val id: Int,
    val fId: String,
    val keyId: Int?,
    val itemDesignation: String?
) : DatabaseBaseModel<Any?, DomainItem> {
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseItem::class, DomainItem::class).transform(this)
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
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseItemToLine::class, DomainItemToLine::class).transform(this)
}

@DatabaseView(
    viewName = "items_complete",
    value = "SELECT * FROM `items`"
)
data class DatabaseItemComplete(
    @Embedded
    val item: DatabaseItem,
    @Relation(
        parentColumn = "keyId",
        entityColumn = "id"
    )
    val key: DatabaseKey,
    @Relation(
        parentColumn = "fId",
        entityColumn = "fItemId"
    )
    val itemToLines: List<DatabaseItemToLine>
) : DatabaseBaseModel<Any?, DomainItemComplete> {
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainItemComplete(
        item = item.toDomainModel(),
        key = key.toDomainModel(),
        itemToLines = itemToLines.map { it.toDomainModel() }
    )
}

@DatabaseView(
    viewName = "item_versions",
    value = "SELECT pv.id as id, ('p'|| pv.id) as fId, pv.productId as itemId, ('p'|| pv.productId) as fItemId, pv.versionDescription, pv.versionDate, pv.statusId, pv.isDefault FROM `9_products_versions` AS pv " +
            "UNION ALL " +
            "SELECT cv.id as id, ('c'|| cv.id) as fId, cv.componentId as itemId, ('c'|| cv.componentId) as fItemId, cv.versionDescription, cv.versionDate, cv.statusId, cv.isDefault FROM `10_components_versions` AS cv " +
            "UNION ALL " +
            "SELECT sv.id as id, ('s'|| sv.id) as fId, sv.componentInStageId as itemId, ('s'|| sv.componentInStageId) as fItemId, sv.versionDescription, sv.versionDate, sv.statusId, sv.isDefault FROM `11_component_in_stage_versions` AS sv;"
)
data class DatabaseItemVersion(
    val id: Int,
    val fId: String,
    val itemId: Int,
    val fItemId: String,
    val versionDescription: String?,
    val versionDate: String?,
    val statusId: Int?,
    val isDefault: Boolean
) : DatabaseBaseModel<Any?, DomainItemVersion> {
    override fun toNetworkModel() = null
    override fun toDomainModel() = ObjectTransformer(DatabaseItemVersion::class, DomainItemVersion::class).transform(this)
}

@DatabaseView(
    viewName = "item_versions_complete",
    value = "SELECT * FROM `item_versions`"
)
data class DatabaseItemVersionComplete(
    @Embedded
    val itemVersion: DatabaseItemVersion,
    @Relation(
        parentColumn = "statusId",
        entityColumn = "id"
    )
    val versionStatus: DatabaseVersionStatus,
    @Relation(
        parentColumn = "fItemId",
        entityColumn = "fId"
    )
    val itemComplete: DatabaseItemComplete
) : DatabaseBaseModel<Any?, DomainItemVersionComplete> {
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainItemVersionComplete(
        itemVersion = itemVersion.toDomainModel(),
        versionStatus = versionStatus.toDomainModel(),
        itemComplete = itemComplete.toDomainModel()
    )
}
