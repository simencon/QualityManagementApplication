package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentKind
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentKindDao : DaoBaseModel<ID, ID, DatabaseComponentKind> {
    @Query("SELECT * FROM `3_component_kinds` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentKind>

    @Query("select * from `3_component_kinds` where productKindId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseComponentKind>

    @Query("SELECT * FROM `3_component_kinds` WHERE id = :id")
    abstract override fun getRecordById(id: ID): DatabaseComponentKind?

    @Query("SELECT * FROM `3_component_kinds` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentKind>>

    @Transaction
    @Query("SELECT * FROM `component_kinds_complete` WHERE id = :id")
    abstract fun getRecordCompleteById(id: ID): DatabaseComponentKind.DatabaseComponentKindComplete?
    @Transaction
    @Query("select * from component_kinds_complete where productKindId = :pId")
    abstract fun getRecordsCompleteForUI(pId: ID): Flow<List<DatabaseComponentKind.DatabaseComponentKindComplete>>
}