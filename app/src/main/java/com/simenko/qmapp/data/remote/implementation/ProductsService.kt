package com.simenko.qmapp.data.remote.implementation

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
import com.simenko.qmapp.other.Constants.PRODUCT_BASES
import com.simenko.qmapp.other.Constants.PRODUCT_KINDS
import com.simenko.qmapp.other.Constants.PRODUCT_KINDS_KEYS
import com.simenko.qmapp.other.Constants.PRODUCT_KINDS_PRODUCTS
import com.simenko.qmapp.other.Constants.PRODUCT_TOLERANCES
import com.simenko.qmapp.other.Constants.PRODUCT_VERSIONS
import com.simenko.qmapp.other.Constants.VERSION_STATUSES
import com.simenko.qmapp.other.Constants.WITH_RELATED_RECORDS
import com.simenko.qmapp.data.remote.entities.adapters.ComponentComponentStageWithRelatedRecordsResponse
import com.simenko.qmapp.data.remote.entities.adapters.ProductComponentWithRelatedRecordsResponse
import com.simenko.qmapp.data.remote.entities.adapters.ProductKindProductWithRelatedRecordsResponse
import com.simenko.qmapp.data.remote.entities.products.*
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

    @POST(PRODUCT_BASES)
    suspend fun insertProductBase(@Body value: NetworkProductBase): Response<NetworkProductBase>


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

    @POST(COMPONENT_KINDS)
    suspend fun insertComponentKind(@Body value: NetworkComponentKind): Response<NetworkComponentKind>

    @DELETE("$COMPONENT_KINDS/{id}")
    suspend fun deleteComponentKind(@Path("id") id: ID): Response<NetworkComponentKind>

    @PUT("$COMPONENT_KINDS/{id}")
    suspend fun editComponentKind(@Path("id") id: ID, @Body value: NetworkComponentKind): Response<NetworkComponentKind>


    @GET(COMPONENT_STAGE_KINDS)
    suspend fun getComponentStageKinds(): Response<List<NetworkComponentStageKind>>

    @POST(COMPONENT_STAGE_KINDS)
    suspend fun insertComponentStageKind(@Body value: NetworkComponentStageKind): Response<NetworkComponentStageKind>

    @DELETE("$COMPONENT_STAGE_KINDS/{id}")
    suspend fun deleteComponentStageKind(@Path("id") id: ID): Response<NetworkComponentStageKind>

    @PUT("$COMPONENT_STAGE_KINDS/{id}")
    suspend fun editComponentStageKind(@Path("id") id: ID, @Body value: NetworkComponentStageKind): Response<NetworkComponentStageKind>


    @GET(PRODUCT_KINDS_KEYS)
    suspend fun getProductKindsKeys(): Response<List<NetworkProductKindKey>>

    @POST(PRODUCT_KINDS_KEYS)
    suspend fun insertProductKindKey(@Body record: NetworkProductKindKey): Response<NetworkProductKindKey>

    @DELETE("${PRODUCT_KINDS_KEYS}/{id}")
    suspend fun deleteProductKindKey(@Path("id") id: ID): Response<NetworkProductKindKey>


    @GET(COMPONENT_KINDS_KEYS)
    suspend fun getComponentKindsKeys(): Response<List<NetworkComponentKindKey>>

    @POST(COMPONENT_KINDS_KEYS)
    suspend fun insertComponentKindKey(@Body record: NetworkComponentKindKey): Response<NetworkComponentKindKey>

    @DELETE("${COMPONENT_KINDS_KEYS}/{id}")
    suspend fun deleteComponentKindKey(@Path("id") id: ID): Response<NetworkComponentKindKey>


    @GET(COMPONENT_STAGE_KINDS_KEYS)
    suspend fun getComponentStageKindsKeys(): Response<List<NetworkComponentStageKindKey>>

    @POST(COMPONENT_STAGE_KINDS_KEYS)
    suspend fun insertComponentStageKindKey(@Body record: NetworkComponentStageKindKey): Response<NetworkComponentStageKindKey>

    @DELETE("${COMPONENT_STAGE_KINDS_KEYS}/{id}")
    suspend fun deleteComponentStageKindKey(@Path("id") id: ID): Response<NetworkComponentStageKindKey>


    @GET(CHARACTERISTICS_PRODUCT_KINDS)
    suspend fun getCharacteristicsProductKinds(): Response<List<NetworkCharacteristicProductKind>>

    @POST(CHARACTERISTICS_PRODUCT_KINDS)
    suspend fun insertCharacteristicProductKind(@Body record: NetworkCharacteristicProductKind): Response<NetworkCharacteristicProductKind>

    @DELETE("${CHARACTERISTICS_PRODUCT_KINDS}/{id}")
    suspend fun deleteCharacteristicProductKind(@Path("id") id: ID): Response<NetworkCharacteristicProductKind>


    @GET(CHARACTERISTICS_COMPONENT_KINDS)
    suspend fun getCharacteristicsComponentKinds(): Response<List<NetworkCharacteristicComponentKind>>

    @POST(CHARACTERISTICS_COMPONENT_KINDS)
    suspend fun insertCharacteristicComponentKind(@Body record: NetworkCharacteristicComponentKind): Response<NetworkCharacteristicComponentKind>

    @DELETE("${CHARACTERISTICS_COMPONENT_KINDS}/{id}")
    suspend fun deleteCharacteristicComponentKind(@Path("id") id: ID): Response<NetworkCharacteristicComponentKind>


    @GET(CHARACTERISTICS_COMPONENT_STAGE_KINDS)
    suspend fun getCharacteristicsComponentStageKinds(): Response<List<NetworkCharacteristicComponentStageKind>>

    @POST(CHARACTERISTICS_COMPONENT_STAGE_KINDS)
    suspend fun insertCharacteristicComponentStageKind(@Body record: NetworkCharacteristicComponentStageKind): Response<NetworkCharacteristicComponentStageKind>

    @DELETE("${CHARACTERISTICS_COMPONENT_STAGE_KINDS}/{id}")
    suspend fun deleteCharacteristicComponentStageKind(@Path("id") id: ID): Response<NetworkCharacteristicComponentStageKind>


    @GET(PRODUCTS)
    suspend fun getProducts(): Response<List<NetworkProduct>>

    @POST(PRODUCTS)
    suspend fun insertProduct(@Body value: NetworkProduct): Response<NetworkProduct>

    @PUT("$PRODUCTS/{id}")
    suspend fun editProduct(@Path("id") id: ID, @Body value: NetworkProduct): Response<NetworkProduct>


    @GET(COMPONENTS)
    suspend fun getComponents(): Response<List<NetworkComponent>>

    @POST(COMPONENTS)
    suspend fun insertComponent(@Body value: NetworkComponent): Response<NetworkComponent>

    @PUT("$COMPONENTS/{id}")
    suspend fun editComponent(@Path("id") id: ID, @Body value: NetworkComponent): Response<NetworkComponent>


    @GET(COMPONENTS_IN_STAGE)
    suspend fun getComponentStages(): Response<List<NetworkComponentStage>>

    @POST(COMPONENTS_IN_STAGE)
    suspend fun insertComponentStage(@Body value: NetworkComponentStage): Response<NetworkComponentStage>

    @PUT("$COMPONENTS_IN_STAGE/{id}")
    suspend fun editComponentStage(@Path("id") id: ID, @Body value: NetworkComponentStage): Response<NetworkComponentStage>



    @GET(PRODUCT_KINDS_PRODUCTS)
    suspend fun getProductKindsProducts(): Response<List<NetworkProductKindProduct>>

    @POST(PRODUCT_KINDS_PRODUCTS)
    suspend fun insertProductKindProduct(@Body record: NetworkProductKindProduct): Response<NetworkProductKindProduct>

    @DELETE("${PRODUCT_KINDS_PRODUCTS}/${WITH_RELATED_RECORDS}/{id}")
    suspend fun deleteProductKindProduct(@Path("id") id: ID): Response<ProductKindProductWithRelatedRecordsResponse>


    @GET(COMPONENT_KINDS_COMPONENTS)
    suspend fun getComponentKindsComponents(): Response<List<NetworkComponentKindComponent>>

    @POST(COMPONENT_KINDS_COMPONENTS)
    suspend fun insertComponentKindComponent(@Body record: NetworkComponentKindComponent): Response<NetworkComponentKindComponent>


    @GET(COMPONENT_STAGE_KINDS_COMPONENT_STAGES)
    suspend fun getComponentStageKindsComponentStages(): Response<List<NetworkComponentStageKindComponentStage>>

    @POST(COMPONENT_STAGE_KINDS_COMPONENT_STAGES)
    suspend fun insertStageKindStage(@Body record: NetworkComponentStageKindComponentStage): Response<NetworkComponentStageKindComponentStage>


    @GET(PRODUCTS_COMPONENTS)
    suspend fun getProductsComponents(): Response<List<NetworkProductComponent>>

    @POST(PRODUCTS_COMPONENTS)
    suspend fun insertProductComponent(@Body record: NetworkProductComponent): Response<NetworkProductComponent>

    @PUT("$PRODUCTS_COMPONENTS/{id}")
    suspend fun editProductComponent(@Path("id") id: ID, @Body value: NetworkProductComponent): Response<NetworkProductComponent>

    @DELETE("${PRODUCTS_COMPONENTS}/${WITH_RELATED_RECORDS}/{id}")
    suspend fun deleteProductComponent(@Path("id") id: ID): Response<ProductComponentWithRelatedRecordsResponse>


    @GET(COMPONENTS_COMPONENT_STAGES)
    suspend fun getComponentsComponentStages(): Response<List<NetworkComponentComponentStage>>

    @POST(COMPONENTS_COMPONENT_STAGES)
    suspend fun insertComponentComponentStage(@Body record: NetworkComponentComponentStage): Response<NetworkComponentComponentStage>

    @PUT("$COMPONENTS_COMPONENT_STAGES/{id}")
    suspend fun editComponentComponentStage(@Path("id") id: ID, @Body value: NetworkComponentComponentStage): Response<NetworkComponentComponentStage>

    @DELETE("${COMPONENTS_COMPONENT_STAGES}/${WITH_RELATED_RECORDS}/{id}")
    suspend fun deleteComponentComponentStage(@Path("id") id: ID): Response<ComponentComponentStageWithRelatedRecordsResponse>


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


    @POST("${PRODUCT_VERSIONS}/${WITH_RELATED_RECORDS}")
    suspend fun makeProductVersion(@Body version: @JvmSuppressWildcards Pair<NetworkProductVersion, List<NetworkProductTolerance>>): Response<Pair<NetworkProductVersion, List<NetworkProductTolerance>>>

    @POST("${COMPONENT_VERSIONS}/${WITH_RELATED_RECORDS}")
    suspend fun makeComponentVersion(@Body version: @JvmSuppressWildcards Pair<NetworkComponentVersion, List<NetworkComponentTolerance>>): Response<Pair<NetworkComponentVersion, List<NetworkComponentTolerance>>>

    @POST("${COMPONENT_IN_STAGE_VERSIONS}/${WITH_RELATED_RECORDS}")
    suspend fun makeStageVersion(@Body version: @JvmSuppressWildcards Pair<NetworkComponentStageVersion, List<NetworkComponentInStageTolerance>>): Response<Pair<NetworkComponentStageVersion, List<NetworkComponentInStageTolerance>>>


    @DELETE("${PRODUCT_VERSIONS}/{id}")
    suspend fun deleteProductVersion(@Path("id") id: ID): Response<NetworkProductVersion>

    @DELETE("${COMPONENT_VERSIONS}/{id}")
    suspend fun deleteComponentVersion(@Path("id") id: ID): Response<NetworkComponentVersion>

    @DELETE("${COMPONENT_IN_STAGE_VERSIONS}/{id}")
    suspend fun deleteStageVersion(@Path("id") id: ID): Response<NetworkComponentStageVersion>
}

