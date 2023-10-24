package com.simenko.qmapp.room.entities

import androidx.room.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.room.contract.DatabaseBaseModel
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
data class DatabaseEmployee constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var fullName: String,
    @ColumnInfo(index = true)
    var companyId: Int,
    @ColumnInfo(index = true)
    var departmentId: Int,
    @ColumnInfo(index = true)
    var subDepartmentId: Int? = null,
    var department: String,
    @ColumnInfo(index = true)
    var jobRoleId: Int,
    var jobRole: String,
    var email: String? = null,
    var passWord: String? = null
) : DatabaseBaseModel<NetworkEmployee, DomainEmployee> {
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
data class DatabaseCompany constructor(
    @PrimaryKey(autoGenerate = true)
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
    @ColumnInfo(index = true)
    var companyManagerId: Int
) : DatabaseBaseModel<NetworkCompany, DomainCompany> {
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
    var id: Int,
    @ColumnInfo(index = true)
    var companyId: Int,
    var jobRoleDescription: String
) : DatabaseBaseModel<NetworkJobRole, DomainJobRole> {
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
data class DatabaseDepartment constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val depAbbr: String?,
    val depName: String?,
    @ColumnInfo(index = true)
    val depManager: Int?,
    val depOrganization: String?,
    val depOrder: Int?,
    @ColumnInfo(index = true)
    val companyId: Int?
) : DatabaseBaseModel<NetworkDepartment, DomainDepartment> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseDepartment::class, NetworkDepartment::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseDepartment::class, DomainDepartment::class).transform(this)
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
    var id: Int,
    @ColumnInfo(index = true)
    var depId: Int,
    var subDepAbbr: String? = null,
    var subDepDesignation: String? = null,
    var subDepOrder: Int? = null
) : DatabaseBaseModel<NetworkSubDepartment, DomainSubDepartment> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseSubDepartment::class, NetworkSubDepartment::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseSubDepartment::class, DomainSubDepartment::class).transform(this)
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
    var id: Int,
    @ColumnInfo(index = true)
    var subDepId: Int,
    var channelAbbr: String? = null,
    var channelDesignation: String? = null,
    var channelOrder: Int? = null
) : DatabaseBaseModel<NetworkManufacturingChannel, DomainManufacturingChannel> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseManufacturingChannel::class, NetworkManufacturingChannel::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseManufacturingChannel::class, DomainManufacturingChannel::class).transform(this)

    @DatabaseView(
        viewName = "manufacturingChannelsWithParents",
        value = """
        select d.id as departmentId, d.depOrder, d.depAbbr, d.depName, 
        sd.id as subDepartmentId, sd.subDepOrder, sd.subDepAbbr, sd.subDepDesignation, 
        mc.id, mc.channelOrder, mc.channelAbbr, mc.channelDesignation
        from `12_manufacturing_channels` as mc
        inner join `11_sub_departments` as sd on mc.subDepId = sd.id
        inner join `10_departments` as d on sd.depId = d.id
        order by d.depOrder, sd.subDepOrder, mc.channelOrder;
        """
    )
    data class DatabaseManufacturingChannelWithParents(
        val departmentId: Int,
        val depOrder: Int,
        val depAbbr: String?,
        val depName: String?,
        val subDepartmentId: Int,
        val subDepOrder: Int,
        val subDepAbbr: String?,
        val subDepDesignation: String?,
        val id: Int,
        val channelOrder: Int,
        val channelAbbr: String?,
        val channelDesignation: String?
    ) : DatabaseBaseModel<Any?, DomainManufacturingChannel.DomainManufacturingChannelWithParents> {
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
    var id: Int,
    @ColumnInfo(index = true)
    var chId: Int,
    var lineAbbr: String,
    var lineDesignation: String,
    var lineOrder: Int
) : DatabaseBaseModel<NetworkManufacturingLine, DomainManufacturingLine> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseManufacturingLine::class, NetworkManufacturingLine::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseManufacturingLine::class, DomainManufacturingLine::class).transform(this)

    @DatabaseView(
        viewName = "manufacturingLinesComplete",
        value = " select * from `13_manufacturing_lines` as ml order by ml.lineOrder;"
    )
    data class DatabaseManufacturingLineComplete(
        @Embedded
        val line: DatabaseManufacturingLine,
        @Relation(
            entity = DatabaseManufacturingChannel.DatabaseManufacturingChannelWithParents::class,
            parentColumn = "chId",
            entityColumn = "id"
        )
        val channelComplete: DatabaseManufacturingChannel.DatabaseManufacturingChannelWithParents,
    ) : DatabaseBaseModel<Any?, DomainManufacturingLine.DomainManufacturingLineComplete> {
        override fun getRecordId(): Any = this.line.id
        override fun toNetworkModel(): Any? = null
        override fun toDomainModel(): DomainManufacturingLine.DomainManufacturingLineComplete {
            return DomainManufacturingLine.DomainManufacturingLineComplete(
                line = this.line.toDomainModel(),
                channelComplete = this.channelComplete.toDomainModel()
            )
        }
    }

    @DatabaseView(
        viewName = "manufacturingLinesWithParents",
        value = """
        select d.id as departmentId, d.depOrder, d.depAbbr, d.depName, 
        sd.id as subDepartmentId, sd.subDepOrder, sd.subDepAbbr, sd.subDepDesignation, 
        mc.id as channelId, mc.channelOrder, mc.channelAbbr, mc.channelDesignation, 
        ml.id, ml.lineOrder, ml.lineAbbr, ml.lineDesignation 
        from `13_manufacturing_lines` as ml
        inner join `12_manufacturing_channels` as mc on ml.chId = mc.id
        inner join `11_sub_departments` as sd on mc.subDepId = sd.id
        inner join `10_departments` as d on sd.depId = d.id
        order by d.depOrder, sd.subDepOrder, mc.channelOrder, ml.lineOrder;
        """
    )
    data class DatabaseManufacturingLineWithParents(
        val departmentId: Int,
        val depOrder: Int,
        val depAbbr: String?,
        val depName: String?,
        val subDepartmentId: Int,
        val subDepOrder: Int,
        val subDepAbbr: String?,
        val subDepDesignation: String?,
        val channelId: Int,
        val channelOrder: Int,
        val channelAbbr: String?,
        val channelDesignation: String?,
        val id: Int,
        val lineOrder: Int,
        val lineAbbr: String?,
        val lineDesignation: String
    ) : DatabaseBaseModel<Any?, DomainManufacturingLine.DomainManufacturingLineWithParents> {
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
    var id: Int,
    @ColumnInfo(index = true)
    var lineId: Int,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int,
    var equipment: String?
) : DatabaseBaseModel<NetworkManufacturingOperation, DomainManufacturingOperation> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseManufacturingOperation::class, NetworkManufacturingOperation::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseManufacturingOperation::class, DomainManufacturingOperation::class).transform(this)

    @DatabaseView(
        viewName = "manufacturingOperationsComplete",
        value = " select * from `14_manufacturing_operations` as mo order by mo.operationOrder;"
    )
    data class DatabaseManufacturingOperationComplete(
        @Embedded
        val operation: DatabaseManufacturingOperation,
        @Relation(
            entity = DatabaseManufacturingLine.DatabaseManufacturingLineWithParents::class,
            parentColumn = "lineId",
            entityColumn = "id"
        )
        val lineComplete: DatabaseManufacturingLine.DatabaseManufacturingLineWithParents,
        @Relation(
            entity = DatabaseOperationsFlow.DatabaseOperationsFlowComplete::class,
            parentColumn = "id",
            entityColumn = "currentOperationId"
        )
        val previousOperations: List<DatabaseOperationsFlow.DatabaseOperationsFlowComplete>
    ) : DatabaseBaseModel<Any?, DomainManufacturingOperation.DomainManufacturingOperationComplete> {
        override fun getRecordId(): Any = this.operation.id
        override fun toNetworkModel(): Any? = null
        override fun toDomainModel(): DomainManufacturingOperation.DomainManufacturingOperationComplete {
            return DomainManufacturingOperation.DomainManufacturingOperationComplete(
                operation = this.operation.toDomainModel(),
                lineComplete = this.lineComplete.toDomainModel(),
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
    var id: Int,
    @ColumnInfo(index = true)
    var currentOperationId: Int,
    @ColumnInfo(index = true)
    var previousOperationId: Int
) : DatabaseBaseModel<NetworkOperationsFlow, DomainOperationsFlow> {
    override fun getRecordId() = id
    override fun toNetworkModel() = ObjectTransformer(DatabaseOperationsFlow::class, NetworkOperationsFlow::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseOperationsFlow::class, DomainOperationsFlow::class).transform(this)

    @DatabaseView(
        viewName = "operationsFlowsComplete",
        value = """
        select mof.id, mof.currentOperationId, mof.previousOperationId, pmo.operationOrder, pmo.operationAbbr, pmo.operationDesignation, pmo.equipment, 
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
        order by d.depOrder, sd.subDepOrder, mc.channelOrder, ml.lineOrder, pmo.operationOrder;
        """
    )
    data class DatabaseOperationsFlowComplete(
        val id: Int,
        val currentOperationId: Int,
        val previousOperationId: Int,
        val operationOrder: Int,
        val operationAbbr: String?,
        val operationDesignation: String?,
        val equipment: String?,
        val depId: Int,
        val depOrder: Int,
        val depAbbr: String?,
        val subDepId: Int,
        val subDepOrder: Int,
        val subDepAbbr: String?,
        val channelId: Int,
        val channelOrder: Int,
        val channelAbbr: String?,
        val lineId: Int,
        val lineOrder: Int,
        val lineAbbr: String?,
    ) : DatabaseBaseModel<Any?, DomainOperationsFlow.DomainOperationsFlowComplete> {
        override fun getRecordId(): Any = this.id
        override fun toNetworkModel(): Any? = null
        override fun toDomainModel(): DomainOperationsFlow.DomainOperationsFlowComplete =
            ObjectTransformer(DatabaseOperationsFlowComplete::class, DomainOperationsFlow.DomainOperationsFlowComplete::class).transform(this)
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
) : DatabaseBaseModel<Boolean, DomainEmployeeComplete> {
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
) : DatabaseBaseModel<Any?, DomainDepartmentComplete> {
    override fun getRecordId() = department.id
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainDepartmentComplete(
        department = department.toDomainModel(),
        depManager = depManager.toDomainModel(),
        company = company.toDomainModel()
    )
}