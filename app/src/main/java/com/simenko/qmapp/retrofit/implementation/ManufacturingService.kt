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
import com.simenko.qmapp.retrofit.entities.*
import retrofit2.Response
import retrofit2.http.*

interface ManufacturingService {
    @GET(EMPLOYEES)
    suspend fun getTeamMembers(): Response<List<NetworkTeamMember>>

    @POST(EMPLOYEES)
    suspend fun insertTeamMember(@Body teamMember: NetworkTeamMember): Response<NetworkTeamMember>

    @DELETE("$EMPLOYEES/{id}")
    suspend fun deleteTeamMember(@Path("id") id: Int): Response<NetworkTeamMember>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$EMPLOYEES/{id}")
    suspend fun editTeamMember(@Path("id") id: Int, @Body body: NetworkTeamMember): Response<NetworkTeamMember>

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

    @GET(MANUFACTURING_OPERATIONS)
    suspend fun getManufacturingOperations(): Response<List<NetworkManufacturingOperation>>

    @GET(MANUFACTURING_OPERATIONS_FLOWS)
    suspend fun getOperationsFlows(): Response<List<NetworkOperationsFlow>>
}