package com.simenko.qmapp.domain.usecase

import com.simenko.qmapp.data.repository.SystemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckIfInitialStateUseCase @Inject constructor(private val systemRepository: SystemRepository) {
    suspend fun execute(): Boolean {
        return systemRepository.checkIfInitialState()
    }
}