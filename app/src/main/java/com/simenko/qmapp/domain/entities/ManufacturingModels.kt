package com.simenko.qmapp.domain.entities

import androidx.compose.runtime.Stable
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ObjectTransformer

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
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false,
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
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false,
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
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseManufacturingLine>() {
    override fun getRecordId() = id
    override fun getParentId() = chId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainManufacturingLine::class, DatabaseManufacturingLine::class).transform(this)

    data class DomainManufacturingLineComplete(
        val departmentId: Int = NoRecord.num,
        val depOrder: Int = NoRecord.num,
        val depAbbr: String? = null,
        val depName: String? = null,
        val subDepartmentId: Int = NoRecord.num,
        val subDepOrder: Int = NoRecord.num,
        val subDepAbbr: String? = null,
        val subDepDesignation: String? = null,
        val channelId: Int = NoRecord.num,
        val channelOrder: Int = NoRecord.num,
        val channelAbbr: String? = null,
        val channelDesignation: String? = null,
        val id: Int = NoRecord.num,
        val lineOrder: Int = NoRecord.num,
        val lineAbbr: String? = null,
        val lineDesignation: String = EmptyString.str
    ) : DomainBaseModel<DatabaseManufacturingLine.DatabaseManufacturingLineComplete>() {
        override fun getRecordId() = this.id
        override fun getParentId() = this.departmentId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel(): DatabaseManufacturingLine.DatabaseManufacturingLineComplete =
            ObjectTransformer(DomainManufacturingLineComplete::class, DatabaseManufacturingLine.DatabaseManufacturingLineComplete::class).transform(this)
    }
}

@Stable
data class DomainManufacturingOperation(
    var id: Int = NoRecord.num,
    var lineId: Int = NoRecord.num,
    var operationAbbr: String = EmptyString.str,
    var operationDesignation: String = EmptyString.str,
    var operationOrder: Int = NoRecord.num,
    var equipment: String? = null,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseManufacturingOperation>() {
    override fun getRecordId() = id
    override fun getParentId() = lineId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainManufacturingOperation::class, DatabaseManufacturingOperation::class).transform(this)

    data class DomainManufacturingOperationComplete(
        val operation: DomainManufacturingOperation = DomainManufacturingOperation(),
        val lineComplete: DomainManufacturingLine.DomainManufacturingLineComplete = DomainManufacturingLine.DomainManufacturingLineComplete(),
        val previousOperations: List<DomainOperationsFlow.DomainOperationsFlowComplete> = listOf(),
        var detailsVisibility: Boolean = false,
        var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseManufacturingOperation.DatabaseManufacturingOperationComplete>() {
        override fun getRecordId(): Any = operation.id
        override fun getParentId(): Int = operation.lineId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel(): DatabaseManufacturingOperation.DatabaseManufacturingOperationComplete {
            return DatabaseManufacturingOperation.DatabaseManufacturingOperationComplete(
                operation = this.operation.toDatabaseModel(),
                lineComplete = this.lineComplete.toDatabaseModel(),
                previousOperations = this.previousOperations.map { it.toDatabaseModel() }
            )
        }
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
    override fun toDatabaseModel() = ObjectTransformer(DomainOperationsFlow::class, DatabaseOperationsFlow::class).transform(this)

    data class DomainOperationsFlowComplete(
        val id: Int = NoRecord.num,
        val currentOperationId: Int = NoRecord.num,
        val previousOperationId: Int = NoRecord.num,
        val operationOrder: Int = NoRecord.num,
        val operationAbbr: String? = null,
        val operationDesignation: String? = null,
        val equipment: String? = null,
        val depId: Int = NoRecord.num,
        val depOrder: Int = NoRecord.num,
        val depAbbr: String? = null,
        val subDepId: Int = NoRecord.num,
        val subDepOrder: Int = NoRecord.num,
        val subDepAbbr: String? = null,
        val channelId: Int = NoRecord.num,
        val channelOrder: Int = NoRecord.num,
        val channelAbbr: String? = null,
        val lineId: Int = NoRecord.num,
        val lineOrder: Int = NoRecord.num,
        val lineAbbr: String? = null,
        val detailsVisibility: Boolean = false,
        val isExpanded: Boolean = false,
        val toBeDeleted: Boolean = false
    ): DomainBaseModel<DatabaseOperationsFlow.DatabaseOperationsFlowComplete>() {
        override fun getRecordId(): Int = this.id
        override fun getParentId(): Int = currentOperationId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel(): DatabaseOperationsFlow.DatabaseOperationsFlowComplete
                = ObjectTransformer(DomainOperationsFlowComplete::class, DatabaseOperationsFlow.DatabaseOperationsFlowComplete::class).transform(this)

        fun toSimplestModel(): DomainOperationsFlow = DomainOperationsFlow(id = this.id, currentOperationId = this.currentOperationId, previousOperationId = this.previousOperationId)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DomainOperationsFlowComplete

            if (id != other.id) return false
            if (currentOperationId != other.currentOperationId) return false
            if (previousOperationId != other.previousOperationId) return false
            if (operationOrder != other.operationOrder) return false
            if (operationAbbr != other.operationAbbr) return false
            if (operationDesignation != other.operationDesignation) return false
            if (equipment != other.equipment) return false
            if (depId != other.depId) return false
            if (depOrder != other.depOrder) return false
            if (depAbbr != other.depAbbr) return false
            if (subDepId != other.subDepId) return false
            if (subDepOrder != other.subDepOrder) return false
            if (subDepAbbr != other.subDepAbbr) return false
            if (channelId != other.channelId) return false
            if (channelOrder != other.channelOrder) return false
            if (channelAbbr != other.channelAbbr) return false
            if (lineId != other.lineId) return false
            if (lineOrder != other.lineOrder) return false
            if (lineAbbr != other.lineAbbr) return false
            if (detailsVisibility != other.detailsVisibility) return false
            if (isExpanded != other.isExpanded) return false
            if (toBeDeleted != other.toBeDeleted) return false

            return true
        }

        override fun hashCode(): Int {
            var result = currentOperationId
            result = 31 * result + previousOperationId
            return result
        }
    }
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
