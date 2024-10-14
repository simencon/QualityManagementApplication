package com.simenko.qmapp.data.cache.db.contract

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.data.remote.NetworkBaseModel

interface DaoTimeDependentModel<ID, PID, DB : DatabaseBaseModel<NetworkBaseModel<DB>, DomainBaseModel<DB>, ID, PID>> {
    fun getRecordsByTimeRange(timeRange: Pair<Long, Long>): List<DB>
}