package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentStage
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageDao: DaoBaseModel<ID, ID, DatabaseComponentStage> {
    @Query("SELECT * FROM `6_components_in_stages` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentStage>

    @Query("select * from `6_components_in_stages` where keyId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseComponentStage>

    @Query("SELECT * FROM `6_components_in_stages` WHERE id = :id")
    abstract override fun getRecordById(id: ID): DatabaseComponentStage?

    @Query("SELECT * FROM `6_components_in_stages` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentStage>>

}