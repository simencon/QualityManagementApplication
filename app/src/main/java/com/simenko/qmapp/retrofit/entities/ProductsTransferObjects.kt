package com.simenko.qmapp.retrofit.entities

import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.utils.ObjectTransformer
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkElementIshModel constructor(
    var id: Int,
    var ishElement: String? = null
) : NetworkBaseModel<DatabaseElementIshModel> {
    override fun toDatabaseModel(): DatabaseElementIshModel {
        return ObjectTransformer(NetworkElementIshModel::class, DatabaseElementIshModel::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkIshSubCharacteristic constructor(
    var id: Int,
    var ishElement: String? = null,
    var measurementGroupRelatedTime: Double? = null
) : NetworkBaseModel<DatabaseIshSubCharacteristic> {
    override fun toDatabaseModel(): DatabaseIshSubCharacteristic {
        return ObjectTransformer(NetworkIshSubCharacteristic::class, DatabaseIshSubCharacteristic::class).transform(this)
    }
}

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
) : NetworkBaseModel<DatabaseManufacturingProject> {
    override fun toDatabaseModel(): DatabaseManufacturingProject {
        return ObjectTransformer(NetworkManufacturingProject::class, DatabaseManufacturingProject::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkCharacteristic constructor(
    var id: Int,
    var ishCharId: Int,
    var charOrder: Int? = null,
    var charDesignation: String? = null,
    var charDescription: String? = null,
    var ishSubChar: Int,
    var projectId: Int,
    var sampleRelatedTime: Double? = null,
    var measurementRelatedTime: Double? = null
) : NetworkBaseModel<DatabaseCharacteristic> {
    override fun toDatabaseModel(): DatabaseCharacteristic {
        return ObjectTransformer(NetworkCharacteristic::class, DatabaseCharacteristic::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkMetrix constructor(
    var id: Int,
    var charId: Int,
    var metrixOrder: Int? = null,
    var metrixDesignation: String? = null,
    var metrixDescription: String? = null,
    var units: String? = null
) : NetworkBaseModel<DatabaseMetrix> {
    override fun toDatabaseModel(): DatabaseMetrix {
        return ObjectTransformer(NetworkMetrix::class, DatabaseMetrix::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkKey(
    var id: Int,
    var projectId: Int?,
    var componentKey: String?,
    var componentKeyDescription: String?
) : NetworkBaseModel<DatabaseKey> {
    override fun toDatabaseModel(): DatabaseKey {
        return ObjectTransformer(NetworkKey::class, DatabaseKey::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkProductBase(
    var id: Int,
    var projectId: Int?,
    var componentBaseDesignation: String?
) : NetworkBaseModel<DatabaseProductBase> {
    override fun toDatabaseModel(): DatabaseProductBase {
        return ObjectTransformer(NetworkProductBase::class, DatabaseProductBase::class).transform(this)
    }
}


@JsonClass(generateAdapter = true)
data class NetworkProduct(
    var id: Int,
    var productBaseId: Int?,
    var keyId: Int?,
    var productDesignation: String?
) : NetworkBaseModel<DatabaseProduct> {
    override fun toDatabaseModel(): DatabaseProduct {
        return ObjectTransformer(NetworkProduct::class, DatabaseProduct::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkComponent(
    var id: Int,
    var keyId: Int?,
    var componentDesignation: String?,
    var ifAny: Int?
) : NetworkBaseModel<DatabaseComponent> {
    override fun toDatabaseModel(): DatabaseComponent {
        return ObjectTransformer(NetworkComponent::class, DatabaseComponent::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkComponentInStage(
    var id: Int,
    var keyId: Int?,
    var componentInStageDescription: String?,
    var ifAny: Int?
) : NetworkBaseModel<DatabaseComponentInStage> {
    override fun toDatabaseModel(): DatabaseComponentInStage {
        return ObjectTransformer(NetworkComponentInStage::class, DatabaseComponentInStage::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkVersionStatus(
    var id: Int,
    var statusDescription: String?
) : NetworkBaseModel<DatabaseVersionStatus> {
    override fun toDatabaseModel(): DatabaseVersionStatus {
        return ObjectTransformer(NetworkVersionStatus::class, DatabaseVersionStatus::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkProductVersion(
    var id: Int,
    var productId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
) : NetworkBaseModel<DatabaseProductVersion> {
    override fun toDatabaseModel(): DatabaseProductVersion {
        return ObjectTransformer(NetworkProductVersion::class, DatabaseProductVersion::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkComponentVersion(
    var id: Int,
    var componentId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
) : NetworkBaseModel<DatabaseComponentVersion> {
    override fun toDatabaseModel(): DatabaseComponentVersion {
        return ObjectTransformer(NetworkComponentVersion::class, DatabaseComponentVersion::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkComponentInStageVersion(
    var id: Int,
    var componentInStageId: Int,
    var versionDescription: String?,
    var versionDate: String?,
    var statusId: Int?,
    var isDefault: Boolean
) : NetworkBaseModel<DatabaseComponentInStageVersion> {
    override fun toDatabaseModel(): DatabaseComponentInStageVersion {
        return ObjectTransformer(NetworkComponentInStageVersion::class, DatabaseComponentInStageVersion::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkProductTolerance(
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : NetworkBaseModel<DatabaseProductTolerance> {
    override fun toDatabaseModel(): DatabaseProductTolerance {
        return ObjectTransformer(NetworkProductTolerance::class, DatabaseProductTolerance::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkComponentTolerance(
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : NetworkBaseModel<DatabaseComponentTolerance> {
    override fun toDatabaseModel(): DatabaseComponentTolerance {
        return ObjectTransformer(NetworkComponentTolerance::class, DatabaseComponentTolerance::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkComponentInStageTolerance(
    var id: Int,
    var metrixId: Int?,
    var versionId: Int?,
    var nominal: Float?,
    var lsl: Float?,
    var usl: Float?,
    var isActual: Boolean
) : NetworkBaseModel<DatabaseComponentInStageTolerance> {
    override fun toDatabaseModel(): DatabaseComponentInStageTolerance {
        return ObjectTransformer(NetworkComponentInStageTolerance::class, DatabaseComponentInStageTolerance::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkProductToLine(
    var id: Int,
    var lineId: Int,
    var productId: Int
) : NetworkBaseModel<DatabaseProductToLine> {
    override fun toDatabaseModel(): DatabaseProductToLine {
        return ObjectTransformer(NetworkProductToLine::class, DatabaseProductToLine::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkComponentToLine(
    var id: Int,
    var lineId: Int,
    var componentId: Int
) : NetworkBaseModel<DatabaseComponentToLine> {
    override fun toDatabaseModel(): DatabaseComponentToLine {
        return ObjectTransformer(NetworkComponentToLine::class, DatabaseComponentToLine::class).transform(this)
    }
}

@JsonClass(generateAdapter = true)
data class NetworkComponentInStageToLine(
    var id: Int,
    var lineId: Int,
    var componentInStageId: Int
) : NetworkBaseModel<DatabaseComponentInStageToLine> {
    override fun toDatabaseModel(): DatabaseComponentInStageToLine {
        return ObjectTransformer(NetworkComponentInStageToLine::class, DatabaseComponentInStageToLine::class).transform(this)
    }
}
