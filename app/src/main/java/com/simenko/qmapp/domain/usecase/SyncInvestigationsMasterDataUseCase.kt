package com.simenko.qmapp.domain.usecase

import com.simenko.qmapp.data.repository.InvestigationsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncInvestigationsMasterDataUseCase @Inject constructor(private val repository: InvestigationsRepository,) {
    suspend fun execute() {
        repository.syncInputForOrder()
        repository.syncOrdersStatuses()
        repository.syncInvestigationReasons()
        repository.syncInvestigationTypes()
        repository.syncResultsDecryptions()
    }
}