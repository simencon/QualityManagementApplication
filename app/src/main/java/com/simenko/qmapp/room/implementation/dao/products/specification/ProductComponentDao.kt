package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductComponent
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductComponentDao : DaoBaseModel<DatabaseProductComponent> {
    @Query("SELECT * FROM `2_4_products_components` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductComponent>

    @Query("select * from `2_4_products_components` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseProductComponent>

    @Query("SELECT * FROM `2_4_products_components` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProductComponent?

    @Query("SELECT * FROM `2_4_products_components` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductComponent>>
}