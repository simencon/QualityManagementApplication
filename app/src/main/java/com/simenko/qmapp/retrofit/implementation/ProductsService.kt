package com.simenko.qmapp.retrofit.implementation

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
import com.simenko.qmapp.other.Constants.MANUFACTURING_PROJECTS
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
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.entities.products.NetworkCharacteristicComponentKind
import com.simenko.qmapp.retrofit.entities.products.NetworkCharacteristicComponentStageKind
import com.simenko.qmapp.retrofit.entities.products.NetworkCharacteristicProductKind
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentComponentStage
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentKind
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentKindComponent
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentKindKey
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentStageKind
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentStageKindComponentStage
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentStageKindKey
import com.simenko.qmapp.retrofit.entities.products.NetworkProductComponent
import com.simenko.qmapp.retrofit.entities.products.NetworkProductKind
import com.simenko.qmapp.retrofit.entities.products.NetworkProductKindKey
import com.simenko.qmapp.retrofit.entities.products.NetworkProductKindProduct
import retrofit2.Response
import retrofit2.http.GET

interface ProductsService {
    @GET(MANUFACTURING_PROJECTS)
    suspend fun getManufacturingProjects(): Response<List<NetworkManufacturingProject>>
    @GET(PRODUCTS_KEYS)
    suspend fun getKeys(): Response<List<NetworkKey>>
    @GET(PRODUCT_BASES)
    suspend fun getProductBases(): Response<List<NetworkProductBase>>
    @GET(CHARACTERISTICS_GROUPS)
    suspend fun getCharacteristicGroups(): Response<List<NetworkElementIshModel>>
    @GET(CHARACTERISTICS_SUB_GROUPS)
    suspend fun getCharacteristicSubGroups(): Response<List<NetworkIshSubCharacteristic>>
    @GET(CHARACTERISTICS)
    suspend fun getCharacteristics(): Response<List<NetworkCharacteristic>>
    @GET(METRICS)
    suspend fun getMetrics(): Response<List<NetworkMetrix>>

    @GET(VERSION_STATUSES)
    suspend fun getVersionStatuses(): Response<List<NetworkVersionStatus>>


    @GET(PRODUCT_KINDS)
    suspend fun getProductKinds(): Response<List<NetworkProductKind>>
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
    suspend fun getComponentStages(): Response<List<NetworkComponentInStage>>


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
    suspend fun getComponentStageVersions(): Response<List<NetworkComponentInStageVersion>>
    @GET(COMPONENT_IN_STAGE_TOLERANCES)
    suspend fun getComponentStageTolerances(): Response<List<NetworkComponentInStageTolerance>>
}

