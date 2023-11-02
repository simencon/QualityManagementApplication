package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseManufacturingProject

@Dao
abstract class ManufacturingProjectDao: DaoBaseModel<DatabaseManufacturingProject> {
    @Query("SELECT * FROM `0_manufacturing_project` ORDER BY startDate ASC")
    abstract override fun getRecords(): List<DatabaseManufacturingProject>

    @Query("select * from `0_manufacturing_project` where companyId = :parentId order by startDate asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseManufacturingProject>

    @Query("SELECT * FROM `0_manufacturing_project` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseManufacturingProject?

    @Query("SELECT * FROM `0_manufacturing_project` ORDER BY startDate ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseManufacturingProject>>

}