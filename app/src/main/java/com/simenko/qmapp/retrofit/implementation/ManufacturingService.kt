package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.other.Constants.COMPANIES
import com.simenko.qmapp.other.Constants.DEPARTMENTS
import com.simenko.qmapp.other.Constants.MANUFACTURING_CHANNELS
import com.simenko.qmapp.other.Constants.MANUFACTURING_LINES
import com.simenko.qmapp.other.Constants.MANUFACTURING_OPERATIONS
import com.simenko.qmapp.other.Constants.MANUFACTURING_OPERATIONS_FLOWS
import com.simenko.qmapp.other.Constants.POSITIONS_LEVELS
import com.simenko.qmapp.other.Constants.SUB_DEPARTMENTS
import com.simenko.qmapp.other.Constants.TEAM_MEMBERS
import com.simenko.qmapp.retrofit.entities.*
import retrofit2.Response
import retrofit2.http.*

interface ManufacturingService {
    @GET(TEAM_MEMBERS)
    suspend fun getTeamMembers(): Response<List<NetworkTeamMember>>

    @POST(TEAM_MEMBERS)
    suspend fun insertTeamMember(@Body teamMember: NetworkTeamMember): Response<NetworkTeamMember>

    @DELETE("$TEAM_MEMBERS/{id}")
    suspend fun deleteTeamMember(@Path("id") id: Int): Response<NetworkTeamMember>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$TEAM_MEMBERS/{id}")
    suspend fun editTeamMember(@Path("id") id: Int, @Body body: NetworkTeamMember): Response<NetworkTeamMember>

    @GET(COMPANIES)
    suspend fun getCompanies(): List<NetworkCompany>

    @GET(DEPARTMENTS)
    suspend fun getDepartments(): Response<List<NetworkDepartment>>

    @GET(SUB_DEPARTMENTS)
    suspend fun getSubDepartments(): List<NetworkSubDepartment>

    @GET(MANUFACTURING_CHANNELS)
    suspend fun getManufacturingChannels(): List<NetworkManufacturingChannel>

    @GET(MANUFACTURING_LINES)
    suspend fun getManufacturingLines(): List<NetworkManufacturingLine>

    @GET(MANUFACTURING_OPERATIONS)
    suspend fun getManufacturingOperations(): List<NetworkManufacturingOperation>

    @GET(MANUFACTURING_OPERATIONS_FLOWS)
    suspend fun getOperationsFlows(): List<NetworkOperationsFlow>

    @GET(POSITIONS_LEVELS)
    suspend fun getPositionLevels(): List<NetworkPositionLevel>
}