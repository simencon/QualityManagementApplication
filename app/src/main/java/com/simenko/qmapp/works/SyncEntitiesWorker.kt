package com.simenko.qmapp.works

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.NoSelectedRecord
import com.simenko.qmapp.repository.contract.InvRepository
import com.simenko.qmapp.utils.InvestigationsUtils.getPeriodToSync
import com.simenko.qmapp.works.WorkerKeys.EXCLUDED_MILLIS
import com.simenko.qmapp.works.WorkerKeys.LATEST_MILLIS
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random

private const val TAG = "SyncEntitiesWorker"

@HiltWorker
class SyncEntitiesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val invRepository: InvRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
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

    private suspend fun startForegroundService(msg: String) {
        setForeground(
            foregroundInfo = ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(
                    context,
                    "sync_notification_channel"
                )
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentText(msg)
                    .build()
            )
        )
    }
}