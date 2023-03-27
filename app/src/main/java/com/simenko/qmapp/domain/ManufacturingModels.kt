package com.simenko.qmapp.domain

interface ListOfItems {
    fun selectedRecord(): String
}

data class DomainPositionLevel(
    var id: Int,
    var levelDescription: String
)

data class DomainTeamMember(
    val id: Int,
    val departmentId: Int,
    val department: String,
    val email: String? = null,
    val fullName: String,
    val jobRole: String,
    val roleLevelId: Int,
    val passWord: String? = null,
    val companyId: Int,
    var detailsVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainModel(), ListOfItems {
    override fun getRecordId() = id
    override fun getParentOneId() = departmentId
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }

    override fun getName(): String {
        return fullName
    }

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
    val companyId: Int?,
    var isSelected: Boolean = false
) : DomainModel(), ListOfItems {
    override fun getRecordId() = id
    override fun getParentOneId() = companyId?:0
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }

    override fun selectedRecord(): String {
        return "$depName ($depAbbr)"
    }
}

data class DomainSubDepartment(
    var id: Int,
    var depId: Int,
    var subDepAbbr: String? = null,
    var subDepDesignation: String? = null,
    var subDepOrder: Int? = null,
    var channelsVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainModel(), ListOfItems {
    override fun getRecordId() = id
    override fun getParentOneId() = depId
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }

    override fun selectedRecord(): String {
        return "$subDepDesignation ($subDepAbbr)"
    }
}

data class DomainManufacturingChannel(
    var id: Int,
    var subDepId: Int,
    var channelAbbr: String? = null,
    var channelDesignation: String? = null,
    var channelOrder: Int? = null,
    var linesVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId() = id
    override fun getParentOneId() = subDepId
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }
}

data class DomainManufacturingLine(
    var id: Int,
    var chId: Int,
    var lineAbbr: String,
    var lineDesignation: String,
    var lineOrder: Int,
    var operationVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId() = id
    override fun getParentOneId() = chId
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }
}

data class DomainManufacturingOperation(
    var id: Int,
    var lineId: Int,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int,
    var equipment: String?,
    var detailsVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId() = id
    override fun getParentOneId() = lineId
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }
}

data class DomainOperationsFlow(
    var id: Int,
    var currentOperationId: Int,
    var previousOperationId: Int
)

data class DomainDepartmentComplete(
    val departments: DomainDepartment,
    val depManagerDetails: List<DomainTeamMember>,
    val companies: List<DomainCompany>,
    var departmentDetailsVisibility: Boolean = false
) : ListOfItems {
    override fun selectedRecord(): String {
        return "${depManagerDetails[0].fullName} (${departments.depName})"
    }
}
