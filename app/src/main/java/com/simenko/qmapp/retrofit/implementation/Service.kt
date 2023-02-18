package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.retrofit.entities.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface QualityManagementManufacturingService {
    @GET("api/_0PositionLevel")
    suspend fun getPositionLevels(): List<NetworkPositionLevel>
    @GET("api/_8TeamMember")
    suspend fun getTeamMembers(): List<NetworkTeamMembers>
    @GET("api/_0Company")
    suspend fun getCompanies(): List<NetworkCompany>
    @GET("api/_10department")
    suspend fun getDepartments(): List<NetworkDepartment>
    @GET("api/_11SubDepartment")
    suspend fun getSubDepartments(): List<NetworkSubDepartment>
    @GET("api/_12ManufacturingChannel")
    suspend fun getManufacturingChannels(): List<NetworkManufacturingChannel>
    @GET("api/_13ManufacturingLine")
    suspend fun getManufacturingLines(): List<NetworkManufacturingLine>
    @GET("api/_14ManufacturingOperation")
    suspend fun getManufacturingOperations(): List<NetworkManufacturingOperation>
}

interface QualityManagementProductsService {
    @GET("api/_101DElementIshModel")
    suspend fun getElementIshModels(): List<NetworkElementIshModel>
    @GET("api/_0IshSubCharacteristic")
    suspend fun getIshSubCharacteristics(): List<NetworkIshSubCharacteristic>
    @GET("api/_0ManufacturingProject")
    suspend fun getManufacturingProjects(): List<NetworkManufacturingProject>
    @GET("api/_7Characteristic")
    suspend fun getCharacteristics(): List<NetworkCharacteristic>
    @GET("api/_8Metrix")
    suspend fun getMetrixes(): List<NetworkMetrix>
}

interface QualityManagementInvestigationsService {
    @GET("api/orderinput")
    suspend fun getInputForOrder(): List<NetworkInputForOrder>
    @GET("api/_0OrdersStatus")
    suspend fun getOrdersStatuses(): List<NetworkOrdersStatus>
    @GET("api/_0MeasurementReason")
    suspend fun getMeasurementReasons(): List<NetworkMeasurementReason>
    @GET("api/_0OrdersType")
    suspend fun getOrdersTypes(): List<NetworkOrdersType>
    @GET("api/_12Order")
    suspend fun getOrders(): List<NetworkOrder>
    @GET("api/_13SubOrder")
    suspend fun getSubOrders(): List<NetworkSubOrder>
    @GET("api/_137SubOrderTask")
    suspend fun getSubOrderTasks(): List<NetworkSubOrderTask>
    @GET("api/_14Sample")
    suspend fun getSamples(): List<NetworkSample>
    @GET("api/_0ResultsDecryption")
    suspend fun getResultsDecryptions(): List<NetworkResultsDecryption>
    @GET("api/_148Result")
    suspend fun getResults(): List<NetworkResult>
}

object QualityManagementNetwork {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://restapiforqualityappv120221213121016.azurewebsites.net/")
        .addConverterFactory(MoshiConverterFactory.create(
            Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
        ))
        .build()

    val serviceholderManufacturing = retrofit.create(QualityManagementManufacturingService::class.java)
    val serviceholderProducts = retrofit.create(QualityManagementProductsService::class.java)
    val serviceholderInvestigations = retrofit.create(QualityManagementInvestigationsService::class.java)
}