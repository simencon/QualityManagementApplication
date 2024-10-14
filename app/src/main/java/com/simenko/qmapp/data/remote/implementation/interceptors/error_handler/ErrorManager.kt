package com.simenko.qmapp.data.remote.implementation.interceptors.error_handler

import android.app.Application
import com.simenko.qmapp.presentation.ui.BaseActivity

sealed class ErrorType {
    data class Error1(val title: String, val msg: String) : ErrorType()
    data object Error2 : ErrorType()
    data class Error3(val text: String) : ErrorType()
}

interface ErrorManager {

    fun init()

    fun handleError(error: ErrorType)
}

class ErrorManagerImpl(
    private val application: Application,
) : ErrorManager {

    private val endSessionLiveData = SingleLiveEvent<ErrorType>()

    override fun init() {
        application.registerActivityLifecycleCallbacks(
            ActivityLifecycleCallbacksWrapper(
                onActivityCreated = { activity, _ ->
                    if (activity is BaseActivity) {
                        observeLiveData(activity)
                    }
                },
            )
        )
    }

    override fun handleError(error: ErrorType) {
        endSessionLiveData.postValue(error)
    }

    private fun observeLiveData(activity: BaseActivity) {
        activity.observe(endSessionLiveData) { type ->
            when (type) {
                is ErrorType.Error1 -> {
                    activity.showDialog(type.title, type.msg)
                }

                is ErrorType.Error2 -> {
                    activity.openNewActionView()
                }

                is ErrorType.Error3 -> {
                    // TODO: toast
                }
            }
        }
    }
}