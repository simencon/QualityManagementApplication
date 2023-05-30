package com.simenko.qmapp.works

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.NoSelectedRecord
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.works.WorkerKeys.EXCLUDED_MILLIS
import com.simenko.qmapp.works.WorkerKeys.LATEST_MILLIS
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import kotlin.random.Random

private const val TAG = "SyncEntitiesWorker"

@HiltWorker
class SyncEntitiesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val invRepository: InvestigationsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val completePeriod = invRepository.getCompleteOrdersRange()
            val result = invRepository.refreshInvestigationsIfNecessary(
                getPeriodToSync(
                    invRepository.getCompleteOrdersRange(),
                    inputData.getLong(LATEST_MILLIS, NoSelectedRecord.num.toLong()),
                    inputData.getLong(EXCLUDED_MILLIS, NoSelectedRecord.num.toLong())
                )
            )

            startForegroundService(result)

            return Result.success(
                workDataOf(
                    WorkerKeys.EXCLUDED_MILLIS to result
                )
            )
        } catch (e: Exception) {
            if (e.message.toString().startsWith("5")) {
                return Result.retry()
            }
            return Result.failure(
                workDataOf(
                    WorkerKeys.ERROR_MSG to "Invalid server response"
                )
            )
        }
    }

    private suspend fun getPeriodToSync(
        currentState: Pair<Long, Long>,
        latest: Long,
        exclude: Long
    ): Pair<Long, Long> {

        val thisMoment = Instant.now()

        val latestMillis = thisMoment.minusMillis(
            latest
        ).toEpochMilli()

        val excludedMillis = thisMoment.minusMillis(
            exclude
        ).toEpochMilli()

        return Pair(

            if ((currentState.first > latestMillis ||
                        (latestMillis == NoSelectedRecord.num.toLong() && latestMillis == NoSelectedRecord.num.toLong()))
                &&
                (currentState.first != NoSelectedRecord.num.toLong() && currentState.second != NoSelectedRecord.num.toLong())
            )
                currentState.first else latestMillis,

            excludedMillis
        )
    }

    private suspend fun startForegroundService(msg: String) {
        setForeground(
            foregroundInfo = ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(
                    context,
                    "sync_not_channel"
                )
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentText(msg)
                    .build()
            )
        )
    }
}