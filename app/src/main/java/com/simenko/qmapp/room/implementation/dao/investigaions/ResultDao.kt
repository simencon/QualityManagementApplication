package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseResult
import com.simenko.qmapp.room.entities.DatabaseResultComplete
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.contract.DaoTimeDependentModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ResultDao : DaoBaseModel<DatabaseResult>, DaoTimeDependentModel<DatabaseResult> {
    @Query("SELECT * FROM `14_8_results` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseResult>

    @Query("SELECT * FROM `14_8_results` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseResult

    @Query("SELECT * FROM `14_8_results` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseResult>>

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

    @Transaction
    @Query(
        "select r.* from `13_sub_orders` so " +
                "join `13_7_sub_order_tasks` t on so.id = t.subOrderId " +
                "join `14_samples` s on so.id = s.subOrderId " +
                "join `result_complete` r on t.id = r.taskId and s.id = r.sampleId " +
                "where so.ID = :parentId;"
    )
    abstract fun getRecordsByParentIdForUI(parentId: Int): Flow<List<DatabaseResultComplete>>
}