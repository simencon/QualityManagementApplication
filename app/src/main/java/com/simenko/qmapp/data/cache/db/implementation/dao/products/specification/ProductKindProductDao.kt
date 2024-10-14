package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseProductKindProduct
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKindProductDao : DaoBaseModel<ID, ID, DatabaseProductKindProduct> {
    @Query("SELECT * FROM `1_2_product_kinds_products` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductKindProduct>

    @Query("SELECT * FROM `1_2_product_kinds_products` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductKindProduct>>

    @Query("select * from `1_2_product_kinds_products` where id = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProductKindProduct>

    @Query("SELECT * FROM `1_2_product_kinds_products` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductKindProduct?

    @Transaction
    @Query("select * from product_kinds_products_complete")
    abstract fun getAllRecordsComplete(): Flow<List<DatabaseProductKindProduct.DatabaseProductKindProductComplete>>

    @Transaction
    @Query("select * from product_kinds_products_complete where productKindId = :pId")
    abstract fun getRecordsCompleteForUI(pId: ID): Flow<List<DatabaseProductKindProduct.DatabaseProductKindProductComplete>>

    @Transaction
    @Query("select * from product_kinds_products_complete where productKindId = :productKindId and productId = :productId")
    abstract suspend fun getRecordCompleteById(productKindId: ID, productId: ID): DatabaseProductKindProduct.DatabaseProductKindProductComplete?
}