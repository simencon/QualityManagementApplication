package com.simenko.qmapp.room.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentKeyToChannel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentKeyToChannelDao: DaoBaseModel<ID, ID, DatabaseComponentKeyToChannel> {
    @Query("SELECT * FROM `12_3_components_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentKeyToChannel>

    @Query("SELECT * FROM `12_3_components_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentKeyToChannel>>

    @Query("select * from `12_3_components_keys` where chId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): Flow<List<DatabaseComponentKeyToChannel>>

    @Query("SELECT * FROM `12_3_components_keys` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentKeyToChannel?
}