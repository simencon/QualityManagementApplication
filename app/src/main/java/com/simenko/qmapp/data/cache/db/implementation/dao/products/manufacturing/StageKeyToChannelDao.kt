package com.simenko.qmapp.data.cache.db.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseStageKeyToChannel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StageKeyToChannelDao: DaoBaseModel<ID, ID, DatabaseStageKeyToChannel> {
    @Query("SELECT * FROM `12_5_stages_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseStageKeyToChannel>

    @Query("SELECT * FROM `12_5_stages_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseStageKeyToChannel>>

    @Query("select * from `12_5_stages_keys` where chId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): Flow<List<DatabaseStageKeyToChannel>>

    @Query("SELECT * FROM `12_5_stages_keys` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseStageKeyToChannel?
}