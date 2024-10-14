package com.simenko.qmapp.data.cache.db.implementation.dao.investigaions

import androidx.room.*
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.entities.DatabaseReason
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MeasurementReasonDao : DaoBaseModel<ID, ID, DatabaseReason> {
    @Query("SELECT * FROM `0_measurement_reasons` ORDER BY reasonOrder ASC")
    abstract override fun getRecords(): List<DatabaseReason>

    @Query("SELECT * FROM `0_measurement_reasons` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseReason>>

    //    ToDo - change this when reason and types will be connected
    @Query("select * from `0_measurement_reasons` where id = :parentId order by reasonOrder asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseReason>

    @Query("SELECT * FROM `0_measurement_reasons` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseReason?
}