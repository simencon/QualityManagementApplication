package com.simenko.qmapp.room.entities

import androidx.room.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.room.DatabaseBaseModel
import com.simenko.qmapp.utils.ObjectTransformer

@Entity(tableName = "0_position_levels")
data class DatabasePositionLevel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var levelDescription: String
) : DatabaseBaseModel<NetworkPositionLevel, DomainPositionLevel> {
    override fun toNetworkModel() = ObjectTransformer(DatabasePositionLevel::class, NetworkPositionLevel::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabasePositionLevel::class, DomainPositionLevel::class).transform(this)
}

@Entity(
    tableName = "8_team_members",
    foreignKeys = [
//        ToDo Cannot be used as foreign key because appears before Departments
//        ForeignKey(
//            entity = DatabaseDepartment::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("departmentId"),
//            onDelete = ForeignKey.NO_ACTION,
//            onUpdate = ForeignKey.NO_ACTION
//        ),
        ForeignKey(
            entity = DatabasePositionLevel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("roleLevelId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
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
data class DatabaseTeamMember constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(index = true)
    var departmentId: Int,
    var department: String,
    var email: String? = null,
    var fullName: String,
    var jobRole: String,
    @ColumnInfo(index = true)
    var roleLevelId: Int,
    var passWord: String? = null,
    @ColumnInfo(index = true)
    var companyId: Int
) : DatabaseBaseModel<NetworkTeamMember, DomainTeamMember> {
    override fun toNetworkModel() = ObjectTransformer(DatabaseTeamMember::class, NetworkTeamMember::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseTeamMember::class, DomainTeamMember::class).transform(this)
}

@Entity(
    tableName = "0_companies",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseTeamMember::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("companyManagerId"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )]
)
data class DatabaseCompany constructor(
    @PrimaryKey
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
    override fun toNetworkModel() = ObjectTransformer(DatabaseCompany::class, NetworkCompany::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseCompany::class, DomainCompany::class).transform(this)
}

@Entity(
    tableName = "10_departments",
    foreignKeys = [
        ForeignKey(
            entity = DatabaseTeamMember::class,
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
    override fun toNetworkModel() = ObjectTransformer(DatabaseOperationsFlow::class, NetworkOperationsFlow::class).transform(this)
    override fun toDomainModel() = ObjectTransformer(DatabaseOperationsFlow::class, DomainOperationsFlow::class).transform(this)
}

data class DatabaseTeamMemberComplete(
    @Embedded
    val teamMember: DatabaseTeamMember,
    @Relation(
        entity = DatabaseDepartment::class,
        parentColumn = "departmentId",
        entityColumn = "id"
    )
    val department: DatabaseDepartment?,
    @Relation(
        entity = DatabaseCompany::class,
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val company: DatabaseCompany?
) : DatabaseBaseModel<Boolean, DomainTeamMemberComplete> {
    override fun toNetworkModel() = false
    override fun toDomainModel() = DomainTeamMemberComplete(
        teamMember = teamMember.toDomainModel(),
        department = department?.toDomainModel(),
        company = company?.toDomainModel()
    )
}

data class DatabaseDepartmentsComplete(
    @Embedded
    val department: DatabaseDepartment,
    @Relation(
        entity = DatabaseTeamMember::class,
        parentColumn = "depManager",
        entityColumn = "id"
    )
    val depManager: DatabaseTeamMember,
    @Relation(
        entity = DatabaseCompany::class,
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val company: DatabaseCompany
) : DatabaseBaseModel<Any?, DomainDepartmentComplete> {
    override fun toNetworkModel() = null
    override fun toDomainModel() = DomainDepartmentComplete(
        department = department.toDomainModel(),
        depManager = depManager.toDomainModel(),
        company = company.toDomainModel()
    )
}