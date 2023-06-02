package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.retrofit.entities.*
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
    suspend fun getLatestOrderDateEpoch(): Response<Long>

    @GET("orders/latestOrder/{latestOrderDate}")
    suspend fun getLatestOrdersByStartingOrderDate(
        @Path("latestOrderDate") latestOrderDate: Long
    ): List<NetworkOrder>

    @GET("orders/earliestOrder/{earliestOrderDate}")
    suspend fun getEarliestOrdersByStartingOrderDate(
        @Path("earliestOrderDate") earliestOrderDate: Long
    ): List<NetworkOrder>

    @GET("orders/hashCode/{btnDate}/{topDate}")
    suspend fun Pair<Long, Long>.getOrdersHashCodeForDatePeriod(
        @Path("btnDate") btnDate: Long = first,
        @Path("topDate") topDate: Long = second
    ): Int

    @GET("orders/{btnDate}/{topDate}")
    suspend fun getOrdersByDateRange(
        @Path("btnDate") btnDate: Long,
        @Path("topDate") topDate: Long
    ): List<NetworkOrder>

    @GET("orders/{btnDate}/{topDate}")
    suspend fun Pair<Long, Long>.getOrdersByDateRange(
        @Path("btnDate") btnDate: Long = this.first,
        @Path("topDate") topDate: Long = this.second
    ): List<NetworkOrder>

    @POST("orders")
    suspend fun createOrder(@Body networkOrder: NetworkOrder): NetworkOrder

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("orders/{id}")
    suspend fun editOrder(@Path("id") id: Int, @Body body: NetworkOrder): Response<Unit>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): NetworkOrder

    @GET("subOrders/hashCode/{btnDate}/{topDate}")
    suspend fun Pair<Long,Long>.getSubOrdersHashCodeForDatePeriod(
        @Path("btnDate") btnDate: Long = first,
        @Path("topDate") topDate: Long = second
    ): Int

    @GET("subOrders/{btnDate}/{topDate}")
    suspend fun Pair<Long,Long>.getSubOrdersByDateRange(
        @Path("btnDate") btnNumber: Long = first,
        @Path("topDate") topNumber: Long = second
    ): List<NetworkSubOrder>

    @POST("subOrders")
    suspend fun createSubOrder(@Body networkSubOrder: NetworkSubOrder): NetworkSubOrder

    @DELETE("subOrders/{id}")
    suspend fun deleteSubOrder(@Path("id") id: Int): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("subOrders/{id}")
    suspend fun editSubOrder(@Path("id") id: Int, @Body body: NetworkSubOrder): Response<Unit>

    @GET("subOrders/{id}")
    suspend fun getSubOrder(@Path("id") id: Int): NetworkSubOrder

    @GET("subOrderTasks/hashCode/{btnDate}/{topDate}")
    suspend fun Pair<Long, Long>.getTasksHashCodeForDatePeriod(
        @Path("btnDate") btnDate: Long = first,
        @Path("topDate") topDate: Long = second
    ): Int

    @GET("subOrderTasks/{btnDate}/{topDate}")
    suspend fun Pair<Long,Long>.getTasksDateRange(
        @Path("btnDate") btnDate: Long = first,
        @Path("topDate") topDate: Long = second
    ): List<NetworkSubOrderTask>

    @POST("subOrderTasks")
    suspend fun createSubOrderTask(@Body networkSubOrderTask: NetworkSubOrderTask): NetworkSubOrderTask

    @DELETE("subOrderTasks/{id}")
    suspend fun deleteSubOrderTask(@Path("id") id: Int): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("subOrderTasks/{id}")
    suspend fun editSubOrderTask(
        @Path("id") id: Int,
        @Body body: NetworkSubOrderTask
    ): Response<Unit>

    @GET("subOrderTasks/{id}")
    suspend fun getSubOrderTask(@Path("id") id: Int): NetworkSubOrderTask

    @GET("samples/hashCode/{btnDate}/{topDate}")
    suspend fun Pair<Long, Long>.getSamplesHashCodeForDatePeriod(
        @Path("btnDate") btnDate: Long = first,
        @Path("topDate") topDate: Long = second
    ): Int

    @GET("samples/{btnDate}/{topDate}")
    suspend fun Pair<Long,Long>.getSamplesByDateRange(
        @Path("btnDate") btnDate: Long = first,
        @Path("topDate") topDate: Long = second
    ): List<NetworkSample>

    @POST("samples")
    suspend fun createSample(@Body networkSample: NetworkSample): NetworkSample

    @DELETE("samples/{id}")
    suspend fun deleteSample(@Path("id") id: Int): Response<Unit>

    @GET("resultsDecriptions")
    suspend fun getResultsDecryptions(): List<NetworkResultsDecryption>

    @GET("results/hashCode/{btnDate}/{topDate}")
    suspend fun Pair<Long, Long>.getResultsHashCodeForDatePeriod(
        @Path("btnDate") btnDate: Long = first,
        @Path("topDate") topDate: Long = second
    ): Int

    @GET("results/{btnDate}/{topDate}")
    suspend fun Pair<Long,Long>.getResultsByDateRange(
        @Path("btnDate") btnDate: Long = first,
        @Path("topDate") topDate: Long = second
    ): List<NetworkResult>

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