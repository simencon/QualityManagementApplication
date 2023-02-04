package com.simenko.qmapp.retrofit_entities

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkElementIshModel constructor(
    var id: Int,
    var ishElement: String? = null
)

@JsonClass(generateAdapter = true)
data class NetworkIshSubCharacteristic constructor(
    var id: Int,
    var ishElement: String? = null,
    var measurementGroupRelatedTime: Double? = null
)

@JsonClass(generateAdapter = true)
data class NetworkManufacturingProject(
    var id: Int,
    var companyId: Int,
    var factoryLocationDep: Int? = null,
    var factoryLocationDetails: String? = null,
    var customerName: String? = null,
    var team: Int? = null,
    var modelYear: String? = null,
    var projectSubject: String? = null,
    var startDate: String? = null,
    var revisionDate: String? = null,
    var refItem: String? = null,
    var pfmeaNum: String? = null,
    var processOwner: Int? = null,
    var confLevel: Int? = null
)

@JsonClass(generateAdapter = true)
data class NetworkCharacteristic constructor(
    var id: Int,
    var ishCharId: Int,
    var charOrder: Int? = null,
    var ishCharDesignation: String? = null,
    var typeDescription: String? = null,
    var ishSubChar: Int,
    var projectId: Int,
    var sampleRelatedTime: Double? = null,
    var measurementRelatedTime: Double? = null
)

@JsonClass(generateAdapter = true)
data class NetworkMetrix constructor(
    var id: Int,
    var charId: Int,
    var metrixOrder: Int? = null,
    var metrixDesignation: String? = null,
    var metrixDescription: String? = null
)