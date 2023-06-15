package com.simenko.qmapp.room.implementation

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.DatabaseBaseModel

interface DaoBase<DB: DatabaseBaseModel<NetworkBaseModel<DB>, DomainBaseModel<DB>>> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: DB)

    @Update
    fun updateRecord(record: DB)

    @Delete
    fun deleteRecord(record: DB)

    fun getRecords(): List<DB>

    fun getRecordById(id: String): DB

    fun getRecordsForUI(): LiveData<List<DB>>
}