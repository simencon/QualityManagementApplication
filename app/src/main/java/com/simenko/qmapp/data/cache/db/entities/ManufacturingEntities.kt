package com.simenko.qmapp.data.cache.db.entities

import androidx.room.*
import com.simenko.qmapp.data.cache.db.contract.DatabaseBaseModel
import com.simenko.qmapp.data.remote.entities.NetworkSubDepartment
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.data.remote.entities.*
import com.simenko.qmapp.utils.ObjectTransformer

@Entity(
    tableName = "8_employees",
    foreignKeys = [
//        ToDo Cannot be used as foreign key because appears before Departments
//        ForeignKey(
//            entity = DatabaseDepartment::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("departmentId"),
//            onDelete = ForeignKey.NO_ACTION,
//            onUpdate = ForeignKey.NO_ACTION
//        ),
//        ToDo Useless field, access are managed vie REST Service
//        ForeignKey(
//            entity = DatabasePositionLevel::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("roleLevelId"),
//            onDelete = ForeignKey.NO_ACTION,
//            onUpdate = ForeignKey.NO_ACTION
//        )
//        ToDo Cannot be used as foreign key because appears before Companies
//        ForeignKey(
//            entity = DatabaseCompany::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("companyId"),
//            onDelete = ForeignKey.NO_ACTION,
//            onUpdate = ForeignKey.NO_ACTION
//        )
    ]
)
data class DatabaseEmployee (
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    var fullName: String,
    @ColumnInfo(index = true)
    var companyId: ID,
    @ColumnInfo(index = true)
    var departmentId: ID,
    @ColumnInfo(index = true)
    var subDepartmentId: ID? = null,
    var department: String,
    @ColumnInfo(index = true)
    var jobRoleId: ID,
    var jobRole: String,
    var email: String? = null,
    var passWord: String? = null
) : DatabaseBaseModel<NetworkEmployee, DomainEmployee, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseEmployee::class, NetworkEmployee::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseEmployee::class, DomainEmployee::class).transform(this)
}

@Entity(
    tableName = "0_companies",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseEmployee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("companyManagerId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseCompany (
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    var companyName: String? = null,
    var companyCountry: String? = null,
    var companyCity: String? = null,
    var companyAddress: String? = null,
    var companyPhoneNo: String? = null,
    var companyPostCode: String? = null,
    var companyRegion: String? = null,
    var companyOrder: Int,
    var companyIndustrialClassification: String? = null,
    @ColumnInfo(index = true)
    var companyManagerId: ID
) : DatabaseBaseModel<NetworkCompany, DomainCompany, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseCompany::class, NetworkCompany::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCompany::class, DomainCompany::class).transform(this)
}

