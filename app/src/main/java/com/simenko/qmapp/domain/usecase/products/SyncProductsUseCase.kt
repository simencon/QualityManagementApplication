package com.simenko.qmapp.domain.usecase.products

import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncProductsUseCase @Inject constructor(private val productsRepository: ProductsRepository, private val manufacturingRepository: ManufacturingRepository) {
    suspend fun execute() {
        with(productsRepository) {
            syncProductLines()
            syncProductLineKeys()
            syncProductBases()
            syncCharacteristicGroups()
            syncCharacteristicSubGroups()
            syncCharacteristics()
            syncMetrics()
            syncVersionStatuses()

            syncProductKinds()
            syncComponentKinds()
            syncComponentStageKinds()

            syncProductKindsKeys()
            syncComponentKindsKeys()
            syncComponentStageKindsKeys()

            syncCharacteristicsProductKinds()
            syncCharacteristicsComponentKinds()
            syncCharacteristicsComponentStageKinds()

            syncProducts()
            syncComponents()
            syncComponentStages()

            manufacturingRepository.syncProductLinesDepartments()

            manufacturingRepository.syncProductKindsSubDepartments()
            manufacturingRepository.syncComponentKindsSubDepartments()
            manufacturingRepository.syncStageKindsSubDepartments()

            manufacturingRepository.syncProductKeysChannels()
            manufacturingRepository.syncComponentKeysChannels()
            manufacturingRepository.syncStageKeysChannels()

            manufacturingRepository.syncProductsToLines()
            manufacturingRepository.syncComponentsToLines()
            manufacturingRepository.syncComponentStagesToLines()

            manufacturingRepository.syncCharacteristicsOperations()

            syncProductKindsProducts()
            syncComponentKindsComponents()
            syncComponentStageKindsComponentStages()

            syncProductsComponents()
            syncComponentsComponentStages()

            syncProductVersions()
            syncComponentVersions()
            syncComponentStageVersions()

            syncProductTolerances()
            syncComponentTolerances()
            syncComponentStageTolerances()
        }
    }
}