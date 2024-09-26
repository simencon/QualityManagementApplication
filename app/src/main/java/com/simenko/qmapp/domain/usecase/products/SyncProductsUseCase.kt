package com.simenko.qmapp.domain.usecase.products

import com.simenko.qmapp.repository.ProductsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncProductsUseCase @Inject constructor( private val productsRepository: ProductsRepository) {
    suspend fun execute() {
        productsRepository.syncProductLines()
        productsRepository.syncProductLineKeys()
        productsRepository.syncProductBases()
        productsRepository.syncCharacteristicGroups()
        productsRepository.syncCharacteristicSubGroups()
        productsRepository.syncCharacteristics()
        productsRepository.syncMetrics()
        productsRepository.syncVersionStatuses()

        productsRepository.syncProductKinds()
        productsRepository.syncComponentKinds()
        productsRepository.syncComponentStageKinds()

        productsRepository.syncProductKindsKeys()
        productsRepository.syncComponentKindsKeys()
        productsRepository.syncComponentStageKindsKeys()

        productsRepository.syncCharacteristicsProductKinds()
        productsRepository.syncCharacteristicsComponentKinds()
        productsRepository.syncCharacteristicsComponentStageKinds()

        productsRepository.syncProducts()
        productsRepository.syncComponents()
        productsRepository.syncComponentStages()

        productsRepository.syncProductsToLines()
        productsRepository.syncComponentsToLines()
        productsRepository.syncComponentStagesToLines()

        productsRepository.syncProductKindsProducts()
        productsRepository.syncComponentKindsComponents()
        productsRepository.syncComponentStageKindsComponentStages()

        productsRepository.syncProductsComponents()
        productsRepository.syncComponentsComponentStages()

        productsRepository.syncProductVersions()
        productsRepository.syncComponentVersions()
        productsRepository.syncComponentStageVersions()

        productsRepository.syncProductTolerances()
        productsRepository.syncComponentTolerances()
        productsRepository.syncComponentStageTolerances()
    }
}