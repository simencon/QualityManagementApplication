package com.simenko.qmapp.room.contract

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.retrofit.NetworkBaseModel

interface DaoBaseModel<DB : DatabaseBaseModel<NetworkBaseModel<DB>, DomainBaseModel<DB>>> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: DB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecords(record: List<DB>)

    @Update
    fun updateRecord(record: DB)

    @Delete
    fun deleteRecord(record: DB)

    @Delete
    fun deleteRecords(record: List<DB>)

    fun getRecords(): List<DB>

    /**
     * when Entity has no parent, function returns single record where id = parentId
     * */
    fun getRecordsByParentId(parentId: Int): List<DB>

    fun getRecordById(id: String): DB?

    fun getRecordsForUI(): LiveData<List<DB>>
}