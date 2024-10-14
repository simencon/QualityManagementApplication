package com.simenko.qmapp.data.cache.db.implementation.dao.investigaions

import androidx.room.*
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.entities.DatabaseSample
import com.simenko.qmapp.data.cache.db.entities.DatabaseSampleComplete
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.contract.DaoTimeDependentModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SampleDao : DaoBaseModel<ID, ID, DatabaseSample>, DaoTimeDependentModel<ID, ID, DatabaseSample> {
    @Query("SELECT * FROM `14_samples` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseSample>

    @Query("SELECT * FROM `14_samples` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseSample>>

    @Transaction
    @Query(
        "select s.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "join `14_samples` s on so.id = s.subOrderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    abstract override fun getRecordsByTimeRange(timeRange: Pair<Long, Long>): List<DatabaseSample>

    @Query("SELECT s.* FROM `14_samples` as s where s.subOrderId = :parentId")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseSample>

    @Query("SELECT * FROM `14_samples` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseSample?

    @Transaction
    @Query(
        "select s.* from `samples_results` s " +
                "where s.subOrderId = :parentId;"
    )
    abstract fun getRecordsByParentIdForUI(parentId: ID): Flow<List<DatabaseSampleComplete>>
}