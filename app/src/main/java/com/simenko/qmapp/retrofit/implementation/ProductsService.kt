package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.other.Constants.CHARACTERISTICS
import com.simenko.qmapp.other.Constants.CHARACTERISTICS_GROUPS
import com.simenko.qmapp.other.Constants.CHARACTERISTICS_SUB_GROUPS
import com.simenko.qmapp.other.Constants.COMPONENTS
import com.simenko.qmapp.other.Constants.COMPONENTS_IN_STAGE
import com.simenko.qmapp.other.Constants.COMPONENTS_IN_STAGE_TO_LINES
import com.simenko.qmapp.other.Constants.COMPONENTS_TO_LINES
import com.simenko.qmapp.other.Constants.COMPONENT_IN_STAGE_TOLERANCES
import com.simenko.qmapp.other.Constants.COMPONENT_IN_STAGE_VERSIONS
import com.simenko.qmapp.other.Constants.COMPONENT_TOLERANCES
import com.simenko.qmapp.other.Constants.COMPONENT_VERSIONS
import com.simenko.qmapp.other.Constants.MANUFACTURING_PROJECTS
import com.simenko.qmapp.other.Constants.METRICS
import com.simenko.qmapp.other.Constants.PRODUCTS
import com.simenko.qmapp.other.Constants.PRODUCTS_KEYS
import com.simenko.qmapp.other.Constants.PRODUCTS_TO_LINES
import com.simenko.qmapp.other.Constants.PRODUCT_BASES
import com.simenko.qmapp.other.Constants.PRODUCT_TOLERANCES
import com.simenko.qmapp.other.Constants.PRODUCT_VERSIONS
import com.simenko.qmapp.other.Constants.VERSION_STATUSES
import com.simenko.qmapp.retrofit.entities.*
import retrofit2.http.GET

interface ProductsService {
    @GET(MANUFACTURING_PROJECTS)
    suspend fun getManufacturingProjects(): List<NetworkManufacturingProject>

    @GET(PRODUCTS_KEYS)
    suspend fun getKeys(): List<NetworkKey>

    @GET(PRODUCT_BASES)
    suspend fun getProductBases(): List<NetworkProductBase>

    @GET(CHARACTERISTICS_GROUPS)
    suspend fun getElementIshModels(): List<NetworkElementIshModel>

    @GET(CHARACTERISTICS_SUB_GROUPS)
    suspend fun getIshSubCharacteristics(): List<NetworkIshSubCharacteristic>

    @GET(CHARACTERISTICS)
    suspend fun getCharacteristics(): List<NetworkCharacteristic>

    @GET(METRICS)
    suspend fun getMetrixes(): List<NetworkMetrix>

    @GET(VERSION_STATUSES)
    suspend fun getVersionStatuses(): List<NetworkVersionStatus>

    @GET(PRODUCTS)
    suspend fun getProducts(): List<NetworkProduct>

    @GET(PRODUCT_VERSIONS)
    suspend fun getProductVersions(): List<NetworkProductVersion>

    @GET(PRODUCT_TOLERANCES)
    suspend fun getProductTolerances(): List<NetworkProductTolerance>

    @GET(PRODUCTS_TO_LINES)
    suspend fun getProductsToLines(): List<NetworkProductToLine>

    @GET(COMPONENTS)
    suspend fun getComponents(): List<NetworkComponent>

    @GET(COMPONENT_VERSIONS)
    suspend fun getComponentVersions(): List<NetworkComponentVersion>

    @GET(COMPONENT_TOLERANCES)
    suspend fun getComponentTolerances(): List<NetworkComponentTolerance>

    @GET(COMPONENTS_TO_LINES)
    suspend fun getComponentsToLines(): List<NetworkComponentToLine>

    @GET(COMPONENTS_IN_STAGE)
    suspend fun getComponentInStages(): List<NetworkComponentInStage>

    @GET(COMPONENT_IN_STAGE_VERSIONS)
    suspend fun getComponentInStageVersions(): List<NetworkComponentInStageVersion>

    @GET(COMPONENT_IN_STAGE_TOLERANCES)
    suspend fun getComponentInStageTolerances(): List<NetworkComponentInStageTolerance>

    @GET(COMPONENTS_IN_STAGE_TO_LINES)
    suspend fun getComponentInStagesToLines(): List<NetworkComponentInStageToLine>
}

