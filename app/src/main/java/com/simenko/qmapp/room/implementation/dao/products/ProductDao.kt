package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseProduct

@Dao
abstract class ProductDao: DaoBaseModel<DatabaseProduct> {
    @Query("SELECT * FROM `2_products` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProduct>

    @Query("select * from `2_products` where productBaseId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseProduct>

    @Query("SELECT * FROM `2_products` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProduct?

    @Query("SELECT * FROM `2_products` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseProduct>>

}