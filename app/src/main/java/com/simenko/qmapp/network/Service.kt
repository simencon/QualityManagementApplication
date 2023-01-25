package com.simenko.qmapp.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET

interface QualityManagementService {
    @GET("api/_10department")
    suspend fun getDepartments(): List<NetworkDepartment>

    @GET("api/_8TeamMember")
    suspend fun getTeamMembers(): List<NetworkTeamMembers>
}

object QualityManagementNetwork {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://restapiforqualityappv120221213121016.azurewebsites.net/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val serviceholder = retrofit.create(QualityManagementService::class.java)
}