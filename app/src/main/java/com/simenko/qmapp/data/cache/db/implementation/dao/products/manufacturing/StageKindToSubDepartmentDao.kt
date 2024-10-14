package com.simenko.qmapp.data.cache.db.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseStageKindToSubDepartment
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StageKindToSubDepartmentDao: DaoBaseModel<ID, ID, DatabaseStageKindToSubDepartment> {
    @Query("SELECT * FROM `11_5_comp_stages_to_s_departments` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseStageKindToSubDepartment>

    @Query("SELECT * FROM `11_5_comp_stages_to_s_departments` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseStageKindToSubDepartment>>

    @Query("select * from `11_5_comp_stages_to_s_departments` where subDepId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): Flow<List<DatabaseStageKindToSubDepartment>>

    @Query("SELECT * FROM `11_5_comp_stages_to_s_departments` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseStageKindToSubDepartment?
}