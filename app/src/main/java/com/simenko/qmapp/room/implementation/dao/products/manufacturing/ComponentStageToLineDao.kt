package com.simenko.qmapp.room.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentInStageToLine
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageToLineDao: DaoBaseModel<ID, ID, DatabaseComponentInStageToLine> {
    @Query("SELECT * FROM `13_5_component_in_stages_to_lines` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentInStageToLine>

    @Query("SELECT * FROM `13_5_component_in_stages_to_lines` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentInStageToLine>>

    @Query("select * from `13_5_component_in_stages_to_lines` where lineId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): Flow<List<DatabaseComponentInStageToLine>>

    @Query("SELECT * FROM `13_5_component_in_stages_to_lines` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentInStageToLine?
}