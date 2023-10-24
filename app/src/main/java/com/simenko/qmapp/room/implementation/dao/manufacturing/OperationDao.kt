package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseManufacturingOperation
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OperationDao: DaoBaseModel<DatabaseManufacturingOperation> {
    @Query("SELECT * FROM `14_manufacturing_operations` ORDER BY operationOrder ASC")
    abstract override fun getRecords(): List<DatabaseManufacturingOperation>

    @Query("select * from `14_manufacturing_operations` where lineId = :parentId order by operationOrder asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseManufacturingOperation>

    @Query("SELECT * FROM `14_manufacturing_operations` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseManufacturingOperation?

    @Query("SELECT * FROM `14_manufacturing_operations` ORDER BY operationOrder ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseManufacturingOperation>>

    @Transaction
    @Query("""
        select mo.* from manufacturingOperationsComplete mo 
        where(:lineId = -1 or mo.lineId = :lineId)
    """)
    abstract fun getRecordsFlowForUI(lineId: Int): Flow<List<DatabaseManufacturingOperation.DatabaseManufacturingOperationComplete>>

    @Transaction
    @Query("SELECT * FROM manufacturingOperationsComplete WHERE id = :id")
    abstract fun getRecordCompleteById(id: Int): DatabaseManufacturingOperation.DatabaseManufacturingOperationComplete
}