package com.simenko.qmapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.services.MessagingService
import com.simenko.qmapp.works.WorkerKeys
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects
import javax.inject.Inject
@AndroidEntryPoint
class NotificationActionsReceiver : BroadcastReceiver() {
    @Inject
    lateinit var workManager: WorkManager
    @Inject
    lateinit var notificationManager: NotificationManagerCompat
    override fun onReceive(context: Context, intent: Intent?) {
        intent?.action?.let { a ->
            if (a == ContextCompat.getString(context, R.string.REMIND_LATER_ACTION)) {
                intent.extras?.let { b ->
                    val email = b.getString(WorkerKeys.EMAIL)?: EmptyString.str
                    val data: Map<String, String> = mapOf(
                        Pair(WorkerKeys.TITLE, b.getString(WorkerKeys.TITLE)?: EmptyString.str),
                        Pair(WorkerKeys.BODY, b.getString(WorkerKeys.BODY)?: EmptyString.str),
                        Pair(WorkerKeys.ACTION, b.getString(WorkerKeys.ACTION)?: EmptyString.str),
                        Pair(WorkerKeys.EMAIL, email)
                    )
                    MessagingService.scheduleJob(workManager, data, 24L)
                    notificationManager.activeNotifications.find { it.id == Objects.hash(email) }?.let { notificationManager.cancel(it.id) }
                }
            }
        }
    }
}