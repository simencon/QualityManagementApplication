package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseSubDepartment
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SubDepartmentDao : DaoBaseModel<DatabaseSubDepartment> {
    @Query("SELECT * FROM `11_sub_departments` ORDER BY subDepOrder ASC")
    abstract override fun getRecords(): List<DatabaseSubDepartment>

    @Query("select * from `11_sub_departments` where depId = :parentId order by subDepOrder asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseSubDepartment>

    @Query("SELECT * FROM `11_sub_departments` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseSubDepartment?

    @Query("SELECT * FROM `11_sub_departments` ORDER BY subDepOrder ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseSubDepartment>>

    @Query("SELECT * FROM `11_sub_departments` ORDER BY subDepOrder ASC")
    abstract fun getRecordsFlowForUI(): Flow<List<DatabaseSubDepartment>>

    @Query("SELECT * FROM subDepartmentWithParents WHERE id = :id")
    abstract fun getRecordWithParentsById(id: Int): DatabaseSubDepartment.DatabaseSubDepartmentWithParents

    @Transaction
    @Query("SELECT * FROM `11_sub_departments` AS sd WHERE id = :id ORDER BY sd.subDepOrder")
    abstract fun getRecordCompleteById(id: Int): DatabaseSubDepartment.DatabaseSubDepartmentComplete
}