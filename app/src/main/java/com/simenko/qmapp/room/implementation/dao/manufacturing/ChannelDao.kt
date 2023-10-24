package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseManufacturingChannel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ChannelDao: DaoBaseModel<DatabaseManufacturingChannel> {
    @Query("SELECT * FROM `12_manufacturing_channels` ORDER BY channelOrder ASC")
    abstract override fun getRecords(): List<DatabaseManufacturingChannel>

    @Query("select * from `12_manufacturing_channels` where subDepId = :parentId order by channelOrder asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseManufacturingChannel>

    @Query("SELECT * FROM `12_manufacturing_channels` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseManufacturingChannel?

    @Query("SELECT * FROM `12_manufacturing_channels` ORDER BY channelOrder ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseManufacturingChannel>>

    @Query("SELECT * FROM `12_manufacturing_channels` ORDER BY channelOrder ASC")
    abstract fun getRecordsFlowForUI(): Flow<List<DatabaseManufacturingChannel>>

    @Transaction
    @Query("SELECT * FROM manufacturingChannelsWithParents WHERE id = :id")
    abstract fun getRecordCompleteById(id: Int): DomainManufacturingChannel.DomainManufacturingChannelWithParents
}