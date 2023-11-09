package com.simenko.qmapp.retrofit.entities.products

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.products.*
import com.simenko.qmapp.utils.ObjectTransformer
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkProductToLine(
    var id: ID,
    var lineId: ID,
    var productId: ID
) : NetworkBaseModel<DatabaseProductToLine> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkProductToLine::class, DatabaseProductToLine::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentToLine(
    var id: ID,
    var lineId: ID,
    var componentId: ID
) : NetworkBaseModel<DatabaseComponentToLine> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentToLine::class, DatabaseComponentToLine::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkComponentInStageToLine(
    var id: ID,
    var lineId: ID,
    var componentInStageId: ID
) : NetworkBaseModel<DatabaseComponentInStageToLine> {
    override fun getRecordId() = id
    override fun toDatabaseModel() = ObjectTransformer(NetworkComponentInStageToLine::class, DatabaseComponentInStageToLine::class).transform(this)
}