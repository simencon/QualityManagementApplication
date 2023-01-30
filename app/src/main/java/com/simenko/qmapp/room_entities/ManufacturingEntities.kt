package com.simenko.qmapp.room_entities

import androidx.room.*

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
            entity = DatabaseCompanies::class,
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

@Entity(tableName = "8_team_members")
data class DatabaseTeamMember constructor(
    @PrimaryKey
    var id: Int,
    var departmentId: Int,
    var department: String,
    var email: String? = null,
    var fullName: String,
    var jobRole: String,
    var roleLevelId: Int,
    var passWord: String? = null,
    var companyId: Int
)

@Entity(tableName = "0_companies")
data class DatabaseCompanies constructor(
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
    var companyManagerId: Int
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
        entity = DatabaseCompanies::class,
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val companies: List<DatabaseCompanies>
)