@Entity(
    tableName = "0_job_roles",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseCompany::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("companyId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseJobRole(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var companyId: ID,
    var jobRoleDescription: String
) : DatabaseBaseModel<NetworkJobRole, DomainJobRole, ID, ID> {
    override fun getRecordId() = this.id
    override fun toNetworkModel() = ObjectTransformer(DatabaseJobRole::class, NetworkJobRole::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseJobRole::class, DomainJobRole::class).transform(this)
}

@Entity(
    tableName = "10_departments",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseEmployee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("depManager"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatabaseCompany::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("companyId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class DatabaseDepartment (
    @PrimaryKey(autoGenerate = true)
    val id: ID,
    val depAbbr: String?,
    val depName: String?,
    @ColumnInfo(index = true)
    val depManager: ID?,
    val depOrganization: String?,
    val depOrder: Int?,
    @ColumnInfo(index = true)
    val companyId: ID?
) : DatabaseBaseModel<NetworkDepartment, DomainDepartment, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseDepartment::class, NetworkDepartment::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseDepartment::class, DomainDepartment::class).transform(this)

    data class DatabaseDepartmentsComplete(
        @Embedded
        val department: DatabaseDepartment,
        @Relation(
            entity = DatabaseEmployee::class,
            parentColumn = "depManager",
            entityColumn = "id"
        )
        val depManager: DatabaseEmployee,
        @Relation(
            entity = DatabaseCompany::class,
            parentColumn = "companyId",
            entityColumn = "id"
        )
        val company: DatabaseCompany
    ) : DatabaseBaseModel<Any?, DomainDepartment.DomainDepartmentComplete, ID, ID> {
        override fun getRecordId() = department.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = DomainDepartment.DomainDepartmentComplete(
            department = department.toDomainModel(),
            depManager = depManager.toDomainModel(),
            company = company.toDomainModel()
        )
    }
}

@Entity(
    tableName = "11_sub_departments",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("depId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseSubDepartment(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var depId: ID,
    var subDepAbbr: String? = null,
    var subDepDesignation: String? = null,
    var subDepOrder: Int? = null
) : DatabaseBaseModel<NetworkSubDepartment, DomainSubDepartment, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseSubDepartment::class, NetworkSubDepartment::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseSubDepartment::class, DomainSubDepartment::class).transform(this)
    data class DatabaseSubDepartmentComplete(
        @Embedded
        val subDepartment: DatabaseSubDepartment,
        @Relation(
            entity = DatabaseDepartment::class,
            parentColumn = "depId",
            entityColumn = "id"
        )
        val department: DatabaseDepartment,
    ) : DatabaseBaseModel<Any?, DomainSubDepartment.DomainSubDepartmentComplete, ID, ID> {
        override fun getRecordId() = this.subDepartment.id
        override fun toNetworkModel(): Any? = null
        override fun toDomainModel(): DomainSubDepartment.DomainSubDepartmentComplete {
            return DomainSubDepartment.DomainSubDepartmentComplete(
                subDepartment = this.subDepartment.toDomainModel(),
                department = this.department.toDomainModel()
            )
        }
    }

    @DatabaseView(
        viewName = "subDepartmentWithParents",
        value = """
        select c.id as companyId, c.companyOrder, c.companyName,
        d.id as departmentId, d.depOrder, d.depAbbr, d.depName, 
        sd.id, sd.subDepOrder, sd.subDepAbbr, sd.subDepDesignation
        from `11_sub_departments` as sd
        inner join `10_departments` as d on sd.depId = d.id
        inner join `0_companies` as c on d.companyId = c.id
        order by d.depOrder, sd.subDepOrder;
        """
    )
    data class DatabaseSubDepartmentWithParents(
        val companyId: ID,
        val companyOrder: Int,
        val companyName: String,
        val departmentId: ID,
        val depOrder: Int,
        val depAbbr: String?,
        val depName: String?,
        val id: ID,
        val subDepOrder: Int,
        val subDepAbbr: String?,
        val subDepDesignation: String?,
    ) : DatabaseBaseModel<Any?, DomainSubDepartment.DomainSubDepartmentWithParents, ID, ID> {
        override fun getRecordId() = this.id
        override fun toNetworkModel() = null
        override fun toDomainModel(): DomainSubDepartment.DomainSubDepartmentWithParents =
            ObjectTransformer(DatabaseSubDepartmentWithParents::class, DomainSubDepartment.DomainSubDepartmentWithParents::class).transform(this)
    }
}

