package com.simenko.qmapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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
//        val notificationManager = getSystemService(NotificationManager::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    private fun createSyncWork(syncPeriod: SyncPeriods, repetition: Duration) {
        val work = PeriodicWorkRequestBuilder<SyncEntitiesWorker>(repetition)
            .setInputData(
                Data.Builder()
                    .putLong(LATEST_MILLIS, syncPeriod.latestMillis)
                    .putLong(EXCLUDE_MILLIS, syncPeriod.excludeMillis)
                    .build()
//                workDataOf(
//                    LATEST_MILLIS to syncPeriod.latestMillis,
//                    EXCLUDE_MILLIS to syncPeriod.excludeMillis
//                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .setInitialDelay(repetition)
//            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            syncPeriod.name,
            ExistingPeriodicWorkPolicy.KEEP,
            work
        )
    }

    private fun setupRecurringWork() {
        createSyncWork(SyncPeriods.LAST_HOUR, Duration.ofMinutes(15))
        createSyncWork(SyncPeriods.LAST_DAY, Duration.ofMinutes(30))
        createSyncWork(SyncPeriods.LAST_WEEK, Duration.ofHours(1))
        createSyncWork(SyncPeriods.LAST_MONTH, Duration.ofDays(1))
        createSyncWork(SyncPeriods.LAST_QUARTER, Duration.ofDays(7))
        createSyncWork(SyncPeriods.LAST_YEAR, Duration.ofDays(14))
        createSyncWork(SyncPeriods.COMPLETE_PERIOD, Duration.ofDays(28))
    }
}