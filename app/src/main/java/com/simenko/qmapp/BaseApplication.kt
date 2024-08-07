package com.simenko.qmapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.simenko.qmapp.other.Constants.SYNC_NOTIFICATION_CHANNEL_ID
import com.simenko.qmapp.other.Constants.SYNC_NOTIFICATION_CHANNEL_NAME
import com.simenko.qmapp.retrofit.implementation.interceptors.error_handler.ErrorManager
import com.simenko.qmapp.works.SyncEntitiesWorker
import com.simenko.qmapp.works.SyncPeriods
import com.simenko.qmapp.works.WorkerKeys.EXCLUDE_MILLIS
import com.simenko.qmapp.works.WorkerKeys.LATEST_MILLIS
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var errorManager: ErrorManager
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        errorManager.init()
        val notificationChannel = NotificationChannel(
            SYNC_NOTIFICATION_CHANNEL_ID,
            SYNC_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

    }

    companion object {
        fun setupOneTimeSync(context: Context) {
            with(context) {
                scheduleOneTimeSyncWork(SyncPeriods.LAST_HOUR, Duration.ofSeconds(0L))
                scheduleOneTimeSyncWork(SyncPeriods.LAST_DAY, Duration.ofSeconds(0L))
                scheduleOneTimeSyncWork(SyncPeriods.LAST_WEEK, Duration.ofSeconds(0L))
                scheduleOneTimeSyncWork(SyncPeriods.LAST_MONTH, Duration.ofSeconds(0L))
                scheduleOneTimeSyncWork(SyncPeriods.LAST_QUARTER, Duration.ofSeconds(0L))
                scheduleOneTimeSyncWork(SyncPeriods.LAST_YEAR, Duration.ofSeconds(0L))
                scheduleOneTimeSyncWork(SyncPeriods.COMPLETE_PERIOD, Duration.ofSeconds(0L))
            }
        }

        private fun Context.scheduleOneTimeSyncWork(syncPeriod: SyncPeriods, initialDelay: Duration) {
            WorkManager.getInstance(applicationContext)
                .enqueueUniqueWork("${syncPeriod.name}_oneTime", ExistingWorkPolicy.KEEP, prepareOneTimeSyncWork(syncPeriod, initialDelay))
        }


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