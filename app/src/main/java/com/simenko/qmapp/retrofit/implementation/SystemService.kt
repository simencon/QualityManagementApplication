package com.simenko.qmapp.retrofit.implementation

import com.simenko.qmapp.other.Constants.ROLES
import com.simenko.qmapp.other.Constants.AUTHORIZE_USER
import com.simenko.qmapp.other.Constants.COMPANY_DATA
import com.simenko.qmapp.other.Constants.PRINCIPLES
import com.simenko.qmapp.other.Constants.REMOVE_USER
import com.simenko.qmapp.retrofit.entities.NetworkUser
import com.simenko.qmapp.retrofit.entities.NetworkUserRole
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

interface SystemService {
    @GET("${PRINCIPLES}/${ROLES}")
    suspend fun getUserRoles(): Response<List<NetworkUserRole>>

    @GET(PRINCIPLES)
    suspend fun getUsers(): Response<List<NetworkUser>>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$PRINCIPLES/$COMPANY_DATA/{id}")
    suspend fun editUserCompanyData(@Path("id") id: String, @Body body: NetworkUser): Response<NetworkUser>

    @Headers(value = ["Content-Type: application/json"])
    @PUT("$PRINCIPLES/$COMPANY_DATA/$AUTHORIZE_USER/{id}")
    suspend fun authorizeUser(@Path("id") id: String, @Body body: NetworkUser): Response<NetworkUser>

    @PUT("$PRINCIPLES/$COMPANY_DATA/$REMOVE_USER/{id}")
    suspend fun removeUser(@Path("id") id: String): Response<NetworkUser>
}