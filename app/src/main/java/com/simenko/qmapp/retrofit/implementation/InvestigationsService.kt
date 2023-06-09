package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.converters.PairParam
import retrofit2.Response
import retrofit2.http.*

interface InvestigationsService {
    @GET("inputsForMeasurementRegister")
    suspend fun getInputForOrder(): List<NetworkInputForOrder>

    @GET("ordersStatuses")
    suspend fun getOrdersStatuses(): List<NetworkOrdersStatus>

    @GET("measurementReasons")
    suspend fun getMeasurementReasons(): List<NetworkReason>

    @GET("ordersTypes")
    suspend fun getOrdersTypes(): List<NetworkOrdersType>

    @GET("orders/latestOrder")
    suspend fun getLatestOrderDate(): Response<Long>

    @GET("orders/earliestOrder/{earliestOrderDate}")
    suspend fun getEarliestOrdersByStartingOrderDate(
        @Path("earliestOrderDate") earliestOrderDate: Long
    ): Response<List<NetworkOrder>>

    @GET("orders/hashCode/{timeRange}")
    suspend fun getOrdersHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Int

    @GET("orders/{timeRange}")
    suspend fun getOrdersByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkOrder>>

    @POST("orders")
    suspend fun createOrder(@Body networkOrder: NetworkOrder): NetworkOrder

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("orders/{id}")
    suspend fun editOrder(@Path("id") id: Int, @Body body: NetworkOrder): Response<Unit>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): NetworkOrder

    @GET("subOrders/hashCode/{timeRange}")
    suspend fun getSubOrdersHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Int

    @GET("subOrders/{timeRange}")
    suspend fun getSubOrdersByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkSubOrder>>

    @POST("subOrders")
    suspend fun createSubOrder(@Body networkSubOrder: NetworkSubOrder): NetworkSubOrder

    @DELETE("subOrders/{id}")
    suspend fun deleteSubOrder(@Path("id") id: Int): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("subOrders/{id}")
    suspend fun editSubOrder(@Path("id") id: Int, @Body body: NetworkSubOrder): Response<Unit>

    @GET("subOrders/{id}")
    suspend fun getSubOrder(@Path("id") id: Int): NetworkSubOrder

    @GET("subOrderTasks/hashCode/{timeRange}")
    suspend fun getTasksHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Int

    @GET("subOrderTasks/{timeRange}")
    suspend fun getTasksDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkSubOrderTask>>

    @POST("subOrderTasks")
    suspend fun createSubOrderTask(@Body networkSubOrderTask: NetworkSubOrderTask): NetworkSubOrderTask

    @DELETE("subOrderTasks/{id}")
    suspend fun deleteSubOrderTask(@Path("id") id: Int): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("subOrderTasks/{id}")
    suspend fun editSubOrderTask(
        @Path("id") id: Int,
        @Body body: NetworkSubOrderTask
    ): Response<NetworkSubOrderTask>

    @GET("subOrderTasks/{id}")
    suspend fun getSubOrderTask(@Path("id") id: Int): NetworkSubOrderTask

    @GET("samples/hashCode/{timeRange}")
    suspend fun getSamplesHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Int

    @GET("samples/{timeRange}")
    suspend fun getSamplesByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkSample>>

    @POST("samples")
    suspend fun createSample(@Body networkSample: NetworkSample): NetworkSample

    @DELETE("samples/{id}")
    suspend fun deleteSample(@Path("id") id: Int): Response<Unit>

    @GET("resultsDecriptions")
    suspend fun getResultsDecryptions(): List<NetworkResultsDecryption>

    @GET("results/hashCode/{timeRange}")
    suspend fun getResultsHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Int

    @GET("results/{timeRange}")
    suspend fun getResultsByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkResult>>

    @POST("results/records")
    suspend fun createResults(@Body records: List<NetworkResult>): List<NetworkResult>

    @DELETE("results/{taskId}/{id}")
    suspend fun deleteResults(
        @Path("taskId") taskId: Int = 0,
        @Path("id") id: Int = 0
    ): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("results/{id}")
    suspend fun editResult(
        @Path("id") id: Int,
        @Body body: NetworkResult
    ): Response<Unit>
}