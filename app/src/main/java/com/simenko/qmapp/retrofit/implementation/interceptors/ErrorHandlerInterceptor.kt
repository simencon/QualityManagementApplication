package com.simenko.qmapp.retrofit.implementation.interceptors

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.domain.EmptyString
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorHandlerInterceptor(private val appContext: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        try {
            val response = chain.proceed(request)
            when (response.code()) {
                in 200..299 -> showDialog("Success!", "Got data from ${request.url().encodedPath()} endpoint")
                in 400..499 -> showDialog("Some issue on client side", response.message())
                in 500..599 -> showDialog("Some issue on server side", response.message())
                else -> Unit
            }
            return response
        } catch (e: Throwable) {
            when (e) {
                is IOException -> showDialog("IO Exception", e.message ?: EmptyString.str)
                else -> showDialog("Unknown exception", e.message ?: EmptyString.str)
            }
            return chain.proceed(request)
        }
    }

    private fun showDialog(title: String, msg: String, positiveBtnTitle: String = "OK") {
        (appContext as BaseApplication).getCurrentActivity()?.let { activity ->
            activity.runOnUiThread {
                MaterialAlertDialogBuilder(activity)
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton(positiveBtnTitle) { _, _ -> }
                    .create()
                    .show()
            }
        }
    }
}