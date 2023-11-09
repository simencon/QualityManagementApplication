package com.simenko.qmapp.room.implementation.dao.manufacturing

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseSubDepartment
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SubDepartmentDao : DaoBaseModel<DatabaseSubDepartment> {
    @Query("select * from `11_sub_departments` order by subDepOrder asc")
    abstract override fun getRecords(): List<DatabaseSubDepartment>

    @Query("select * from `11_sub_departments` where depId = :parentId order by subDepOrder asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseSubDepartment>

    @Query("select * from `11_sub_departments` where id = :id")
    abstract override fun getRecordById(id: String): DatabaseSubDepartment?

    @Query("select * from `11_sub_departments` order by subDepOrder asc")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseSubDepartment>>

    @Query("""
        select * from `11_sub_departments` as sd
        where(:parentId = -1 or sd.depId = :parentId)
        order by subDepOrder asc
        """)
    abstract fun getRecordsFlowForUI(parentId: ID): Flow<List<DatabaseSubDepartment>>

    @Query("select * from subDepartmentWithParents where id = :id")
    abstract fun getRecordWithParentsById(id: ID): DatabaseSubDepartment.DatabaseSubDepartmentWithParents

    @Transaction
    @Query("select * from `11_sub_departments` AS sd where id = :id order by sd.subDepOrder")
    abstract fun getRecordCompleteById(id: ID): DatabaseSubDepartment.DatabaseSubDepartmentComplete
}