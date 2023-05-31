package com.simenko.qmapp.repository.contract

interface InvRepository {
    suspend fun refreshInvestigationsIfNecessary(timeRange: Pair<Long, Long>): String
    suspend fun getCompleteOrdersRange(): Pair<Long, Long>
}