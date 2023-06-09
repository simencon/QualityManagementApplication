package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseInputForOrder
import com.simenko.qmapp.room.contract.DaoBaseModel

@Dao
abstract class InputForOrderDao : DaoBaseModel<DatabaseInputForOrder> {
    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` ORDER BY charOrder ASC")
    abstract override fun getRecords(): List<DatabaseInputForOrder>

    /**
     * as parent is used lineId but in fact should be companyId in future
     * */
    @Query("select * from `1_1_inputformeasurementregister` where lineId = :parentId order by charOrder asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseInputForOrder>

    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseInputForOrder?

    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` ORDER BY charOrder ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseInputForOrder>>
}