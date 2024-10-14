package com.simenko.qmapp.data.cache.db.implementation.dao.investigaions

import androidx.room.*
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.entities.DatabaseResultsDecryption
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ResultDecryptionDao : DaoBaseModel<ID, ID, DatabaseResultsDecryption> {
    @Query("SELECT * FROM `0_results_decryptions` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseResultsDecryption>

    @Query("SELECT * FROM `0_results_decryptions` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseResultsDecryption>>

    @Query("select * from `0_results_decryptions` where id = :parentId")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseResultsDecryption>

    @Query("SELECT * FROM `0_results_decryptions` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseResultsDecryption?
}