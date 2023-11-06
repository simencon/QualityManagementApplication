package com.simenko.qmapp.room.implementation.dao.products.rest

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentStageKindComponentStage
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageKindComponentStageDao : DaoBaseModel<DatabaseComponentStageKindComponentStage> {
    @Query("SELECT * FROM `5_6_component_stage_kinds_component_stages` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentStageKindComponentStage>

    @Query("select * from `5_6_component_stage_kinds_component_stages` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentStageKindComponentStage>

    @Query("SELECT * FROM `5_6_component_stage_kinds_component_stages` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentStageKindComponentStage?

    @Query("SELECT * FROM `5_6_component_stage_kinds_component_stages` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentStageKindComponentStage>>
}