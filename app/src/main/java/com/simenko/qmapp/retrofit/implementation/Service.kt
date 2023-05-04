package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.retrofit.entities.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface QualityManagementManufacturingService {
    @GET("positionLevels")
    suspend fun getPositionLevels(): List<NetworkPositionLevel>

    @GET("teamMembers")
    suspend fun getTeamMembers(): List<NetworkTeamMembers>

    @GET("companies")
    suspend fun getCompanies(): List<NetworkCompany>

    @GET("departments")
    suspend fun getDepartments(): List<NetworkDepartment>

    @GET("subDepartments")
    suspend fun getSubDepartments(): List<NetworkSubDepartment>

    @GET("manufacturingChannels")
    suspend fun getManufacturingChannels(): List<NetworkManufacturingChannel>

    @GET("manufacturingLines")
    suspend fun getManufacturingLines(): List<NetworkManufacturingLine>

    @GET("manufacturingOperations")
    suspend fun getManufacturingOperations(): List<NetworkManufacturingOperation>

    @GET("manufacturingOperationsFlows")
    suspend fun getOperationsFlows(): List<NetworkOperationsFlow>
}

interface QualityManagementProductsService {
    @GET("characteristicGroups")
    suspend fun getElementIshModels(): List<NetworkElementIshModel>

    @GET("characteristicSubGroups")
    suspend fun getIshSubCharacteristics(): List<NetworkIshSubCharacteristic>

    @GET("manufacturingProjects")
    suspend fun getManufacturingProjects(): List<NetworkManufacturingProject>

    @GET("characteristics")
    suspend fun getCharacteristics(): List<NetworkCharacteristic>

    @GET("metrics")
    suspend fun getMetrixes(): List<NetworkMetrix>

    @GET("productsKeys")
    suspend fun getKeys(): List<NetworkKey>

    @GET("productBases")
    suspend fun getProductBases(): List<NetworkProductBase>

    @GET("products")
    suspend fun getProducts(): List<NetworkProduct>

    @GET("components")
    suspend fun getComponents(): List<NetworkComponent>

    @GET("componentsInStage")
    suspend fun getComponentInStages(): List<NetworkComponentInStage>

    @GET("versionStatuses")
    suspend fun getVersionStatuses(): List<NetworkVersionStatus>

    @GET("productVersions")
    suspend fun getProductVersions(): List<NetworkProductVersion>

    @GET("componentVersions")
    suspend fun getComponentVersions(): List<NetworkComponentVersion>

    @GET("componentInStageVersions")
    suspend fun getComponentInStageVersions(): List<NetworkComponentInStageVersion>

    @GET("productTolerances")
    suspend fun getProductTolerances(): List<NetworkProductTolerance>

    @GET("componentTolerances")
    suspend fun getComponentTolerances(): List<NetworkComponentTolerance>

    @GET("componentInStageTolerances")
    suspend fun getComponentInStageTolerances(): List<NetworkComponentInStageTolerance>

    @GET("productsToLines")
    suspend fun getProductsToLines(): List<NetworkProductToLine>

    @GET("componentsToLines")
    suspend fun getComponentsToLines(): List<NetworkComponentToLine>

    @GET("componentsInStageToLines")
    suspend fun getComponentInStagesToLines(): List<NetworkComponentInStageToLine>
}

interface QualityManagementInvestigationsService {
    @GET("inputsForMeasurementRegister")
    suspend fun getInputForOrder(): List<NetworkInputForOrder>

    @GET("ordersStatuses")
    suspend fun getOrdersStatuses(): List<NetworkOrdersStatus>

    @GET("measurementReasons")
    suspend fun getMeasurementReasons(): List<NetworkReason>

    @GET("ordersTypes")
    suspend fun getOrdersTypes(): List<NetworkOrdersType>

    @GET("orders")
    suspend fun getOrders(): List<NetworkOrder>

    @POST("orders")
    suspend fun createOrder(@Body networkOrder: NetworkOrder): NetworkOrder

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("orders/{id}")
    suspend fun editOrder(@Path("id") id: Int, @Body body: NetworkOrder): Response<Unit>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): NetworkOrder

    @GET("subOrders")
    suspend fun getSubOrders(): List<NetworkSubOrder>

    @POST("subOrders")
    suspend fun createSubOrder(@Body networkSubOrder: NetworkSubOrder): NetworkSubOrder

    @DELETE("subOrders/{id}")
    suspend fun deleteSubOrder(@Path("id") id: Int): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("subOrders/{id}")
    suspend fun editSubOrder(@Path("id") id: Int, @Body body: NetworkSubOrder): Response<Unit>

    @GET("subOrders/{id}")
    suspend fun getSubOrder(@Path("id") id: Int): NetworkSubOrder

    @GET("subOrderTasks")
    suspend fun getSubOrderTasks(): List<NetworkSubOrderTask>

    @POST("subOrderTasks")
    suspend fun createSubOrderTask(@Body networkSubOrderTask: NetworkSubOrderTask): NetworkSubOrderTask

    @DELETE("subOrderTasks/{id}")
    suspend fun deleteSubOrderTask(@Path("id") id: Int): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("subOrderTasks/{id}")
    suspend fun editSubOrderTask(@Path("id") id: Int, @Body body: NetworkSubOrderTask): Response<Unit>

    @GET("subOrderTasks/{id}")
    suspend fun getSubOrderTask(@Path("id") id: Int): NetworkSubOrderTask

    @GET("samples")
    suspend fun getSamples(): List<NetworkSample>

    @POST("samples")
    suspend fun createSample(@Body networkSample: NetworkSample): NetworkSample

    @DELETE("samples/{id}")
    suspend fun deleteSample(@Path("id") id: Int): Response<Unit>

    @GET("resultsDecriptions")
    suspend fun getResultsDecryptions(): List<NetworkResultsDecryption>

    @GET("results")
    suspend fun getResults(): List<NetworkResult>

    @POST("results")
    suspend fun createResult(@Body networkResult: NetworkResult): NetworkResult

    @POST("results/records")
    fun createResults(@Body records: List<NetworkResult>): List<NetworkResult>

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

object QualityManagementNetwork {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://qualityappspring.azurewebsites.net/api/v1/")
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
            )
        )
        .build()

    val serviceHolderManufacturing: QualityManagementManufacturingService =
        retrofit.create(QualityManagementManufacturingService::class.java)
    val serviceHolderProducts: QualityManagementProductsService =
        retrofit.create(QualityManagementProductsService::class.java)
    val serviceHolderInvestigations: QualityManagementInvestigationsService =
        retrofit.create(QualityManagementInvestigationsService::class.java)
}