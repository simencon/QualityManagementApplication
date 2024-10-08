package com.simenko.qmapp.room.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductKeyToChannel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKeyToChannelDao: DaoBaseModel<ID, ID, DatabaseProductKeyToChannel> {
    @Query("SELECT * FROM `12_1_products_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductKeyToChannel>

    @Query("SELECT * FROM `12_1_products_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductKeyToChannel>>

    @Query("select * from `12_1_products_keys` where chId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProductKeyToChannel>

    @Query("SELECT * FROM `12_1_products_keys` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductKeyToChannel?
}