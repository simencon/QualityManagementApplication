package com.simenko.qmapp.room.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentTolerance
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentToleranceDao : DaoBaseModel<ID, ID, DatabaseComponentTolerance> {
    @Query("SELECT * FROM `10_8_component_tolerances` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentTolerance>

    @Query("SELECT * FROM `10_8_component_tolerances` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentTolerance>>
}