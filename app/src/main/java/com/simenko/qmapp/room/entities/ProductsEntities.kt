package com.simenko.qmapp.room.entities

import androidx.room.*

//ToDo - add products/components/raw material related entities with versions and specifications (9tbl.)

@Entity(tableName = "10_1_d_element_ish_model")
data class DatabaseElementIshModel constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var ishElement: String? = null
)

@Entity(tableName = "0_ish_sub_characteristics")
data class DatabaseIshSubCharacteristic constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var ishElement: String? = null,
    var measurementGroupRelatedTime: Double? = null
)

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
)

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
)

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
    var metrixDescription: String? = null
)

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
)

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
)


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
)

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
)

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
)

@Entity(tableName = "0_versions_status")
data class DatabaseVersionStatus(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var statusDescription: String?
)

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
)


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
)

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
)

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
)

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
)

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
)

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
)

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
)

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
)

@DatabaseView(
    viewName = "products_complete",
    value = "SELECT p.* FROM `2_products` p\n" +
            "JOIN `0_keys` k on k.id = p.keyId\n" +
            "JOIN `13_1_products_to_lines` ptl on p.id = ptl.productId;"
)
data class DatabaseProductComplete(
    @Embedded
    val product: DatabaseProduct,
    @Relation(
        parentColumn = "keyId",
        entityColumn = "id"
    )
    val key: DatabaseKey,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val productToLines: List<DatabaseProductToLine>
)

@DatabaseView(
    viewName = "components_complete",
    value = "SELECT c.* FROM `4_components` c\n" +
            "JOIN `0_keys` k on k.id = c.keyId\n" +
            "JOIN `13_3_components_to_lines` ctl on c.ID = ctl.componentID;"
)
data class DatabaseComponentComplete(
    @Embedded
    val component: DatabaseComponent,
    @Relation(
        parentColumn = "keyId",
        entityColumn = "id"
    )
    val key: DatabaseKey,
    @Relation(
        parentColumn = "id",
        entityColumn = "componentId"
    )
    val componentToLines: List<DatabaseComponentToLine>
)

@DatabaseView(
    viewName = "components_in_stage_complete",
    value = "SELECT s.* FROM `6_components_in_stages` s\n" +
            "JOIN `0_keys` k on k.id = s.keyId\n" +
            "JOIN `13_5_component_in_stages_to_lines` stl on s.ID = stl.componentInStageId;"
)
data class DatabaseComponentInStageComplete(
    @Embedded
    val componentInStage: DatabaseComponentInStage,
    @Relation(
        parentColumn = "keyId",
        entityColumn = "id"
    )
    val key: DatabaseKey,
    @Relation(
        parentColumn = "id",
        entityColumn = "componentInStageId"
    )
    val componentInStageToLines: List<DatabaseComponentInStageToLine>
)

@DatabaseView(
    viewName = "product_versions_complete",
    value = "SELECT pv.* FROM `9_products_versions` pv\n" +
            "         JOIN `0_versions_status` vs on vs.id = pv.statusId\n" +
            "         JOIN products_complete pc on pv.productId = pc.id;"
)
data class DatabaseProductVersionComplete(
    @Embedded
    val productVersion: DatabaseProductVersion,
    @Relation(
        parentColumn = "statusId",
        entityColumn = "id"
    )
    val versionStatus: DatabaseVersionStatus,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val productComplete: DatabaseProductComplete
)

@DatabaseView(
    viewName = "component_versions_complete",
    value = "SELECT cv.* FROM `10_components_versions` cv\n" +
            "         JOIN `0_versions_status` vs on vs.id = cv.statusId\n" +
            "         JOIN components_complete cc on cv.componentID = cc.id;"
)
data class DatabaseComponentVersionComplete(
    @Embedded
    val componentVersion: DatabaseComponentVersion,
    @Relation(
        parentColumn = "statusId",
        entityColumn = "id"
    )
    val versionStatus: DatabaseVersionStatus,
    @Relation(
        parentColumn = "componentId",
        entityColumn = "id"
    )
    val componentComplete: DatabaseComponentComplete
)

@DatabaseView(
    viewName = "component_in_stage_versions_complete",
    value = "SELECT sv.* FROM `11_component_in_stage_versions` sv\n" +
            "         JOIN `0_versions_status` vs on vs.id = sv.statusId\n" +
            "         JOIN components_in_stage_complete sc on sv.componentInStageId = sc.id;"
)
data class DatabaseComponentInStageVersionComplete(
    @Embedded
    val componentInStageVersion: DatabaseComponentInStageVersion,
    @Relation(
        parentColumn = "statusId",
        entityColumn = "id"
    )
    val versionStatus: DatabaseVersionStatus,
    @Relation(
        parentColumn = "componentInStageId",
        entityColumn = "id"
    )
    val componentInStageComplete: DatabaseComponentInStageComplete
)
