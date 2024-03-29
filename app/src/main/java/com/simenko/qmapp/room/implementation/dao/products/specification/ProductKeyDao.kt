package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseKey
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKeyDao: DaoBaseModel<DatabaseKey> {
    @Query("SELECT * FROM `0_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseKey>

    @Query("select * from `0_keys` where projectId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseKey>

    @Query("SELECT * FROM `0_keys` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseKey?

    @Query("SELECT * FROM `0_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseKey>>

    @Transaction
    @Query("select * from `keys_complete` where projectId = :parentId order by id  asc")
    abstract fun getRecordsCompleteForUI(parentId: ID): Flow<List<DatabaseKey.DatabaseKeyComplete>>
}