package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseComponentVersion

@Dao
abstract class ComponentVersionDao: DaoBaseModel<DatabaseComponentVersion> {
    @Query("SELECT * FROM `10_components_versions` ORDER BY versionDate ASC")
    abstract override fun getRecords(): List<DatabaseComponentVersion>

    @Query("select * from `10_components_versions` where componentId = :parentId order by versionDate  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseComponentVersion>

    @Query("SELECT * FROM `10_components_versions` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseComponentVersion?

    @Query("SELECT * FROM `10_components_versions` ORDER BY versionDate ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseComponentVersion>>

}