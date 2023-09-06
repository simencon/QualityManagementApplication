package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.retrofit.entities.NetworkUser
import com.simenko.qmapp.retrofit.entities.NetworkUserRole
import retrofit2.Response
import retrofit2.http.GET

interface SystemService {
    @GET("${Constants.PRINCIPLES}/${Constants.ROLES}")
    suspend fun getUserRoles(): Response<List<NetworkUserRole>>

    @GET(Constants.PRINCIPLES)
    suspend fun getUsers(): Response<List<NetworkUser>>
}