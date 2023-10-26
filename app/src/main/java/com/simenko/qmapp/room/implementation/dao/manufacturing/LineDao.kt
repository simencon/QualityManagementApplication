package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.entities.DomainManufacturingLine
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseManufacturingLine
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LineDao: DaoBaseModel<DatabaseManufacturingLine> {
    @Query("SELECT * FROM `13_manufacturing_lines` ORDER BY lineOrder ASC")
    abstract override fun getRecords(): List<DatabaseManufacturingLine>

    @Query("select * from `13_manufacturing_lines` where chId = :parentId order by lineOrder asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseManufacturingLine>

    @Query("SELECT * FROM `13_manufacturing_lines` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseManufacturingLine?

    @Query("SELECT * FROM `13_manufacturing_lines` ORDER BY lineOrder ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseManufacturingLine>>

    @Query("""
        SELECT * FROM `13_manufacturing_lines` as ml 
        WHERE(:parentId = -1 OR ml.chId = :parentId)
        ORDER BY lineOrder ASC;
    """)
    abstract fun getRecordsFlowForUI(parentId: Int): Flow<List<DatabaseManufacturingLine>>

    @Query("SELECT * FROM manufacturingLinesWithParents WHERE id = :id")
    abstract fun getRecordWithParentsById(id: Int): DatabaseManufacturingLine.DatabaseManufacturingLineWithParents

    @Transaction
    @Query("SELECT * FROM `13_manufacturing_lines` AS ml WHERE id = :id ORDER BY ml.lineOrder")
    abstract fun getRecordCompleteById(id: Int): DatabaseManufacturingLine.DatabaseManufacturingLineComplete
}