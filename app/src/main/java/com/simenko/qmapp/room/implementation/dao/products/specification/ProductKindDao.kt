package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseCharacteristicItemKind
import com.simenko.qmapp.room.entities.products.DatabaseItemTolerance
import com.simenko.qmapp.room.entities.products.DatabaseProductKind
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKindDao : DaoBaseModel<DatabaseProductKind> {
    @Query("SELECT * FROM `1_product_kinds` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductKind>

    @Query("select * from `1_product_kinds` where projectId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseProductKind>

    @Query("SELECT * FROM `1_product_kinds` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProductKind?

    @Query("SELECT * FROM `1_product_kinds` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductKind>>

    @Transaction
    @Query("SELECT * FROM `product_kinds_complete` WHERE id = :id")
    abstract fun getRecordCompleteById(id: ID): DatabaseProductKind.DatabaseProductKindComplete?
    @Transaction
    @Query("select * from product_kinds_complete where projectId = :parentId")
    abstract fun getRecordsCompleteForUI(parentId: ID): Flow<List<DatabaseProductKind.DatabaseProductKindComplete>>
    @Transaction
    @Query("SELECT * FROM item_kind_characteristic where itemKindFId = :itemKindFId")
    abstract fun getItemVersionTolerancesComplete(itemKindFId: String): Flow<List<DatabaseCharacteristicItemKind.DatabaseCharacteristicItemKindComplete>>
}