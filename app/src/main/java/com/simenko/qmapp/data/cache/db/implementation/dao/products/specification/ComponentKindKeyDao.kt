package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseComponentKindKey
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentKindKeyDao : DaoBaseModel<ID, ID, DatabaseComponentKindKey> {
    @Query("SELECT * FROM `3_1_component_kind_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentKindKey>

    @Query("SELECT * FROM `3_1_component_kind_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentKindKey>>

    @Transaction
    @Query("select * from `component_kind_keys_complete` where componentKindId = :parentId order by id  asc")
    abstract suspend fun getRecordsByParentId(parentId: ID): List<DatabaseComponentKindKey.DatabaseComponentKindKeyComplete>

    @Query("SELECT * FROM `3_1_component_kind_keys` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentKindKey?

    @Transaction
    @Query("select * from component_kind_keys_complete where componentKindId = :pId")
    abstract fun getRecordsCompleteForUI(pId: ID): Flow<List<DatabaseComponentKindKey.DatabaseComponentKindKeyComplete>>
}