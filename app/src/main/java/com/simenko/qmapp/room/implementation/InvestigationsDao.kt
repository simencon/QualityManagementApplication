package com.simenko.qmapp.room.implementation

import androidx.lifecycle.LiveData
import androidx.room.*
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
    fun getOrdersStatuses(): LiveData<List<DatabaseOrdersStatus>>

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
    suspend fun getOrderById(id: String): DatabaseOrder


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
    suspend fun getSubOrderById(id: String): DatabaseSubOrder


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
    @Query("SELECT * FROM `14_samples` ORDER BY sampleNumber ASC")
    suspend fun getSamplesByList(): List<DatabaseSample>
    @Transaction
    @Query("SELECT s.* FROM `14_samples` as s " +
            "where s.subOrderId = :subOrderId")
    suspend fun getSamplesBySubOrderId(subOrderId: Int): List<DatabaseSample>


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

    @Query("SELECT * FROM `14_8_results` ORDER BY id ASC")
    fun getResultsByList(): List<DatabaseResult>

    @Transaction
    @Query("SELECT * FROM '12_orders' ORDER BY orderNumber;")
    suspend fun getOrdersDetailedList(): List<DatabaseOrderComplete>

    @Transaction
    @Query("SELECT * FROM `13_sub_orders` ORDER BY subOrderNumber;")
    suspend fun getSubOrdersDetailedList(): List<DatabaseSubOrderComplete>

    @Transaction
    @Query("SELECT * FROM `13_sub_orders`")
    fun getSubOrderWithChildren(): LiveData<List<DatabaseSubOrderShort>>

    @Transaction
    @Query("SELECT * FROM sub_order_task_complete")
    suspend fun getTasksDetailedList(): List<DatabaseSubOrderTaskComplete>

    @Transaction
    @Query("SELECT * FROM `samples_results`")
    suspend fun getSamplesDetailedList(): List<DatabaseSampleComplete>

    @Transaction
    @Query("SELECT * FROM result_complete")
    fun getResultsDetailed(): LiveData<List<DatabaseResultComplete>>

    @Transaction
    @Query("SELECT * FROM result_complete")
    fun getResultsDetailedFlow(): Flow<List<DatabaseResultComplete>>
}