package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.other.Constants.CHARACTERISTIC_TO_OPERATION
import com.simenko.qmapp.other.Constants.COMPANIES
import com.simenko.qmapp.other.Constants.COMPONENTS_IN_STAGE_TO_LINES
import com.simenko.qmapp.other.Constants.COMPONENTS_TO_LINES
import com.simenko.qmapp.other.Constants.COMPONENT_KEY_TO_CHANNEL
import com.simenko.qmapp.other.Constants.COMPONENT_KIND_TO_SUB_DEPARTMENT
import com.simenko.qmapp.other.Constants.DEPARTMENTS
import com.simenko.qmapp.other.Constants.JOB_ROLES
import com.simenko.qmapp.other.Constants.MANUFACTURING_CHANNELS
import com.simenko.qmapp.other.Constants.MANUFACTURING_LINES
import com.simenko.qmapp.other.Constants.MANUFACTURING_OPERATIONS
import com.simenko.qmapp.other.Constants.MANUFACTURING_OPERATIONS_FLOWS
import com.simenko.qmapp.other.Constants.SUB_DEPARTMENTS
import com.simenko.qmapp.other.Constants.EMPLOYEES
import com.simenko.qmapp.other.Constants.PRODUCTS_TO_LINES
import com.simenko.qmapp.other.Constants.PRODUCT_KEY_TO_CHANNEL
import com.simenko.qmapp.other.Constants.PRODUCT_KIND_TO_SUB_DEPARTMENT
import com.simenko.qmapp.other.Constants.PRODUCT_LINE_TO_DEPARTMENT
import com.simenko.qmapp.other.Constants.RECORDS
import com.simenko.qmapp.other.Constants.STAGE_KEY_TO_CHANNEL
import com.simenko.qmapp.other.Constants.STAGE_KIND_TO_SUB_DEPARTMENT
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.entities.products.NetworkCharacteristicToOperation
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentInStageToLine
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentKeyToChannel
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentKindToSubDepartment
import com.simenko.qmapp.retrofit.entities.products.NetworkComponentToLine
import com.simenko.qmapp.retrofit.entities.products.NetworkProductKeyToChannel
import com.simenko.qmapp.retrofit.entities.products.NetworkProductKindToSubDepartment
import com.simenko.qmapp.retrofit.entities.products.NetworkProductLineToDepartment
import com.simenko.qmapp.retrofit.entities.products.NetworkProductToLine
import com.simenko.qmapp.retrofit.entities.products.NetworkStageKeyToChannel
import com.simenko.qmapp.retrofit.entities.products.NetworkStageKindToSubDepartment
import retrofit2.Response
import retrofit2.http.*

interface ManufacturingService {
    @GET(EMPLOYEES)
    suspend fun getEmployees(): Response<List<NetworkEmployee>>

    @POST(EMPLOYEES)
    suspend fun insertEmployee(@Body teamMember: NetworkEmployee): Response<NetworkEmployee>

    @DELETE("$EMPLOYEES/{id}")
    suspend fun deleteEmployee(@Path("id") id: ID): Response<NetworkEmployee>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$EMPLOYEES/{id}")
    suspend fun editEmployee(@Path("id") id: ID, @Body body: NetworkEmployee): Response<NetworkEmployee>

    @GET(COMPANIES)
    suspend fun getCompanies(): Response<List<NetworkCompany>>

    @GET(JOB_ROLES)
    suspend fun getJobRoles(): Response<List<NetworkJobRole>>

    @GET(DEPARTMENTS)
    suspend fun getDepartments(): Response<List<NetworkDepartment>>

    @POST(DEPARTMENTS)
    suspend fun insertDepartment(@Body value: NetworkDepartment): Response<NetworkDepartment>

    @DELETE("$DEPARTMENTS/{id}")
    suspend fun deleteDepartment(@Path("id") id: ID): Response<NetworkDepartment>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$DEPARTMENTS/{id}")
    suspend fun editDepartment(@Path("id") id: ID, @Body body: NetworkDepartment): Response<NetworkDepartment>


    @GET(SUB_DEPARTMENTS)
    suspend fun getSubDepartments(): Response<List<NetworkSubDepartment>>

    @POST(SUB_DEPARTMENTS)
    suspend fun insertSubDepartment(@Body value: NetworkSubDepartment): Response<NetworkSubDepartment>

    @DELETE("$SUB_DEPARTMENTS/{id}")
    suspend fun deleteSubDepartment(@Path("id") id: ID): Response<NetworkSubDepartment>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$SUB_DEPARTMENTS/{id}")
    suspend fun editSubDepartment(@Path("id") id: ID, @Body body: NetworkSubDepartment): Response<NetworkSubDepartment>


