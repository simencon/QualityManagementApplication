package com.simenko.qmapp.room.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseCharacteristic
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacteristicDao : DaoBaseModel<DatabaseCharacteristic> {
    @Query("SELECT * FROM `7_characteristics` ORDER BY charOrder ASC")
    abstract override fun getRecords(): List<DatabaseCharacteristic>

    @Query("select * from `7_characteristics` where ishSubCharId = :parentId order by charOrder  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseCharacteristic>

    @Query("SELECT * FROM `7_characteristics` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseCharacteristic?

    @Query("SELECT * FROM `7_characteristics` ORDER BY charOrder ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseCharacteristic>>

    @Transaction
    @Query("select * from characteristic_complete where ishSubCharId = :parentId ")
    abstract fun getRecordsCompleteForUI(parentId: ID): Flow<List<DatabaseCharacteristic.DatabaseCharacteristicComplete>>
}