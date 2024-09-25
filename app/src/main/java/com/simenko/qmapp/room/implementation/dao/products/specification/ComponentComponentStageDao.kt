package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentComponentStage
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentComponentStageDao : DaoBaseModel<ID, ID, DatabaseComponentComponentStage> {
    @Query("SELECT * FROM `4_6_components_component_stages` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentComponentStage>

    @Query("SELECT * FROM `4_6_components_component_stages` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentComponentStage>>

    @Query("select * from `4_6_components_component_stages` where id = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseComponentComponentStage>

    @Query("SELECT * FROM `4_6_components_component_stages` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentComponentStage?

    @Transaction
    @Query(
        "select ccs.* from components_component_stages_complete as ccs join `5_6_component_stage_kinds_component_stages` as csk on ccs.stageKindStageID = csk.id " +
                "where (ccs.componentId = :cId or :cId = -1) and (csk.componentStageKindId = :cskId or :cskId = -1)"
    )
    abstract fun getRecordsCompleteForUI(cId: ID, cskId: ID): Flow<List<DatabaseComponentComponentStage.DatabaseComponentComponentStageComplete>>

    @Transaction
    @Query(
        "select ccs.* from components_component_stages_complete as ccs join `5_6_component_stage_kinds_component_stages` as csk on ccs.stageKindStageID = csk.id " +
                "where ccs.componentId = :cId and csk.componentStageKindId = :cskId and csk.componentStageId = :csId"
    )
    abstract suspend fun getRecordCompleteById(cId: ID, cskId: ID, csId: ID): DatabaseComponentComponentStage.DatabaseComponentComponentStageComplete?

    @Transaction
    @Query("select pcc.* from components_component_stages_complete as pcc")
    abstract fun getAllRecordsComplete(): Flow<List<DatabaseComponentComponentStage.DatabaseComponentComponentStageComplete>>
}