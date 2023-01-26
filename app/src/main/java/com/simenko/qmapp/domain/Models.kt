package com.simenko.qmapp.domain

interface ListOfItems {
    fun  selectedRecord (): String
}

data class DomainDepartment(
    val id: Int,
    val depAbbr: String?,
    val depName: String?,
    val depManager: Int?,
    val depOrganization: String?,
    val depOrder: Int?,
    val companyId: Int?
): ListOfItems {
    override fun selectedRecord(): String {
        return "$depName ($depAbbr)"
    }
}

data class DomainTeamMembers (
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
