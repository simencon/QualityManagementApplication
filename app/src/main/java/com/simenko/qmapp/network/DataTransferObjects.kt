package com.simenko.qmapp.network

import com.simenko.qmapp.database.DatabaseDepartment
import com.simenko.qmapp.database.DatabaseTeamMembers
import com.simenko.qmapp.domain.DomainDepartment
import com.simenko.qmapp.domain.DomainTeamMembers
import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)
//data class NetworkDepartmentContainer(val departments: List<NetworkDepartment>)

@JsonClass(generateAdapter = true)
data class NetworkDepartment(
    val id: Int,
    val depAbbr: String?,
    val depName: String?,
    val depManager: Int?,
    val depOrganization: String?,
    val depOrder: Int?,
    val companyId: Int?
)

@JsonClass(generateAdapter = true)
data class NetworkTeamMembers(
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

fun List<NetworkDepartment>.asDomainModel(): List<DomainDepartment> {
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

fun List<NetworkDepartment>.asDatabaseModel(): List<DatabaseDepartment> {
    return map {
        DatabaseDepartment(
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

fun List<NetworkTeamMembers>.asDomainModelTm(): List<DomainTeamMembers> {
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

fun List<NetworkTeamMembers>.asDatabaseModelTm(): List<DatabaseTeamMembers> {
    return map {
        DatabaseTeamMembers(
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