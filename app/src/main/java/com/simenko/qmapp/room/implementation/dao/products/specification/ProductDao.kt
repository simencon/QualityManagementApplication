package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseProduct
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao : DaoBaseModel<ID, ID, DatabaseProduct> {
    @Query("SELECT * FROM `2_products` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseProduct>

    @Query("SELECT * FROM `2_products` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseProduct>>

    @Query("select * from `2_products` where productBaseId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseProduct>

    @Transaction
    @Query("SELECT * FROM `products_complete` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseProduct.DatabaseProductComplete?

    @Transaction
    @Query("""
        select p.*, k_mc.chID, pk_sd.subDepID
        from `products_complete` p
            join `12_1_products_keys` k_mc on p.keyID = k_mc.keyID
            join `12_manufacturing_channels` mc on k_mc.chID = mc.ID
            join `1_2_product_kinds_products` p_pk on p.ID = p_pk.productID
            join `1_product_kinds` pk on p_pk.productKindID = pk.ID
            join `1_1_product_kind_keys` pkk on pk.ID = pkk.productKindID and p.keyID = pkk.keyID
            join `11_1_prod_kinds_to_s_departments` pk_sd on pk.ID = pk_sd.prodKindID and mc.subDepID = pk_sd.subDepID
            where pk_sd.subDepId = :subDepId and k_mc.chId = :channelId
    """)
    abstract fun getRecordsBySubDepIdAndChannelId(subDepId: ID, channelId: ID): Flow<List<DatabaseProduct.DatabaseProductComplete>>
}