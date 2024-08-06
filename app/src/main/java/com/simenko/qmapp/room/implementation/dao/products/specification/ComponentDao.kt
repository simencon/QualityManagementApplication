package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponent
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentDao: DaoBaseModel<ID, ID, DatabaseComponent> {
    @Query("SELECT * FROM `4_components` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponent>

    @Query("SELECT * FROM `4_components` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponent>>

    @Query("select * from `4_components` where keyId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseComponent>

    @Transaction
    @Query("SELECT * FROM `components_complete` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponent.DatabaseComponentComplete?
}