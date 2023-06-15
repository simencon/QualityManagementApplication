package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseInputForOrder
import com.simenko.qmapp.room.entities.DatabaseOrdersType
import com.simenko.qmapp.room.implementation.DaoBase

@Dao
abstract class InvestigationTypeDao : DaoBase<DatabaseOrdersType> {
    @Query("SELECT * FROM `0_orders_types` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseOrdersType>

    @Query("SELECT * FROM `0_orders_types` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseOrdersType

    @Query("SELECT * FROM `0_orders_types` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseOrdersType>>
}