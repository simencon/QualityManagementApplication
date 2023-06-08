package com.simenko.qmapp.room.implementation

import androidx.lifecycle.LiveData
import androidx.room.*
import com.simenko.qmapp.other.Constants.UI_SAFETY_GAP
import com.simenko.qmapp.other.Constants.UI_TOTAL_VISIBLE
import com.simenko.qmapp.room.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestigationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInputForOrderAll(company: List<DatabaseInputForOrder>)

    @Query("SELECT * FROM `1_1_inputForMeasurementRegister` ORDER BY charOrder ASC")
    fun getInputForOrder(): LiveData<List<DatabaseInputForOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrdersStatusesAll(company: List<DatabaseOrdersStatus>)

    @Query("SELECT * FROM `0_orders_statuses` ORDER BY id ASC")
    fun getOrdersStatusesFlow(): Flow<List<DatabaseOrdersStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeasurementReasonsAll(company: List<DatabaseReason>)

    @Query("SELECT * FROM `0_measurement_reasons` ORDER BY reasonOrder ASC")
    fun getMeasurementReasons(): LiveData<List<DatabaseReason>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrdersTypesAll(company: List<DatabaseOrdersType>)

    @Query("SELECT * FROM `0_orders_types` ORDER BY id ASC")
    fun getOrdersTypes(): LiveData<List<DatabaseOrdersType>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrdersAll(records: List<DatabaseOrder>)

    @Query("DELETE FROM `12_orders`")
    fun deleteOrdersAll()

    @Query("SELECT * FROM `12_orders` ORDER BY orderNumber ASC")
    fun getOrders(): LiveData<List<DatabaseOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(record: DatabaseOrder)

    @Update
    fun updateOrder(record: DatabaseOrder)

    @Delete
    fun deleteOrder(record: DatabaseOrder)

    @Query("SELECT * FROM `12_orders` ORDER BY orderNumber ASC")
    fun getOrdersByList(): List<DatabaseOrder>

    @Query("SELECT * FROM `12_orders` WHERE id=:id ")
    suspend fun getOrderById(id: String): DatabaseOrder?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubOrdersAll(records: List<DatabaseSubOrder>)

    @Query("DELETE FROM `13_sub_orders`")
    fun deleteSubOrdersAll()

    @Query("SELECT * FROM `13_sub_orders` ORDER BY subOrderNumber ASC")
    fun getSubOrders(): LiveData<List<DatabaseSubOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubOrder(record: DatabaseSubOrder)

    @Update
    fun updateSubOrder(record: DatabaseSubOrder)

    @Delete
    fun deleteSubOrder(record: DatabaseSubOrder)

    @Query("SELECT * FROM `13_sub_orders` ORDER BY subOrderNumber ASC")
    fun getSubOrdersByList(): List<DatabaseSubOrder>

    @Query("SELECT * FROM `13_sub_orders` WHERE id=:id ")
    suspend fun getSubOrderById(id: String): DatabaseSubOrder?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubOrderTasksAll(records: List<DatabaseSubOrderTask>)

    @Query("DELETE FROM `13_7_sub_order_tasks`")
    fun deleteSubOrderTasksAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubOrderTask(record: DatabaseSubOrderTask)

    @Update
    fun updateSubOrderTask(record: DatabaseSubOrderTask)

    @Delete
    fun deleteSubOrderTask(record: DatabaseSubOrderTask)

    @Query("SELECT * FROM `13_7_sub_order_tasks` ORDER BY id ASC")
    fun getSubOrderTasksByList(): List<DatabaseSubOrderTask>

    @Query("SELECT * FROM `13_7_sub_order_tasks` WHERE id=:id ")
    suspend fun getSubOrderTaskById(id: String): DatabaseSubOrderTask?

    @Query("SELECT * FROM `13_7_sub_order_tasks` ORDER BY charId ASC")
    fun getSubOrderTasks(): LiveData<List<DatabaseSubOrderTask>>

    @Query("SELECT * FROM `13_7_sub_order_tasks` WHERE subOrderId=:sunOrderId ")
    suspend fun getTasksBySubOrderId(sunOrderId: String): List<DatabaseSubOrderTask>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSamplesAll(records: List<DatabaseSample>)

    @Query("DELETE FROM `14_samples`")
    fun deleteSamplesAll()

    @Query("SELECT * FROM `14_samples` ORDER BY sampleNumber ASC")
    fun getSamples(): LiveData<List<DatabaseSample>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSample(record: DatabaseSample)

    @Update
    fun updateSample(record: DatabaseSample)

    @Delete
    fun deleteSample(record: DatabaseSample)

    @Transaction
    @Query("SELECT s.* FROM `14_samples` as s where s.subOrderId = :subOrderId")
    suspend fun getSamplesBySubOrderId(subOrderId: Int): List<DatabaseSample>

    @Query("SELECT * FROM `14_samples` WHERE id=:id ")
    suspend fun getSampleById(id: String): DatabaseSample?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResultsDecryptionsAll(company: List<DatabaseResultsDecryption>)

    @Query("SELECT * FROM `0_results_decryptions` ORDER BY id ASC")
    fun getResultsDecryptions(): LiveData<List<DatabaseResultsDecryption>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResultsAll(records: List<DatabaseResult>)

    @Query("SELECT * FROM `14_8_results` ORDER BY id ASC")
    fun getResults(): LiveData<List<DatabaseResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResult(record: DatabaseResult)

    @Update
    fun updateResult(record: DatabaseResult)

    @Delete
    fun deleteResult(record: DatabaseResult)

    @Query("SELECT * FROM `14_8_results` WHERE id=:id ")
    suspend fun getResultById(id: String): DatabaseResult?

    @Query("SELECT * FROM `14_8_results` WHERE taskId=:id ")
    suspend fun getResultsByTaskId(id: String): List<DatabaseResult>

    @Transaction
    @Query("select max(createdDate) from `12_orders`")
    suspend fun getLatestOrderDate(): Long?

    @Transaction
    @Query("select min(createdDate) from `12_orders`")
    suspend fun getEarliestOrderDate(): Long?

    @Transaction
    @Query("select max(id) from `12_orders` where createdDate = :latestOrderDate")
    suspend fun getLatestOrderId(latestOrderDate: Long): Int?

    @Transaction
    @Query(
        "select * from( select * from  `12_orders` o where o.createdDate >=:lastVisibleCreateDate " +
                "order by o.createdDate asc limit :safetyGap +:totalVisible) " +
                "union " +
                "select * from (select * from  `12_orders` o where o.createdDate <:lastVisibleCreateDate " +
                "order by o.createdDate desc limit :safetyGap) " +
                "order by createdDate desc"
    )
    fun ordersListByLastVisibleId(
        lastVisibleCreateDate: Long,
        safetyGap: Int = UI_SAFETY_GAP,
        totalVisible: Int = UI_TOTAL_VISIBLE
    ): Flow<List<DatabaseOrderComplete>>

    @Transaction
    @Query(
        "select o.* from `12_orders` o " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    suspend fun getOrdersByDateRange(timeRange: Pair<Long, Long>): List<DatabaseOrder>

    @Transaction
    @Query(
        "select so.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    fun getSubOrdersByDateRange(timeRange: Pair<Long, Long>): Flow<List<DatabaseSubOrderComplete>>

    @Transaction
    @Query(
        "select so.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    suspend fun getSubOrdersByDateRangeL(timeRange: Pair<Long, Long>): List<DatabaseSubOrder>

    @Transaction
    @Query("select so.* from `13_sub_orders` so where so.id = :subOrderId")
    suspend fun getSubOrdersById(subOrderId: Int): DatabaseSubOrderComplete?

    @Transaction
    @Query("SELECT * FROM `13_sub_orders`")
    fun getSubOrderWithChildren(): LiveData<List<DatabaseSubOrderShort>>

    @Transaction
    @Query(
        "select t.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "join `sub_order_task_complete` t on so.id = t.subOrderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    fun getTasksDateRange(timeRange: Pair<Long, Long>): Flow<List<DatabaseSubOrderTaskComplete>>

    @Transaction
    @Query(
        "select t.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "join `sub_order_task_complete` t on so.id = t.subOrderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    suspend fun getTasksByDateRangeL(timeRange: Pair<Long, Long>): List<DatabaseSubOrderTask>

    @Transaction
    @Query(
        "select s.* from `13_sub_orders` so " +
                "join `samples_results` s on so.id = s.subOrderId " +
                "where so.id = :subOrderId;"
    )
    fun getSamplesBySubOrder(subOrderId: Int): Flow<List<DatabaseSampleComplete>>

    @Transaction
    @Query(
        "select s.* from `12_orders` o " +
                "join `13_sub_orders` so on o.id = so.orderId " +
                "join `14_samples` s on so.id = s.subOrderId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    suspend fun getSamplesByDateRange(timeRange: Pair<Long, Long>): List<DatabaseSample>

    @Transaction
    @Query(
        "select r.* from `13_sub_orders` so " +
                "join `13_7_sub_order_tasks` t on so.id = t.subOrderId " +
                "join `14_samples` s on so.id = s.subOrderId " +
                "join `result_complete` r on t.id = r.taskId and s.id = r.sampleId " +
                "where so.ID = :subOrderId;"
    )
    fun getResultsBySubOrder(subOrderId: Int): Flow<List<DatabaseResultComplete>>

    @Transaction
    @Query(
        "select r.* from `12_orders` o join `13_sub_orders` so on o.id = so.orderId " +
                "join `13_7_sub_order_tasks` t on so.id = t.subOrderId " +
                "join `14_samples` s on so.id = s.subOrderId " +
                "join `14_8_results` r on t.id = r.taskId and s.id = r.sampleId " +
                "where o.createdDate >= substr(:timeRange,1,instr(:timeRange,':')-1) " +
                "and o.createdDate <= substr(:timeRange,instr(:timeRange,':')+1,length(:timeRange)-instr(:timeRange,':')) ;"
    )
    suspend fun getResultsByDateRange(timeRange: Pair<Long, Long>): List<DatabaseResult>
}