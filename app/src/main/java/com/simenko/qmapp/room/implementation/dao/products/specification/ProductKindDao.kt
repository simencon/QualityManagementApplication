package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductKind
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKindDao : DaoBaseModel<DatabaseProductKind> {
    @Query("SELECT * FROM `1_product_kinds` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductKind>

    @Query("select * from `1_product_kinds` where projectId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseProductKind>

    @Query("SELECT * FROM `1_product_kinds` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProductKind?

    @Query("SELECT * FROM `1_product_kinds` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductKind>>
}