package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.room.entities.DatabaseSubOrder
import com.simenko.qmapp.room.entities.DatabaseSubOrderComplete
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.contract.DaoTimeDependentModel
import com.simenko.qmapp.room.entities.DatabaseOrder
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SubOrderDao : DaoBaseModel<DatabaseSubOrder>, DaoTimeDependentModel<DatabaseSubOrder> {
    @Query("SELECT * FROM `13_sub_orders` order by orderId asc")
    abstract override fun getRecords(): List<DatabaseSubOrder>

    @Query("select * from `13_sub_orders` where orderId = :parentId order by orderId asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseSubOrder>

    @Query("SELECT * FROM `13_sub_orders` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseSubOrder?

    @Query("SELECT * FROM `13_sub_orders` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseSubOrder>>

    @Transaction
    @Query(
        "select so.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    abstract override fun getRecordsByTimeRange(timeRange: Pair<Long, Long>): List<DatabaseSubOrder>

    @Transaction
    @Query(
        "select so.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    abstract fun getRecordsByTimeRangeForUI(timeRange: Pair<Long, Long>): Flow<List<DatabaseSubOrderComplete>>

    @Transaction
    @Query("select so.* from `13_sub_orders` so where so.id = :id")
    abstract fun getRecordByIdComplete(id: Int): DatabaseSubOrderComplete?
}