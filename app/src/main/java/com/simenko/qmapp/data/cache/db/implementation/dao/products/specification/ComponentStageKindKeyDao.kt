package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseComponentStageKindKey
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageKindKeyDao : DaoBaseModel<ID, ID, DatabaseComponentStageKindKey> {
    @Query("SELECT * FROM `5_1_component_stage_kind_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentStageKindKey>

    @Query("SELECT * FROM `5_1_component_stage_kind_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentStageKindKey>>

    @Transaction
    @Query("select * from `component_stage_kind_keys_complete` where componentStageKindId = :parentId order by id  asc")
    abstract suspend fun getRecordsByParentId(parentId: ID): List<DatabaseComponentStageKindKey.DatabaseComponentStageKindKeyComplete>

    @Query("SELECT * FROM `5_1_component_stage_kind_keys` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentStageKindKey?

    @Transaction
    @Query("select * from component_stage_kind_keys_complete where componentStageKindId = :pId")
    abstract fun getRecordsCompleteForUI(pId: ID): Flow<List<DatabaseComponentStageKindKey.DatabaseComponentStageKindKeyComplete>>
}