package com.simenko.qmapp.presentation.services.app_update

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.simenko.qmapp.data.repository.SystemRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject


@AndroidEntryPoint
class UpdateService : Service() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var systemRepository: SystemRepository
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "UpdateService started", Toast.LENGTH_LONG).show()
        scope.launch(Dispatchers.IO) {
            systemRepository.cacheNotificationData("this is SPARTA!!!!, ${Instant.now()}")
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}