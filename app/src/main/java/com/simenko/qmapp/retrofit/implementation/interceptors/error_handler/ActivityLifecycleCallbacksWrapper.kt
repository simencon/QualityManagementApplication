package com.simenko.qmapp.retrofit.implementation.interceptors.error_handler

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class ActivityLifecycleCallbacksWrapper(
    private val onActivityCreated: ((Activity, Bundle?) -> Unit)? = null,
    private val onActivityStarted: ((Activity) -> Unit)? = null,
    private val onActivityResumed: ((Activity) -> Unit)? = null,
    private val onActivityPaused: ((Activity) -> Unit)? = null,
    private val onActivityStopped: ((Activity) -> Unit)? = null,
    private val onActivitySaveInstanceState: ((Activity, Bundle) -> Unit)? = null,
    private val onActivityDestroyed: ((Activity) -> Unit)? = null,
    private val onActivityPostCreated: ((Activity, Bundle?) -> Unit)? = null,
) : Application.ActivityLifecycleCallbacks {

    override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
        onActivityPostCreated?.invoke(activity, savedInstanceState)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        onActivityCreated?.invoke(activity, savedInstanceState)
    }

    override fun onActivityStarted(activity: Activity) {
        onActivityStarted?.invoke(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        onActivityResumed?.invoke(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        onActivityPaused?.invoke(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        onActivityStopped?.invoke(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        onActivitySaveInstanceState?.invoke(activity, outState)
    }

    override fun onActivityDestroyed(activity: Activity) {
        onActivityDestroyed?.invoke(activity)
    }
}

fun <T> ComponentActivity.observe(liveData: LiveData<T>, observer: Observer<T>) {
    liveData.observe(this, observer)
}