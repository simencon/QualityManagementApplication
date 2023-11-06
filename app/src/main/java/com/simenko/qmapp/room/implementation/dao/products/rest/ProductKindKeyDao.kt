package com.simenko.qmapp.room.implementation.dao.products.rest

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductKindKey
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKindKeyDao : DaoBaseModel<DatabaseProductKindKey> {
    @Query("SELECT * FROM `1_1_product_kind_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductKindKey>

    @Query("select * from `1_1_product_kind_keys` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseProductKindKey>

    @Query("SELECT * FROM `1_1_product_kind_keys` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProductKindKey?

    @Query("SELECT * FROM `1_1_product_kind_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductKindKey>>
}