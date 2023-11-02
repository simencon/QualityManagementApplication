package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseResultsDecryption
import com.simenko.qmapp.room.contract.DaoBaseModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ResultDecryptionDao : DaoBaseModel<DatabaseResultsDecryption> {
    @Query("SELECT * FROM `0_results_decryptions` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseResultsDecryption>

    @Query("select * from `0_results_decryptions` where id = :parentId")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseResultsDecryption>

    @Query("SELECT * FROM `0_results_decryptions` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseResultsDecryption?

    @Query("SELECT * FROM `0_results_decryptions` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseResultsDecryption>>
}