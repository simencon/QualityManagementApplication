package com.simenko.qmapp.room.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentToLine
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentToLineDao : DaoBaseModel<ID, ID, DatabaseComponentToLine> {
    @Query("SELECT * FROM `13_3_components_to_lines` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentToLine>

    @Query("SELECT * FROM `13_3_components_to_lines` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentToLine>>

    @Query("select * from `13_3_components_to_lines` where lineId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): Flow<List<DatabaseComponentToLine>>

    @Query("SELECT * FROM `13_3_components_to_lines` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentToLine?
}