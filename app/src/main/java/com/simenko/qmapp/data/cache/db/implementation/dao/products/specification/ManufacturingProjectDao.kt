package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseProductLine
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ManufacturingProjectDao : DaoBaseModel<ID, ID, DatabaseProductLine> {
    @Query("SELECT * FROM `0_manufacturing_project` ORDER BY startDate ASC")
    abstract override fun getRecords(): List<DatabaseProductLine>

    @Query("SELECT * FROM `0_manufacturing_project` ORDER BY startDate ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductLine>>

    @Query("select * from `0_manufacturing_project` where companyId = :parentId order by startDate asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProductLine>

    @Query("SELECT * FROM `0_manufacturing_project` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductLine?

    @Transaction
    @Query("SELECT * FROM `0_manufacturing_project` WHERE id = :id")
    abstract fun getRecordCompleteById(id: ID): DatabaseProductLine.DatabaseProductLineComplete?

    @Transaction
    @Query("select * from product_line_complete where companyId = :parentId;")
    abstract fun getRecordsCompleteForUI(parentId: ID): Flow<List<DatabaseProductLine.DatabaseProductLineComplete>>
}