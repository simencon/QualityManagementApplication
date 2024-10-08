package com.simenko.qmapp.room.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductLineToDepartment
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductLineToDepartmentDao: DaoBaseModel<ID, ID, DatabaseProductLineToDepartment> {
    @Query("SELECT * FROM `10_0_prod_projects_to_departments` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductLineToDepartment>

    @Query("SELECT * FROM `10_0_prod_projects_to_departments` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductLineToDepartment>>

    @Query("select * from `10_0_prod_projects_to_departments` where depId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProductLineToDepartment>

    @Query("SELECT * FROM `10_0_prod_projects_to_departments` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductLineToDepartment?
}