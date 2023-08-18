package com.simenko.qmapp.works

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.other.Constants.DEFAULT_REST_API_URL
import com.simenko.qmapp.other.Constants.SYNC_NOTIFICATION_CHANNEL_ID
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.main.MainActivity
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
import java.util.Objects

private const val TAG = "SyncEntitiesWorker"

@HiltWorker
class SyncEntitiesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val userRepository: UserRepository,
    private val invRepository: InvestigationsRepository,
    private val notificationManagerCompat: NotificationManagerCompat
) : CoroutineWorker(context, workerParams) {

    @RequiresPermission("android.permission.POST_NOTIFICATIONS")
    override suspend fun doWork(): Result {
        val latestMillis = inputData.getLong(LATEST_MILLIS, NoRecord.num.toLong())
        val excludeMillis = inputData.getLong(EXCLUDE_MILLIS, NoRecord.num.toLong())

        runCatching {
            if (userRepository.getRestApiUrl != DEFAULT_REST_API_URL) {
                invRepository.syncInvEntitiesByTimeRange(getPeriodToSync(invRepository.getCompleteOrdersRange(), latestMillis, excludeMillis))
            } else {
                listOf()
            }
        }.also { result ->
            result.getOrNull().also {
                return if (it != null) {
                    it.forEach { n -> makeNotification(n) }
                    Result.success(
                        Data.Builder()
                            .putLong(LATEST_MILLIS, SyncPeriods.LAST_DAY.latestMillis)
                            .putLong(EXCLUDE_MILLIS, SyncPeriods.LAST_DAY.excludeMillis)
                            .build()
                    )
                } else {
                    Result.failure(
                        Data.Builder()
                            .putString(ERROR_MSG, result.exceptionOrNull()?.message)
                            .build()
                    )
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
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
            getPendingIntent(
                Objects.hash(notificationData.orderId, notificationData.subOrderId),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val builder = NotificationCompat.Builder(
            context,
            SYNC_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_investigations)
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        with(notificationManagerCompat) {
            notify(notificationData.subOrderId, builder.build())
        }
    }
}