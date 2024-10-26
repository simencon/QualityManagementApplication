package com.simenko.qmapp.presentation.works

import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.simenko.qmapp.domain.usecase.GetUserCompanyIdUseCase
import com.simenko.qmapp.domain.usecase.SyncInvestigationsMasterDataUseCase
import com.simenko.qmapp.domain.usecase.SyncProductsUseCase
import com.simenko.qmapp.domain.usecase.SyncStructureDataUseCase
import com.simenko.qmapp.domain.usecase.SyncTeamDataUseCase
import com.simenko.qmapp.domain.usecase.UploadNewInvestigationsUseCase
import com.simenko.qmapp.other.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope

@HiltWorker
class SyncMasterDataWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val getUserCompanyIdUseCase: GetUserCompanyIdUseCase,
    private val syncTeamDataUseCase: SyncTeamDataUseCase,
    private val syncStructureDataUseCase: SyncStructureDataUseCase,
    private val syncProductsUseCase: SyncProductsUseCase,
    private val syncInvestigationsMasterDataUseCase: SyncInvestigationsMasterDataUseCase,
    private val uploadNewInvestigationsUseCase: UploadNewInvestigationsUseCase,
) : CoroutineWorker(context, workerParams) {

    @RequiresPermission("android.permission.POST_NOTIFICATIONS")
    override suspend fun doWork(): Result {

        getUserCompanyIdUseCase.execute()
        syncTeamDataUseCase.execute()
        syncStructureDataUseCase.execute()
        syncProductsUseCase.execute()
        syncInvestigationsMasterDataUseCase.execute()

        coroutineScope {
            uploadNewInvestigationsUseCase.execute(this).consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> {}
                        Status.SUCCESS -> {}
                        Status.ERROR -> {}
                    }
                }
            }
        }

        return Result.success()
    }

    companion object {
        const val UNIQUE_WORK_NAME = "SyncMasterDataWorker"
        const val PERIODIC_WORK_NAME = "SyncMasterDataWorkerPeriodic"
    }
}