package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseManufacturingLine
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LineDao: DaoBaseModel<ID, ID, DatabaseManufacturingLine> {
    @Query("SELECT * FROM `13_manufacturing_lines` ORDER BY lineOrder ASC")
    abstract override fun getRecords(): List<DatabaseManufacturingLine>

    @Query("SELECT * FROM `13_manufacturing_lines` ORDER BY lineOrder ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseManufacturingLine>>

    @Query("select * from `13_manufacturing_lines` where chId = :parentId order by lineOrder asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseManufacturingLine>

    @Query("SELECT * FROM `13_manufacturing_lines` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseManufacturingLine?

    @Query("""
        SELECT * FROM `13_manufacturing_lines` as ml 
        WHERE(:parentId = -1 OR ml.chId = :parentId)
        ORDER BY lineOrder ASC;
    """)
    abstract fun getRecordsFlowForUI(parentId: ID): Flow<List<DatabaseManufacturingLine>>

    @Query("SELECT * FROM manufacturingLinesWithParents WHERE id = :id")
    abstract fun getRecordWithParentsById(id: ID): DatabaseManufacturingLine.DatabaseManufacturingLineWithParents

    @Transaction
    @Query("SELECT * FROM `13_manufacturing_lines` AS ml WHERE id = :id ORDER BY ml.lineOrder")
    abstract fun getRecordCompleteById(id: ID): DatabaseManufacturingLine.DatabaseManufacturingLineComplete
}