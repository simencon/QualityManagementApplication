package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseProductToLine

@Dao
abstract class ProductToLineDao: DaoBaseModel<DatabaseProductToLine> {
    @Query("SELECT * FROM `13_1_products_to_lines` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductToLine>

    @Query("select * from `13_1_products_to_lines` where lineId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseProductToLine>

    @Query("SELECT * FROM `13_1_products_to_lines` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProductToLine?

    @Query("SELECT * FROM `13_1_products_to_lines` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseProductToLine>>

}