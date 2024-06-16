package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.other.Constants.CHARACTERISTICS
import com.simenko.qmapp.other.Constants.CHARACTERISTICS_COMPONENT_KINDS
import com.simenko.qmapp.other.Constants.CHARACTERISTICS_COMPONENT_STAGE_KINDS
import com.simenko.qmapp.other.Constants.CHARACTERISTICS_GROUPS
import com.simenko.qmapp.other.Constants.CHARACTERISTICS_PRODUCT_KINDS
import com.simenko.qmapp.other.Constants.CHARACTERISTICS_SUB_GROUPS
import com.simenko.qmapp.other.Constants.COMPONENTS
import com.simenko.qmapp.other.Constants.COMPONENTS_COMPONENT_STAGES
import com.simenko.qmapp.other.Constants.COMPONENTS_IN_STAGE
import com.simenko.qmapp.other.Constants.COMPONENTS_IN_STAGE_TO_LINES
import com.simenko.qmapp.other.Constants.COMPONENTS_TO_LINES
import com.simenko.qmapp.other.Constants.COMPONENT_IN_STAGE_TOLERANCES
import com.simenko.qmapp.other.Constants.COMPONENT_IN_STAGE_VERSIONS
import com.simenko.qmapp.other.Constants.COMPONENT_KINDS
import com.simenko.qmapp.other.Constants.COMPONENT_KINDS_COMPONENTS
import com.simenko.qmapp.other.Constants.COMPONENT_KINDS_KEYS
import com.simenko.qmapp.other.Constants.COMPONENT_STAGE_KINDS
import com.simenko.qmapp.other.Constants.COMPONENT_STAGE_KINDS_COMPONENT_STAGES
import com.simenko.qmapp.other.Constants.COMPONENT_STAGE_KINDS_KEYS
import com.simenko.qmapp.other.Constants.COMPONENT_TOLERANCES
import com.simenko.qmapp.other.Constants.COMPONENT_VERSIONS
import com.simenko.qmapp.other.Constants.PRODUCT_LINES
import com.simenko.qmapp.other.Constants.METRICS
import com.simenko.qmapp.other.Constants.PRODUCTS
import com.simenko.qmapp.other.Constants.PRODUCTS_COMPONENTS
import com.simenko.qmapp.other.Constants.PRODUCTS_KEYS
import com.simenko.qmapp.other.Constants.PRODUCTS_TO_LINES
import com.simenko.qmapp.other.Constants.PRODUCT_BASES
import com.simenko.qmapp.other.Constants.PRODUCT_KINDS
import com.simenko.qmapp.other.Constants.PRODUCT_KINDS_KEYS
import com.simenko.qmapp.other.Constants.PRODUCT_KINDS_PRODUCTS
import com.simenko.qmapp.other.Constants.PRODUCT_TOLERANCES
import com.simenko.qmapp.other.Constants.PRODUCT_VERSIONS
import com.simenko.qmapp.other.Constants.VERSION_STATUSES
import com.simenko.qmapp.retrofit.entities.products.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductsService {
    @GET(PRODUCT_LINES)
    suspend fun getProductLines(): Response<List<NetworkProductLine>>

    @POST(PRODUCT_LINES)
    suspend fun insertProductLine(@Body value: NetworkProductLine): Response<NetworkProductLine>

    @DELETE("$PRODUCT_LINES/{id}")
    suspend fun deleteProductLine(@Path("id") id: ID): Response<NetworkProductLine>

    @PUT("$PRODUCT_LINES/{id}")
    suspend fun editProductLine(@Path("id") id: ID, @Body value: NetworkProductLine): Response<NetworkProductLine>


    @GET(PRODUCTS_KEYS)
    suspend fun getKeys(): Response<List<NetworkKey>>

    @POST(PRODUCTS_KEYS)
    suspend fun insertProductLineKey(@Body value: NetworkKey): Response<NetworkKey>

    @DELETE("$PRODUCTS_KEYS/{id}")
    suspend fun deleteProductLineKey(@Path("id") id: ID): Response<NetworkKey>

    @PUT("$PRODUCTS_KEYS/{id}")
    suspend fun editProductLineKey(@Path("id") id: ID, @Body value: NetworkKey): Response<NetworkKey>


    @GET(PRODUCT_BASES)
    suspend fun getProductBases(): Response<List<NetworkProductBase>>


    @GET(CHARACTERISTICS_GROUPS)
    suspend fun getCharacteristicGroups(): Response<List<NetworkCharGroup>>

    @POST(CHARACTERISTICS_GROUPS)
    suspend fun insertCharacteristicGroup(@Body record: NetworkCharGroup): Response<NetworkCharGroup>

    @DELETE("${CHARACTERISTICS_GROUPS}/{id}")
    suspend fun deleteCharacteristicGroup(@Path("id") id: ID): Response<NetworkCharGroup>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("${CHARACTERISTICS_GROUPS}/{id}")
    suspend fun editCharacteristicGroup(@Path("id") id: ID, @Body body: NetworkCharGroup): Response<NetworkCharGroup>


    @GET(CHARACTERISTICS_SUB_GROUPS)
    suspend fun getCharacteristicSubGroups(): Response<List<NetworkCharSubGroup>>

    @POST(CHARACTERISTICS_SUB_GROUPS)
    suspend fun insertCharacteristicSubGroup(@Body record: NetworkCharSubGroup): Response<NetworkCharSubGroup>

    @DELETE("${CHARACTERISTICS_SUB_GROUPS}/{id}")
    suspend fun deleteCharacteristicSubGroup(@Path("id") id: ID): Response<NetworkCharSubGroup>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("${CHARACTERISTICS_SUB_GROUPS}/{id}")
    suspend fun editCharacteristicSubGroup(@Path("id") id: ID, @Body body: NetworkCharSubGroup): Response<NetworkCharSubGroup>


    @GET(CHARACTERISTICS)
    suspend fun getCharacteristics(): Response<List<NetworkCharacteristic>>

    @POST(CHARACTERISTICS)
    suspend fun insertCharacteristic(@Body record: NetworkCharacteristic): Response<NetworkCharacteristic>

    @DELETE("${CHARACTERISTICS}/{id}")
    suspend fun deleteCharacteristic(@Path("id") id: ID): Response<NetworkCharacteristic>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("${CHARACTERISTICS}/{id}")
    suspend fun editCharacteristic(@Path("id") id: ID, @Body body: NetworkCharacteristic): Response<NetworkCharacteristic>


    @GET(METRICS)
    suspend fun getMetrics(): Response<List<NetworkMetrix>>

    @POST(METRICS)
    suspend fun insertMetric(@Body record: NetworkMetrix): Response<NetworkMetrix>

    @DELETE("${METRICS}/{id}")
    suspend fun deleteMetric(@Path("id") id: ID): Response<NetworkMetrix>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("${METRICS}/{id}")
    suspend fun editMetric(@Path("id") id: ID, @Body body: NetworkMetrix): Response<NetworkMetrix>


    @GET(VERSION_STATUSES)
    suspend fun getVersionStatuses(): Response<List<NetworkVersionStatus>>


    @GET(PRODUCT_KINDS)
    suspend fun getProductKinds(): Response<List<NetworkProductKind>>

    @POST(PRODUCT_KINDS)
    suspend fun insertProductKind(@Body value: NetworkProductKind): Response<NetworkProductKind>

    @DELETE("$PRODUCT_KINDS/{id}")
    suspend fun deleteProductKind(@Path("id") id: ID): Response<NetworkProductKind>

    @PUT("$PRODUCT_KINDS/{id}")
    suspend fun editProductKind(@Path("id") id: ID, @Body value: NetworkProductKind): Response<NetworkProductKind>


    @GET(COMPONENT_KINDS)
    suspend fun getComponentKinds(): Response<List<NetworkComponentKind>>

    @GET(COMPONENT_STAGE_KINDS)
    suspend fun getComponentStageKinds(): Response<List<NetworkComponentStageKind>>


    @GET(PRODUCT_KINDS_KEYS)
    suspend fun getProductKindsKeys(): Response<List<NetworkProductKindKey>>

    @GET(COMPONENT_KINDS_KEYS)
    suspend fun getComponentKindsKeys(): Response<List<NetworkComponentKindKey>>

    @GET(COMPONENT_STAGE_KINDS_KEYS)
    suspend fun getComponentStageKindsKeys(): Response<List<NetworkComponentStageKindKey>>


    @GET(CHARACTERISTICS_PRODUCT_KINDS)
    suspend fun getCharacteristicsProductKinds(): Response<List<NetworkCharacteristicProductKind>>

    @GET(CHARACTERISTICS_COMPONENT_KINDS)
    suspend fun getCharacteristicsComponentKinds(): Response<List<NetworkCharacteristicComponentKind>>

    @GET(CHARACTERISTICS_COMPONENT_STAGE_KINDS)
    suspend fun getCharacteristicsComponentStageKinds(): Response<List<NetworkCharacteristicComponentStageKind>>


    @GET(PRODUCTS)
    suspend fun getProducts(): Response<List<NetworkProduct>>

    @GET(COMPONENTS)
    suspend fun getComponents(): Response<List<NetworkComponent>>

    @GET(COMPONENTS_IN_STAGE)
    suspend fun getComponentStages(): Response<List<NetworkComponentStage>>


    @GET(PRODUCTS_TO_LINES)
    suspend fun getProductsToLines(): Response<List<NetworkProductToLine>>

    @GET(COMPONENTS_TO_LINES)
    suspend fun getComponentsToLines(): Response<List<NetworkComponentToLine>>

    @GET(COMPONENTS_IN_STAGE_TO_LINES)
    suspend fun getComponentStagesToLines(): Response<List<NetworkComponentInStageToLine>>


    @GET(PRODUCT_KINDS_PRODUCTS)
    suspend fun getProductKindsProducts(): Response<List<NetworkProductKindProduct>>

    @GET(COMPONENT_KINDS_COMPONENTS)
    suspend fun getComponentKindsComponents(): Response<List<NetworkComponentKindComponent>>

    @GET(COMPONENT_STAGE_KINDS_COMPONENT_STAGES)
    suspend fun getComponentStageKindsComponentStages(): Response<List<NetworkComponentStageKindComponentStage>>


    @GET(PRODUCTS_COMPONENTS)
    suspend fun getProductsComponents(): Response<List<NetworkProductComponent>>

    @GET(COMPONENTS_COMPONENT_STAGES)
    suspend fun getComponentsComponentStages(): Response<List<NetworkComponentComponentStage>>


    @GET(PRODUCT_VERSIONS)
    suspend fun getProductVersions(): Response<List<NetworkProductVersion>>

    @GET(PRODUCT_TOLERANCES)
    suspend fun getProductTolerances(): Response<List<NetworkProductTolerance>>

    @GET(COMPONENT_VERSIONS)
    suspend fun getComponentVersions(): Response<List<NetworkComponentVersion>>


    @GET(COMPONENT_TOLERANCES)
    suspend fun getComponentTolerances(): Response<List<NetworkComponentTolerance>>

    @GET(COMPONENT_IN_STAGE_VERSIONS)
    suspend fun getComponentStageVersions(): Response<List<NetworkComponentStageVersion>>

    @GET(COMPONENT_IN_STAGE_TOLERANCES)
    suspend fun getComponentStageTolerances(): Response<List<NetworkComponentInStageTolerance>>
}

