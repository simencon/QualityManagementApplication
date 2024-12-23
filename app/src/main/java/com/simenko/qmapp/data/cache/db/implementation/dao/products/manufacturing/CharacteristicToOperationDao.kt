package com.simenko.qmapp.data.cache.db.implementation.dao.products.manufacturing

import androidx.room.Dao
import androidx.room.Query
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.products.DatabaseCharacteristicToOperation
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacteristicToOperationDao: DaoBaseModel<ID, ID, DatabaseCharacteristicToOperation> {
    @Query("SELECT * FROM `14_7_operations_to_chars` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseCharacteristicToOperation>

    @Query("SELECT * FROM `14_7_operations_to_chars` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseCharacteristicToOperation>>

    @Query("select * from `14_7_operations_to_chars` where operationId = :parentId order by id  asc")
    abstract fun getRecordsByParentId(parentId: ID): Flow<List<DatabaseCharacteristicToOperation>>

    @Query("SELECT * FROM `14_7_operations_to_chars` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseCharacteristicToOperation?
}