package com.simenko.qmapp.data.cache.db.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.DatabaseDepartment
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DepartmentDao : DaoBaseModel<ID, ID, DatabaseDepartment> {
    @Query("SELECT * FROM `10_departments` ORDER BY depOrder ASC")
    abstract override fun getRecords(): List<DatabaseDepartment>

    @Query("SELECT * FROM `10_departments` ORDER BY depOrder ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseDepartment>>

    @Query("select * from `10_departments` where companyId = :parentId order by depOrder asc")
    abstract fun getRecordsByParentId(parentId: ID): Flow<List<DatabaseDepartment>>

    @Query("SELECT * FROM `10_departments` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseDepartment?

    @Transaction
    @Query(
        """
        select * from `10_departments` as d
        where (:parentId = -1 or d.companyId = :parentId)
        order by depOrder;
        """
    )
    abstract fun getRecordsComplete(parentId: ID): Flow<List<DatabaseDepartment.DatabaseDepartmentsComplete>>

    @Transaction
    @Query("""select * from `10_departments` as d where d.id = :id;""")
    abstract fun getRecordCompleteById(id: ID): DatabaseDepartment.DatabaseDepartmentsComplete
}