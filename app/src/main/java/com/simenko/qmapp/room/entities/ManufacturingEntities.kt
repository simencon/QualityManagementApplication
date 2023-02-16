package com.simenko.qmapp.room.entities

import androidx.room.*

@Entity(tableName = "0_position_levels")
data class DatabasePositionLevel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var levelDescription: String
)

@Entity(
    tableName = "8_team_members",
    foreignKeys = [
//        ToDo Cannot be used as foreign key because appears before Departments
//        ForeignKey(
//            entity = DatabaseDepartment::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("departmentId"),
//            onDelete = ForeignKey.NO_ACTION,
//            onUpdate = ForeignKey.NO_ACTION
//        ),
        ForeignKey(
            entity = DatabasePositionLevel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("roleLevelId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
//        ToDo Cannot be used as foreign key because appears before Companies
//        ForeignKey(
//            entity = DatabaseCompany::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("companyId"),
//            onDelete = ForeignKey.NO_ACTION,
//            onUpdate = ForeignKey.NO_ACTION
//        )
    ]
)
data class DatabaseTeamMember constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var departmentId: Int,
    var department: String,
    var email: String? = null,
    var fullName: String,
    var jobRole: String,
    @ColumnInfo(index = true)
    var roleLevelId: Int,
    var passWord: String? = null,
    @ColumnInfo(index = true)
    var companyId: Int
)

@Entity(
    tableName = "0_companies",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseTeamMember::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("companyManagerId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseCompany constructor(
    @PrimaryKey
    var id: Int,
    var companyName: String? = null,
    var companyCountry: String? = null,
    var companyCity: String? = null,
    var companyAddress: String? = null,
    var companyPhoneNo: String? = null,
    var companyPostCode: String? = null,
    var companyRegion: String? = null,
    var companyOrder: Int,
    var companyIndustrialClassification: String? = null,
    @ColumnInfo(index = true)
    var companyManagerId: Int
)

@Entity(
    tableName = "10_departments",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseTeamMember::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("depManager"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseCompany::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("companyId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class DatabaseDepartment constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val depAbbr: String?,
    val depName: String?,
    @ColumnInfo(index = true)
    val depManager: Int?,
    val depOrganization: String?,
    val depOrder: Int?,
    @ColumnInfo(index = true)
    val companyId: Int?
)

@Entity(
    tableName = "11_sub_departments",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("depId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseSubDepartment(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var depId: Int,
    var subDepAbbr: String? = null,
    var subDepDesignation: String? = null,
    var subDepOrder: Int? = null
)

@Entity(
    tableName = "12_manufacturing_channels",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseSubDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subDepId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseManufacturingChannel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var subDepId: Int,
    var channelAbbr: String? = null,
    var channelDesignation: String? = null,
    var channelOrder: Int? = null
)

@Entity(
    tableName = "13_manufacturing_lines",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingChannel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseManufacturingLine(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var chId: Int,
    var lineAbbr: String,
    var lineDesignation: String,
    var lineOrder: Int
)

@Entity(
    tableName = "14_manufacturing_operations",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lineId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseManufacturingOperation(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var lineId: Int,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int
)

data class DatabaseDepartmentsDetailed(
    @Embedded
    val departments: DatabaseDepartment,
    @Relation(
        entity = DatabaseTeamMember::class,
        parentColumn = "depManager",
        entityColumn = "id"
    )
    val depManagerDetails: List<DatabaseTeamMember>,
    @Relation(
        entity = DatabaseCompany::class,
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val companies: List<DatabaseCompany>
)