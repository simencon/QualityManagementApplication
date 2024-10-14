package com.simenko.qmapp.data.cache.db.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseProductTolerance
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductToleranceDao : DaoBaseModel<ID, ID, DatabaseProductTolerance> {
    @Query("SELECT * FROM `9_8_product_tolerances` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductTolerance>

    @Query("SELECT * FROM `9_8_product_tolerances` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductTolerance>>

    @Query("select * from `9_8_product_tolerances` where versionId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProductTolerance>

    @Query("SELECT * FROM `9_8_product_tolerances` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductTolerance?
}