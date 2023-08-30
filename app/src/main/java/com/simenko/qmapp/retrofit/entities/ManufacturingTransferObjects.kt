package com.simenko.qmapp.retrofit.entities

import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ObjectTransformer
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkTeamMember(
    var id: Int,
    var departmentId: Int,
    var department: String,
    var email: String? = null,
    var fullName: String,
    var jobRole: String,
    var roleLevelId: Int,
    var passWord: String? = null,
    var companyId: Int
) : NetworkBaseModel<DatabaseTeamMember> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseTeamMember {
        return ObjectTransformer(NetworkTeamMember::class, DatabaseTeamMember::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkCompany constructor(
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
    var companyManagerId: Int
) : NetworkBaseModel<DatabaseCompany> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseCompany {
        return ObjectTransformer(NetworkCompany::class, DatabaseCompany::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkDepartment(
    val id: Int,
    val depAbbr: String?,
    val depName: String?,
    val depManager: Int?,
    val depOrganization: String?,
    val depOrder: Int?,
    val companyId: Int?
) : NetworkBaseModel<DatabaseDepartment> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseDepartment {
        return ObjectTransformer(NetworkDepartment::class, DatabaseDepartment::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkSubDepartment(
    var id: Int,
    var depId: Int,
    var subDepAbbr: String? = null,
    var subDepDesignation: String? = null,
    var subDepOrder: Int? = null
) : NetworkBaseModel<DatabaseSubDepartment> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseSubDepartment {
        return ObjectTransformer(NetworkSubDepartment::class, DatabaseSubDepartment::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkManufacturingChannel(
    var id: Int,
    var subDepId: Int,
    var channelAbbr: String? = null,
    var channelDesignation: String? = null,
    var channelOrder: Int? = null
) : NetworkBaseModel<DatabaseManufacturingChannel> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseManufacturingChannel {
        return ObjectTransformer(NetworkManufacturingChannel::class, DatabaseManufacturingChannel::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkManufacturingLine(
    var id: Int,
    var chId: Int,
    var lineAbbr: String,
    var lineDesignation: String,
    var lineOrder: Int
) : NetworkBaseModel<DatabaseManufacturingLine> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseManufacturingLine {
        return ObjectTransformer(NetworkManufacturingLine::class, DatabaseManufacturingLine::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkManufacturingOperation(
    var id: Int,
    var lineId: Int,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int,
    var equipment: String?
) : NetworkBaseModel<DatabaseManufacturingOperation> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseManufacturingOperation {
        return ObjectTransformer(NetworkManufacturingOperation::class, DatabaseManufacturingOperation::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkOperationsFlow(
    var id: Int,
    var currentOperationId: Int,
    var previousOperationId: Int
) : NetworkBaseModel<DatabaseOperationsFlow> {
    override fun getRecordId() = id
    override fun toDatabaseModel(): DatabaseOperationsFlow {
        return ObjectTransformer(NetworkOperationsFlow::class, DatabaseOperationsFlow::class).transform(this)
    }
}