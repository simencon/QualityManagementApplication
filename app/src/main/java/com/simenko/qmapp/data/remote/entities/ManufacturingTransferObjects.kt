package com.simenko.qmapp.data.remote.entities

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.remote.NetworkBaseModel
import com.simenko.qmapp.data.cache.db.entities.*
import com.simenko.qmapp.utils.ObjectTransformer
import kotlinx.serialization.Serializable

@Serializable
data class NetworkEmployee(
    var id: ID,
    var fullName: String,
    var companyId: ID,
    var departmentId: ID,
    var subDepartmentId: ID? = null,
    var department: String,
    var jobRoleId: ID,
    var jobRole: String,
    var email: String? = null,
    var passWord: String? = null
) : NetworkBaseModel<DatabaseEmployee> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseEmployee {
        return ObjectTransformer(NetworkEmployee::class, DatabaseEmployee::class).transform(this)
    }
}

@Serializable
data class NetworkCompany (
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
    var companyManagerId: ID
) : NetworkBaseModel<DatabaseCompany> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCompany::class, DatabaseCompany::class).transform(this)
}

@Serializable
data class NetworkJobRole(
    val id: ID,
    val companyId: ID,
    val jobRoleDescription: String
) : NetworkBaseModel<DatabaseJobRole> {
    override fun getRecordId() = this.id
    override fun toDatabaseModel() = ObjectTransformer(NetworkJobRole::class, DatabaseJobRole::class).transform(this)
}

@Serializable
data class NetworkDepartment(
    val id: ID,
    val depAbbr: String?,
    val depName: String?,
    val depManager: ID?,
    val depOrganization: String?,
    val depOrder: Int?,
    val companyId: ID?
) : NetworkBaseModel<DatabaseDepartment> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkDepartment::class, DatabaseDepartment::class).transform(this)
}

@Serializable
data class NetworkSubDepartment(
    var id: ID,
    var depId: ID,
    var subDepAbbr: String? = null,
    var subDepDesignation: String? = null,
    var subDepOrder: Int? = null
) : NetworkBaseModel<DatabaseSubDepartment> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkSubDepartment::class, DatabaseSubDepartment::class).transform(this)
}

@Serializable
data class NetworkManufacturingChannel(
    var id: ID,
    var subDepId: ID,
    var channelAbbr: String? = null,
    var channelDesignation: String? = null,
    var channelOrder: Int? = null
) : NetworkBaseModel<DatabaseManufacturingChannel> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkManufacturingChannel::class, DatabaseManufacturingChannel::class).transform(this)
}

@Serializable
data class NetworkManufacturingLine(
    var id: ID,
    var chId: ID,
    var lineAbbr: String,
    var lineDesignation: String,
    var lineOrder: Int
) : NetworkBaseModel<DatabaseManufacturingLine> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkManufacturingLine::class, DatabaseManufacturingLine::class).transform(this)
}

@Serializable
data class NetworkManufacturingOperation(
    var id: ID,
    var lineId: ID,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int,
    var equipment: String?
) : NetworkBaseModel<DatabaseManufacturingOperation> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkManufacturingOperation::class, DatabaseManufacturingOperation::class).transform(this)
}

@Serializable
data class NetworkOperationsFlow(
    var id: ID,
    var currentOperationId: ID,
    var previousOperationId: ID
) : NetworkBaseModel<DatabaseOperationsFlow> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkOperationsFlow::class, DatabaseOperationsFlow::class).transform(this)
}