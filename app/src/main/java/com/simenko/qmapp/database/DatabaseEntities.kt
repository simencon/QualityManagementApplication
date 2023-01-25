package com.simenko.qmapp.database

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simenko.qmapp.domain.DomainDepartment
import com.simenko.qmapp.domain.DomainTeamMembers

@Entity(tableName = "10_departments")
data class DatabaseDepartment constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val depAbbr: String?,
    val depName: String?,
    val depManager: Int?,
    val depOrganization: String?,
    val depOrder: Int?,
    val companyId: Int?
)

@Entity(tableName = "8_team_members")
data class DatabaseTeamMembers constructor(
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

fun List<DatabaseTeamMembers>.asDomainModelTm(): List<DomainTeamMembers> {
    return map {
        DomainTeamMembers(
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