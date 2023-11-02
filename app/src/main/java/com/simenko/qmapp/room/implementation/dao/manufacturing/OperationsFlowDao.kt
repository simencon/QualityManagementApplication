package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseOperationsFlow
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OperationsFlowDao: DaoBaseModel<DatabaseOperationsFlow> {
    @Query("SELECT * FROM `14_14_manufacturing_operations_flow` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseOperationsFlow>

    @Query("select * from `14_14_manufacturing_operations_flow` where previousOperationId = :parentId order by id asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseOperationsFlow>

    @Query("SELECT * FROM `14_14_manufacturing_operations_flow` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseOperationsFlow?

    @Query("SELECT * FROM `14_14_manufacturing_operations_flow` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseOperationsFlow>>
}