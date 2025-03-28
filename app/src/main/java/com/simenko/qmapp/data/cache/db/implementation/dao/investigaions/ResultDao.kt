package com.simenko.qmapp.data.cache.db.implementation.dao.investigaions

import androidx.room.*
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.entities.DatabaseResult
import com.simenko.qmapp.data.cache.db.entities.DatabaseResultComplete
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.contract.DaoTimeDependentModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ResultDao : DaoBaseModel<ID, ID, DatabaseResult>, DaoTimeDependentModel<ID, ID, DatabaseResult> {
    @Query("SELECT * FROM `14_8_results` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseResult>

    @Query("SELECT * FROM `14_8_results` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseResult>>

    @Transaction
    @Query(
        "select r.* from `12_orders` o join `13_sub_orders` so on o.id = so.orderId " +
                "join `13_7_sub_order_tasks` t on so.id = t.subOrderId " +
                "join `14_samples` s on so.id = s.subOrderId " +
                "join `14_8_results` r on t.id = r.taskId and s.id = r.sampleId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    abstract override fun getRecordsByTimeRange(timeRange: Pair<Long, Long>): List<DatabaseResult>

    //    ToDo - is not 100% correct because parent is taskId + sampleId
    @Query("SELECT s.* FROM `14_8_results` as s where s.taskId = :parentId")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseResult>

    @Query("SELECT * FROM `14_8_results` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseResult?

    @Transaction
    @Query("select r.* from `result_complete` r where r.taskId = :taskId and r.sampleId = :sampleId;")
    abstract fun getRecordsByParentIdForUI(taskId: ID, sampleId: ID): Flow<List<DatabaseResultComplete>>

    @Transaction
    @Query("select r.* from `result_complete` r join `13_7_sub_order_tasks` t on r.taskId = t.id where t.subOrderId = :subOrderId;")
    abstract suspend fun getRecordsCompleteByParentIdForUI(subOrderId: ID): List<DatabaseResultComplete>
}