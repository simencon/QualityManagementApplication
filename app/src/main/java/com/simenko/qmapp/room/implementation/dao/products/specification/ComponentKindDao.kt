package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentKind
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentKindDao : DaoBaseModel<DatabaseComponentKind> {
    @Query("SELECT * FROM `3_component_kinds` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentKind>

    @Query("select * from `3_component_kinds` where productKindId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentKind>

    @Query("SELECT * FROM `3_component_kinds` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentKind?

    @Query("SELECT * FROM `3_component_kinds` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentKind>>
}