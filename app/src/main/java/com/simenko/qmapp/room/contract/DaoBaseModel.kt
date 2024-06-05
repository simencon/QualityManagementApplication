package com.simenko.qmapp.room.contract

import androidx.room.*
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.retrofit.NetworkBaseModel
import kotlinx.coroutines.flow.Flow

interface DaoBaseModel<ID, PID, DB : DatabaseBaseModel<NetworkBaseModel<DB>, DomainBaseModel<DB>, ID, PID>> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: DB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecords(record: List<DB>)

    @Update
    fun updateRecord(record: DB)

    @Update
    fun updateRecords(record: List<DB>)

    @Delete
    fun deleteRecord(record: DB)

    @Delete
    fun deleteRecords(record: List<DB>)

    fun getRecords(): List<DB>

    /**
     * when Entity has no parent, function returns single record where id = parentId
     * */
    fun getRecordsByParentId(parentId: PID): List<DB>
    fun getRecordById(id: ID): DB?
    fun getRecordsForUI(): Flow<List<DB>>
}