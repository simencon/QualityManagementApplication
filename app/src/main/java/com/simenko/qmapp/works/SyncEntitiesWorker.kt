package com.simenko.qmapp.works

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.simenko.qmapp.R
import com.simenko.qmapp.other.WorkerKeys
import com.simenko.qmapp.repository.InvestigationsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.random.Random

private const val TAG = "SyncEntitiesWorker"

@HiltWorker
class SyncEntitiesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val invRepository: InvestigationsRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "syncInvestigationEntities"
    }

    override suspend fun doWork(): Result {
        try {
            val result = invRepository.refreshOrdersIfNecessary(invRepository.getCompleteOrdersRange())
            startForegroundService(result)
            return Result.success(
                workDataOf(
                    WorkerKeys.WAS_UP_TO_DATE to result
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
                    "sync_not_channel"
                )
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentText(msg)
                    .build()
            )
        )
    }
}