package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.retrofit.entities.*
import retrofit2.http.GET

interface ProductsService {
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

