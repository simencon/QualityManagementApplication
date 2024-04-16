package com.simenko.qmapp.retrofit.implementation.interceptors

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ActivityLifeCycleCallbackImpl: Application.ActivityLifecycleCallbacks {
    private var setOfActivities: HashSet<Activity> = HashSet()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        setOfActivities.add(activity)
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        setOfActivities.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    fun getCurrentActivity(): Activity? {
        return if (setOfActivities.isEmpty()) null else setOfActivities.iterator().next()
    }
}