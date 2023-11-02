package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseComponentInStageTolerance

@Dao
abstract class ComponentStageToleranceDao: DaoBaseModel<DatabaseComponentInStageTolerance> {
    @Query("SELECT * FROM `11_8_component_in_stage_tolerances` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentInStageTolerance>

    @Query("select * from `11_8_component_in_stage_tolerances` where versionId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentInStageTolerance>

    @Query("SELECT * FROM `11_8_component_in_stage_tolerances` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentInStageTolerance?

    @Query("SELECT * FROM `11_8_component_in_stage_tolerances` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseComponentInStageTolerance>>

}