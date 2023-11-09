package com.simenko.qmapp.room.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseCharacteristicComponentKind
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacteristicComponentKindDao : DaoBaseModel<DatabaseCharacteristicComponentKind> {
    @Query("SELECT * FROM `3_7_characteristics_component_kinds` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseCharacteristicComponentKind>

    @Query("select * from `3_7_characteristics_component_kinds` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseCharacteristicComponentKind>

    @Query("SELECT * FROM `3_7_characteristics_component_kinds` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseCharacteristicComponentKind?

    @Query("SELECT * FROM `3_7_characteristics_component_kinds` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseCharacteristicComponentKind>>
}