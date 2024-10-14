package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseComponentStageKindComponentStage
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageKindComponentStageDao : DaoBaseModel<ID, ID, DatabaseComponentStageKindComponentStage> {
    @Query("SELECT * FROM `5_6_component_stage_kinds_component_stages` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentStageKindComponentStage>

    @Query("SELECT * FROM `5_6_component_stage_kinds_component_stages` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentStageKindComponentStage>>

    @Query("select * from `5_6_component_stage_kinds_component_stages` where id = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseComponentStageKindComponentStage>

    @Query("SELECT * FROM `5_6_component_stage_kinds_component_stages` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentStageKindComponentStage?

    @Query("SELECT * FROM `5_6_component_stage_kinds_component_stages` WHERE componentStageKindId = :itemKindId AND componentStageId = :itemId")
    abstract suspend fun findExistingRecord(itemKindId: ID, itemId: ID): DatabaseComponentStageKindComponentStage?
}