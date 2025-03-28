package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseVersionStatus
import kotlinx.coroutines.flow.Flow

@Dao
abstract class VersionStatusDao : DaoBaseModel<ID, ID, DatabaseVersionStatus> {
    @Query("SELECT * FROM `0_versions_status` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseVersionStatus>

    @Query("SELECT * FROM `0_versions_status` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseVersionStatus>>

    @Query("select * from `0_versions_status` where id = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseVersionStatus>

    @Query("SELECT * FROM `0_versions_status` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseVersionStatus?

}