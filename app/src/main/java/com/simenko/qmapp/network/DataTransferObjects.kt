package com.simenko.qmapp.network

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simenko.qmapp.database.DatabaseCompanies
import com.simenko.qmapp.database.DatabaseDepartment
import com.simenko.qmapp.database.DatabaseTeamMember
import com.simenko.qmapp.domain.DomainDepartment
import com.simenko.qmapp.domain.DomainTeamMember
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

@JsonClass(generateAdapter = true)
data class NetworkCompanies constructor(
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

fun List<NetworkTeamMembers>.asDatabaseModelTm(): List<DatabaseTeamMember> {
    return map {
        DatabaseTeamMember(
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

fun List<NetworkCompanies>.asDatabaseModelCm(): List<DatabaseCompanies> {
    return map {
        DatabaseCompanies(
            id = it.id,
            companyName = it.companyName,
            companyCountry = it.companyCountry,
            companyCity = it.companyCity,
            companyAddress = it.companyAddress,
            companyPhoneNo = it.companyPhoneNo,
            companyPostCode = it.companyPostCode,
            companyRegion = it.companyRegion,
            companyOrder = it.companyOrder,
            companyIndustrialClassification = it.companyIndustrialClassification,
            companyManagerId = it.companyManagerId
        )
    }
}

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

fun List<NetworkTeamMembers>.asDomainModelTm(): List<DomainTeamMember> {
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