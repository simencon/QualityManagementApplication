package com.simenko.qmapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.simenko.qmapp.R
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.ui.Screen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val channelId = "notification_channel"
const val channelName = "pushNotifications"


@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var storage: Storage
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("MessagingService - token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
        println("MessagingService - ${message.data}")
        makeNotification(message)
    }

    private fun makeNotification(message: RemoteMessage) {
        when (message.data["action"]) {
            ActionType.NEW_USER_REGISTERED.actionName -> {
                message.data["email"]?.let {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("${Screen.Domain.route}/${Screen.Main.Team.route}/${Screen.Main.Team.UserEdit.route}/$it")
                    )
                    println("MessagingService - $intent")

                    val pendingIntent = TaskStackBuilder.create(applicationContext).run {
                        addNextIntentWithParentStack(intent)
                        getPendingIntent(
                            -1,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    }
                    println("MessagingService - $pendingIntent")

                    val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                        .setOnlyAlertOnce(true)
                        .setContentIntent(pendingIntent)

                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                    notificationManager.createNotificationChannel(notificationChannel)

                    notificationManager.notify(-1, builder.build())
                }
            }
            ActionType.USER_GOT_CREDENTIALS.actionName -> {}
            ActionType.ORDER_STATUS_CHANGED.actionName -> {}
        }
    }

    internal enum class ActionType(val actionName: String) {
        NEW_USER_REGISTERED("newUserRegistered"),
        USER_GOT_CREDENTIALS("userGotCredentials"),
        ORDER_STATUS_CHANGED("orderStatusChanged")
    }
}

