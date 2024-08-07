package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProduct
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao : DaoBaseModel<ID, ID, DatabaseProduct> {
    @Query("SELECT * FROM `2_products` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProduct>

    @Query("SELECT * FROM `2_products` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProduct>>

    @Query("select * from `2_products` where productBaseId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProduct>

    @Transaction
    @Query("SELECT * FROM `products_complete` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProduct.DatabaseProductComplete?
}