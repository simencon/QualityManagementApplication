package com.simenko.qmapp.works

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.other.Constants.SYNC_NOTIFICATION_CHANNEL_ID
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.Route.Companion.DOMAIN
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

@HiltWorker
class SyncEntitiesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val invRepository: InvestigationsRepository,
    private val notificationManagerCompat: NotificationManagerCompat
) : CoroutineWorker(context, workerParams) {

    @RequiresPermission("android.permission.POST_NOTIFICATIONS")
    override suspend fun doWork(): Result {
        val latestMillis = inputData.getLong(LATEST_MILLIS, NoRecord.num)
        val excludeMillis = inputData.getLong(EXCLUDE_MILLIS, NoRecord.num)

        return try {
            invRepository.syncInvEntitiesByTimeRange(getPeriodToSync(invRepository.completeOrdersRange(), latestMillis, excludeMillis)).forEach {
                makeNotification(it)
            }
            Result.success(
                Data.Builder()
                    .putLong(LATEST_MILLIS, SyncPeriods.LAST_DAY.latestMillis)
                    .putLong(EXCLUDE_MILLIS, SyncPeriods.LAST_DAY.excludeMillis)
                    .build()
            )
        } catch (e: Throwable) {
            Result.failure(
                Data.Builder()
                    .putString(ERROR_MSG, e.message)
                    .build()
            )
        }
    }


    @SuppressLint("MissingPermission")
    fun makeNotification(nData: NotificationData) {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            val link = "${with(Route) { Route.Main.AllInvestigations.AllInvestigationsList::class.simpleName?.withArgs(nData.orderId.toString(), nData.subOrderId.toString()) }}"
            data = "${DOMAIN}/$link".toUri()
        }

        var title: String
        var msg: String

        nData.let {
            title = it.notificationReason.reason + concatTwoStrings1(it.orderNumber.toString(), it.subOrderStatus)
            msg = concatThreeStrings(it.departmentAbbr, it.channelAbbr, it.itemTypeCompleteDesignation)
        }

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(Objects.hash(nData.orderId, nData.subOrderId), PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        val builder = NotificationCompat.Builder(context, SYNC_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_investigations)
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        with(notificationManagerCompat) {
            notify(nData.subOrderId.toInt(), builder.build())
        }
    }
}