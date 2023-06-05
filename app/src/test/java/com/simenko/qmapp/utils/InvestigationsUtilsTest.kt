package com.simenko.qmapp.utils

import com.google.common.truth.Truth
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.works.SyncPeriods
import org.junit.Test
import java.time.Instant

class InvestigationsUtilsTest {
    /**
     * Last hour
     * */
    @Test
    fun `if current state db is empty and requested last hour - function returns one hour exactly`() {
        val currentState = Pair(NoRecord.num.toLong(), NoRecord.num.toLong())

        val syncPeriod = SyncPeriods.LAST_HOUR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 40 to 20 minutes ago and requested last hour - function returns one hour exactly`() {
        val fromTimeDays = 40
        val toTimeDays = 20
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_HOUR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 120 to 20 minutes ago and requested last hour - function returns one hour exactly`() {
        val fromTimeDays = 120
        val toTimeDays = 20
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_HOUR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 7_5 to 1_5 days ago and requested last hour - function returns one hour exactly`() {
        val fromTimeDays = 7.5
        val toTimeDays = 1.5
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_HOUR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }
    /**
     * Last day
     * */
    @Test
    fun `if current state db is empty and requested last day - function returns 23 hours`() {
        val currentState = Pair(NoRecord.num.toLong(), NoRecord.num.toLong())

        val syncPeriod = SyncPeriods.LAST_DAY
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60
        ).isEqualTo(23.0)
    }

    @Test
    fun `if current state db is from 40 to 20 minutes ago and requested last day - function returns 23 hours`() {
        val fromTimeDays = 40
        val toTimeDays = 20
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_DAY
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60
        ).isEqualTo(23.0)
    }

    @Test
    fun `if current state db is from 120 to 20 minutes ago and requested last day - function returns 23 hours`() {
        val fromTimeDays = 120
        val toTimeDays = 20
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_DAY
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60
        ).isEqualTo(23.0)
    }

    @Test
    fun `if current state db is from 120 to 90 minutes ago and requested last day - function returns 23 hours`() {
        val fromTimeDays = 120
        val toTimeDays = 90
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_DAY
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60
        ).isEqualTo(23.0)
    }

    @Test
    fun `if current state db is from 6 days to 90 minutes ago and requested last day - function returns 23 hours`() {
        val fromTimeDays = 6
        val toTimeDays = 90
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_DAY
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60
        ).isEqualTo(23.0)
    }

    @Test
    fun `if current state db is from 6 days to 15 minutes ago and requested last day - function returns 23 hours`() {
        val fromTimeDays = 6
        val toTimeDays = 15
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_DAY
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60
        ).isEqualTo(23.0)
    }

    @Test
    fun `if current state db is from 7_5 to 1_5 days ago and requested last day - function returns 23 hours`() {
        val fromTimeDays = 7.5
        val toTimeDays = 1.5
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_DAY
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60
        ).isEqualTo(23.0)
    }
    /**
     * Last week
     * */
    @Test
    fun `if current state db is empty and requested last week - function returns one hour exactly to sync`() {
        val currentState = Pair(NoRecord.num.toLong(), NoRecord.num.toLong())

        val syncPeriod = SyncPeriods.LAST_WEEK
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 0_7 to 0_3 days ago and requested last week - function returns one hour exactly to sync`() {
        val fromTimeDays = 0.7
        val toTimeDays = 0.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_WEEK
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 3_5 to 0_3 days ago and requested last week - function returns 2_5 days to sync`() {
        val fromTimeDays = 3.5
        val toTimeDays = 0.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_WEEK
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(2.5)
    }

    @Test
    fun `if current state db is from 5_5 to 1_5 days ago and requested last week - function returns 4_5 days to sync`() {
        val fromTimeDays = 5.5
        val toTimeDays = 1.5
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_WEEK
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(4.5)
    }

    @Test
    fun `if current state db is from 15_5 to 1_5 days ago and requested last week - function returns 6 days to sync`() {
        val fromTimeDays = 15.5
        val toTimeDays = 1.5
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_WEEK
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(6.0)
    }

    @Test
    fun `if current state db is from 15_5 to 0_1 days ago and requested last week - function returns 6 days to sync`() {
        val fromTimeDays = 15.5
        val toTimeDays = 0.1
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_WEEK
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(6.0)
    }

    @Test
    fun `if current state db is from 20 to 10 days ago and requested last week - function returns 6 days to sync`() {
        val fromTimeDays = 20
        val toTimeDays = 10
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_WEEK
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(6.0)
    }
    /**
     * Last month
     * */
    @Test
    fun `if current state db is empty and requested last month - function returns one hour exactly to sync`() {
        val currentState = Pair(NoRecord.num.toLong(), NoRecord.num.toLong())

        val syncPeriod = SyncPeriods.LAST_MONTH
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 6_5 to 0_3 days ago and requested last month - function returns one hour exactly to sync`() {
        val fromTimeDays = 6.5
        val toTimeDays = 0.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_MONTH
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 20 to 0_3 days ago and requested last month - function returns 13 days to sync`() {
        val fromTimeDays = 20
        val toTimeDays = 0.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_MONTH
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(13.0)
    }

    @Test
    fun `if current state db is from 20 to 8_3 days ago and requested last month - function returns 13 days to sync`() {
        val fromTimeDays = 20
        val toTimeDays = 8.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24 * 60 * 60 * 1000).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_MONTH
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(13.0)
    }

    @Test
    fun `if current state db is from 50 to 8_3 days ago and requested last month - function returns 24 days to sync`() {
        val fromTimeDays = 50
        val toTimeDays = 8.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_MONTH
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(24.0)
    }

    @Test
    fun `if current state db is from 50 to 0_3 days ago and requested last month - function returns 24 days to sync`() {
        val fromTimeDays = 50
        val toTimeDays = 0.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_MONTH
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(24.0)
    }

    @Test
    fun `if current state db is from 50 to 40 days ago and requested last month - function returns 24 days to sync`() {
        val fromTimeDays = 50
        val toTimeDays = 40
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_MONTH
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(24.0)
    }
    /**
     * Last quarter
     * */
    @Test
    fun `if current state db is empty and requested last quarter - function returns one hour exactly to sync`() {
        val currentState = Pair(NoRecord.num.toLong(), NoRecord.num.toLong())

        val syncPeriod = SyncPeriods.LAST_QUARTER
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 27_5 to 0_3 days ago and requested last quarter - function returns one hour exactly to sync`() {
        val fromTimeDays = 27.5
        val toTimeDays = 0.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_QUARTER
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 60 to 18 days ago and requested last quarter - function returns 29 days to sync`() {
        val fromTimeDays = 60
        val toTimeDays = 18
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_QUARTER
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(29.0)
    }

    @Test
    fun `if current state db is from 60 to 37 days ago and requested last quarter - function returns 29 days to sync`() {
        val fromTimeDays = 60
        val toTimeDays = 37
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_QUARTER
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(29.0)
    }

    @Test
    fun `if current state db is from 160 to 37 days ago and requested last quarter - function returns 59 days to sync`() {
        val fromTimeDays = 160
        val toTimeDays = 37
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_QUARTER
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(59.0)
    }

    @Test
    fun `if current state db is from 160 to 0_3 days ago and requested last quarter - function returns 59 days to sync`() {
        val fromTimeDays = 160
        val toTimeDays = 0.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_QUARTER
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(59.0)
    }

    @Test
    fun `if current state db is from 160 to 100 days ago and requested last quarter - function returns 59 days to sync`() {
        val fromTimeDays = 160
        val toTimeDays = 100
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_QUARTER
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(59.0)
    }
    /**
     * Last year
     * */
    @Test
    fun `if current state db is empty and requested last year - function returns one hour exactly to sync`() {
        val currentState = Pair(NoRecord.num.toLong(), NoRecord.num.toLong())

        val syncPeriod = SyncPeriods.LAST_YEAR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 89 to 0_3 days ago and requested last year - function returns one hour exactly to sync`() {
        val fromTimeDays = 89
        val toTimeDays = 0.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_YEAR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 110 to 18 days ago and requested last year - function returns 20 days to sync`() {
        val fromTimeDays = 110
        val toTimeDays = 18
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_YEAR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(20.0)
    }

    @Test
    fun `if current state db is from 110 to 95 days ago and requested last year - function returns 20 days to sync`() {
        val fromTimeDays = 110
        val toTimeDays = 95
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_YEAR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(20.0)
    }

    @Test
    fun `if current state db is from 385 to 95 days ago and requested last year - function returns 275 days to sync`() {
        val fromTimeDays = 385
        val toTimeDays = 95
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_YEAR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(275.0)
    }

    @Test
    fun `if current state db is from 385 to 0_3 days ago and requested last year - function returns 275 days to sync`() {
        val fromTimeDays = 385
        val toTimeDays = 0.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_YEAR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(275.0)
    }

    @Test
    fun `if current state db is from 500 to 400 days ago and requested last year - function returns 275 days to sync`() {
        val fromTimeDays = 500
        val toTimeDays = 400
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.LAST_YEAR
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(275.0)
    }
    /**
     * Complete period
     * */
    @Test
    fun `if current state db is empty and requested complete - function returns one hour exactly to sync`() {
        val currentState = Pair(NoRecord.num.toLong(), NoRecord.num.toLong())

        val syncPeriod = SyncPeriods.COMPLETE_PERIOD
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 364 to 0_3 days ago and requested complete period - function returns one hour exactly to sync`() {
        val fromTimeDays = 364
        val toTimeDays = 0.3
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.COMPLETE_PERIOD
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60
        ).isEqualTo(60.0)
    }

    @Test
    fun `if current state db is from 385 to 18 days ago and requested complete period - function returns 20 days to sync`() {
        val fromTimeDays = 385
        val toTimeDays = 18
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.COMPLETE_PERIOD
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(20.0)
    }

    @Test
    fun `if current state db is from 385 to 375 days ago and requested complete period - function returns 20 days to sync`() {
        val fromTimeDays = 385
        val toTimeDays = 375
        val currentState = Pair(
            Instant.now().minusMillis((fromTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli(),
            Instant.now().minusMillis((toTimeDays * 24L * 60L * 60L * 1000L).toLong()).toEpochMilli()
        )

        val syncPeriod = SyncPeriods.COMPLETE_PERIOD
        val resultSyncPeriod = InvestigationsUtils.getPeriodToSync(
            currentState,
            syncPeriod.latestMillis,
            syncPeriod.excludeMillis
        )
        val startTime = Instant.ofEpochMilli(resultSyncPeriod.first).epochSecond
        val endTime = Instant.ofEpochMilli(resultSyncPeriod.second).epochSecond

        Truth.assertThat(
            (endTime - startTime).toDouble() / 60 / 60 / 24
        ).isEqualTo(20.0)
    }
}