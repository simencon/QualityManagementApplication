package com.simenko.qmapp.repository

import com.simenko.qmapp.domain.NoSelectedRecord
import com.simenko.qmapp.repository.contract.InvRepository

class FakeInvRepositoryAndroidTest : InvRepository {
    override suspend fun refreshInvestigationsIfNecessary(timeRange: Pair<Long, Long>): String {
        return "3 new orders, 5 status changed"
    }

    override suspend fun getCompleteOrdersRange(): Pair<Long, Long> {
        return Pair(NoSelectedRecord.num.toLong(),NoSelectedRecord.num.toLong())
    }
}