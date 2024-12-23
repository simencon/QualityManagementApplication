package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseProductComponent
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductComponentDao : DaoBaseModel<ID, ID, DatabaseProductComponent> {
    @Query("SELECT * FROM `2_4_products_components` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProductComponent>

    @Query("SELECT * FROM `2_4_products_components` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProductComponent>>

    @Query("select * from `2_4_products_components` where id = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProductComponent>

    @Query("SELECT * FROM `2_4_products_components` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProductComponent?

    @Transaction
    @Query(
        "select pcc.* from products_components_complete as pcc join `3_4_component_kinds_components` as ckc on pcc.componentKindComponentId = ckc.id " +
                "where (pcc.productId = :pId or :pId = -1) and (ckc.componentKindId = :ckId or :ckId = -1)"
    )
//    ckId cold be a list
    abstract fun getRecordsCompleteForUI(pId: ID, ckId: ID): Flow<List<DatabaseProductComponent.DatabaseProductComponentComplete>>

    @Transaction
    @Query(
        "select pcc.* from products_components_complete as pcc join `3_4_component_kinds_components` as ckc on pcc.componentKindComponentId = ckc.id " +
                "where pcc.productId = :pId and ckc.componentKindId = :ckId and ckc.componentId = :cId"
    )
    abstract suspend fun getRecordCompleteById(pId: ID, ckId: ID, cId: ID): DatabaseProductComponent.DatabaseProductComponentComplete?

    @Transaction
    @Query("select pcc.* from products_components_complete as pcc")
    abstract fun getAllRecordsComplete(): Flow<List<DatabaseProductComponent.DatabaseProductComponentComplete>>

    @Query("SELECT * FROM `2_4_products_components` WHERE productId = :itemParentId AND componentKindComponentId = :itemId")
    abstract suspend fun findExistingRecord(itemParentId: ID, itemId: ID): DatabaseProductComponent?
}