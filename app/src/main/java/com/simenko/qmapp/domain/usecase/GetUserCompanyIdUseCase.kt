package com.simenko.qmapp.domain.usecase

import com.simenko.qmapp.data.repository.ManufacturingRepository
import com.simenko.qmapp.data.repository.UserRepository
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserCompanyIdUseCase @Inject constructor(private val userRepository: UserRepository, private val manufacturingRepository: ManufacturingRepository) {
    suspend fun execute(): ID {
        return manufacturingRepository.companyByName(userRepository.profile.company)?.id ?: NoRecord.num
    }
}