package com.simenko.qmapp.domain.usecase.products

import com.simenko.qmapp.repository.ProductsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncProductsUseCase @Inject constructor(private val productsRepository: ProductsRepository) {
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

            syncProductLinesDepartments()

            syncProductKindsSubDepartments()
            syncComponentKindsSubDepartments()
            syncStageKindsSubDepartments()

            syncProductKeysChannels()
            syncComponentKeysChannels()
            syncStageKeysChannels()

            syncProductsToLines()
            syncComponentsToLines()
            syncComponentStagesToLines()

            syncCharacteristicsOperations()

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