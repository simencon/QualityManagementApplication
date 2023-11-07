package com.simenko.qmapp.room.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentTolerance
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentToleranceDao: DaoBaseModel<DatabaseComponentTolerance> {
    @Query("SELECT * FROM `10_8_component_tolerances` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentTolerance>

    @Query("select * from `10_8_component_tolerances` where versionId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentTolerance>

    @Query("SELECT * FROM `10_8_component_tolerances` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentTolerance?

    @Query("SELECT * FROM `10_8_component_tolerances` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentTolerance>>

}