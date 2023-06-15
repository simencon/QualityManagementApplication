package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseInputForOrder
import com.simenko.qmapp.room.entities.DatabaseResultsDecryption
import com.simenko.qmapp.room.implementation.DaoBase

@Dao
abstract class ResultDecryptionDao : DaoBase<DatabaseResultsDecryption> {
    @Query("SELECT * FROM `0_results_decryptions` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseResultsDecryption>

    @Query("SELECT * FROM `0_results_decryptions` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseResultsDecryption

    @Query("SELECT * FROM `0_results_decryptions` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseResultsDecryption>>
}