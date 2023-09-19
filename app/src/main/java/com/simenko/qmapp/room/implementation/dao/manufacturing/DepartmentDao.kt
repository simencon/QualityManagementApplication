package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseDepartment
import com.simenko.qmapp.room.entities.DatabaseDepartmentsComplete
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DepartmentDao: DaoBaseModel<DatabaseDepartment> {
    @Query("SELECT * FROM `10_departments` ORDER BY depOrder ASC")
    abstract override fun getRecords(): List<DatabaseDepartment>

    @Query("select * from `10_departments` where companyId = :parentId order by depOrder asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseDepartment>

    @Query("SELECT * FROM `10_departments` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseDepartment?

    @Query("SELECT * FROM `10_departments` ORDER BY depOrder ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseDepartment>>

    @Query("SELECT * FROM `10_departments` ORDER BY depOrder ASC")
    abstract fun getRecordsFlowForUI(): Flow<List<DatabaseDepartment>>

    @Transaction
    @Query("select * from `10_departments` order by depOrder")
    abstract fun getRecordsDetailedFlowForUI(): LiveData<List<DatabaseDepartmentsComplete>>
}