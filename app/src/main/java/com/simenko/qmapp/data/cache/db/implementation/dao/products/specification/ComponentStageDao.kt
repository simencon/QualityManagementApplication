package com.simenko.qmapp.data.cache.db.implementation.dao.products.specification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseComponentStage
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ComponentStageDao : DaoBaseModel<ID, ID, DatabaseComponentStage> {
    @Query("SELECT * FROM `6_components_in_stages` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseComponentStage>

    @Query("SELECT * FROM `6_components_in_stages` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseComponentStage>>

    @Query("select * from `6_components_in_stages` where keyId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseComponentStage>

    @Query("SELECT * FROM `6_components_in_stages` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseComponentStage?

    @Transaction
    @Query(
        """
        select s.*
        from `component_stages_complete` s
            join `12_5_stages_keys` k_mc on s.keyID = k_mc.keyID
            join `12_manufacturing_channels` mc on k_mc.chID = mc.ID
            join `5_6_component_stage_kinds_component_stages` s_sk on s.ID = s_sk.componentStageId
            join `5_component_stage_kinds` sk on s_sk.componentStageKindId = sk.ID
            join `5_1_component_stage_kind_keys` skk on sk.ID = skk.componentStageKindId and s.keyID = skk.keyID
            join `11_5_comp_stages_to_s_departments` sk_sd on sk.ID = sk_sd.stageKindId and mc.subDepID = sk_sd.subDepID
            where sk_sd.subDepId = :subDepId and k_mc.chId = :channelId
    """
    )
    abstract fun getRecordsBySubDepIdAndChannelId(subDepId: ID, channelId: ID): Flow<List<DatabaseComponentStage.DatabaseComponentStageComplete>>
}