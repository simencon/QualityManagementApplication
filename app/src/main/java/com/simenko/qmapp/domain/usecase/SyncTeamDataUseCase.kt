package com.simenko.qmapp.domain.usecase

import com.simenko.qmapp.data.repository.ManufacturingRepository
import com.simenko.qmapp.data.repository.SystemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncTeamDataUseCase @Inject constructor(private val systemRepository: SystemRepository, private val manufacturingRepository: ManufacturingRepository) {
    suspend fun execute() {
        systemRepository.syncUserRoles()
        systemRepository.syncUsers()
        manufacturingRepository.syncTeamMembers()
    }
}