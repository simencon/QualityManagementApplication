package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseEmployee
import com.simenko.qmapp.room.entities.DatabaseEmployeeComplete
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EmployeeDao : DaoBaseModel<ID, ID, DatabaseEmployee> {
    @Query("SELECT * FROM `8_employees` ORDER BY id DESC")
    abstract override fun getRecords(): List<DatabaseEmployee>

    @Query("SELECT * FROM `8_employees` ORDER BY id DESC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseEmployee>>

    @Query("select * from `8_employees` where departmentId = :parentId order by id DESC")
    abstract fun getRecordsByParentId(parentId: ID): Flow<List<DatabaseEmployee>>

    @Query("SELECT * FROM `8_employees` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseEmployee?

    @Transaction
    @Query(
        """
            SELECT * FROM '8_employees'
            WHERE (:fullName = '' or fullName like :fullName)
            and (:companyId = -1 or companyId = :companyId)
            ORDER BY id DESC
        """
    )
    abstract fun getRecordsCompleteFlowForUI(fullName: String, companyId: ID): Flow<List<DatabaseEmployeeComplete>>
}