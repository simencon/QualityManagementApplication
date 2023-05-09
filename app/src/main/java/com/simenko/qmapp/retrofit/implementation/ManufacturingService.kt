package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.retrofit.entities.*
import retrofit2.http.GET

interface ManufacturingService {
    @GET("positionLevels")
    suspend fun getPositionLevels(): List<NetworkPositionLevel>

    @GET("teamMembers")
    suspend fun getTeamMembers(): List<NetworkTeamMembers>

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
}