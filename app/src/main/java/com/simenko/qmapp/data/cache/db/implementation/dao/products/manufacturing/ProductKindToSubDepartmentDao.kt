package com.simenko.qmapp.data.cache.db.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseProductKindToSubDepartment
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKindToSubDepartmentDao: DaoBaseModel<ID, ID, DatabaseProductKindToSubDepartment> {
    @Query("SELECT * FROM `11_1_prod_kinds_to_s_departments` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductKindToSubDepartment>

    @Query("SELECT * FROM `11_1_prod_kinds_to_s_departments` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductKindToSubDepartment>>

    @Query("select * from `11_1_prod_kinds_to_s_departments` where subDepId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): Flow<List<DatabaseProductKindToSubDepartment>>

    @Query("SELECT * FROM `11_1_prod_kinds_to_s_departments` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductKindToSubDepartment?
}