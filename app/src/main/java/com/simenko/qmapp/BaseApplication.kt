package com.simenko.qmapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.simenko.qmapp.works.SyncEntitiesWorker
import com.simenko.qmapp.works.SyncPeriodInSec
import com.simenko.qmapp.works.WorkerKeys.EXCLUDED_MILLIS
import com.simenko.qmapp.works.WorkerKeys.LATEST_MILLIS
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        val notificationChannel = NotificationChannel(
            "sync_not_channel",
            "Entity synchronization",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun createWork(syncPeriod: SyncPeriodInSec, repetition: Duration) {
        val work = PeriodicWorkRequestBuilder<SyncEntitiesWorker>(repetition)
            .setInputData(
                workDataOf(
                    LATEST_MILLIS to syncPeriod.latestMillis,
                    EXCLUDED_MILLIS to syncPeriod.excludedMillis
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .setInitialDelay(repetition)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            syncPeriod.name,
            ExistingPeriodicWorkPolicy.KEEP,
            work
        )
    }

    private fun setupRecurringWork() {
        createWork(SyncPeriodInSec.LAST_HOUR, Duration.ofMinutes(15))
        createWork(SyncPeriodInSec.LAST_DAY, Duration.ofMinutes(30))
        createWork(SyncPeriodInSec.LAST_WEEK, Duration.ofHours(1))
        createWork(SyncPeriodInSec.LAST_MONTH, Duration.ofDays(1))
        createWork(SyncPeriodInSec.LAST_QUARTER, Duration.ofDays(7))
        createWork(SyncPeriodInSec.LAST_YEAR, Duration.ofDays(14))
        createWork(SyncPeriodInSec.COMPLETE_PERIOD, Duration.ofDays(28))
    }
}