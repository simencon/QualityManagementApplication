package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.room.*
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.entities.DatabaseInputForOrder
import com.simenko.qmapp.room.contract.DaoBaseModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class InputForOrderDao : DaoBaseModel<String, ID, DatabaseInputForOrder> {
    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` ORDER BY charOrder ASC")
    abstract override fun getRecords(): List<DatabaseInputForOrder>

    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` ORDER BY charOrder ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseInputForOrder>>

    /**
     * as parent is used lineId but in fact should be companyId in future
     * */
    @Query("select * from `1_1_inputformeasurementregister` where lineId = :parentId order by charOrder asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseInputForOrder>

    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` WHERE id = :id")
    abstract fun getRecordById(id: String): DatabaseInputForOrder?
}