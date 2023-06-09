package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseOrdersStatus
import com.simenko.qmapp.room.contract.DaoBaseModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderStatusDao : DaoBaseModel<DatabaseOrdersStatus> {
    @Query("SELECT * FROM `0_orders_statuses` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseOrdersStatus>

    @Query("select * from `0_orders_statuses` where id = :parentId")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseOrdersStatus>

    @Query("SELECT * FROM `0_orders_statuses` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseOrdersStatus?

    @Query("SELECT * FROM `0_orders_statuses` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseOrdersStatus>>

    @Query("SELECT * FROM `0_orders_statuses` ORDER BY id ASC")
    abstract fun getRecordsFlowForUI(): Flow<List<DatabaseOrdersStatus>>
}