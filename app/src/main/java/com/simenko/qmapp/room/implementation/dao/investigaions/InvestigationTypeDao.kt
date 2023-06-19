package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseOrdersType
import com.simenko.qmapp.room.contract.DaoBaseModel

@Dao
abstract class InvestigationTypeDao : DaoBaseModel<DatabaseOrdersType> {
    @Query("SELECT * FROM `0_orders_types` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseOrdersType>

    /**
     * as parent is used id but in fact should be companyId in future
     * */
    @Query("select * from `0_orders_types` where id = :parentId")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseOrdersType>

    @Query("SELECT * FROM `0_orders_types` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseOrdersType?

    @Query("SELECT * FROM `0_orders_types` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseOrdersType>>
}