package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseManufacturingProject
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ManufacturingProjectDao : DaoBaseModel<DatabaseManufacturingProject> {
    @Query("SELECT * FROM `0_manufacturing_project` ORDER BY startDate ASC")
    abstract override fun getRecords(): List<DatabaseManufacturingProject>

    @Query("select * from `0_manufacturing_project` where companyId = :parentId order by startDate asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseManufacturingProject>

    @Query("SELECT * FROM `0_manufacturing_project` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseManufacturingProject?

    @Query("SELECT * FROM `0_manufacturing_project` ORDER BY startDate ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseManufacturingProject>>

    @Transaction
    @Query("select * from `0_manufacturing_project` where companyId = :parentId order by startDate")
    abstract fun getRecordsCompleteForUI(parentId: Long): Flow<List<DatabaseManufacturingProject.DatabaseManufacturingProjectComplete>>
}