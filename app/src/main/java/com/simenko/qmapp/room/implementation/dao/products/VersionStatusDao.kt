package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseVersionStatus

@Dao
abstract class VersionStatusDao : DaoBaseModel<DatabaseVersionStatus> {
    @Query("SELECT * FROM `0_versions_status` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseVersionStatus>

    @Query("select * from `0_versions_status` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseVersionStatus>

    @Query("SELECT * FROM `0_versions_status` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseVersionStatus?

    @Query("SELECT * FROM `0_versions_status` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseVersionStatus>>

}