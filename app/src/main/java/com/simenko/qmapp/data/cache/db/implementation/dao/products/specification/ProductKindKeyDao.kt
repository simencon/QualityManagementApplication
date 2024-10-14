package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseProductKindKey
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKindKeyDao : DaoBaseModel<ID, ID, DatabaseProductKindKey> {
    @Query("SELECT * FROM `1_1_product_kind_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductKindKey>

    @Query("SELECT * FROM `1_1_product_kind_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductKindKey>>

    @Transaction
    @Query("select * from `product_kind_keys_complete` where productKindId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProductKindKey.DatabaseProductKindKeyComplete>

    @Query("SELECT * FROM `1_1_product_kind_keys` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductKindKey?

    @Transaction
    @Query("select * from product_kind_keys_complete where productKindId = :pId")
    abstract fun getRecordsCompleteForUI(pId: ID): Flow<List<DatabaseProductKindKey.DatabaseProductKindKeyComplete>>
}