package com.simenko.qmapp.domain.entities

import androidx.compose.runtime.Stable
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ObjectTransformer

@Stable
data class DomainTeamMember(
    var id: Int = NoRecord.num,
    var departmentId: Int = NoRecord.num,
    var department: String = NoString.str,
    var email: String? = null,
    var fullName: String = NoString.str,
    var jobRole: String = NoString.str,
    var roleLevelId: Int = NoRecord.num,
    var passWord: String? = null,
    var companyId: Int = NoRecord.num,
    var detailsVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseTeamMember>() {
    override fun getRecordId() = id
    override fun getParentId() = departmentId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun getName() = fullName
    override fun toDatabaseModel(): DatabaseTeamMember {
        return ObjectTransformer(DomainTeamMember::class, DatabaseTeamMember::class).transform(this)
    }
}

data class DomainCompany(
    var id: Int = NoRecord.num,
    var companyName: String? = null,
    var companyCountry: String? = null,
    var companyCity: String? = null,
    var companyAddress: String? = null,
    var companyPhoneNo: String? = null,
    var companyPostCode: String? = null,
    var companyRegion: String? = null,
    var companyOrder: Int = NoRecord.num,
    var companyIndustrialClassification: String? = null,
    var companyManagerId: Int = NoRecord.num
) : DomainBaseModel<DatabaseCompany>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseCompany {
        return ObjectTransformer(DomainCompany::class, DatabaseCompany::class).transform(this)
    }
}

@Stable
data class DomainDepartment(
    var id: Int = NoRecord.num,
    var depAbbr: String? = null,
    var depName: String? = null,
    var depManager: Int? = null,
    var depOrganization: String? = null,
    var depOrder: Int? = null,
    var companyId: Int? = null,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseDepartment>() {
    override fun getRecordId() = id
    override fun getParentId() = companyId ?: 0
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseDepartment {
        return ObjectTransformer(DomainDepartment::class, DatabaseDepartment::class).transform(this)
    }
}

@Stable
data class DomainSubDepartment(
    var id: Int = NoRecord.num,
    var depId: Int = NoRecord.num,
    var subDepAbbr: String? = null,
    var subDepDesignation: String? = null,
    var subDepOrder: Int? = null,
    var channelsVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseSubDepartment>() {
    override fun getRecordId() = id
    override fun getParentId() = depId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseSubDepartment {
        return ObjectTransformer(DomainSubDepartment::class, DatabaseSubDepartment::class).transform(this)
    }
}

@Stable
data class DomainManufacturingChannel(
    var id: Int = NoRecord.num,
    var subDepId: Int = NoRecord.num,
    var channelAbbr: String? = null,
    var channelDesignation: String? = null,
    var channelOrder: Int? = null,
    var linesVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseManufacturingChannel>() {
    override fun getRecordId() = id
    override fun getParentId() = subDepId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseManufacturingChannel {
        return ObjectTransformer(DomainManufacturingChannel::class, DatabaseManufacturingChannel::class).transform(this)
    }
}

@Stable
data class DomainManufacturingLine(
    var id: Int = NoRecord.num,
    var chId: Int = NoRecord.num,
    var lineAbbr: String = NoString.str,
    var lineDesignation: String = NoString.str,
    var lineOrder: Int = NoRecord.num,
    var operationVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseManufacturingLine>() {
    override fun getRecordId() = id
    override fun getParentId() = chId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseManufacturingLine {
        return ObjectTransformer(DomainManufacturingLine::class, DatabaseManufacturingLine::class).transform(this)
    }
}

@Stable
data class DomainManufacturingOperation(
    var id: Int = NoRecord.num,
    var lineId: Int = NoRecord.num,
    var operationAbbr: String = NoString.str,
    var operationDesignation: String = NoString.str,
    var operationOrder: Int = NoRecord.num,
    var equipment: String? = null,
    var detailsVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseManufacturingOperation>() {
    override fun getRecordId() = id
    override fun getParentId() = lineId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseManufacturingOperation {
        return ObjectTransformer(DomainManufacturingOperation::class, DatabaseManufacturingOperation::class).transform(this)
    }
}

data class DomainOperationsFlow(
    var id: Int,
    var currentOperationId: Int,
    var previousOperationId: Int
) : DomainBaseModel<DatabaseOperationsFlow>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseOperationsFlow {
        return ObjectTransformer(DomainOperationsFlow::class, DatabaseOperationsFlow::class).transform(this)
    }
}

@Stable
data class DomainTeamMemberComplete(
    val teamMember: DomainTeamMember = DomainTeamMember(),
    val department: DomainDepartment? = DomainDepartment(),
    val company: DomainCompany? = DomainCompany(),
    var isSelected: Boolean = false,
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseTeamMemberComplete>() {
    override fun getRecordId() = teamMember.id
    override fun getParentId() = teamMember.departmentId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseTeamMemberComplete {
        return DatabaseTeamMemberComplete(
            teamMember = teamMember.toDatabaseModel(),
            department = department?.toDatabaseModel(),
            company = company?.toDatabaseModel()
        )
    }
}

data class DomainDepartmentComplete(
    val department: DomainDepartment,
    val depManager: DomainTeamMember,
    val company: DomainCompany,
    var departmentDetailsVisibility: Boolean = false
) : DomainBaseModel<DatabaseDepartmentsComplete>() {
    override fun getRecordId() = department.id
    override fun getParentId() = department.companyId ?: NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseDepartmentsComplete {
        return DatabaseDepartmentsComplete(
            department = department.toDatabaseModel(),
            depManager = depManager.toDatabaseModel(),
            company = company.toDatabaseModel()
        )
    }
}
