package com.simenko.qmapp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

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

@Entity(tableName = "0_vesrions_status")
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
/*
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
    var isDefault: Int
)*/

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
