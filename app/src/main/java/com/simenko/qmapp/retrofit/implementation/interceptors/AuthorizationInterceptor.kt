package com.simenko.qmapp.retrofit.implementation.interceptors

import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.repository.UserRepository
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(private val userRepository: UserRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val finalUrl = HttpUrl.parse(userRepository.getRestApiUrl.ifEmpty { Constants.DEFAULT_REST_API_URL })?.let { url ->
            chain.request().url().newBuilder()
                .scheme(url.scheme())
                .host(url.url().toURI().host)
                .encodedPath(url.url().toURI().path + chain.request().url().encodedPath())
                .build()
        } ?: chain.request().url()

        val request = chain
            .request()
            .newBuilder()
            .url(finalUrl)
            .addHeader("Authorization", "Bearer ${userRepository.authToken}")
            .build()

        return chain.proceed(request)
    }
}