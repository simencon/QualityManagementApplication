package com.simenko.qmapp.repository

import com.simenko.qmapp.domain.NoSelectedRecord
import com.simenko.qmapp.repository.contract.InvRepository
import com.simenko.qmapp.utils.NotificationData

class FakeInvRepositoryAndroidTest : InvRepository {
    override suspend fun refreshInvestigationsIfNecessary(timeRange: Pair<Long, Long>): List<NotificationData> {
        return listOf()
    }

    override suspend fun getCompleteOrdersRange(): Pair<Long, Long> {
        return Pair(NoSelectedRecord.num.toLong(),NoSelectedRecord.num.toLong())
    }
}