package com.simenko.qmapp.data.remote.implementation.interceptors

import com.simenko.qmapp.BuildConfig
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(private val userRepository: UserRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val baseUrl = if(BuildConfig.IS_API_LOCAL_HOST || userRepository.getRestApiUrl.isEmpty()) Constants.LOCAL_HOST_REST_API_URL else userRepository.getRestApiUrl
        val bearerToken = runBlocking { userRepository.getActualFbToken() }

        val finalUrl = baseUrl.toHttpUrlOrNull()?.let { url ->
            chain.request().url.newBuilder()
                .scheme(url.scheme)
                .host(url.toUrl().toURI().host)
                .encodedPath(url.toUrl().toURI().path + chain.request().url.encodedPath)
                .build()
        } ?: chain.request().url

        val request = chain
            .request()
            .newBuilder()
            .url(finalUrl)
            .addHeader("Authorization", "Bearer $bearerToken")
            .build()

        return chain.proceed(request)
    }
}