package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseMetrix

@Dao
abstract class MetricDao: DaoBaseModel<DatabaseMetrix> {
    @Query("SELECT * FROM `8_metrixes` ORDER BY metrixOrder ASC")
    abstract override fun getRecords(): List<DatabaseMetrix>

    @Query("select * from `8_metrixes` where charId = :parentId order by metrixOrder  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseMetrix>

    @Query("SELECT * FROM `8_metrixes` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseMetrix?

    @Query("SELECT * FROM `8_metrixes` ORDER BY metrixOrder ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseMetrix>>

}