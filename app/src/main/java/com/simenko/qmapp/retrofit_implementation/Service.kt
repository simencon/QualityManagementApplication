package com.simenko.qmapp.retrofit_entities

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface QualityManagementService {
    @GET("api/_10department")
    suspend fun getDepartments(): List<NetworkDepartment>

    @GET("api/_8TeamMember")
    suspend fun getTeamMembers(): List<NetworkTeamMembers>

    @GET("api/_0Company")
    suspend fun getCompanies(): List<NetworkCompanies>
}

object QualityManagementNetwork {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://restapiforqualityappv120221213121016.azurewebsites.net/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val serviceholder = retrofit.create(QualityManagementService::class.java)
}