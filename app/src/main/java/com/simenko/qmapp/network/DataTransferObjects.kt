package com.simenko.qmapp.network

import com.simenko.qmapp.database.DatabaseDepartment
import com.simenko.qmapp.domain.DomainDepartment
import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)
//data class NetworkDepartmentContainer(val departments: List<NetworkDepartment>)

@JsonClass(generateAdapter = true)
data class NetworkDepartment (
    val id: Int,
    val depAbbr: String?,
    val depName: String?,
    val depManager: Int?,
    val depOrganization: String?,
    val depOrder: Int?,
    val companyId: Int?
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