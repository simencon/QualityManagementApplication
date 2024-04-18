package com.simenko.qmapp.services.app_update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val TAG = "UpdateReceiver"

class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_REPLACED) {
            Log.d(TAG, "onReceive: ${context.packageName}")
            val packageName = intent.data?.encodedSchemeSpecificPart
            if (packageName != null && packageName == context.packageName) {
                // Start the service when the app is updated
                val serviceIntent = Intent(context, UpdateService::class.java)
                context.startService(serviceIntent)
            }
        }
    }
}