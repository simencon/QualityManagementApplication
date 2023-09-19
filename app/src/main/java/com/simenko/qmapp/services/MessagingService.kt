package com.simenko.qmapp.services

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.works.NewNotificationWorker
import com.simenko.qmapp.works.WorkerKeys
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import javax.inject.Inject

const val channelId = "notification_channel"


@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var storage: Storage

    @Inject
    lateinit var workManager: WorkManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("MessagingService - token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        println("MessagingService - ${message.data}")
        scheduleJob(message.data)
    }

    private fun scheduleJob(data: Map<String, String>) {
        val newNotificationWorker = OneTimeWorkRequestBuilder<NewNotificationWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                Data.Builder()
                    .putString(WorkerKeys.TITLE, data[WorkerKeys.TITLE])
                    .putString(WorkerKeys.BODY, data[WorkerKeys.BODY])
                    .putString(WorkerKeys.ACTION, data[WorkerKeys.ACTION])
                    .putString(WorkerKeys.EMAIL, data[WorkerKeys.EMAIL])
                    .build()
            )
            .setInitialDelay(Duration.ofSeconds(0))
            .build()

        workManager
            .beginUniqueWork("${data[WorkerKeys.ACTION]}_${data[WorkerKeys.EMAIL]}", ExistingWorkPolicy.KEEP, newNotificationWorker)
            .enqueue()
    }

    internal enum class ActionType(val actionName: String) {
        GENERAL("general"),
        NEW_USER_REGISTERED("newUserRegistered"),
        USER_GOT_CREDENTIALS("userGotCredentials"),
        ORDER_STATUS_CHANGED("orderStatusChanged")
    }
}

