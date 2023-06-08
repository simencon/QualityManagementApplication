package com.simenko.qmapp.repository.contract

import com.simenko.qmapp.utils.NotificationData

interface InvRepository {
    suspend fun syncInvEntitiesByTimeRange(timeRange: Pair<Long, Long>): List<NotificationData>
    suspend fun getCompleteOrdersRange(): Pair<Long, Long>
}