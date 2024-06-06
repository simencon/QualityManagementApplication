package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductBase
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductBaseDao: DaoBaseModel<ID, ID, DatabaseProductBase> {
    @Query("SELECT * FROM `0_products_bases` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductBase>

    @Query("SELECT * FROM `0_products_bases` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductBase>>

    @Query("select * from `0_products_bases` where id = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProductBase>

    @Query("SELECT * FROM `0_products_bases` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductBase?
}