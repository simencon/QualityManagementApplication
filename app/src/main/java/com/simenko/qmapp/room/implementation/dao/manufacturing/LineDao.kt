package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
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

    @Query("SELECT * FROM `13_manufacturing_lines` ORDER BY lineOrder ASC")
    abstract fun getRecordsFlowForUI(): Flow<List<DatabaseManufacturingLine>>
}