package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.other.Constants.EARLIEST_ORDER
import com.simenko.qmapp.other.Constants.HASH_CODE
import com.simenko.qmapp.other.Constants.INPUT_TO_PLACE_INVESTIGATION
import com.simenko.qmapp.other.Constants.INVESTIGATION_REASONS
import com.simenko.qmapp.other.Constants.INVESTIGATION_STATUSES
import com.simenko.qmapp.other.Constants.INVESTIGATION_TYPES
import com.simenko.qmapp.other.Constants.LATEST_ORDER
import com.simenko.qmapp.other.Constants.ORDERS
import com.simenko.qmapp.other.Constants.RESULTS
import com.simenko.qmapp.other.Constants.RESULT_DECRIPTIONS
import com.simenko.qmapp.other.Constants.RECORDS
import com.simenko.qmapp.other.Constants.RESULT_TASK
import com.simenko.qmapp.other.Constants.SAMPLES
import com.simenko.qmapp.other.Constants.SUB_ORDERS
import com.simenko.qmapp.other.Constants.SUB_ORDER_TASKS
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.converters.PairParam
import retrofit2.Response
import retrofit2.http.*

interface InvestigationsService {
    @GET(INPUT_TO_PLACE_INVESTIGATION)
    suspend fun getInputForOrder(): Response<List<NetworkInputForOrder>>

    @GET(INVESTIGATION_TYPES)
    suspend fun getOrdersTypes(): Response<List<NetworkOrdersType>>

    @GET(INVESTIGATION_REASONS)
    suspend fun getMeasurementReasons(): Response<List<NetworkReason>>

    @GET(INVESTIGATION_STATUSES)
    suspend fun getOrdersStatuses(): Response<List<NetworkOrdersStatus>>

    @GET(RESULT_DECRIPTIONS)
    suspend fun getResultsDecryptions(): Response<List<NetworkResultsDecryption>>

    @GET("$ORDERS/$LATEST_ORDER")
    suspend fun getLatestOrderDate(): Response<Long>

    @GET("$ORDERS/$EARLIEST_ORDER/{earliestOrderDate}")
    suspend fun getEarliestOrdersByStartingOrderDate(
        @Path("earliestOrderDate") earliestOrderDate: Long
    ): Response<List<NetworkOrder>>

    @GET("$ORDERS/$HASH_CODE/{timeRange}")
    suspend fun getOrdersHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<Int>

    @GET("$ORDERS/{timeRange}")
    suspend fun getOrdersByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkOrder>>

    @POST(ORDERS)
    suspend fun createOrder(@Body networkOrder: NetworkOrder): Response<NetworkOrder>

    @DELETE("$ORDERS/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<NetworkOrder>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$ORDERS/{id}")
    suspend fun editOrder(@Path("id") id: Int, @Body body: NetworkOrder): Response<NetworkOrder>

    @GET("$ORDERS/{id}")
    suspend fun getOrder(@Path("id") id: Int): Response<NetworkOrder>

    @GET("$SUB_ORDERS/$HASH_CODE/{timeRange}")
    suspend fun getSubOrdersHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<Int>

    @GET("$SUB_ORDERS/{timeRange}")
    suspend fun getSubOrdersByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkSubOrder>>

    @POST(SUB_ORDERS)
    suspend fun createSubOrder(@Body networkSubOrder: NetworkSubOrder): Response<NetworkSubOrder>

    @DELETE("$SUB_ORDERS/{id}")
    suspend fun deleteSubOrder(@Path("id") id: Int): Response<NetworkSubOrder>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$SUB_ORDERS/{id}")
    suspend fun editSubOrder(@Path("id") id: Int, @Body body: NetworkSubOrder): Response<NetworkSubOrder>

    @GET("$SUB_ORDERS/{id}")
    suspend fun getSubOrder(@Path("id") id: Int): Response<NetworkSubOrder>

    @GET("$SUB_ORDER_TASKS/$HASH_CODE/{timeRange}")
    suspend fun getTasksHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<Int>

    @GET("$SUB_ORDER_TASKS/{timeRange}")
    suspend fun getTasksDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkSubOrderTask>>

    @POST(SUB_ORDER_TASKS)
    suspend fun createTask(@Body networkSubOrderTask: NetworkSubOrderTask): Response<NetworkSubOrderTask>

    @POST("$SUB_ORDER_TASKS/$RECORDS")
    suspend fun createTasks(@Body records: List<NetworkSubOrderTask>): Response<List<NetworkSubOrderTask>>

    @DELETE("$SUB_ORDER_TASKS/{id}")
    suspend fun deleteSubOrderTask(@Path("id") id: Int): Response<NetworkSubOrderTask>

    @DELETE("$RECORDS/$SUB_ORDER_TASKS")
    suspend fun deleteSubOrderTasks(@Body ids: List<Int>): Response<List<NetworkSubOrderTask>>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$SUB_ORDER_TASKS/{id}")
    suspend fun editSubOrderTask(
        @Path("id") id: Int,
        @Body body: NetworkSubOrderTask
    ): Response<NetworkSubOrderTask>

    @GET("$SUB_ORDER_TASKS/{id}")
    suspend fun getSubOrderTask(@Path("id") id: Int): Response<NetworkSubOrderTask>

    @GET("$SAMPLES/$HASH_CODE/{timeRange}")
    suspend fun getSamplesHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<Int>

    @GET("$SAMPLES/{timeRange}")
    suspend fun getSamplesByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkSample>>

    @POST(SAMPLES)
    suspend fun createSample(@Body networkSample: NetworkSample): Response<NetworkSample>

    @POST("$SAMPLES/$RECORDS")
    suspend fun createSamples(@Body records: List<NetworkSample>): Response<List<NetworkSample>>

    @DELETE("$SAMPLES/{id}")
    suspend fun deleteSample(@Path("id") id: Int): Response<NetworkSample>
    @DELETE("$RECORDS/$SAMPLES")
    suspend fun deleteSamples(@Body ids: List<Int>): Response<List<NetworkSample>>

    @GET("$RESULTS/$HASH_CODE/{timeRange}")
    suspend fun getResultsHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<Int>

    @GET("$RESULTS/{timeRange}")
    suspend fun getResultsByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkResult>>

    @POST("$RESULTS/$RECORDS")
    suspend fun createResults(@Body records: List<NetworkResult>): Response<List<NetworkResult>>

    @DELETE("$RESULTS/$RESULT_TASK/{taskId}")
    suspend fun deleteResults(
        @Path("taskId") taskId: Int
    ): Response<List<NetworkResult>>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$RESULTS/{id}")
    suspend fun editResult(
        @Path("id") id: Int,
        @Body body: NetworkResult
    ): Response<NetworkResult>
}