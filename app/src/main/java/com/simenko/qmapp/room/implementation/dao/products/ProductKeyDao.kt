package com.simenko.qmapp.room.implementation.dao.products

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseKey
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKeyDao: DaoBaseModel<DatabaseKey> {
    @Query("SELECT * FROM `0_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseKey>

    @Query("select * from `0_keys` where projectId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseKey>

    @Query("SELECT * FROM `0_keys` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseKey?

    @Query("SELECT * FROM `0_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseKey>>

}