@Entity(
    tableName = "12_manufacturing_channels",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseSubDepartment::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subDepId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseManufacturingChannel(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var subDepId: ID,
    var channelAbbr: String? = null,
    var channelDesignation: String? = null,
    var channelOrder: Int? = null
) : DatabaseBaseModel<NetworkManufacturingChannel, DomainManufacturingChannel, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseManufacturingChannel::class, NetworkManufacturingChannel::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseManufacturingChannel::class, DomainManufacturingChannel::class).transform(this)
    data class DatabaseManufacturingChannelComplete(
        @Embedded
        val channel: DatabaseManufacturingChannel,
        @Relation(
            entity = DatabaseSubDepartment.DatabaseSubDepartmentWithParents::class,
            parentColumn = "subDepId",
            entityColumn = "id"
        )
        val subDepartmentWithParents: DatabaseSubDepartment.DatabaseSubDepartmentWithParents,
    ) : DatabaseBaseModel<Any?, DomainManufacturingChannel.DomainManufacturingChannelComplete, ID, ID> {
        override fun getRecordId() = this.channel.id
        override fun toNetworkModel(): Any? = null
        override fun toDomainModel(): DomainManufacturingChannel.DomainManufacturingChannelComplete {
            return DomainManufacturingChannel.DomainManufacturingChannelComplete(
                channel = this.channel.toDomainModel(),
                subDepartmentWithParents = this.subDepartmentWithParents.toDomainModel()
            )
        }
    }

    @DatabaseView(
        viewName = "manufacturingChannelsWithParents",
        value = """
        select c.id as companyId, c.companyOrder, c.companyName,
        d.id as departmentId, d.depOrder, d.depAbbr, d.depName, 
        sd.id as subDepartmentId, sd.subDepOrder, sd.subDepAbbr, sd.subDepDesignation, 
        mc.id, mc.channelOrder, mc.channelAbbr, mc.channelDesignation
        from `12_manufacturing_channels` as mc
        inner join `11_sub_departments` as sd on mc.subDepId = sd.id
        inner join `10_departments` as d on sd.depId = d.id
        inner join `0_companies` as c on d.companyId = c.id
        order by d.depOrder, sd.subDepOrder, mc.channelOrder;
        """
    )
    data class DatabaseManufacturingChannelWithParents(
        val companyId: ID,
        val companyOrder: Int,
        val companyName: String,
        val departmentId: ID,
        val depOrder: Int,
        val depAbbr: String?,
        val depName: String?,
        val subDepartmentId: ID,
        val subDepOrder: Int,
        val subDepAbbr: String?,
        val subDepDesignation: String?,
        val id: ID,
        val channelOrder: Int,
        val channelAbbr: String?,
        val channelDesignation: String?
    ) : DatabaseBaseModel<Any?, DomainManufacturingChannel.DomainManufacturingChannelWithParents, ID, ID> {
        override fun getRecordId() = this.id
        override fun toNetworkModel() = null
        override fun toDomainModel(): DomainManufacturingChannel.DomainManufacturingChannelWithParents =
            ObjectTransformer(DatabaseManufacturingChannelWithParents::class, DomainManufacturingChannel.DomainManufacturingChannelWithParents::class).transform(this)
    }
}

