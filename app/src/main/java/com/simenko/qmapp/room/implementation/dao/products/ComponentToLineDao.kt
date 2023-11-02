package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseComponentToLine

@Dao
abstract class ComponentToLineDao: DaoBaseModel<DatabaseComponentToLine> {
    @Query("SELECT * FROM `13_3_components_to_lines` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentToLine>

    @Query("select * from `13_3_components_to_lines` where lineId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentToLine>

    @Query("SELECT * FROM `13_3_components_to_lines` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentToLine?

    @Query("SELECT * FROM `13_3_components_to_lines` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseComponentToLine>>

}