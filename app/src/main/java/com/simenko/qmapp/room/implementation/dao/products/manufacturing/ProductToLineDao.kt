package com.simenko.qmapp.room.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductToLine
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductToLineDao: DaoBaseModel<DatabaseProductToLine> {
    @Query("SELECT * FROM `13_1_products_to_lines` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductToLine>

    @Query("select * from `13_1_products_to_lines` where lineId = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseProductToLine>

    @Query("SELECT * FROM `13_1_products_to_lines` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProductToLine?

    @Query("SELECT * FROM `13_1_products_to_lines` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductToLine>>

}