package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponent
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentDao: DaoBaseModel<DatabaseComponent> {
    @Query("SELECT * FROM `4_components` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponent>

    @Query("select * from `4_components` where keyId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseComponent>

    @Query("SELECT * FROM `4_components` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponent?

    @Query("SELECT * FROM `4_components` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponent>>

}