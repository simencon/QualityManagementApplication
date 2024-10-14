package com.simenko.qmapp.data.cache.db.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseProductToLine
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductToLineDao : DaoBaseModel<ID, ID, DatabaseProductToLine> {
    @Query("SELECT * FROM `13_1_products_to_lines` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductToLine>

    @Query("SELECT * FROM `13_1_products_to_lines` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductToLine>>

    @Query("select * from `13_1_products_to_lines` where lineId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): Flow<List<DatabaseProductToLine>>

    @Query("SELECT * FROM `13_1_products_to_lines` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductToLine?
}