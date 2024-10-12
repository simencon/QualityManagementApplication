package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponentKind
import com.simenko.qmapp.room.entities.products.DatabaseKey
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductKeyDao : DaoBaseModel<ID, ID, DatabaseKey> {
    @Query("SELECT * FROM `0_keys` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseKey>

    @Query("SELECT * FROM `0_keys` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseKey>>

    @Query("select * from `0_keys` where projectId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseKey>

    @Query("SELECT * FROM `0_keys` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseKey?

    @Transaction
    @Query("select * from `keys_complete` where projectId = :parentId order by id  asc")
    abstract fun getRecordsCompleteForUI(parentId: ID): Flow<List<DatabaseKey.DatabaseKeyComplete>>

    @Transaction
    @Query("SELECT * FROM `keys_complete` WHERE id = :id")
    abstract fun getRecordCompleteById(id: ID): DatabaseKey.DatabaseKeyComplete?

    @Transaction
    @Query(
        """select k.*, pk_sd.subDepID
            from `0_keys` k
                join `1_1_product_kind_keys` pkk on k.ID = pkk.keyID
                join `1_product_kinds` pk on pkk.productKindID = pk.ID
                join `11_1_prod_kinds_to_s_departments` pk_sd on pk.ID = pk_sd.prodKindID
                where pk_sd.subDepId = :depId
        """
    )
    abstract fun getAllProductKeysBySubDepartmentId(depId: ID): Flow<List<DatabaseKey>>

    @Transaction
    @Query(
        """select k.*, ck_sd.subDepID
            from `0_keys` k
                join `3_1_component_kind_keys` ckk on k.ID = ckk.keyID
                join `3_component_kinds` ck on ckk.componentKindId = ck.ID
                join `11_3_comp_kinds_to_s_departments` ck_sd on ck.ID = ck_sd.compKindId
                where ck_sd.subDepId = :depId
        """
    )
    abstract fun getAllComponentKeysBySubDepartmentId(depId: ID): Flow<List<DatabaseKey>>

    @Transaction
    @Query(
        """select k.*, sk_sd.subDepID
            from `0_keys` k
                join `5_1_component_stage_kind_keys` skk on k.ID = skk.keyID
                join `5_component_stage_kinds` sk on skk.componentStageKindId = sk.ID
                join `11_5_comp_stages_to_s_departments` sk_sd on sk.ID = sk_sd.stageKindId
                where sk_sd.subDepId = :depId
        """
    )
    abstract fun getAllStageKeysBySubDepartmentId(depId: ID): Flow<List<DatabaseKey>>
}