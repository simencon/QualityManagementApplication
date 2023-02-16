package com.simenko.qmapp.retrofit.entities

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkPositionLevel(
    var id: Int,
    var levelDescription: String
)

@JsonClass(generateAdapter = true)
data class NetworkTeamMembers(
    var id: Int,
    var departmentId: Int,
    var department: String,
    var email: String? = null,
    var fullName: String,
    var jobRole: String,
    var roleLevelId: Int,
    var passWord: String? = null,
    var companyId: Int
)

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
)

@JsonClass(generateAdapter = true)
data class NetworkDepartment(
    val id: Int,
    val depAbbr: String?,
    val depName: String?,
    val depManager: Int?,
    val depOrganization: String?,
    val depOrder: Int?,
    val companyId: Int?
)

@JsonClass(generateAdapter = true)
data class NetworkSubDepartment(
    var id: Int,
    var depId: Int,
    var subDepAbbr: String? = null,
    var subDepDesignation: String? = null,
    var subDepOrder: Int? = null
)

@JsonClass(generateAdapter = true)
data class NetworkManufacturingChannel(
    var id: Int,
    var subDepId: Int,
    var channelAbbr: String? = null,
    var channelDesignation: String? = null,
    var channelOrder: Int? = null
)

@JsonClass(generateAdapter = true)
data class NetworkManufacturingLine(
    var id: Int,
    var chId: Int,
    var lineAbbr: String,
    var lineDesignation: String,
    var lineOrder: Int
)

@JsonClass(generateAdapter = true)
data class NetworkManufacturingOperation(
    var id: Int,
    var lineId: Int,
    var operationAbbr: String,
    var operationDesignation: String,
    var operationOrder: Int
)