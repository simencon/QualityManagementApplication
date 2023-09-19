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
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.services.MessagingService
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.works.WorkerKeys.ACTION
import com.simenko.qmapp.works.WorkerKeys.BODY
import com.simenko.qmapp.works.WorkerKeys.EMAIL
import com.simenko.qmapp.works.WorkerKeys.ERROR_MSG
import com.simenko.qmapp.works.WorkerKeys.TITLE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Objects
import javax.inject.Named

@HiltWorker
class NewNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    @Named("rest_api_url") private val url: String,
    private val systemRepository: SystemRepository,
    private val notificationManager: NotificationManagerCompat
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        runCatching {
            if (url != EmptyString.str) {
                systemRepository.syncUsers()
                true
            } else {
                false
            }
        }.also { result ->
            result.getOrNull().also {
                return if (it == true) {
                    makeNotification(inputData)
                    Result.success(
                        Data.Builder()
                            .putString(ACTION, inputData.getString(ACTION))
                            .putString(EMAIL, inputData.getString(EMAIL))
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
    private fun makeNotification(message: Data) {

        when (message.getString(ACTION)) {
            MessagingService.ActionType.NEW_USER_REGISTERED.actionName -> {
                message.getString(EMAIL)?.let {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "${Screen.Domain.route}/${Screen.Main.Team.route}/${Screen.Main.Team.UserEdit.route}/$it".toUri(),
                        context,
                        MainActivity::class.java
                    )
                    println("MessagingService - $intent")

                    val pendingIntent = TaskStackBuilder.create(context).run {
                        addNextIntentWithParentStack(intent)
                        getPendingIntent(Objects.hash(it), PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
                    }
                    println("MessagingService - $pendingIntent")

                    val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, SYNC_NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(message.getString(TITLE))
                        .setContentText(message.getString(BODY))
                        .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .addAction(R.drawable.ic_person_add, getString(context, R.string.authorize), pendingIntent)
                        .addAction(R.drawable.ic_watch_later, getString(context, R.string.remind_later), pendingIntent)

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