package com.simenko.qmapp.room.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.products.DatabaseComponent
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentDao : DaoBaseModel<ID, ID, DatabaseComponent> {
    @Query("SELECT * FROM `4_components` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponent>

    @Query("SELECT * FROM `4_components` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponent>>

    @Query("select * from `4_components` where keyId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseComponent>

    @Transaction
    @Query("SELECT * FROM `components_complete` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponent.DatabaseComponentComplete?

    @Transaction
    @Query(
        """
        select c.*, k_mc.chID, ck_sd.subDepID
        from `components_complete` c
            join `12_3_components_keys` k_mc on c.keyID = k_mc.keyID
            join `12_manufacturing_channels` mc on k_mc.chID = mc.ID
            join `3_4_component_kinds_components` c_ck on c.ID = c_ck.componentId
            join `3_component_kinds` ck on c_ck.componentKindId = ck.ID
            join `3_1_component_kind_keys` ckk on ck.ID = ckk.componentKindId and c.keyID = ckk.keyID
            join `11_3_comp_kinds_to_s_departments` ck_sd on ck.ID = ck_sd.compKindId and mc.subDepID = ck_sd.subDepID
            where ck_sd.subDepId = :subDepId and k_mc.chId = :channelId
    """
    )
    abstract fun getRecordsBySubDepIdAndChannelId(subDepId: ID, channelId: ID): Flow<List<DatabaseComponent.DatabaseComponentComplete>>
}