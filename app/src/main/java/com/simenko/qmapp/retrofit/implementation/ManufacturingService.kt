package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.retrofit.entities.*
import retrofit2.Response
import retrofit2.http.*

interface ManufacturingService {
    @GET("positionLevels")
    suspend fun getPositionLevels(): List<NetworkPositionLevel>

    @GET("teamMembers")
    suspend fun getTeamMembers(): List<NetworkTeamMember>

    @GET("companies")
    suspend fun getCompanies(): List<NetworkCompany>

    @GET("departments")
    suspend fun getDepartments(): List<NetworkDepartment>

    @GET("subDepartments")
    suspend fun getSubDepartments(): List<NetworkSubDepartment>

    @GET("manufacturingChannels")
    suspend fun getManufacturingChannels(): List<NetworkManufacturingChannel>

    @GET("manufacturingLines")
    suspend fun getManufacturingLines(): List<NetworkManufacturingLine>

    @GET("manufacturingOperations")
    suspend fun getManufacturingOperations(): List<NetworkManufacturingOperation>

    @GET("manufacturingOperationsFlows")
    suspend fun getOperationsFlows(): List<NetworkOperationsFlow>

    @POST("teamMembers")
    suspend fun insertTeamMember(@Body teamMember: NetworkTeamMember): Response<NetworkTeamMember>

    @DELETE("teamMembers/{id}")
    suspend fun deleteTeamMember(@Path("id") id: Int): Response<Unit>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("teamMembers/{id}")
    suspend fun updateTeamMember(@Path("id") id: Int, @Body body: NetworkTeamMember): Response<NetworkTeamMember>
}