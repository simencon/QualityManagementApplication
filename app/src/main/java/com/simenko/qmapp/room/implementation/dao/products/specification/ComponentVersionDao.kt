package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentVersion
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentVersionDao: DaoBaseModel<ID, ID, DatabaseComponentVersion> {
    @Query("SELECT * FROM `10_components_versions` ORDER BY versionDate ASC")
    abstract override fun getRecords(): List<DatabaseComponentVersion>

    @Query("SELECT * FROM `10_components_versions` ORDER BY versionDate ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentVersion>>

    @Query("select * from `10_components_versions` where componentId = :parentId order by versionDate  asc")
    abstract suspend fun getRecordsByParentId(parentId: ID): List<DatabaseComponentVersion>

    @Query("SELECT * FROM `10_components_versions` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentVersion?
}