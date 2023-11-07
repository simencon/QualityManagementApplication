package com.simenko.qmapp.room.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseElementIshModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacteristicGroupDao: DaoBaseModel<DatabaseElementIshModel> {
    @Query("SELECT * FROM `10_1_d_element_ish_model` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseElementIshModel>

    @Query("select * from `10_1_d_element_ish_model` where id = :parentId order by id asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseElementIshModel>

    @Query("SELECT * FROM `10_1_d_element_ish_model` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseElementIshModel?

    @Query("SELECT * FROM `10_1_d_element_ish_model` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseElementIshModel>>

}