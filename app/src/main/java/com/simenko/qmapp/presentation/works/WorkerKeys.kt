package com.simenko.qmapp.presentation.works

import com.simenko.qmapp.domain.NoRecord
import java.util.concurrent.TimeUnit

object WorkerKeys {
    //    SyncJobKeys
    const val ERROR_MSG = "errorMsg"
    const val LATEST_MILLIS = "latestMillis"
    const val EXCLUDE_MILLIS = "excludedMillis"

    //    NewUserRegisteredKeys
    const val ACTION = "action"
    const val TITLE = "title"
    const val BODY = "body"
    const val EMAIL = "email"
}

enum class SyncPeriods(val latestMillis: Long, val excludeMillis: Long) {
    LAST_HOUR(TimeUnit.HOURS.toMillis(1), 0L),
    LAST_DAY(TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(1)),
    LAST_WEEK(TimeUnit.DAYS.toMillis(7), TimeUnit.DAYS.toMillis(1)),
    LAST_MONTH(TimeUnit.DAYS.toMillis(31), TimeUnit.DAYS.toMillis(7)),
    LAST_QUARTER(TimeUnit.DAYS.toMillis(90), TimeUnit.DAYS.toMillis(31)),
    LAST_YEAR(TimeUnit.DAYS.toMillis(365), TimeUnit.DAYS.toMillis(90)),
    COMPLETE_PERIOD(NoRecord.num.toLong(), TimeUnit.DAYS.toMillis(365))
}