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

data class DomainManufacturingProject(
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

data class DomainCharacteristic constructor(
    var id: Int,
    var ishCharId: Int,
    var charOrder: Int? = null,
    var charDesignation: String? = null,
    var charDescription: String? = null,
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

data class DomainKey(
    var id: Int,
    var projectId: Int?,
    var componentKey: String?,
    var componentKeyDescription: String?
)

data class DomainProductBase(
    var id: Int,
    var projectId: Int?,
    var componentBaseDesignation: String?
)

data class DomainProduct(
    var id: Int,
    var productBaseId: Int?,
    var keyId: Int?,
    var productDesignation: String?
)

data class DomainComponent(
    var id: Int,
    var keyId: Int?,
    var componentDesignation: String?,
    var ifAny: Int?
)

data class DomainComponentInStage(
    var id: Int,
    var keyId: Int?,
    var componentInStageDescription: String?,
    var ifAny: Int?
)

data class DomainVersionStatus(
    var id: Int,
    var statusDescription: String?
)

data class DomainProductVersion(
    var id: Int,
    var productId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
)

data class DomainComponentVersion(
    var id: Int,
    var componentId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
)

data class DomainComponentInStageVersion(
    var id: Int,
    var componentInStageId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
)

data class ProductTolerance(
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
)
