package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseJobRole
import kotlinx.coroutines.flow.Flow

@Dao
abstract class JobRoleDao : DaoBaseModel<ID, ID, DatabaseJobRole> {
    @Query("SELECT * FROM `0_job_roles` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseJobRole>

    @Query("SELECT * FROM `0_job_roles` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseJobRole>>

    @Query("select * from `0_job_roles` where companyId = :parentId order by id asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseJobRole>

    @Query("SELECT * FROM `0_job_roles` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseJobRole?
}