@Entity(
    tableName = "13_manufacturing_lines",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingChannel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseManufacturingLine(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var chId: ID,
    var lineAbbr: String,
    var lineDesignation: String,
    var lineOrder: Int
) : DatabaseBaseModel<NetworkManufacturingLine, DomainManufacturingLine, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseManufacturingLine::class, NetworkManufacturingLine::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseManufacturingLine::class, DomainManufacturingLine::class).transform(this)
    data class DatabaseManufacturingLineComplete(
        @Embedded
        val line: DatabaseManufacturingLine,
        @Relation(
            entity = DatabaseManufacturingChannel.DatabaseManufacturingChannelWithParents::class,
            parentColumn = "chId",
            entityColumn = "id"
        )
        val channelWithParents: DatabaseManufacturingChannel.DatabaseManufacturingChannelWithParents,
    ) : DatabaseBaseModel<Any?, DomainManufacturingLine.DomainManufacturingLineComplete, ID, ID> {
        override fun getRecordId() = this.line.id
        override fun toNetworkModel(): Any? = null
        override fun toDomainModel(): DomainManufacturingLine.DomainManufacturingLineComplete {
            return DomainManufacturingLine.DomainManufacturingLineComplete(
                line = this.line.toDomainModel(),
                channelWithParents = this.channelWithParents.toDomainModel()
            )
        }
    }

    @DatabaseView(
        viewName = "manufacturingLinesWithParents",
        value = """
        select c.id as companyId, c.companyOrder, c.companyName,
        d.id as departmentId, d.depOrder, d.depAbbr, d.depName, 
        sd.id as subDepartmentId, sd.subDepOrder, sd.subDepAbbr, sd.subDepDesignation, 
        mc.id as channelId, mc.channelOrder, mc.channelAbbr, mc.channelDesignation, 
        ml.id, ml.lineOrder, ml.lineAbbr, ml.lineDesignation 
        from `13_manufacturing_lines` as ml
        inner join `12_manufacturing_channels` as mc on ml.chId = mc.id
        inner join `11_sub_departments` as sd on mc.subDepId = sd.id
        inner join `10_departments` as d on sd.depId = d.id
        inner join `0_companies` as c on d.companyId = c.id
        order by d.depOrder, sd.subDepOrder, mc.channelOrder, ml.lineOrder;
        """
    )
    data class DatabaseManufacturingLineWithParents(
        val companyId: ID,
        val companyOrder: Int,
        val companyName: String,
        val departmentId: ID,
        val depOrder: Int,
        val depAbbr: String?,
        val depName: String?,
        val subDepartmentId: ID,
        val subDepOrder: Int,
        val subDepAbbr: String?,
        val subDepDesignation: String?,
        val channelId: ID,
        val channelOrder: Int,
        val channelAbbr: String?,
        val channelDesignation: String?,
        val id: ID,
        val lineOrder: Int,
        val lineAbbr: String?,
        val lineDesignation: String
    ) : DatabaseBaseModel<Any?, DomainManufacturingLine.DomainManufacturingLineWithParents, ID, ID> {
        override fun getRecordId() = this.id
        override fun toNetworkModel() = null
        override fun toDomainModel(): DomainManufacturingLine.DomainManufacturingLineWithParents =
            ObjectTransformer(DatabaseManufacturingLineWithParents::class, DomainManufacturingLine.DomainManufacturingLineWithParents::class).transform(this)
    }
}

@Entity(
    tableName = "14_manufacturing_operations",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingLine::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lineId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseManufacturingOperation(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var lineId: ID,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int,
    var equipment: String?
) : DatabaseBaseModel<NetworkManufacturingOperation, DomainManufacturingOperation, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseManufacturingOperation::class, NetworkManufacturingOperation::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseManufacturingOperation::class, DomainManufacturingOperation::class).transform(this)
    data class DatabaseManufacturingOperationComplete(
        @Embedded
        val operation: DatabaseManufacturingOperation,
        @Relation(
            entity = DatabaseManufacturingLine.DatabaseManufacturingLineWithParents::class,
            parentColumn = "lineId",
            entityColumn = "id"
        )
        val lineWithParents: DatabaseManufacturingLine.DatabaseManufacturingLineWithParents,
        @Relation(
            entity = DatabaseOperationsFlow.DatabaseOperationsFlowComplete::class,
            parentColumn = "id",
            entityColumn = "currentOperationId"
        )
        val previousOperations: List<DatabaseOperationsFlow.DatabaseOperationsFlowComplete>
    ) : DatabaseBaseModel<Any?, DomainManufacturingOperation.DomainManufacturingOperationComplete, ID, ID> {
        override fun getRecordId() = this.operation.id
        override fun toNetworkModel(): Any? = null
        override fun toDomainModel(): DomainManufacturingOperation.DomainManufacturingOperationComplete {
            return DomainManufacturingOperation.DomainManufacturingOperationComplete(
                operation = this.operation.toDomainModel(),
                lineWithParents = this.lineWithParents.toDomainModel(),
                previousOperations = this.previousOperations.map { it.toDomainModel() }
            )
        }
    }
}

