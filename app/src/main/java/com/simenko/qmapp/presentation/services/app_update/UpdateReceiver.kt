package com.simenko.qmapp.presentation.services.app_update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            Toast.makeText(context, "UpdateReceiver received event", Toast.LENGTH_LONG).show()
            val serviceIntent = Intent(context, UpdateService::class.java)
            context.startService(serviceIntent)
        }
    }
}