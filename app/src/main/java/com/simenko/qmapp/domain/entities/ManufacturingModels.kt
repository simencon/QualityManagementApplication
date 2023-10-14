package com.simenko.qmapp.domain.entities

import androidx.compose.runtime.Stable
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ObjectTransformer
import kotlin.reflect.jvm.internal.impl.types.TypeCheckerState.SupertypesPolicy.DoCustomTransform

@Stable
data class DomainEmployee(
    var id: Int = NoRecord.num,
    var fullName: String = EmptyString.str,
    var companyId: Int = NoRecord.num,
    var departmentId: Int = NoRecord.num,
    var subDepartmentId: Int? = null,
    var department: String = EmptyString.str,
    var jobRoleId: Int = NoRecord.num,
    var jobRole: String = EmptyString.str,
    var email: String? = null,
    var passWord: String? = null,
    var detailsVisibility: Boolean = false,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseEmployee>() {
    override fun getRecordId() = id
    override fun getParentId() = departmentId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun getName() = fullName
    override fun toDatabaseModel(): DatabaseEmployee {
        return ObjectTransformer(DomainEmployee::class, DatabaseEmployee::class).transform(this)
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
    var companyManagerId: Int = NoRecord.num,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseCompany>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainCompany::class, DatabaseCompany::class).transform(this)
}

data class DomainJobRole(
    var id: Int = NoRecord.num,
    var companyId: Int = NoRecord.num,
    var jobRoleDescription: String = EmptyString.str,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseJobRole>() {
    override fun getRecordId() = this.id
    override fun getParentId() = this.companyId
    override fun setIsSelected(value: Boolean) {
        this.isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainJobRole::class, DatabaseJobRole::class).transform(this)
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

    override fun toDatabaseModel() = ObjectTransformer(DomainDepartment::class, DatabaseDepartment::class).transform(this)
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

    override fun toDatabaseModel() = ObjectTransformer(DomainSubDepartment::class, DatabaseSubDepartment::class).transform(this)
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

    override fun toDatabaseModel() = ObjectTransformer(DomainManufacturingChannel::class, DatabaseManufacturingChannel::class).transform(this)
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

    override fun toDatabaseModel() = ObjectTransformer(DomainManufacturingLine::class, DatabaseManufacturingLine::class).transform(this)
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

    override fun toDatabaseModel() = ObjectTransformer(DomainManufacturingOperation::class, DatabaseManufacturingOperation::class).transform(this)
}

data class DomainOperationsFlow(
    var id: Int,
    var currentOperationId: Int,
    var previousOperationId: Int
) : DomainBaseModel<DatabaseOperationsFlow>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainOperationsFlow::class, DatabaseOperationsFlow::class).transform(this)
}

@Stable
data class DomainEmployeeComplete(
    val teamMember: DomainEmployee = DomainEmployee(),
    val company: DomainCompany? = DomainCompany(),
    val department: DomainDepartment? = DomainDepartment(),
    val subDepartment: DomainSubDepartment? = DomainSubDepartment(),
    val jobRole: DomainJobRole? = DomainJobRole(),
    var isSelected: Boolean = false,
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseEmployeeComplete>() {
    override fun getRecordId() = teamMember.id
    override fun getParentId() = teamMember.departmentId
    override fun getName() = this.teamMember.fullName

    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel(): DatabaseEmployeeComplete {
        return DatabaseEmployeeComplete(
            teamMember = teamMember.toDatabaseModel(),
            company = company?.toDatabaseModel(),
            department = department?.toDatabaseModel(),
            subDepartment = subDepartment?.toDatabaseModel(),
            jobRole = jobRole?.toDatabaseModel()
        )
    }
}

data class DomainDepartmentComplete(
    val department: DomainDepartment = DomainDepartment(),
    val depManager: DomainEmployee = DomainEmployee(),
    val company: DomainCompany = DomainCompany(),
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false
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
