package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseItemComplete
import com.simenko.qmapp.room.entities.products.DatabaseItemTolerance
import com.simenko.qmapp.room.entities.products.DatabaseItemVersionComplete
import com.simenko.qmapp.room.entities.products.DatabaseProductVersion
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductVersionDao : DaoBaseModel<ID, ID, DatabaseProductVersion> {
    @Query("SELECT * FROM `9_products_versions` ORDER BY versionDate ASC")
    abstract override fun getRecords(): List<DatabaseProductVersion>

    @Query("SELECT * FROM `9_products_versions` ORDER BY versionDate ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductVersion>>

    @Query("select * from `9_products_versions` where productId = :parentId order by versionDate  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProductVersion>

    @Query("SELECT * FROM `9_products_versions` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductVersion?

    @Transaction
    @Query("SELECT * FROM item_versions where fItemId = :fpId or :fpId = '-1'")
    abstract fun getRecordsCompleteForUI(fpId: String): Flow<List<DatabaseItemVersionComplete>>

    @Transaction
    @Query("SELECT * FROM items_complete where fId = :fId")
    abstract suspend fun getParentRecordCompleteForUI(fId: String): DatabaseItemComplete?

    @Transaction
    @Query("SELECT * FROM item_versions where fId = :fId")
    abstract suspend fun getRecordCompleteForUI(fId: String): DatabaseItemVersionComplete?

    @Transaction
    @Query("SELECT * FROM items_tolerances where fVersionId = :fVersionId")
    abstract suspend fun getItemVersionTolerancesComplete(fVersionId: String): List<DatabaseItemTolerance.DatabaseItemToleranceComplete>
}