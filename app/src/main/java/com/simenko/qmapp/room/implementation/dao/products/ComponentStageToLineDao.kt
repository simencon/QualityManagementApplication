package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseComponentInStageToLine

@Dao
abstract class ComponentStageToLineDao: DaoBaseModel<DatabaseComponentInStageToLine> {
    @Query("SELECT * FROM `13_5_component_in_stages_to_lines` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentInStageToLine>

    @Query("select * from `13_5_component_in_stages_to_lines` where lineId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentInStageToLine>

    @Query("SELECT * FROM `13_5_component_in_stages_to_lines` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentInStageToLine?

    @Query("SELECT * FROM `13_5_component_in_stages_to_lines` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseComponentInStageToLine>>

}