package com.simenko.qmapp.domain.usecase

import com.simenko.qmapp.data.repository.ManufacturingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncStructureDataUseCase @Inject constructor(private val manufacturingRepository: ManufacturingRepository) {
    suspend fun execute() {
        manufacturingRepository.syncCompanies()
        manufacturingRepository.syncJobRoles()
        manufacturingRepository.syncDepartments()
        manufacturingRepository.syncSubDepartments()
        manufacturingRepository.syncChannels()
        manufacturingRepository.syncLines()
        manufacturingRepository.syncOperations()
        manufacturingRepository.syncOperationsFlows()
    }
}