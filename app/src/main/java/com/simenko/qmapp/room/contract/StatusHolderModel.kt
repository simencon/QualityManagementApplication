package com.simenko.qmapp.room.contract

import com.simenko.qmapp.utils.NotificationData
import com.simenko.qmapp.utils.NotificationReasons

interface StatusHolderModel {
    fun toNotificationData(reason: NotificationReasons): NotificationData
}