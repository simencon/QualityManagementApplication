package com.simenko.qmapp.room.implementation.dao.investigaions

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.other.Constants.UI_SAFETY_GAP
import com.simenko.qmapp.other.Constants.UI_TOTAL_VISIBLE
import com.simenko.qmapp.room.entities.DatabaseOrder
import com.simenko.qmapp.room.entities.DatabaseOrderComplete
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.contract.DaoTimeDependentModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderDao : DaoBaseModel<DatabaseOrder>, DaoTimeDependentModel<DatabaseOrder> {
    @Query("SELECT * FROM `12_orders` ORDER BY orderNumber desc")
    abstract override fun getRecords(): List<DatabaseOrder>

    @Query("select * from `12_orders` where orderTypeId = :parentId order by orderNumber asc")
    abstract override fun getRecordsByParentId(parentId: Int): List<DatabaseOrder>

    @Query("SELECT * FROM `12_orders` WHERE id = :id")
    abstract override fun getRecordById(id: String): DatabaseOrder?

    @Query("SELECT * FROM `12_orders` ORDER BY orderNumber desc")
    abstract override fun getRecordsForUI(): LiveData<List<DatabaseOrder>>

    @Transaction
    @Query(
        "select o.* from `12_orders` o " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    abstract override fun getRecordsByTimeRange(timeRange: Pair<Long, Long>): List<DatabaseOrder>

    @Transaction
    @Query("select max(createdDate) from `12_orders`")
    abstract fun getLatestOrderDate(): Long?

    @Transaction
    @Query("select min(createdDate) from `12_orders`")
    abstract fun getEarliestOrderDate(): Long?

    @Transaction
    @Query("select max(id) from `12_orders` where createdDate = :latestOrderDate")
    abstract fun getLatestOrderId(latestOrderDate: Long): Int?

    @Transaction
    @Query(
        """
            select * from(
            select * from  `12_orders` o
            where (o.createdDate >=:lastVisibleCreateDate)
            and (:orderTypeId = -1 or o.orderTypeId = :orderTypeId)
            and (:orderStatusId = -1 or o.statusId = :orderStatusId)
            and (:orderNumber = '' or o.orderNumber like :orderNumber)
            order by o.createdDate asc limit :safetyGap +:totalVisible
            )
            union
            select * from (
            select * from  `12_orders` o
            where (o.createdDate <:lastVisibleCreateDate)
            and (:orderTypeId = -1 or o.orderTypeId = :orderTypeId)
            and (:orderStatusId = -1 or o.statusId = :orderStatusId)
            and (:orderNumber = '' or o.orderNumber like :orderNumber)
            order by o.createdDate desc limit :safetyGap
            )
            order by createdDate desc
        """
    )
    abstract fun getRecordsByTimeRangeForUI(
        lastVisibleCreateDate: Long,
        safetyGap: Int = UI_SAFETY_GAP,
        totalVisible: Int = UI_TOTAL_VISIBLE,
        orderTypeId: Int,
        orderStatusId: Int,
        orderNumber: String
    ): Flow<List<DatabaseOrderComplete>>
}