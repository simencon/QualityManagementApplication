package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseComponentStageVersion
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageVersionDao: DaoBaseModel<ID, ID, DatabaseComponentStageVersion> {
    @Query("SELECT * FROM `11_component_in_stage_versions` ORDER BY versionDate ASC")
    abstract override fun getRecords(): List<DatabaseComponentStageVersion>

    @Query("SELECT * FROM `11_component_in_stage_versions` ORDER BY versionDate ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentStageVersion>>

    @Query("select * from `11_component_in_stage_versions` where componentInStageId = :parentId order by versionDate  asc")
    abstract suspend fun getRecordsByParentId(parentId: ID): List<DatabaseComponentStageVersion>

    @Query("SELECT * FROM `11_component_in_stage_versions` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentStageVersion?
}