package com.simenko.qmapp.domain

import com.simenko.qmapp.room_entities.DatabaseCompany
import com.simenko.qmapp.room_entities.DatabaseDepartment
import com.simenko.qmapp.room_entities.DatabaseDepartmentsDetailed
import com.simenko.qmapp.room_entities.DatabaseTeamMember
import com.simenko.qmapp.utils.ListTransformer

interface ListOfItems {
    fun selectedRecord(): String
}

data class DomainPositionLevel(
    var id: Int,
    var levelDescription: String
)

data class DomainTeamMember(
    var id: Int,
    var departmentId: Int,
    var department: String,
    var email: String? = null,
    var fullName: String,
    var jobRole: String,
    var roleLevelId: Int,
    var passWord: String? = null,
    var companyId: Int
) : ListOfItems {
    override fun selectedRecord(): String {
        return "$fullName ($department)"
    }
}

data class DomainCompany(
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

data class DomainDepartment(
    val id: Int,
    val depAbbr: String?,
    val depName: String?,
    val depManager: Int?,
    val depOrganization: String?,
    val depOrder: Int?,
    val companyId: Int?
) : ListOfItems {
    override fun selectedRecord(): String {
        return "$depName ($depAbbr)"
    }
}

data class DomainSubDepartment(
    var id: Int,
    var depId: Int,
    var subDepAbbr: String? = null,
    var subDepDesignation: String? = null,
    var subDepOrder: String? = null
)

data class DomainManufacturingChannel(
    var id: Int,
    var subDepId: Int,
    var channelAbbr: String? = null,
    var channelDesignation: String? = null,
    var channelOrder: Int? = null
)

data class DomainManufacturingLine(
    var id: Int,
    var chId: Int,
    var lineAbbr: String,
    var lineDesignation: String,
    var lineOrder: Int
)

data class DomainManufacturingOperation(
    var id: Int,
    var lineId: Int,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int
)

data class DomainDepartmentsDetailed(
    val departments: DomainDepartment,
    val depManagerDetails: List<DomainTeamMember>,
    val companies: List<DomainCompany>
): ListOfItems {
    override fun selectedRecord(): String {
        return "${depManagerDetails[0].fullName} (${departments.depName})"
    }
}

fun List<DatabaseDepartmentsDetailed>.asDepartmentsDetailedDomainModel(): List<DomainDepartmentsDetailed> {
    return map {
        DomainDepartmentsDetailed(
            departments = ListTransformer(
                DatabaseDepartment::class,
                DomainDepartment::class
            ).transform(it.departments),
            depManagerDetails = ListTransformer(
                it.depManagerDetails,
                DatabaseTeamMember::class,
                DomainTeamMember::class
            ).generateList(),
            companies = ListTransformer(
                it.companies,
                DatabaseCompany::class,
                DomainCompany::class
            ).generateList()
        )
    }
}
