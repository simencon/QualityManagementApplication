package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.other.Constants.COMPANIES
import com.simenko.qmapp.other.Constants.DEPARTMENTS
import com.simenko.qmapp.other.Constants.JOB_ROLES
import com.simenko.qmapp.other.Constants.MANUFACTURING_CHANNELS
import com.simenko.qmapp.other.Constants.MANUFACTURING_LINES
import com.simenko.qmapp.other.Constants.MANUFACTURING_OPERATIONS
import com.simenko.qmapp.other.Constants.MANUFACTURING_OPERATIONS_FLOWS
import com.simenko.qmapp.other.Constants.SUB_DEPARTMENTS
import com.simenko.qmapp.other.Constants.EMPLOYEES
import com.simenko.qmapp.other.Constants.RECORDS
import com.simenko.qmapp.retrofit.entities.*
import retrofit2.Response
import retrofit2.http.*

interface ManufacturingService {
    @GET(EMPLOYEES)
    suspend fun getEmployees(): Response<List<NetworkEmployee>>

    @POST(EMPLOYEES)
    suspend fun insertEmployee(@Body teamMember: NetworkEmployee): Response<NetworkEmployee>

    @DELETE("$EMPLOYEES/{id}")
    suspend fun deleteEmployee(@Path("id") id: Int): Response<NetworkEmployee>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$EMPLOYEES/{id}")
    suspend fun editEmployee(@Path("id") id: Int, @Body body: NetworkEmployee): Response<NetworkEmployee>

    @GET(COMPANIES)
    suspend fun getCompanies(): Response<List<NetworkCompany>>

    @GET(JOB_ROLES)
    suspend fun getJobRoles(): Response<List<NetworkJobRole>>

    @GET(DEPARTMENTS)
    suspend fun getDepartments(): Response<List<NetworkDepartment>>

    @GET(SUB_DEPARTMENTS)
    suspend fun getSubDepartments(): Response<List<NetworkSubDepartment>>

    @GET(MANUFACTURING_CHANNELS)
    suspend fun getManufacturingChannels(): Response<List<NetworkManufacturingChannel>>

    @GET(MANUFACTURING_LINES)
    suspend fun getManufacturingLines(): Response<List<NetworkManufacturingLine>>
    @POST(MANUFACTURING_LINES)
    suspend fun insertManufacturingLine(@Body value: NetworkManufacturingLine): Response<NetworkManufacturingLine>
    @DELETE("$MANUFACTURING_LINES/{id}")
    suspend fun deleteManufacturingLine(@Path("id") id: Int): Response<NetworkManufacturingLine>
    @Headers(value = ["Content-Type: application/json"])
    @PUT("$MANUFACTURING_LINES/{id}")
    suspend fun editManufacturingLine(@Path("id") id: Int, @Body body: NetworkManufacturingLine): Response<NetworkManufacturingLine>

    @GET(MANUFACTURING_OPERATIONS)
    suspend fun getManufacturingOperations(): Response<List<NetworkManufacturingOperation>>

    @POST(MANUFACTURING_OPERATIONS)
    suspend fun insertManufacturingOperation(@Body teamMember: NetworkManufacturingOperation): Response<NetworkManufacturingOperation>

    @DELETE("$MANUFACTURING_OPERATIONS/{id}")
    suspend fun deleteManufacturingOperation(@Path("id") id: Int): Response<NetworkManufacturingOperation>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$MANUFACTURING_OPERATIONS/{id}")
    suspend fun editManufacturingOperation(@Path("id") id: Int, @Body body: NetworkManufacturingOperation): Response<NetworkManufacturingOperation>

    @GET(MANUFACTURING_OPERATIONS_FLOWS)
    suspend fun getOperationsFlows(): Response<List<NetworkOperationsFlow>>

    @POST("${MANUFACTURING_OPERATIONS_FLOWS}/${RECORDS}")
    suspend fun createOpFlows(@Body records: List<NetworkOperationsFlow>): Response<List<NetworkOperationsFlow>>

    @HTTP(method = "DELETE", path = "${MANUFACTURING_OPERATIONS_FLOWS}/$RECORDS", hasBody = true)
    suspend fun deleteOpFlows(@Body records: List<NetworkOperationsFlow>): Response<List<NetworkOperationsFlow>>
}