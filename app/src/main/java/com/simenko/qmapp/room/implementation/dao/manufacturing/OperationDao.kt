package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseManufacturingOperation
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OperationDao: DaoBaseModel<ID, ID, DatabaseManufacturingOperation> {
    @Query("SELECT * FROM `14_manufacturing_operations` ORDER BY operationOrder ASC")
    abstract override fun getRecords(): List<DatabaseManufacturingOperation>

    @Query("SELECT * FROM `14_manufacturing_operations` ORDER BY operationOrder ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseManufacturingOperation>>

    @Query("select * from `14_manufacturing_operations` where lineId = :parentId order by operationOrder asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseManufacturingOperation>

    @Query("SELECT * FROM `14_manufacturing_operations` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseManufacturingOperation?

    @Transaction
    @Query("""
        select mo.* from `14_manufacturing_operations` as mo
        where(:lineId = -1 or mo.lineId = :lineId)
        order by mo.operationOrder
    """)
    abstract fun getRecordsFlowForUI(lineId: ID): Flow<List<DatabaseManufacturingOperation.DatabaseManufacturingOperationComplete>>

    @Transaction
    @Query("SELECT * FROM `14_manufacturing_operations` WHERE id = :id")
    abstract fun getRecordCompleteById(id: ID): DatabaseManufacturingOperation.DatabaseManufacturingOperationComplete
}