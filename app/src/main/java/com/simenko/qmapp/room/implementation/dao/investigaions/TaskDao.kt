package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseSubOrderTask
import com.simenko.qmapp.room.entities.DatabaseSubOrderTaskComplete
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.contract.DaoTimeDependentModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TaskDao : DaoBaseModel<DatabaseSubOrderTask>, DaoTimeDependentModel<DatabaseSubOrderTask> {
    @Query("SELECT * FROM `13_7_sub_order_tasks` ORDER BY id ASC")
    abstract override fun getRecords(): List<DatabaseSubOrderTask>

    @Query("SELECT * FROM `13_7_sub_order_tasks` WHERE subOrderId=:parentId ")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseSubOrderTask>

    @Query("SELECT * FROM `13_7_sub_order_tasks` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseSubOrderTask?

    @Query("SELECT * FROM `13_7_sub_order_tasks` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseSubOrderTask>>

    @Transaction
    @Query(
        "select t.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "join `sub_order_task_complete` t on so.id = t.subOrderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    abstract override fun getRecordsByTimeRange(timeRange: Pair<Long, Long>): List<DatabaseSubOrderTask>

    @Transaction
    @Query(
        "select t.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "join `sub_order_task_complete` t on so.id = t.subOrderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    abstract fun getRecordsByTimeRangeForUI(timeRange: Pair<Long, Long>): Flow<List<DatabaseSubOrderTaskComplete>>
}