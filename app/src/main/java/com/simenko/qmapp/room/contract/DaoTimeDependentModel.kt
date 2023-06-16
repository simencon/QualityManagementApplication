package com.simenko.qmapp.room.contract

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.retrofit.NetworkBaseModel

interface DaoTimeDependentModel<DB : DatabaseBaseModel<NetworkBaseModel<DB>, DomainBaseModel<DB>>> {
    fun getRecordsByTimeRange(timeRange: Pair<Long, Long>): List<DB>
}