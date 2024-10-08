package com.simenko.qmapp.retrofit.entities.products

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkProductLineToDepartment(
    val id: ID,
    val depId: ID,
    @SerialName("projectId") val productLineId: ID
) : NetworkBaseModel<DatabaseProductLineToDepartment> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductLineToDepartment::class, DatabaseProductLineToDepartment::class).transform(this)
}

@Serializable
data class NetworkProductKindToSubDepartment(
    val id: ID,
    val subDepId: ID,
    val prodKindId: ID
) : NetworkBaseModel<DatabaseProductKindToSubDepartment> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKindToSubDepartment::class, DatabaseProductKindToSubDepartment::class).transform(this)
}

@Serializable
data class NetworkComponentKindToSubDepartment(
    val id: ID,
    val subDepId: ID,
    val compKindId: ID
) : NetworkBaseModel<DatabaseComponentKindToSubDepartment> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKindToSubDepartment::class, DatabaseComponentKindToSubDepartment::class).transform(this)
}

@Serializable
data class NetworkStageKindToSubDepartment(
    val id: ID,
    val subDepId: ID,
    @SerialName("compStageId") val stageKindId: ID
) : NetworkBaseModel<DatabaseStageKindToSubDepartment> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkStageKindToSubDepartment::class, DatabaseStageKindToSubDepartment::class).transform(this)
}

@Serializable
data class NetworkProductKeyToChannel(
    val id: ID,
    val chId: ID,
    val keyId: ID
) : NetworkBaseModel<DatabaseProductKeyToChannel> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductKeyToChannel::class, DatabaseProductKeyToChannel::class).transform(this)
}

@Serializable
data class NetworkComponentKeyToChannel(
    val id: ID,
    val chId: ID,
    val keyId: ID
) : NetworkBaseModel<DatabaseComponentKeyToChannel> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentKeyToChannel::class, DatabaseComponentKeyToChannel::class).transform(this)
}

@Serializable
data class NetworkStageKeyToChannel(
    val id: ID,
    val chId: ID,
    val keyId: ID
) : NetworkBaseModel<DatabaseStageKeyToChannel> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkStageKeyToChannel::class, DatabaseStageKeyToChannel::class).transform(this)
}

@Serializable
data class NetworkProductToLine(
    val id: ID,
    val lineId: ID,
    val productId: ID
) : NetworkBaseModel<DatabaseProductToLine> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductToLine::class, DatabaseProductToLine::class).transform(this)
}

@Serializable
data class NetworkComponentToLine(
    val id: ID,
    val lineId: ID,
    val componentId: ID
) : NetworkBaseModel<DatabaseComponentToLine> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentToLine::class, DatabaseComponentToLine::class).transform(this)
}

@Serializable
data class NetworkComponentInStageToLine(
    val id: ID,
    val lineId: ID,
    val componentInStageId: ID
) : NetworkBaseModel<DatabaseComponentInStageToLine> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentInStageToLine::class, DatabaseComponentInStageToLine::class).transform(this)
}

@Serializable
data class NetworkCharacteristicToOperation(
    val id: ID,
    val charId: ID,
    val operationId: ID
) : NetworkBaseModel<DatabaseCharacteristicToOperation> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkCharacteristicToOperation::class, DatabaseCharacteristicToOperation::class).transform(this)
}