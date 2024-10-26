package com.simenko.qmapp.data.remote.implementation.interceptors.error_handler

import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.BuildConfig
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.utils.FileLogger
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorHandlerInterceptor(private val errorManager: ErrorManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        try {
            val response = chain.proceed(request)
            FileLogger.logEvent(BaseApplication.instance, "${request.url.encodedPath}, response code - ${response.code} ")
            when (response.code) {
                in 200..299 -> if (BuildConfig.DEBUG) errorManager.handleError(ErrorType.Error1("Success!", "Got data from ${request.url.encodedPath} endpoint"))
                in 400..499 -> errorManager.handleError(ErrorType.Error1("Some issue on client side", response.message))
                in 500..599 -> errorManager.handleError(ErrorType.Error1("Some issue on server side", response.message))
                else -> Unit
            }
            return response
        } catch (e: Throwable) {
            when (e) {
                is IOException -> errorManager.handleError(ErrorType.Error1("IO Exception", e.message ?: EmptyString.str))
                else -> errorManager.handleError(ErrorType.Error1("Unknown exception", e.message ?: EmptyString.str))
            }
            return chain.proceed(request)
        }
    }
}