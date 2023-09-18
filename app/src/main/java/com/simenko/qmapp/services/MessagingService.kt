package com.simenko.qmapp.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.simenko.qmapp.storage.Storage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var storage: Storage
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("MessagingService - token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println("MessagingService - ${message.data}")
    }
}

