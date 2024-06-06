package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.room.*
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.entities.DatabaseOrdersStatus
import com.simenko.qmapp.room.contract.DaoBaseModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderStatusDao : DaoBaseModel<ID, ID, DatabaseOrdersStatus> {
    @Query("SELECT * FROM `0_orders_statuses` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseOrdersStatus>

    @Query("SELECT * FROM `0_orders_statuses` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseOrdersStatus>>

    @Query("select * from `0_orders_statuses` where id = :parentId")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseOrdersStatus>

    @Query("SELECT * FROM `0_orders_statuses` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseOrdersStatus?
}