    @GET(MANUFACTURING_CHANNELS)
    suspend fun getManufacturingChannels(): Response<List<NetworkManufacturingChannel>>

    @POST(MANUFACTURING_CHANNELS)
    suspend fun insertManufacturingChannel(@Body value: NetworkManufacturingChannel): Response<NetworkManufacturingChannel>

    @DELETE("$MANUFACTURING_CHANNELS/{id}")
    suspend fun deleteManufacturingChannel(@Path("id") id: ID): Response<NetworkManufacturingChannel>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$MANUFACTURING_CHANNELS/{id}")
    suspend fun editManufacturingChannel(@Path("id") id: ID, @Body body: NetworkManufacturingChannel): Response<NetworkManufacturingChannel>


    @GET(MANUFACTURING_LINES)
    suspend fun getManufacturingLines(): Response<List<NetworkManufacturingLine>>

    @POST(MANUFACTURING_LINES)
    suspend fun insertManufacturingLine(@Body value: NetworkManufacturingLine): Response<NetworkManufacturingLine>

    @DELETE("$MANUFACTURING_LINES/{id}")
    suspend fun deleteManufacturingLine(@Path("id") id: ID): Response<NetworkManufacturingLine>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$MANUFACTURING_LINES/{id}")
    suspend fun editManufacturingLine(@Path("id") id: ID, @Body body: NetworkManufacturingLine): Response<NetworkManufacturingLine>


    @GET(MANUFACTURING_OPERATIONS)
    suspend fun getManufacturingOperations(): Response<List<NetworkManufacturingOperation>>

    @POST(MANUFACTURING_OPERATIONS)
    suspend fun insertManufacturingOperation(@Body teamMember: NetworkManufacturingOperation): Response<NetworkManufacturingOperation>

    @DELETE("$MANUFACTURING_OPERATIONS/{id}")
    suspend fun deleteManufacturingOperation(@Path("id") id: ID): Response<NetworkManufacturingOperation>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$MANUFACTURING_OPERATIONS/{id}")
    suspend fun editManufacturingOperation(@Path("id") id: ID, @Body body: NetworkManufacturingOperation): Response<NetworkManufacturingOperation>


    @GET(MANUFACTURING_OPERATIONS_FLOWS)
    suspend fun getOperationsFlows(): Response<List<NetworkOperationsFlow>>

    @POST("${MANUFACTURING_OPERATIONS_FLOWS}/${RECORDS}")
    suspend fun createOpFlows(@Body records: List<NetworkOperationsFlow>): Response<List<NetworkOperationsFlow>>

    @HTTP(method = "DELETE", path = "${MANUFACTURING_OPERATIONS_FLOWS}/$RECORDS", hasBody = true)
    suspend fun deleteOpFlows(@Body records: List<NetworkOperationsFlow>): Response<List<NetworkOperationsFlow>>


    @GET(PRODUCT_LINE_TO_DEPARTMENT)
    suspend fun getProductLinesToDepartments(): Response<List<NetworkProductLineToDepartment>>

    @GET(PRODUCT_KIND_TO_SUB_DEPARTMENT)
    suspend fun getProductKindsToSubDepartments(): Response<List<NetworkProductKindToSubDepartment>>

    @GET(COMPONENT_KIND_TO_SUB_DEPARTMENT)
    suspend fun getComponentKindsToSubDepartments(): Response<List<NetworkComponentKindToSubDepartment>>

    @GET(STAGE_KIND_TO_SUB_DEPARTMENT)
    suspend fun getStageKindsToSubDepartments(): Response<List<NetworkStageKindToSubDepartment>>

    @GET(PRODUCT_KEY_TO_CHANNEL)
    suspend fun getProductKeysToChannels(): Response<List<NetworkProductKeyToChannel>>

    @GET(COMPONENT_KEY_TO_CHANNEL)
    suspend fun getComponentKeysToChannels(): Response<List<NetworkComponentKeyToChannel>>

    @GET(STAGE_KEY_TO_CHANNEL)
    suspend fun getStageKeysToChannels(): Response<List<NetworkStageKeyToChannel>>

    @GET(PRODUCTS_TO_LINES)
    suspend fun getProductsToLines(): Response<List<NetworkProductToLine>>

    @GET(COMPONENTS_TO_LINES)
    suspend fun getComponentsToLines(): Response<List<NetworkComponentToLine>>

    @GET(COMPONENTS_IN_STAGE_TO_LINES)
    suspend fun getComponentStagesToLines(): Response<List<NetworkComponentInStageToLine>>

    @GET(CHARACTERISTIC_TO_OPERATION)
    suspend fun getCharacteristicsToOperations(): Response<List<NetworkCharacteristicToOperation>>
}