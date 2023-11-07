package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentInStage
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageDao: DaoBaseModel<DatabaseComponentInStage> {
    @Query("SELECT * FROM `6_components_in_stages` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentInStage>

    @Query("select * from `6_components_in_stages` where keyId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentInStage>

    @Query("SELECT * FROM `6_components_in_stages` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentInStage?

    @Query("SELECT * FROM `6_components_in_stages` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentInStage>>

}