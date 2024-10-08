package com.simenko.qmapp.room.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentKindToSubDepartment
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentKindToSubDepartmentDao: DaoBaseModel<ID, ID, DatabaseComponentKindToSubDepartment> {
    @Query("SELECT * FROM `11_3_comp_kinds_to_s_departments` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentKindToSubDepartment>

    @Query("SELECT * FROM `11_3_comp_kinds_to_s_departments` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentKindToSubDepartment>>

    @Query("select * from `11_3_comp_kinds_to_s_departments` where subDepId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseComponentKindToSubDepartment>

    @Query("SELECT * FROM `11_3_comp_kinds_to_s_departments` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentKindToSubDepartment?
}