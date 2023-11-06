package com.simenko.qmapp.room.implementation.dao.products.rest

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductKindProduct
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKindProductDao : DaoBaseModel<DatabaseProductKindProduct> {
    @Query("SELECT * FROM `1_2_product_kinds_products` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductKindProduct>

    @Query("select * from `1_2_product_kinds_products` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseProductKindProduct>

    @Query("SELECT * FROM `1_2_product_kinds_products` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProductKindProduct?

    @Query("SELECT * FROM `1_2_product_kinds_products` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductKindProduct>>
}