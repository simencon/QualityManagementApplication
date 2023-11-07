package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentStageKindKey
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageKindKeyDao : DaoBaseModel<DatabaseComponentStageKindKey> {
    @Query("SELECT * FROM `5_1_component_stage_kind_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentStageKindKey>

    @Query("select * from `5_1_component_stage_kind_keys` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentStageKindKey>

    @Query("SELECT * FROM `5_1_component_stage_kind_keys` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentStageKindKey?

    @Query("SELECT * FROM `5_1_component_stage_kind_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentStageKindKey>>
}