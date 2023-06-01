package com.simenko.qmapp.works

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.NoSelectedRecord
import com.simenko.qmapp.other.Constants.NOTIFICATION_ID
import com.simenko.qmapp.other.Constants.NOTIFICATION_ID_KEY
import com.simenko.qmapp.other.Constants.SYNC_NOTIFICATION_CHANNEL_ID
import com.simenko.qmapp.other.Constants.SYNC_NOTIFICATION_CHANNEL_NAME
import com.simenko.qmapp.repository.contract.InvRepository
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.utils.InvestigationsUtils.getPeriodToSync
import com.simenko.qmapp.works.WorkerKeys.EXCLUDE_MILLIS
import com.simenko.qmapp.works.WorkerKeys.LATEST_MILLIS
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val TAG = "SyncEntitiesWorker"

@HiltWorker
class SyncEntitiesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val invRepository: InvRepository
) : CoroutineWorker(context, workerParams) {

    @RequiresPermission("android.permission.POST_NOTIFICATIONS")
    override suspend fun doWork(): Result {
        try {
            val result = invRepository.refreshInvestigationsIfNecessary(
                getPeriodToSync(
                    invRepository.getCompleteOrdersRange(),
                    inputData.getLong(LATEST_MILLIS, NoSelectedRecord.num.toLong()),
                    inputData.getLong(EXCLUDE_MILLIS, NoSelectedRecord.num.toLong())
                )
            )

//            startForegroundService(result)
//            setForeground(getForegroundInfo())
            makeNotification(result)

            delay(200L)

            return Result.success(
                workDataOf(
                    WorkerKeys.EXCLUDE_MILLIS to result
                )
            )
        } catch (e: Exception) {
            if (e.message.toString().startsWith("5")) {
                return Result.retry()
            }
            return Result.failure(
                workDataOf(
                    WorkerKeys.ERROR_MSG to "Invalid server response"
                )
            )
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {

        val notification = NotificationCompat.Builder(
            context,
            SYNC_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText("test notification")
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification
            )
        }
    }

    private suspend fun startForegroundService(msg: String) {

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID)
        }

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        setForeground(
            foregroundInfo = ForegroundInfo(
                NOTIFICATION_ID,
                NotificationCompat.Builder(
                    context,
                    SYNC_NOTIFICATION_CHANNEL_ID
                )
                    .setSmallIcon(R.drawable.ic_send)
                    .setContentText(msg)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .build()
            )
        )
    }

    @RequiresPermission("android.permission.POST_NOTIFICATIONS")
    fun makeNotification(msg: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID)
        }

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        val builder = NotificationCompat.Builder(
            context,
            SYNC_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_send)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}