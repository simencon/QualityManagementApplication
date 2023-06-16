package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseSample
import com.simenko.qmapp.room.entities.DatabaseSampleComplete
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.contract.DaoTimeDependentModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SampleDao : DaoBaseModel<DatabaseSample>, DaoTimeDependentModel<DatabaseSample> {
    @Query("SELECT * FROM `14_samples` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseSample>

    @Query("SELECT s.* FROM `14_samples` as s where s.subOrderId = :parentId")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseSample>

    @Query("SELECT * FROM `14_samples` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseSample?

    @Query("SELECT * FROM `14_samples` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseSample>>

    @Transaction
    @Query(
        "select s.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "join `14_samples` s on so.id = s.subOrderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    abstract override fun getRecordsByTimeRange(timeRange: Pair<Long, Long>): List<DatabaseSample>

    @Transaction
    @Query(
        "select s.* from `13_sub_orders` so " +
                "join `samples_results` s on so.id = s.subOrderId " +
                "where so.id = :parentId;"
    )
    abstract fun getRecordsByParentIdForUI(parentId: Int): Flow<List<DatabaseSampleComplete>>
}