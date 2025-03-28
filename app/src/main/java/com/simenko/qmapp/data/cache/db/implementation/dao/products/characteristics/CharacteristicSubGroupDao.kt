package com.simenko.qmapp.data.cache.db.implementation.dao.products.characteristics

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseCharSubGroup
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacteristicSubGroupDao: DaoBaseModel<ID, ID, DatabaseCharSubGroup> {
    @Query("SELECT * FROM `0_ish_sub_characteristics` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseCharSubGroup>

    @Query("SELECT * FROM `0_ish_sub_characteristics` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseCharSubGroup>>

    @Query("select * from `0_ish_sub_characteristics` where id = :parentId order by id asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseCharSubGroup>

    @Query("SELECT * FROM `0_ish_sub_characteristics` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseCharSubGroup?

    @Transaction
    @Query("select * from characteristic_sub_group_complete where charGroupId = :parentId")
    abstract fun getRecordsCompleteForUI(parentId: ID): Flow<List<DatabaseCharSubGroup.DatabaseCharSubGroupComplete>>

    @Transaction
    @Query("select * from characteristic_sub_group_complete as csg where csg.id = :id")
    abstract fun getRecordCompleteById(id: ID): DatabaseCharSubGroup.DatabaseCharSubGroupComplete
}