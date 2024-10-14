package com.simenko.qmapp.data.cache.db.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.DatabaseOperationsFlow
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OperationsFlowDao : DaoBaseModel<ID, ID, DatabaseOperationsFlow> {
    @Query("SELECT * FROM `14_14_manufacturing_operations_flow` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseOperationsFlow>

    @Query("SELECT * FROM `14_14_manufacturing_operations_flow` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseOperationsFlow>>

    @Query("select * from `14_14_manufacturing_operations_flow` where previousOperationId = :parentId order by id asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseOperationsFlow>

    @Query("SELECT * FROM `14_14_manufacturing_operations_flow` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseOperationsFlow?
}