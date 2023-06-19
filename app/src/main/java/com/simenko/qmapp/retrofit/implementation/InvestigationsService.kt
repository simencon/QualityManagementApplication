package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.converters.PairParam
import retrofit2.Response
import retrofit2.http.*

interface InvestigationsService {
    @GET("inputsForMeasurementRegister")
    suspend fun getInputForOrder(): Response<List<NetworkInputForOrder>>

    @GET("ordersStatuses")
    suspend fun getOrdersStatuses(): Response<List<NetworkOrdersStatus>>

    @GET("measurementReasons")
    suspend fun getMeasurementReasons(): Response<List<NetworkReason>>

    @GET("ordersTypes")
    suspend fun getOrdersTypes(): Response<List<NetworkOrdersType>>

    @GET("orders/latestOrder")
    suspend fun getLatestOrderDate(): Response<Long>

    @GET("orders/earliestOrder/{earliestOrderDate}")
    suspend fun getEarliestOrdersByStartingOrderDate(
        @Path("earliestOrderDate") earliestOrderDate: Long
    ): Response<List<NetworkOrder>>

    @GET("orders/hashCode/{timeRange}")
    suspend fun getOrdersHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<Int>

    @GET("orders/{timeRange}")
    suspend fun getOrdersByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkOrder>>

    @POST("orders")
    suspend fun createOrder(@Body networkOrder: NetworkOrder): Response<NetworkOrder>

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<NetworkOrder>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("orders/{id}")
    suspend fun editOrder(@Path("id") id: Int, @Body body: NetworkOrder): Response<NetworkOrder>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): Response<NetworkOrder>

    @GET("subOrders/hashCode/{timeRange}")
    suspend fun getSubOrdersHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<Int>

    @GET("subOrders/{timeRange}")
    suspend fun getSubOrdersByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkSubOrder>>

    @POST("subOrders")
    suspend fun createSubOrder(@Body networkSubOrder: NetworkSubOrder): Response<NetworkSubOrder>

    @DELETE("subOrders/{id}")
    suspend fun deleteSubOrder(@Path("id") id: Int): Response<NetworkSubOrder>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("subOrders/{id}")
    suspend fun editSubOrder(@Path("id") id: Int, @Body body: NetworkSubOrder): Response<NetworkSubOrder>

    @GET("subOrders/{id}")
    suspend fun getSubOrder(@Path("id") id: Int): Response<NetworkSubOrder>

    @GET("subOrderTasks/hashCode/{timeRange}")
    suspend fun getTasksHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<Int>

    @GET("subOrderTasks/{timeRange}")
    suspend fun getTasksDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkSubOrderTask>>

    @POST("subOrderTasks")
    suspend fun createSubOrderTask(@Body networkSubOrderTask: NetworkSubOrderTask): Response<NetworkSubOrderTask>

    @DELETE("subOrderTasks/{id}")
    suspend fun deleteSubOrderTask(@Path("id") id: Int): Response<NetworkSubOrderTask>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("subOrderTasks/{id}")
    suspend fun editSubOrderTask(
        @Path("id") id: Int,
        @Body body: NetworkSubOrderTask
    ): Response<NetworkSubOrderTask>

    @GET("subOrderTasks/{id}")
    suspend fun getSubOrderTask(@Path("id") id: Int): Response<NetworkSubOrderTask>

    @GET("samples/hashCode/{timeRange}")
    suspend fun getSamplesHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<Int>

    @GET("samples/{timeRange}")
    suspend fun getSamplesByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkSample>>

    @POST("samples")
    suspend fun createSample(@Body networkSample: NetworkSample): Response<NetworkSample>

    @DELETE("samples/{id}")
    suspend fun deleteSample(@Path("id") id: Int): Response<NetworkSample>

    @GET("resultsDecriptions")
    suspend fun getResultsDecryptions(): Response<List<NetworkResultsDecryption>>

    @GET("results/hashCode/{timeRange}")
    suspend fun getResultsHashCodeForDatePeriod(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<Int>

    @GET("results/{timeRange}")
    suspend fun getResultsByDateRange(
        @Path("timeRange") @PairParam timeRange: Pair<Long, Long>
    ): Response<List<NetworkResult>>

    @POST("results/records")
    suspend fun createResults(@Body records: List<NetworkResult>): Response<List<NetworkResult>>

    @DELETE("results/task/{taskId}")
    suspend fun deleteResults(
        @Path("taskId") taskId: Int
    ): Response<List<NetworkResult>>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("results/{id}")
    suspend fun editResult(
        @Path("id") id: Int,
        @Body body: NetworkResult
    ): Response<NetworkResult>
}