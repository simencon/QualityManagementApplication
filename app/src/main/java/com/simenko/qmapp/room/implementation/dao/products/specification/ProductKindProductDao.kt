package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductKindProduct
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
    @Query("select * from product_kinds_products_complete where productKindId = :pId")
    abstract fun getRecordsCompleteForUI(pId: ID): Flow<List<DatabaseProductKindProduct.DatabaseProductKindProductComplete>>
}