package com.simenko.qmapp.room.implementation.dao.products.rest

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentStageKind
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageKindDao : DaoBaseModel<DatabaseComponentStageKind> {
    @Query("SELECT * FROM `5_component_stage_kinds` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentStageKind>

    @Query("select * from `5_component_stage_kinds` where componentKindId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentStageKind>

    @Query("SELECT * FROM `5_component_stage_kinds` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentStageKind?

    @Query("SELECT * FROM `5_component_stage_kinds` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentStageKind>>
}