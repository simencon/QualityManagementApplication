package com.simenko.qmapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.simenko.qmapp.other.Constants.SYNC_NOTIFICATION_CHANNEL_ID
import com.simenko.qmapp.other.Constants.SYNC_NOTIFICATION_CHANNEL_NAME
import com.simenko.qmapp.works.SyncEntitiesWorker
import com.simenko.qmapp.works.SyncPeriods
import com.simenko.qmapp.works.WorkerKeys.EXCLUDE_MILLIS
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
            SYNC_NOTIFICATION_CHANNEL_ID,
            SYNC_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupPeriodicSync()
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    fun setupOneTimeSync() {
        scheduleOneTimeSyncWork(SyncPeriods.LAST_HOUR, Duration.ofSeconds(0L))
        scheduleOneTimeSyncWork(SyncPeriods.LAST_DAY, Duration.ofSeconds(0L))
        scheduleOneTimeSyncWork(SyncPeriods.LAST_WEEK, Duration.ofSeconds(0L))
        scheduleOneTimeSyncWork(SyncPeriods.LAST_MONTH, Duration.ofSeconds(0L))
        scheduleOneTimeSyncWork(SyncPeriods.LAST_QUARTER, Duration.ofSeconds(0L))
        scheduleOneTimeSyncWork(SyncPeriods.LAST_YEAR, Duration.ofSeconds(0L))
        scheduleOneTimeSyncWork(SyncPeriods.COMPLETE_PERIOD, Duration.ofSeconds(0L))
    }

    private fun setupPeriodicSync() {
        schedulePeriodicSyncWork(SyncPeriods.LAST_HOUR, Duration.ofMinutes(60))
        schedulePeriodicSyncWork(SyncPeriods.LAST_DAY, Duration.ofHours(24))
        schedulePeriodicSyncWork(SyncPeriods.LAST_WEEK, Duration.ofDays(7))
        schedulePeriodicSyncWork(SyncPeriods.LAST_MONTH, Duration.ofDays(7))
        schedulePeriodicSyncWork(SyncPeriods.LAST_QUARTER, Duration.ofDays(7))
        schedulePeriodicSyncWork(SyncPeriods.LAST_YEAR, Duration.ofDays(7))
        schedulePeriodicSyncWork(SyncPeriods.COMPLETE_PERIOD, Duration.ofDays(7))
    }

    private fun scheduleOneTimeSyncWork(syncPeriod: SyncPeriods, initialDelay: Duration) {
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork("${syncPeriod.name}_oneTime", ExistingWorkPolicy.KEEP, prepareOneTimeSyncWork(syncPeriod, initialDelay))
    }

    private fun schedulePeriodicSyncWork(syncPeriod: SyncPeriods, repetition: Duration) {
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(syncPeriod.name, ExistingPeriodicWorkPolicy.KEEP, preparePeriodicSyncWork(syncPeriod, repetition, repetition))
    }

    companion object {
        private fun preparePeriodicSyncWork(syncPeriod: SyncPeriods, repetition: Duration, initialDelay: Duration) =
            PeriodicWorkRequestBuilder<SyncEntitiesWorker>(repetition)
                .setInputData(
                    Data.Builder()
                        .putLong(LATEST_MILLIS, syncPeriod.latestMillis)
                        .putLong(EXCLUDE_MILLIS, syncPeriod.excludeMillis)
                        .build()
                )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInitialDelay(initialDelay)
                .build()

        private fun prepareOneTimeSyncWork(syncPeriod: SyncPeriods, initialDelay: Duration): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<SyncEntitiesWorker>()
                .setInputData(
                    Data.Builder()
                        .putLong(LATEST_MILLIS, syncPeriod.latestMillis)
                        .putLong(EXCLUDE_MILLIS, syncPeriod.excludeMillis)
                        .build()
                )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInitialDelay(initialDelay)
                .build()
        }
    }
}