@Entity(
    tableName = "14_14_manufacturing_operations_flow",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseManufacturingOperation::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("currentOperationId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DatabaseManufacturingOperation::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("currentOperationId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DatabaseOperationsFlow(
    @PrimaryKey(autoGenerate = true)
    var id: ID,
    @ColumnInfo(index = true)
    var currentOperationId: ID,
    @ColumnInfo(index = true)
    var previousOperationId: ID
) : DatabaseBaseModel<NetworkOperationsFlow, DomainOperationsFlow, ID, ID> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseOperationsFlow::class, NetworkOperationsFlow::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseOperationsFlow::class, DomainOperationsFlow::class).transform(this)

    @DatabaseView(
        viewName = "operationsFlowsComplete",
        value = """
        select mof.id, mof.currentOperationId, mof.previousOperationId, pmo.operationOrder, pmo.operationAbbr, pmo.operationDesignation, pmo.equipment, 
        c.id as companyId, c.companyOrder, c.companyName, 
        d.id as depId, d.depOrder, d.depAbbr, 
        sd.id as subDepId, sd.subDepOrder, sd.subDepAbbr, 
        mc.id as channelId, mc.channelOrder, mc.channelAbbr, 
        ml.id as lineId, ml.lineOrder, ml.lineAbbr
        from `14_14_manufacturing_operations_flow` as mof
        inner join `14_manufacturing_operations` as pmo on mof.previousOperationId = pmo.id
        inner join `13_manufacturing_lines` as ml on pmo.lineId = ml.id
        inner join `12_manufacturing_channels` as mc on ml.chId = mc.id
        inner join `11_sub_departments` as sd on mc.subDepId = sd.id
        inner join `10_departments` as d on sd.depId = d.id
        inner join `0_companies` as c on d.companyId = c.id
        order by d.depOrder, sd.subDepOrder, mc.channelOrder, ml.lineOrder, pmo.operationOrder;
        """
    )
    data class DatabaseOperationsFlowComplete(
        val id: ID,
        val currentOperationId: ID,
        val previousOperationId: ID,
        val operationOrder: Int,
        val operationAbbr: String?,
        val operationDesignation: String?,
        val equipment: String?,
        val companyId: ID,
        val companyOrder: Int,
        val companyName: String,
        val depId: ID,
        val depOrder: Int,
        val depAbbr: String?,
        val subDepId: ID,
        val subDepOrder: Int,
        val subDepAbbr: String?,
        val channelId: ID,
        val channelOrder: Int,
        val channelAbbr: String?,
        val lineId: ID,
        val lineOrder: Int,
        val lineAbbr: String?,
    ) : DatabaseBaseModel<Any?, DomainOperationsFlow.DomainOperationsFlowComplete, ID, ID> {
        override fun getRecordId() = this.id
        override fun toNetworkModel() = null
        override fun toDomainModel() = ObjectTransformer(DatabaseOperationsFlowComplete::class, DomainOperationsFlow.DomainOperationsFlowComplete::class).transform(this)
    }
}

data class DatabaseEmployeeComplete(
    @Embedded
    val teamMember: DatabaseEmployee,
    @Relation(
        entity = DatabaseCompany::class,
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val company: DatabaseCompany?,
    @Relation(
        entity = DatabaseDepartment::class,
        parentColumn = "departmentId",
        entityColumn = "id"
    )
    val department: DatabaseDepartment?,
    @Relation(
        entity = DatabaseSubDepartment::class,
        parentColumn = "subDepartmentId",
        entityColumn = "id"
    )
    val subDepartment: DatabaseSubDepartment?,
    @Relation(
        entity = DatabaseJobRole::class,
        parentColumn = "jobRoleId",
        entityColumn = "id"
    )
    val jobRole: DatabaseJobRole?
) : DatabaseBaseModel<Boolean, DomainEmployeeComplete, ID, ID> {
    override fun getRecordId() = teamMember.id
    override fun toNetworkModel() = false
    override fun toDomainModel() = DomainEmployeeComplete(
        teamMember = teamMember.toDomainModel(),
        company = company?.toDomainModel(),
        department = department?.toDomainModel(),
        subDepartment = subDepartment?.toDomainModel(),
        jobRole = jobRole?.toDomainModel()
    )
}