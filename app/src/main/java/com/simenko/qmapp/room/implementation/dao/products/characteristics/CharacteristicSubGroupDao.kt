package com.simenko.qmapp.room.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseIshSubCharacteristic
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacteristicSubGroupDao: DaoBaseModel<DatabaseIshSubCharacteristic> {
    @Query("SELECT * FROM `0_ish_sub_characteristics` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseIshSubCharacteristic>

    @Query("select * from `0_ish_sub_characteristics` where id = :parentId order by id asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseIshSubCharacteristic>

    @Query("SELECT * FROM `0_ish_sub_characteristics` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseIshSubCharacteristic?

    @Query("SELECT * FROM `0_ish_sub_characteristics` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseIshSubCharacteristic>>

}