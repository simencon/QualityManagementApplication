package com.simenko.qmapp.repository

import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.repository.contract.InvRepository
import com.simenko.qmapp.utils.NotificationData

class FakeInvRepositoryAndroidTest : InvRepository {
    override suspend fun syncInvEntitiesByTimeRange(timeRange: Pair<Long, Long>, earlyOrders: Boolean): List<NotificationData> {
        return listOf()
    }

    override suspend fun getCompleteOrdersRange(): Pair<Long, Long> {
        return Pair(NoRecord.num.toLong(),NoRecord.num.toLong())
    }
}