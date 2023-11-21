package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProductComponent
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductComponentDao : DaoBaseModel<DatabaseProductComponent> {
    @Query("SELECT * FROM `2_4_products_components` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductComponent>

    @Query("select * from `2_4_products_components` where id = :parentId order by id  asc")
    abstract override fun getRecordsByParentId(parentId: ID): List<DatabaseProductComponent>

    @Query("SELECT * FROM `2_4_products_components` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseProductComponent?

    @Query("SELECT * FROM `2_4_products_components` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductComponent>>

    @Transaction
    @Query("select pcc.* from products_components_complete as pcc join `3_4_component_kinds_components` as ckc on pcc.componentId = ckc.componentId " +
            "where pcc.productId = :pId and ckc.componentKindId = :ckId")
    abstract fun getRecordsCompleteForUI(pId: ID, ckId: ID): Flow<List<DatabaseProductComponent.DatabaseProductComponentComplete>>
}