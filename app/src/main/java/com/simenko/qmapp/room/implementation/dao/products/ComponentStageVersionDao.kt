package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseComponentInStageVersion

@Dao
abstract class ComponentStageVersionDao: DaoBaseModel<DatabaseComponentInStageVersion> {
    @Query("SELECT * FROM `11_component_in_stage_versions` ORDER BY versionDate ASC")
    abstract override fun getRecords(): List<DatabaseComponentInStageVersion>

    @Query("select * from `11_component_in_stage_versions` where componentInStageId = :parentId order by versionDate  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentInStageVersion>

    @Query("SELECT * FROM `11_component_in_stage_versions` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentInStageVersion?

    @Query("SELECT * FROM `11_component_in_stage_versions` ORDER BY versionDate ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseComponentInStageVersion>>

}