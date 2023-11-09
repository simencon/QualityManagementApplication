package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentComponentStage
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentComponentStageDao : DaoBaseModel<DatabaseComponentComponentStage> {
    @Query("SELECT * FROM `4_6_components_component_stages` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentComponentStage>

    @Query("select * from `4_6_components_component_stages` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseComponentComponentStage>

    @Query("SELECT * FROM `4_6_components_component_stages` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentComponentStage?

    @Query("SELECT * FROM `4_6_components_component_stages` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentComponentStage>>
}