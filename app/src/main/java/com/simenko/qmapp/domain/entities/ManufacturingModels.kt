package com.simenko.qmapp.domain.entities

import androidx.compose.runtime.Stable
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.data.cache.db.entities.*
import com.simenko.qmapp.utils.ObjectTransformer

@Stable
data class DomainEmployee(
    var id: ID = NoRecord.num,
    var fullName: String = EmptyString.str,
    var companyId: ID = NoRecord.num,
    var departmentId: ID = NoRecord.num,
    var subDepartmentId: ID? = null,
    var department: String = EmptyString.str,
    var jobRoleId: ID = NoRecord.num,
    var jobRole: String = EmptyString.str,
    var email: String? = null,
    var passWord: String? = null,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseEmployee>() {
    override fun getRecordId() = id
    override fun getParentId() = departmentId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun getName() = fullName
    override fun toDatabaseModel() = ObjectTransformer(DomainEmployee::class, DatabaseEmployee::class).transform(this)
}

data class DomainCompany(
    var id: ID = NoRecord.num,
    var companyName: String? = null,
    var companyCountry: String? = null,
    var companyCity: String? = null,
    var companyAddress: String? = null,
    var companyPhoneNo: String? = null,
    var companyPostCode: String? = null,
    var companyRegion: String? = null,
    var companyOrder: Int = NoRecord.num.toInt(),
    var companyIndustrialClassification: String? = null,
    var companyManagerId: ID = NoRecord.num,
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
    var id: ID = NoRecord.num,
    var companyId: ID = NoRecord.num,
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
    var id: ID = NoRecord.num,
    var depAbbr: String? = EmptyString.str,
    var depName: String? = EmptyString.str,
    var depManager: ID? = NoRecord.num,
    var depOrganization: String? = EmptyString.str,
    var depOrder: Int? = NoRecord.num.toInt(),
    var companyId: ID? = NoRecord.num,
    var isSelected: Boolean = false
) : DomainBaseModel<DatabaseDepartment>() {
    override fun getRecordId() = id
    override fun getParentId() = companyId ?: NoRecord.num
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainDepartment::class, DatabaseDepartment::class).transform(this)

    data class DomainDepartmentComplete(
        val department: DomainDepartment = DomainDepartment(),
        val depManager: DomainEmployee = DomainEmployee(),
        val company: DomainCompany = DomainCompany(),
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseDepartment.DatabaseDepartmentsComplete>() {
        override fun getRecordId() = department.id
        override fun getParentId() = department.companyId ?: NoRecord.num
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = DatabaseDepartment.DatabaseDepartmentsComplete(
            department = department.toDatabaseModel(),
            depManager = depManager.toDatabaseModel(),
            company = company.toDatabaseModel()
        )
    }
}

@Stable
data class DomainSubDepartment(
    var id: ID = NoRecord.num,
    var depId: ID = NoRecord.num,
    var subDepAbbr: String? = EmptyString.str,
    var subDepDesignation: String? = EmptyString.str,
    var subDepOrder: Int? = NoRecord.num.toInt(),
    var isSelected: Boolean = false,
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseSubDepartment>() {
    override fun getRecordId() = id
    override fun getParentId() = depId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainSubDepartment::class, DatabaseSubDepartment::class).transform(this)

    data class DomainSubDepartmentComplete(
        val subDepartment: DomainSubDepartment = DomainSubDepartment(),
        val department: DomainDepartment = DomainDepartment()
    ) : DomainBaseModel<DatabaseSubDepartment.DatabaseSubDepartmentComplete>() {
        override fun getRecordId() = subDepartment.id
        override fun getParentId() = subDepartment.depId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel() = DatabaseSubDepartment.DatabaseSubDepartmentComplete(
            subDepartment = this.subDepartment.toDatabaseModel(),
            department = this.department.toDatabaseModel()
        )
    }

    data class DomainSubDepartmentWithParents(
        val companyId: ID = NoRecord.num,
        val companyOrder: Int = NoRecord.num.toInt(),
        val companyName: String = NoString.str,
        val departmentId: ID = NoRecord.num,
        val depOrder: Int = NoRecord.num.toInt(),
        val depAbbr: String? = NoString.str,
        val depName: String? = NoString.str,
        val id: ID = NoRecord.num,
        val subDepOrder: Int = NoRecord.num.toInt(),
        val subDepAbbr: String? = NoString.str,
        val subDepDesignation: String? = NoString.str
    ) : DomainBaseModel<DatabaseSubDepartment.DatabaseSubDepartmentWithParents>() {
        override fun getRecordId() = this.id
        override fun getParentId() = this.departmentId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = ObjectTransformer(DomainSubDepartmentWithParents::class, DatabaseSubDepartment.DatabaseSubDepartmentWithParents::class).transform(this)
    }
}

@Stable
data class DomainManufacturingChannel(
    var id: ID = NoRecord.num,
    var subDepId: ID = NoRecord.num,
    var channelAbbr: String? = EmptyString.str,
    var channelDesignation: String? = EmptyString.str,
    var channelOrder: Int? = NoRecord.num.toInt(),
    var isSelected: Boolean = false,
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseManufacturingChannel>() {
    override fun getRecordId() = id
    override fun getParentId() = subDepId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainManufacturingChannel::class, DatabaseManufacturingChannel::class).transform(this)

    data class DomainManufacturingChannelComplete(
        val channel: DomainManufacturingChannel = DomainManufacturingChannel(),
        val subDepartmentWithParents: DomainSubDepartment.DomainSubDepartmentWithParents = DomainSubDepartment.DomainSubDepartmentWithParents()
    ) : DomainBaseModel<DatabaseManufacturingChannel.DatabaseManufacturingChannelComplete>() {
        override fun getRecordId() = channel.id
        override fun getParentId() = channel.subDepId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel() = DatabaseManufacturingChannel.DatabaseManufacturingChannelComplete(
            channel = this.channel.toDatabaseModel(),
            subDepartmentWithParents = this.subDepartmentWithParents.toDatabaseModel()
        )
    }

    data class DomainManufacturingChannelWithParents(
        val companyId: ID = NoRecord.num,
        val companyOrder: Int = NoRecord.num.toInt(),
        val companyName: String = EmptyString.str,
        val departmentId: ID = NoRecord.num,
        val depOrder: Int = NoRecord.num.toInt(),
        val depAbbr: String? = EmptyString.str,
        val depName: String? = EmptyString.str,
        val subDepartmentId: ID = NoRecord.num,
        val subDepOrder: Int = NoRecord.num.toInt(),
        val subDepAbbr: String? = EmptyString.str,
        val subDepDesignation: String? = EmptyString.str,
        val id: ID = NoRecord.num,
        val channelOrder: Int = NoRecord.num.toInt(),
        val channelAbbr: String? = EmptyString.str,
        val channelDesignation: String? = EmptyString.str
    ) : DomainBaseModel<DatabaseManufacturingChannel.DatabaseManufacturingChannelWithParents>() {
        override fun getRecordId() = this.id
        override fun getParentId() = this.subDepartmentId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() =
            ObjectTransformer(DomainManufacturingChannelWithParents::class, DatabaseManufacturingChannel.DatabaseManufacturingChannelWithParents::class).transform(this)
    }
}

@Stable
data class DomainManufacturingLine(
    var id: ID = NoRecord.num,
    var chId: ID = NoRecord.num,
    var lineAbbr: String = EmptyString.str,
    var lineDesignation: String = EmptyString.str,
    var lineOrder: Int = NoRecord.num.toInt(),
    var isSelected: Boolean = false,
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseManufacturingLine>() {
    override fun getRecordId() = id
    override fun getParentId() = chId
    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = ObjectTransformer(DomainManufacturingLine::class, DatabaseManufacturingLine::class).transform(this)

    data class DomainManufacturingLineWithParents(
        val companyId: ID = NoRecord.num,
        val companyOrder: Int = NoRecord.num.toInt(),
        val companyName: String = EmptyString.str,
        val departmentId: ID = NoRecord.num,
        val depOrder: Int = NoRecord.num.toInt(),
        val depAbbr: String? = EmptyString.str,
        val depName: String? = EmptyString.str,
        val subDepartmentId: ID = NoRecord.num,
        val subDepOrder: Int = NoRecord.num.toInt(),
        val subDepAbbr: String? = EmptyString.str,
        val subDepDesignation: String? = EmptyString.str,
        val channelId: ID = NoRecord.num,
        val channelOrder: Int = NoRecord.num.toInt(),
        val channelAbbr: String? = EmptyString.str,
        val channelDesignation: String? = EmptyString.str,
        val id: ID = NoRecord.num,
        val lineOrder: Int = NoRecord.num.toInt(),
        val lineAbbr: String? = EmptyString.str,
        val lineDesignation: String = EmptyString.str
    ) : DomainBaseModel<DatabaseManufacturingLine.DatabaseManufacturingLineWithParents>() {
        override fun getRecordId() = this.id
        override fun getParentId() = this.departmentId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = ObjectTransformer(DomainManufacturingLineWithParents::class, DatabaseManufacturingLine.DatabaseManufacturingLineWithParents::class).transform(this)
    }

    data class DomainManufacturingLineComplete(
        val line: DomainManufacturingLine = DomainManufacturingLine(),
        val channelWithParents: DomainManufacturingChannel.DomainManufacturingChannelWithParents = DomainManufacturingChannel.DomainManufacturingChannelWithParents()
    ) : DomainBaseModel<DatabaseManufacturingLine.DatabaseManufacturingLineComplete>() {
        override fun getRecordId() = line.id
        override fun getParentId() = line.chId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel() = DatabaseManufacturingLine.DatabaseManufacturingLineComplete(
            line = this.line.toDatabaseModel(),
            channelWithParents = this.channelWithParents.toDatabaseModel()
        )
    }
}

@Stable
data class DomainManufacturingOperation(
    var id: ID = NoRecord.num,
    var lineId: ID = NoRecord.num,
    var operationAbbr: String = EmptyString.str,
    var operationDesignation: String = EmptyString.str,
    var operationOrder: Int = NoRecord.num.toInt(),
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
        val lineWithParents: DomainManufacturingLine.DomainManufacturingLineWithParents = DomainManufacturingLine.DomainManufacturingLineWithParents(),
        val previousOperations: List<DomainOperationsFlow.DomainOperationsFlowComplete> = listOf(),
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseManufacturingOperation.DatabaseManufacturingOperationComplete>() {
        override fun getRecordId() = operation.id
        override fun getParentId() = operation.lineId
        override fun setIsSelected(value: Boolean) {}

        override fun toDatabaseModel() = DatabaseManufacturingOperation.DatabaseManufacturingOperationComplete(
            operation = this.operation.toDatabaseModel(),
            lineWithParents = this.lineWithParents.toDatabaseModel(),
            previousOperations = this.previousOperations.map { it.toDatabaseModel() }
        )
    }
}

data class DomainOperationsFlow(
    var id: ID,
    var currentOperationId: ID,
    var previousOperationId: ID
) : DomainBaseModel<DatabaseOperationsFlow>() {
    override fun getRecordId() = id
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainOperationsFlow::class, DatabaseOperationsFlow::class).transform(this)

    data class DomainOperationsFlowComplete(
        val id: ID = NoRecord.num,
        val currentOperationId: ID = NoRecord.num,
        val previousOperationId: ID = NoRecord.num,
        val operationOrder: Int = NoRecord.num.toInt(),
        val operationAbbr: String? = null,
        val operationDesignation: String? = null,
        val equipment: String? = null,
        val companyId: ID = NoRecord.num,
        val companyOrder: Int = NoRecord.num.toInt(),
        val companyName: String = EmptyString.str,
        val depId: ID = NoRecord.num,
        val depOrder: Int = NoRecord.num.toInt(),
        val depAbbr: String? = null,
        val subDepId: ID = NoRecord.num,
        val subDepOrder: Int = NoRecord.num.toInt(),
        val subDepAbbr: String? = null,
        val channelId: ID = NoRecord.num,
        val channelOrder: Int = NoRecord.num.toInt(),
        val channelAbbr: String? = null,
        val lineId: ID = NoRecord.num,
        val lineOrder: Int = NoRecord.num.toInt(),
        val lineAbbr: String? = null,
        val toBeDeleted: Boolean = false,
        override var detailsVisibility: Boolean = false,
        override var isExpanded: Boolean = false
    ) : DomainBaseModel<DatabaseOperationsFlow.DatabaseOperationsFlowComplete>() {
        override fun getRecordId() = this.id
        override fun getParentId() = currentOperationId
        override fun setIsSelected(value: Boolean) {}
        override fun toDatabaseModel() = ObjectTransformer(DomainOperationsFlowComplete::class, DatabaseOperationsFlow.DatabaseOperationsFlowComplete::class).transform(this)
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
            var result = currentOperationId.toInt()
            result = 31 * result + previousOperationId.toInt()
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
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseEmployeeComplete>() {
    override fun getRecordId() = teamMember.id
    override fun getParentId() = teamMember.departmentId
    override fun getName() = this.teamMember.fullName

    override fun setIsSelected(value: Boolean) {
        isSelected = value
    }

    override fun toDatabaseModel() = DatabaseEmployeeComplete(
        teamMember = teamMember.toDatabaseModel(),
        company = company?.toDatabaseModel(),
        department = department?.toDatabaseModel(),
        subDepartment = subDepartment?.toDatabaseModel(),
        jobRole = jobRole?.toDatabaseModel()
    )
}
