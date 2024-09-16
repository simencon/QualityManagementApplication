package com.simenko.qmapp.retrofit.implementation.interceptors

import com.simenko.qmapp.BuildConfig
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.repository.UserRepository
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(private val userRepository: UserRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val baseUrl = if(BuildConfig.IS_API_LOCAL_HOST || userRepository.getRestApiUrl.isEmpty()) Constants.LOCAL_HOST_REST_API_URL else userRepository.getRestApiUrl

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
            .addHeader("Authorization", "Bearer ${userRepository.authToken}")
            .build()

        return chain.proceed(request)
    }
}