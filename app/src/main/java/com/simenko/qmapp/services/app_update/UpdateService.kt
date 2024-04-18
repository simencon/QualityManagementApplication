package com.simenko.qmapp.services.app_update

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.simenko.qmapp.repository.SystemRepository
import javax.inject.Inject


private const val TAG = "UpdateService"
class UpdateService : Service() {
    @Inject
    lateinit var systemRepository: SystemRepository
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: has been updated")
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://music.youtube.com/"))
        startActivity(browserIntent)
        systemRepository.cacheNotificationData("this is SPARTA!!!!")
        // Perform any necessary tasks when the app is updated
        // For example, update database, refresh UI, etc.
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}