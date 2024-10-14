package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseComponentStageKind
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageKindDao : DaoBaseModel<ID, ID, DatabaseComponentStageKind> {
    @Query("SELECT * FROM `5_component_stage_kinds` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentStageKind>

    @Query("SELECT * FROM `5_component_stage_kinds` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentStageKind>>

    @Query("select * from `5_component_stage_kinds` where componentKindId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseComponentStageKind>

    @Query("SELECT * FROM `5_component_stage_kinds` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentStageKind?

    @Transaction
    @Query("SELECT * FROM `component_stage_kinds_complete` WHERE id = :id")
    abstract fun getRecordCompleteById(id: ID): DatabaseComponentStageKind.DatabaseComponentStageKindComplete?

    @Transaction
    @Query("select * from component_stage_kinds_complete where componentKindId = :pId order by componentStageOrder desc")
    abstract fun getRecordsCompleteForUI(pId: ID): Flow<List<DatabaseComponentStageKind.DatabaseComponentStageKindComplete>>

    @Transaction
    @Query(
        """select sk.*
            from `5_component_stage_kinds` sk 
                join `3_component_kinds` ck on sk.componentKindID = ck.ID
                join `1_product_kinds` pk on ck.productKindID = pk.ID
                join `0_manufacturing_project` pl on pk.projectID = pl.ID 
                join `10_0_prod_projects_to_departments` pl_to_dep on pl.ID = pl_to_dep.productLineId
                where pl_to_dep.depId = :depId
        """
    )
    abstract fun getRecordsByDepartmentId(depId: ID): Flow<List<DatabaseComponentStageKind.DatabaseComponentStageKindComplete>>
}