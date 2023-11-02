package com.simenko.qmapp.room.implementation.dao.products

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.DatabaseItemVersionComplete
import com.simenko.qmapp.room.entities.DatabaseProductVersion
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductVersionDao: DaoBaseModel<DatabaseProductVersion> {
    @Query("SELECT * FROM `9_products_versions` ORDER BY versionDate ASC")
    abstract override fun getRecords(): List<DatabaseProductVersion>

    @Query("select * from `9_products_versions` where productId = :parentId order by versionDate  asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseProductVersion>

    @Query("SELECT * FROM `9_products_versions` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProductVersion?

    @Query("SELECT * FROM `9_products_versions` ORDER BY versionDate ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseProductVersion>>

    @Transaction
    @Query("SELECT * FROM item_versions")
    abstract fun getItemVersionsComplete(): Flow<List<DatabaseItemVersionComplete>>

}