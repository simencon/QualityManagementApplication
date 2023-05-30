package com.simenko.qmapp.works

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.simenko.qmapp.repository.InvestigationsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshDataWorker @AssistedInject constructor (
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: InvestigationsRepository
) : CoroutineWorker(appContext,params) {

    companion object {
        const val WORK_NAME = "com.example.moviemania.work.RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        try {
            repository.checkAndUploadNew()
        }catch (e: Exception){
            return Result.retry()
        }
        return Result.success()
    }
}