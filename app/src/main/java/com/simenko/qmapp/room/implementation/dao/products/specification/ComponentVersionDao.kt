package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentVersion
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentVersionDao: DaoBaseModel<DatabaseComponentVersion> {
    @Query("SELECT * FROM `10_components_versions` ORDER BY versionDate ASC")
    abstract override fun getRecords(): List<DatabaseComponentVersion>

    @Query("select * from `10_components_versions` where componentId = :parentId order by versionDate  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseComponentVersion>

    @Query("SELECT * FROM `10_components_versions` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentVersion?

    @Query("SELECT * FROM `10_components_versions` ORDER BY versionDate ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentVersion>>
}