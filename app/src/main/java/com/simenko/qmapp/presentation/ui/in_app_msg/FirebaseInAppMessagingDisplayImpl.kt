package com.simenko.qmapp.presentation.ui.in_app_msg

import android.app.Application
import android.util.Log
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplay
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplayCallbacks
import com.google.firebase.inappmessaging.model.InAppMessage
import com.simenko.qmapp.data.remote.implementation.interceptors.error_handler.ActivityLifecycleCallbacksWrapper
import com.simenko.qmapp.data.remote.implementation.interceptors.error_handler.SingleLiveEvent
import com.simenko.qmapp.data.remote.implementation.interceptors.error_handler.observe
import com.simenko.qmapp.presentation.ui.BaseActivity

private const val TAG = "FirebaseInAppMessagingDisplayImpl"
class FirebaseInAppMessagingDisplayImpl(private val application: Application) : FirebaseInAppMessagingDisplay {
    private val endSessionLiveData = SingleLiveEvent<InAppMessage>()

    init {
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

    override fun displayMessage(inAppMessage: InAppMessage, callbacks: FirebaseInAppMessagingDisplayCallbacks) {
        endSessionLiveData.postValue(inAppMessage)
    }

    private fun observeLiveData(activity: BaseActivity) {
        activity.observe(endSessionLiveData) { inAppMessage ->

            Log.d(TAG, "observeLiveData: $inAppMessage")

//            when (type) {
//                is ErrorType.Error1 -> {
//                    activity.showDialog(type.title, type.msg)
//                }
//
//                is ErrorType.Error2 -> {
//                    activity.openNewActionView()
//                }
//
//                is ErrorType.Error3 -> {
//                    // TODO: toast
//                }
//            }
        }
    }
}