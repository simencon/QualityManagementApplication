package com.simenko.qmapp.domain

data class DomainElementIshModel constructor(
    var id: Int,
    var ishElement: String? = null
)

data class DomainIshSubCharacteristic constructor(
    var id: Int,
    var ishElement: String? = null,
    var measurementGroupRelatedTime: Double? = null
)

data class DomainCharacteristic constructor(
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

data class DomainMetrix constructor(
    var id: Int,
    var charId: Int,
    var metrixOrder: Int? = null,
    var metrixDesignation: String? = null,
    var metrixDescription: String? = null
)