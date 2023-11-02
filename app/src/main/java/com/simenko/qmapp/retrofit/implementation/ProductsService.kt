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
import retrofit2.Response
import retrofit2.http.GET

interface ProductsService {
    @GET(CHARACTERISTICS_GROUPS)
    suspend fun getCharacteristicGroups(): Response<List<NetworkElementIshModel>>
    @GET(CHARACTERISTICS_SUB_GROUPS)
    suspend fun getCharacteristicSubGroups(): Response<List<NetworkIshSubCharacteristic>>
    @GET(MANUFACTURING_PROJECTS)
    suspend fun getManufacturingProjects(): Response<List<NetworkManufacturingProject>>

    @GET(PRODUCTS_KEYS)
    suspend fun getKeys(): Response<List<NetworkKey>>

    @GET(PRODUCT_BASES)
    suspend fun getProductBases(): Response<List<NetworkProductBase>>

    @GET(CHARACTERISTICS)
    suspend fun getCharacteristics(): Response<List<NetworkCharacteristic>>

    @GET(METRICS)
    suspend fun getMetrics(): Response<List<NetworkMetrix>>

    @GET(VERSION_STATUSES)
    suspend fun getVersionStatuses(): Response<List<NetworkVersionStatus>>

    @GET(PRODUCTS)
    suspend fun getProducts(): Response<List<NetworkProduct>>

    @GET(PRODUCT_VERSIONS)
    suspend fun getProductVersions(): Response<List<NetworkProductVersion>>

    @GET(PRODUCT_TOLERANCES)
    suspend fun getProductTolerances(): Response<List<NetworkProductTolerance>>

    @GET(PRODUCTS_TO_LINES)
    suspend fun getProductsToLines(): Response<List<NetworkProductToLine>>

    @GET(COMPONENTS)
    suspend fun getComponents(): Response<List<NetworkComponent>>

    @GET(COMPONENT_VERSIONS)
    suspend fun getComponentVersions(): Response<List<NetworkComponentVersion>>

    @GET(COMPONENT_TOLERANCES)
    suspend fun getComponentTolerances(): Response<List<NetworkComponentTolerance>>

    @GET(COMPONENTS_TO_LINES)
    suspend fun getComponentsToLines(): Response<List<NetworkComponentToLine>>

    @GET(COMPONENTS_IN_STAGE)
    suspend fun getComponentStages(): Response<List<NetworkComponentInStage>>

    @GET(COMPONENT_IN_STAGE_VERSIONS)
    suspend fun getComponentStageVersions(): Response<List<NetworkComponentInStageVersion>>

    @GET(COMPONENT_IN_STAGE_TOLERANCES)
    suspend fun getComponentStageTolerances(): Response<List<NetworkComponentInStageTolerance>>

    @GET(COMPONENTS_IN_STAGE_TO_LINES)
    suspend fun getComponentStagesToLines(): Response<List<NetworkComponentInStageToLine>>
}

