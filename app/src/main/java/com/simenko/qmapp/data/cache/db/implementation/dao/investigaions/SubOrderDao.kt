package com.simenko.qmapp.data.cache.db.implementation.dao.investigaions

import androidx.room.*
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.data.cache.db.entities.DatabaseSubOrder
import com.simenko.qmapp.data.cache.db.entities.DatabaseSubOrderComplete
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.contract.DaoTimeDependentModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SubOrderDao : DaoBaseModel<ID, ID, DatabaseSubOrder>, DaoTimeDependentModel<ID, ID, DatabaseSubOrder> {
    @Query("SELECT * FROM `13_sub_orders` order by orderId asc")
    abstract override fun getRecords(): List<DatabaseSubOrder>

    @Query("SELECT * FROM `13_sub_orders` ORDER BY id ASC")
    abstract override fun getRecordsForUI(): Flow<List<DatabaseSubOrder>>

    @Transaction
    @Query(
        "select so.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    abstract override fun getRecordsByTimeRange(timeRange: Pair<Long, Long>): List<DatabaseSubOrder>

    @Query("select * from `13_sub_orders` where orderId = :parentId order by orderId asc")
    abstract fun getRecordsByParentId(parentId: ID): List<DatabaseSubOrder>

    @Query("SELECT * FROM `13_sub_orders` WHERE id = :id")
    abstract fun getRecordById(id: ID): DatabaseSubOrder?

    @Transaction
    @Query(
        """
            select so.* from `12_orders` o
            join `13_sub_orders` so on o.id = so.orderId
            where (o.createdDate >= :fromDate and o.createdDate <= :toDate)
            and (:orderTypeId = -1 or o.orderTypeId = :orderTypeId)
            and (:orderStatusId = -1 or so.statusId = :orderStatusId)
            and (:orderNumber = '' or o.orderNumber like :orderNumber)
            order by o.orderNumber desc;"""
    )
    abstract fun getRecordsByTimeRangeForUI(
        fromDate: Long,
        toDate: Long,
        orderTypeId: ID,
        orderStatusId: ID,
        orderNumber: String
    ): Flow<List<DatabaseSubOrderComplete>>

    @Transaction
    @Query("select so.* from `13_sub_orders` so where so.id = :id")
    abstract fun getRecordByIdComplete(id: ID): DatabaseSubOrderComplete?
}