package com.simenko.qmapp.room.implementation.dao.products

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductBase
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductBaseDao: DaoBaseModel<DatabaseProductBase> {
    @Query("SELECT * FROM `0_products_bases` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductBase>

    @Query("select * from `0_products_bases` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseProductBase>

    @Query("SELECT * FROM `0_products_bases` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProductBase?

    @Query("SELECT * FROM `0_products_bases` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductBase>>

}