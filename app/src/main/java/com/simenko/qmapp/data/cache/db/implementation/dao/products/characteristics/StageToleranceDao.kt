package com.simenko.qmapp.data.cache.db.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseComponentInStageTolerance
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StageToleranceDao: DaoBaseModel<ID, ID, DatabaseComponentInStageTolerance> {
    @Query("SELECT * FROM `11_8_component_in_stage_tolerances` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentInStageTolerance>

    @Query("SELECT * FROM `11_8_component_in_stage_tolerances` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentInStageTolerance>>

    @Query("select * from `11_8_component_in_stage_tolerances` where versionId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseComponentInStageTolerance>

    @Query("SELECT * FROM `11_8_component_in_stage_tolerances` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentInStageTolerance?
}