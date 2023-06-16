package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseInputForOrder
import com.simenko.qmapp.room.contract.DaoBaseModel

@Dao
abstract class InputForOrderDao : DaoBaseModel<DatabaseInputForOrder> {
    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` ORDER BY charOrder ASC")
    abstract override fun getRecords(): List<DatabaseInputForOrder>

    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseInputForOrder

    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` ORDER BY charOrder ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseInputForOrder>>
}