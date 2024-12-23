package com.simenko.qmapp.data.cache.db.implementation.dao.investigaions

import androidx.room.*
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.entities.DatabaseSubOrderTask
import com.simenko.qmapp.data.cache.db.entities.DatabaseSubOrderTaskComplete
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.contract.DaoTimeDependentModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TaskDao : DaoBaseModel<ID, ID, DatabaseSubOrderTask>, DaoTimeDependentModel<ID, ID, DatabaseSubOrderTask> {
    @Query("SELECT * FROM `13_7_sub_order_tasks` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseSubOrderTask>

    @Query("SELECT * FROM `13_7_sub_order_tasks` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseSubOrderTask>>

    @Transaction
    @Query(
        "select t.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "join `sub_order_task_complete` t on so.id = t.subOrderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    abstract override fun getRecordsByTimeRange(timeRange: Pair<Long, Long>): List<DatabaseSubOrderTask>

    @Query("SELECT * FROM `13_7_sub_order_tasks` WHERE subOrderId=:parentId ")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseSubOrderTask>

    @Query("SELECT * FROM `13_7_sub_order_tasks` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseSubOrderTask?

    @Transaction
    @Query(
        "select t.* from `sub_order_task_complete` t " +
                "where t.subOrderId = :subOrderId;"
    )
    abstract fun getRecordsByParentIdForUI(subOrderId: ID): Flow<List<DatabaseSubOrderTaskComplete>>

    @Transaction
    @Query(
        "select t.* from `sub_order_task_complete` t " +
                "where t.subOrderId = :subOrderId;"
    )
    abstract suspend fun getRecordsCompleteByParentIdForUI(subOrderId: ID): List<DatabaseSubOrderTaskComplete>
}