package com.simenko.qmapp.domain

import androidx.compose.runtime.Stable

data class DomainPositionLevel(
    var id: Int,
    var levelDescription: String
)

@Stable
data class DomainTeamMember(
    var id: Int = NoSelectedRecord.num,
    var departmentId: Int = NoSelectedRecord.num,
    var department: String = NoSelectedString.str,
    var email: String? = null,
    var fullName: String = NoSelectedString.str,
    var jobRole: String = NoSelectedString.str,
    var roleLevelId: Int = NoSelectedRecord.num,
    var passWord: String? = null,
    var companyId: Int = NoSelectedRecord.num,
    var detailsVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId() = id
    override fun getParentOneId() = departmentId
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }

    override fun getName(): String {
        return fullName
    }
}

data class DomainCompany(
    var id: Int = NoSelectedRecord.num,
    var companyName: String? = null,
    var companyCountry: String? = null,
    var companyCity: String? = null,
    var companyAddress: String? = null,
    var companyPhoneNo: String? = null,
    var companyPostCode: String? = null,
    var companyRegion: String? = null,
    var companyOrder: Int = NoSelectedRecord.num,
    var companyIndustrialClassification: String? = null,
    var companyManagerId: Int = NoSelectedRecord.num
)

@Stable
data class DomainDepartment(
    var id: Int = NoSelectedRecord.num,
    var depAbbr: String? = null,
    var depName: String? = null,
    var depManager: Int? = null,
    var depOrganization: String? = null,
    var depOrder: Int? = null,
    var companyId: Int? = null,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId() = id
    override fun getParentOneId() = companyId ?: 0
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }
}

@Stable
data class DomainSubDepartment(
    var id: Int,
    var depId: Int,
    var subDepAbbr: String? = null,
    var subDepDesignation: String? = null,
    var subDepOrder: Int? = null,
    var channelsVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId() = id
    override fun getParentOneId() = depId
    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }
}

@Stable
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

@Stable
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

@Stable
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

@Stable
data class DomainTeamMemberComplete(
    val teamMember: DomainTeamMember = DomainTeamMember(),
    val department: DomainDepartment = DomainDepartment(),
    val company: DomainCompany = DomainCompany(),
    var detailsVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainModel() {
    override fun getRecordId(): Any {
        return teamMember.id
    }

    override fun getParentOneId(): Int {
        return teamMember.departmentId
    }

    override fun setIsChecked(value: Boolean) {
        isSelected = value
    }

}

data class DomainDepartmentComplete(
    val departments: DomainDepartment,
    val depManagerDetails: List<DomainTeamMember>,
    val companies: List<DomainCompany>,
    var departmentDetailsVisibility: Boolean = false
)
