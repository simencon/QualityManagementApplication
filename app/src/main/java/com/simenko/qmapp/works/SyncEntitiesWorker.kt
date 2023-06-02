package com.simenko.qmapp.works

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.NoSelectedRecord
import com.simenko.qmapp.other.Constants.SYNC_NOTIFICATION_CHANNEL_ID
import com.simenko.qmapp.repository.contract.InvRepository
import com.simenko.qmapp.ui.main.createMainActivityIntent
import com.simenko.qmapp.utils.InvestigationsUtils.getPeriodToSync
import com.simenko.qmapp.utils.NotificationData
import com.simenko.qmapp.utils.StringUtils.concatThreeStrings
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings1
import com.simenko.qmapp.works.WorkerKeys.ERROR_MSG
import com.simenko.qmapp.works.WorkerKeys.EXCLUDE_MILLIS
import com.simenko.qmapp.works.WorkerKeys.LATEST_MILLIS
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.util.Objects

private const val TAG = "SyncEntitiesWorker"

@HiltWorker
class SyncEntitiesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val invRepository: InvRepository,
    private val notificationManagerCompat: NotificationManagerCompat
) : CoroutineWorker(context, workerParams) {

    @RequiresPermission("android.permission.POST_NOTIFICATIONS")
    override suspend fun doWork(): Result {
        try {
            val latestMillis = inputData.getLong(LATEST_MILLIS, NoSelectedRecord.num.toLong())
            val excludeMillis = inputData.getLong(EXCLUDE_MILLIS, NoSelectedRecord.num.toLong())

            val result = invRepository.refreshInvestigationsIfNecessary(
                getPeriodToSync(
                    invRepository.getCompleteOrdersRange(),
                    latestMillis,
                    excludeMillis
                )
            )

            Log.d(TAG, "doWork: $result")

            result.forEach {
                makeNotification(it)
            }

            delay(200L)

            return Result.success(
                Data.Builder()
                    .putLong(LATEST_MILLIS, SyncPeriods.LAST_DAY.latestMillis)
                    .putLong(EXCLUDE_MILLIS, SyncPeriods.LAST_DAY.excludeMillis)
                    .build()
            )
        } catch (e: Exception) {
            if (e.message.toString().startsWith("5")) {
                return Result.retry()
            }
            return Result.failure(
                Data.Builder()
                    .putString(ERROR_MSG, "Invalid server response")
                    .build()
            )
        }
    }

    @RequiresPermission("android.permission.POST_NOTIFICATIONS")
    fun makeNotification(notificationData: NotificationData) {
        val intent = createMainActivityIntent(
            context,
            notificationData.orderId,
            notificationData.subOrderId
        )

        var title: String
        var msg: String

        notificationData.let {
            title = it.notificationReason.reason + concatTwoStrings1(it.orderNumber.toString(), it.subOrderStatus)
            msg = concatThreeStrings(it.departmentAbbr, it.channelAbbr, it.itemTypeCompleteDesignation)
        }

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(Objects.hash(notificationData.orderId, notificationData.subOrderId), PendingIntent.FLAG_IMMUTABLE)
        }

        val builder = NotificationCompat.Builder(
            context,
            SYNC_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_investigations)
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        with(notificationManagerCompat) {
            notify(notificationData.subOrderId, builder.build())
        }
    }
}