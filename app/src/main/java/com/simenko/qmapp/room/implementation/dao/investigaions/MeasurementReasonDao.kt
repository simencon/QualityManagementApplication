package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseReason
import com.simenko.qmapp.room.contract.DaoBaseModel

@Dao
abstract class MeasurementReasonDao : DaoBaseModel<DatabaseReason> {
    @Query("SELECT * FROM `0_measurement_reasons` ORDER BY reasonOrder ASC")
    abstract override fun getRecords(): List<DatabaseReason>

    //    ToDo - change this when reason and types will be connected
    @Query("select * from `0_measurement_reasons` order by reasonOrder asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseReason>

    @Query("SELECT * FROM `0_measurement_reasons` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseReason

    @Query("SELECT * FROM `0_measurement_reasons` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseReason>>
}