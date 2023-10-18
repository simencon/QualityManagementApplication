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

@DatabaseView(
    viewName = "manufacturingOperationsComplete",
    value = " select * from `14_manufacturing_operations` as mo order by mo.operationOrder;"
)
data class DatabaseManufacturingOperationComplete(
    @Embedded
    val operation: DatabaseManufacturingOperation,
    @Relation(
        entity = DatabasePreviousOperationComplete::class,
        parentColumn = "id",
        entityColumn = "nextOperationId"
    )
    val previousOperations: List<DatabasePreviousOperationComplete>
) : DatabaseBaseModel<Any?, DomainManufacturingOperationComplete> {
    @DatabaseView(
        viewName = "manufacturingOperationsFlowsComplete",
        value = """
        select mof.id, mof.currentOperationId as nextOperationId, d.depAbbr, sd.subDepAbbr, mc.channelAbbr, ml.lineAbbr, pmo.operationAbbr, pmo.operationDesignation, pmo.equipment
        from `14_14_manufacturing_operations_flow` as mof
        inner join `14_manufacturing_operations` as pmo on mof.previousOperationId = pmo.id
        inner join `13_manufacturing_lines` as ml on pmo.lineId = ml.id
        inner join `12_manufacturing_channels` as mc on ml.chId = mc.id
        inner join `11_sub_departments` as sd on mc.subDepId = sd.id
        inner join `10_departments` as d on sd.depId = d.id
        order by pmo.operationOrder, ml.lineOrder, mc.channelOrder, sd.subDepOrder, d.depAbbr
        """
    )
    data class DatabasePreviousOperationComplete(
        val id: Int,
        val nextOperationId: Int,
        val depAbbr: String?,
        val subDepAbbr: String?,
        val channelAbbr: String?,
        val lineAbbr: String?,
        val operationAbbr: String?,
        val operationDesignation: String?,
        val equipment: String?
    ) : DatabaseBaseModel<Any?, DomainManufacturingOperationComplete.DomainPreviousOperationComplete> {
        override fun getRecordId(): Any = this.id
        override fun toNetworkModel(): Any? = null
        override fun toDomainModel(): DomainManufacturingOperationComplete.DomainPreviousOperationComplete =
            ObjectTransformer(DatabasePreviousOperationComplete::class, DomainManufacturingOperationComplete.DomainPreviousOperationComplete::class).transform(this)
    }

    override fun getRecordId(): Any = this.operation.id
    override fun toNetworkModel(): Any? = null
    override fun toDomainModel(): DomainManufacturingOperationComplete {
        return DomainManufacturingOperationComplete(
            operation = this.operation.toDomainModel(),
            previousOperations = this.previousOperations.map { it.toDomainModel() }
        )
    }
}