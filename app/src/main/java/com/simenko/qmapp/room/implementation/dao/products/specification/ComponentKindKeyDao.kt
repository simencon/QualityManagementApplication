package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentKindKey
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentKindKeyDao : DaoBaseModel<ID, ID, DatabaseComponentKindKey> {
    @Query("SELECT * FROM `3_1_component_kind_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentKindKey>

    @Query("SELECT * FROM `3_1_component_kind_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentKindKey>>

    @Query("select * from `3_1_component_kind_keys` where id = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseComponentKindKey>

    @Query("SELECT * FROM `3_1_component_kind_keys` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentKindKey?

    @Transaction
    @Query("select * from component_kind_keys_complete where componentKindId = :pId")
    abstract fun getRecordsCompleteForUI(pId: ID): Flow<List<DatabaseComponentKindKey.DatabaseComponentKindKeyComplete>>
}