package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.room.*
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.entities.DatabaseOrdersType
import com.simenko.qmapp.room.contract.DaoBaseModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class InvestigationTypeDao : DaoBaseModel<ID, ID, DatabaseOrdersType> {
    @Query("SELECT * FROM `0_orders_types` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseOrdersType>

    @Query("SELECT * FROM `0_orders_types` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseOrdersType>>

    /**
     * as parent is used id but in fact should be companyId in future
     * */
    @Query("select * from `0_orders_types` where id = :parentId")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseOrdersType>

    @Query("SELECT * FROM `0_orders_types` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseOrdersType?
}