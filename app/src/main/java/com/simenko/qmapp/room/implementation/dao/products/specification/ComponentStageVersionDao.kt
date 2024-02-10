package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentStageVersion
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageVersionDao: DaoBaseModel<DatabaseComponentStageVersion> {
    @Query("SELECT * FROM `11_component_in_stage_versions` ORDER BY versionDate ASC")
    abstract override fun getRecords(): List<DatabaseComponentStageVersion>

    @Query("select * from `11_component_in_stage_versions` where componentInStageId = :parentId order by versionDate  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseComponentStageVersion>

    @Query("SELECT * FROM `11_component_in_stage_versions` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentStageVersion?

    @Query("SELECT * FROM `11_component_in_stage_versions` ORDER BY versionDate ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentStageVersion>>
}