package com.simenko.qmapp.data.cache.db.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.DatabaseManufacturingChannel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ChannelDao : DaoBaseModel<ID, ID, DatabaseManufacturingChannel> {
    @Query("SELECT * FROM `12_manufacturing_channels` ORDER BY channelOrder ASC")
    abstract override fun getRecords(): List<DatabaseManufacturingChannel>

    @Query("SELECT * FROM `12_manufacturing_channels` ORDER BY channelOrder ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseManufacturingChannel>>

    @Query("select * from `12_manufacturing_channels` where subDepId = :parentId order by channelOrder asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseManufacturingChannel>

    @Query("SELECT * FROM `12_manufacturing_channels` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseManufacturingChannel?

    @Query(
        """
        SELECT * FROM `12_manufacturing_channels` as mc
        WHERE (:parentId = -1 or mc.subDepId = :parentId)
        ORDER BY channelOrder ASC
    """
    )
    abstract fun getRecordsFlowForUI(parentId: ID): Flow<List<DatabaseManufacturingChannel>>

    @Query("SELECT * FROM manufacturingChannelsWithParents WHERE id = :id")
    abstract fun getRecordWithParentsById(id: ID): DatabaseManufacturingChannel.DatabaseManufacturingChannelWithParents

    @Transaction
    @Query("SELECT * FROM `12_manufacturing_channels` AS mc WHERE id = :id ORDER BY mc.channelOrder;")
    abstract fun getRecordCompleteById(id: ID): DatabaseManufacturingChannel.DatabaseManufacturingChannelComplete
}