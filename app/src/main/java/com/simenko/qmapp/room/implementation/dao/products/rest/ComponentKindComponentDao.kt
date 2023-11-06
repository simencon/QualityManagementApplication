package com.simenko.qmapp.room.implementation.dao.products.rest

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentKindComponent
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentKindComponentDao : DaoBaseModel<DatabaseComponentKindComponent> {
    @Query("SELECT * FROM `3_4_component_kinds_components` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentKindComponent>

    @Query("select * from `3_4_component_kinds_components` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentKindComponent>

    @Query("SELECT * FROM `3_4_component_kinds_components` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentKindComponent?

    @Query("SELECT * FROM `3_4_component_kinds_components` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentKindComponent>>
}