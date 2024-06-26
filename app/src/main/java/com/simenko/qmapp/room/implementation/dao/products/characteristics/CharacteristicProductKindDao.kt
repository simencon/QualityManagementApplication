package com.simenko.qmapp.room.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseCharacteristicProductKind
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacteristicProductKindDao : DaoBaseModel<ID, ID, DatabaseCharacteristicProductKind> {
    @Query("SELECT * FROM `1_7_characteristics_product_kinds` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseCharacteristicProductKind>

    @Query("SELECT * FROM `1_7_characteristics_product_kinds` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseCharacteristicProductKind>>

    @Query("select * from `1_7_characteristics_product_kinds` where id = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseCharacteristicProductKind>

    @Query("SELECT * FROM `1_7_characteristics_product_kinds` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseCharacteristicProductKind?
}