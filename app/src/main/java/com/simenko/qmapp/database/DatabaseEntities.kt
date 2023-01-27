package com.simenko.qmapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.simenko.qmapp.domain.DomainDepartment
import com.simenko.qmapp.domain.DomainTeamMember

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

fun List<DatabaseDepartment>.asDomainModel(): List<DomainDepartment> {
    return map {
        DomainDepartment(
            id = it.id,
            depAbbr = it.depAbbr,
            depName = it.depName,
            depManager = it.depManager,
            depOrganization = it.depOrganization,
            depOrder = it.depOrder,
            companyId = it.companyId
        )
    }
}

fun List<DatabaseTeamMember>.asDomainModelTm(): List<DomainTeamMember> {
    return map {
        DomainTeamMember(
            id = it.id,
            departmentId = it.departmentId,
            department = it.department,
            email = it.email,
            fullName = it.fullName,
            jobRole = it.jobRole,
            roleLevelId = it.roleLevelId,
            passWord = it.passWord,
            companyId = it.companyId
        )
    }
}