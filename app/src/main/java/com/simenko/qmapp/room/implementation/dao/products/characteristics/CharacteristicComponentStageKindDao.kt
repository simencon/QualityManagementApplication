package com.simenko.qmapp.room.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseCharacteristicComponentStageKind
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacteristicComponentStageKindDao : DaoBaseModel<ID, ID, DatabaseCharacteristicComponentStageKind> {
    @Query("SELECT * FROM `5_7_characteristic_component_stage_kinds` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseCharacteristicComponentStageKind>

    @Query("SELECT * FROM `5_7_characteristic_component_stage_kinds` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseCharacteristicComponentStageKind>>

    @Query("select * from `5_7_characteristic_component_stage_kinds` where id = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseCharacteristicComponentStageKind>

    @Query("SELECT * FROM `5_7_characteristic_component_stage_kinds` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseCharacteristicComponentStageKind?
}