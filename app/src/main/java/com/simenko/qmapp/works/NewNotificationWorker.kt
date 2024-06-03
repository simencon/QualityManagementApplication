package com.simenko.qmapp.works

import android.Manifest
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.other.Constants.SYNC_NOTIFICATION_CHANNEL_ID
import com.simenko.qmapp.receivers.NotificationActionsReceiver
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.services.MessagingService
import com.simenko.qmapp.ui.navigation.NavArguments
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.navigation.RouteCompose
import com.simenko.qmapp.works.WorkerKeys.ACTION
import com.simenko.qmapp.works.WorkerKeys.BODY
import com.simenko.qmapp.works.WorkerKeys.EMAIL
import com.simenko.qmapp.works.WorkerKeys.ERROR_MSG
import com.simenko.qmapp.works.WorkerKeys.TITLE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Objects

@HiltWorker
class NewNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val systemRepository: SystemRepository,
    private val notificationManager: NotificationManagerCompat
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            systemRepository.syncUsers()
            systemRepository.cacheNotificationData(inputData.getString(EMAIL) ?: EmptyString.str)

            makeNotification(inputData)
            Result.success(
                Data.Builder()
                    .putString(ACTION, inputData.getString(ACTION))
                    .putString(EMAIL, inputData.getString(EMAIL))
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

    private fun makeNotification(message: Data) {

        when (message.getString(ACTION)) {
            MessagingService.ActionType.NEW_USER_REGISTERED.actionName -> {
                message.getString(EMAIL)?.let {
                    val remindMeLaterIntent = Intent(context, NotificationActionsReceiver::class.java).apply {
                        action = getString(context, R.string.REMIND_LATER_ACTION)
                        putExtra(TITLE, message.getString(TITLE))
                        putExtra(BODY, message.getString(BODY))
                        putExtra(ACTION, message.getString(ACTION))
                        putExtra(EMAIL, message.getString(EMAIL))
                    }
                    val remindMeLaterPendingIntent = PendingIntent.getBroadcast(context, Objects.hash(it), remindMeLaterIntent, PendingIntent.FLAG_IMMUTABLE)

                    val intent = Intent(context, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        val link = "${with(RouteCompose) { RouteCompose.Main.Team.AuthorizeUser::class.simpleName?.withArgs(it) }}"
                        data = "${NavArguments.domain}/$link".toUri()
                    }

                    val pendingIntent = TaskStackBuilder.create(context).run {
                        addNextIntentWithParentStack(intent)
                        getPendingIntent(Objects.hash(it), PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
                    }

                    val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, SYNC_NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(message.getString(TITLE))
                        .setContentText(message.getString(BODY))
                        .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .addAction(R.drawable.ic_person_add, getString(context, R.string.authorize), pendingIntent)
                        .addAction(R.drawable.ic_watch_later, getString(context, R.string.remind_later), remindMeLaterPendingIntent)

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        return
                    } else {
                        notificationManager.notify(Objects.hash(it), builder.build())
                    }
                }
            }

            MessagingService.ActionType.USER_GOT_CREDENTIALS.actionName -> {}
            MessagingService.ActionType.ORDER_STATUS_CHANGED.actionName -> {}
        }
    }
}