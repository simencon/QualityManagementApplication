package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseCharacteristic

@Dao
abstract class CharacteristicDao: DaoBaseModel<DatabaseCharacteristic> {
    @Query("SELECT * FROM `7_characteristics` ORDER BY charOrder ASC")
    abstract override fun getRecords(): List<DatabaseCharacteristic>

    @Query("select * from `7_characteristics` where projectId = :parentId order by charOrder  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseCharacteristic>

    @Query("SELECT * FROM `7_characteristics` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseCharacteristic?

    @Query("SELECT * FROM `7_characteristics` ORDER BY charOrder ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseCharacteristic>>

}