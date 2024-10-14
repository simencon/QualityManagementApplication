package com.simenko.qmapp.data.cache.db.contract

import com.simenko.qmapp.utils.NotificationData
import com.simenko.qmapp.utils.NotificationReasons

interface StatusHolderModel {
    fun toNotificationData(reason: NotificationReasons): NotificationData
}