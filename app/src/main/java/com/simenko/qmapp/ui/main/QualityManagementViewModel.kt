package com.simenko.qmapp.ui.main

import android.content.Context
import androidx.lifecycle.*
import com.simenko.qmapp.di.main.MainScope
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.repository.QualityManagementProductsRepository
import com.simenko.qmapp.room.implementation.getDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@MainScope
class QualityManagementViewModel @Inject constructor(
    context: Context
) : ViewModel() {
    /**
     * Gets data from Repositories - which is live data with list
     */

    private val roomDatabase = getDatabase(context)

    private val qualityManagementManufacturingRepository =
        QualityManagementManufacturingRepository(roomDatabase)
    private val qualityManagementProductsRepository =
        QualityManagementProductsRepository(roomDatabase)
    private val qualityManagementInvestigationsRepository =
        QualityManagementInvestigationsRepository(roomDatabase)
    val isLoadingInProgress = MutableLiveData(false)
    val isNetworkError = MutableLiveData(false)

    val pairedTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

    private val teamMembers = qualityManagementManufacturingRepository.teamMembers
    val teamMembersMediator: MediatorLiveData<Pair<List<DomainTeamMember>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainTeamMember>?, Boolean?>>().apply {
            addSource(teamMembers) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(teamMembers.value, it) }
        }

    fun changeTeamMembersDetailsVisibility(item: DomainTeamMember) {
        teamMembers.value?.find { it.id == item.id }?.let { member ->
            member.detailsVisibility = !member.detailsVisibility
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
        }
    }

    val departments = qualityManagementManufacturingRepository.departments
    val departmentsDetailed = qualityManagementManufacturingRepository.departmentsDetailed
    val subDepartments = qualityManagementManufacturingRepository.subDepartments
    val channels = qualityManagementManufacturingRepository.channels
    val lines = qualityManagementManufacturingRepository.lines
    val operations = qualityManagementManufacturingRepository.operations

    val inputForOrder = qualityManagementInvestigationsRepository.inputForOrder
    val investigationReasons = qualityManagementInvestigationsRepository.investigationReasons

    val currentOrder = MutableLiveData(0)

    private val completeOrders = qualityManagementInvestigationsRepository.completeOrders
    val completeOrdersMediator: MediatorLiveData<Pair<List<DomainOrderComplete>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainOrderComplete>?, Boolean?>>().apply {
            addSource(completeOrders) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(completeOrders.value, it) }
        }

    val itemVersionsCompleteP = qualityManagementProductsRepository.itemsVersionsCompleteP
    val itemVersionsCompleteC = qualityManagementProductsRepository.itemsVersionsCompleteC
    val itemVersionsCompleteS = qualityManagementProductsRepository.itemsVersionsCompleteS

    val currentSubOrder = MutableLiveData(0)

    private val completeSubOrders = qualityManagementInvestigationsRepository.completeSubOrders
    val completeSubOrdersMediator: MediatorLiveData<Pair<List<DomainSubOrderComplete>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainSubOrderComplete>?, Boolean?>>().apply {
            addSource(completeSubOrders) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(completeSubOrders.value, it) }
        }

    private val completeSubOrderTasks =
        qualityManagementInvestigationsRepository.completeSubOrderTasks
    val completeSubOrderTasksMediator: MediatorLiveData<Pair<List<DomainSubOrderTaskComplete>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainSubOrderTaskComplete>?, Boolean?>>().apply {
            addSource(completeSubOrderTasks) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(completeSubOrderTasks.value, it) }
        }

    val currentSubOrderTask = MutableLiveData(0)

    private val samples = qualityManagementInvestigationsRepository.samples
    val samplesMediator: MediatorLiveData<Pair<List<DomainSample>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainSample>?, Boolean?>>().apply {
            addSource(samples) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(samples.value, it) }
        }

    fun changeCompleteOrdersDetailsVisibility(itemId: Int) {
        changeCompleteSubOrdersDetailsVisibility(currentSubOrder.value ?: 0)
        changeCompleteSubOrderTasksDetailsVisibility(currentSubOrderTask.value ?: 0)

        var select = false

        completeOrders.value?.find { it.order.id == itemId }
            ?.let { it ->
                if (!it.detailsVisibility)
                    select = true
                else
                    currentOrder.value = 0
            }

        completeOrders.value?.forEach { it.detailsVisibility = false }

        if (select)
            completeOrders.value?.find { it.order.id == itemId }
                ?.let { order ->
                    currentOrder.value = itemId
                    order.detailsVisibility = !order.detailsVisibility
                    pairedTrigger.value = !(pairedTrigger.value as Boolean)
                }
        else
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
    }

    fun changeCompleteSubOrdersDetailsVisibility(itemId: Int) {

        changeCompleteSubOrderTasksDetailsVisibility(currentSubOrderTask.value ?: 0)

        var select = false

        completeSubOrders.value?.find { it.subOrder.id == itemId }
            ?.let { it ->
                if (!it.detailsVisibility)
                    select = true
                else
                    currentSubOrder.value = 0
            }

        completeSubOrders.value?.forEach { it.detailsVisibility = false }

        if (select)
            completeSubOrders.value?.find { it.subOrder.id == itemId }
                ?.let { subOrder ->
                    currentSubOrder.value = itemId
                    subOrder.detailsVisibility = !subOrder.detailsVisibility
                    pairedTrigger.value = !(pairedTrigger.value as Boolean)
                }
        else
            pairedTrigger.value = !(pairedTrigger.value as Boolean)

    }

    fun changeCompleteSubOrderTasksDetailsVisibility(itemId: Int) {
        var select = false

        completeSubOrderTasks.value?.find { it.subOrderTask.id == itemId }
            ?.let { it ->
                if (!it.measurementsVisibility)
                    select = true
                else
                    currentSubOrderTask.value = 0
            }

        completeSubOrderTasks.value?.forEach { it.measurementsVisibility = false }

        if (select)
            completeSubOrderTasks.value?.find { it.subOrderTask.id == itemId }
                ?.let { subOrderTask ->
                    subOrderTask.measurementsVisibility = !subOrderTask.measurementsVisibility
                    currentSubOrderTask.value = itemId
                    pairedTrigger.value = !(pairedTrigger.value as Boolean)
                }
        else
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
    }

    fun changeCompleteOrdersExpandState(item: DomainOrderComplete) {
        completeOrders.value?.find { it.order.id == item.order.id }?.let { order ->
            order.isExpanded = !order.isExpanded
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
        }
    }

    fun changeCompleteSubOrdersExpandState(item: DomainSubOrderComplete) {
        completeSubOrders.value?.find { it.subOrder.id == item.subOrder.id }?.let { subOrder ->
            subOrder.isExpanded = !subOrder.isExpanded
            pairedTrigger.value = !(pairedTrigger.value as Boolean)
        }
    }

    fun changeCompleteSubOrderTasksExpandState(item: DomainSubOrderTaskComplete) {
        completeSubOrderTasks.value?.find { it.subOrderTask.id == item.subOrderTask.id }
            ?.let { subOrderTask ->
                subOrderTask.isExpanded = !subOrderTask.isExpanded
                pairedTrigger.value = !(pairedTrigger.value as Boolean)
            }
    }

    fun changeSamplesIsSelectedState(item: DomainSample) {
        samples.value?.forEach { it.isSelected = false }
        samples.value?.find { it.id == item.id }
            ?.let { subOrderTask ->
                subOrderTask.isSelected = !subOrderTask.isSelected
                pairedTrigger.value = !(pairedTrigger.value as Boolean)
            }
    }
    /**
     *
     */
    var isStatusDialogVisible = MutableLiveData(false)

    val investigationStatuses = qualityManagementInvestigationsRepository.investigationStatuses
    val investigationStatusesMediator: MediatorLiveData<Pair<List<DomainOrdersStatus>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainOrdersStatus>?, Boolean?>>().apply {
            addSource(investigationStatuses) { value = Pair(it, pairedTrigger.value) }
            addSource(pairedTrigger) { value = Pair(investigationStatuses.value, it) }
        }
    /**
     *
     */
    fun onNetworkErrorShown() {
        isLoadingInProgress.value = false
        isNetworkError.value = false
    }
    /**
     *
     */
    fun refreshOrdersFromRepository() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                qualityManagementInvestigationsRepository.refreshOrders()
                qualityManagementInvestigationsRepository.refreshSubOrders()
                qualityManagementInvestigationsRepository.refreshSubOrderTasks()
                qualityManagementInvestigationsRepository.refreshSamples()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    private fun refreshSubOrdersFromRepository() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                qualityManagementInvestigationsRepository.refreshSubOrders()
                qualityManagementInvestigationsRepository.refreshSubOrderTasks()
                qualityManagementInvestigationsRepository.refreshSamples()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun deleteOrder(order: DomainOrderComplete) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                qualityManagementInvestigationsRepository.deleteOrder(order.order)
                refreshOrdersFromRepository()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun deleteSubOrder(subOrder: DomainSubOrderComplete) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                qualityManagementInvestigationsRepository.deleteSubOrder(subOrder.subOrder)
                refreshSubOrdersFromRepository()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                qualityManagementManufacturingRepository.refreshPositionLevels()
                qualityManagementManufacturingRepository.refreshTeamMembers()
                qualityManagementManufacturingRepository.refreshCompanies()
                qualityManagementManufacturingRepository.refreshDepartments()
                qualityManagementManufacturingRepository.refreshSubDepartments()
                qualityManagementManufacturingRepository.refreshManufacturingChannels()
                qualityManagementManufacturingRepository.refreshManufacturingLines()
                qualityManagementManufacturingRepository.refreshManufacturingOperations()
                qualityManagementManufacturingRepository.refreshOperationsFlows()

                qualityManagementProductsRepository.refreshElementIshModels()
                qualityManagementProductsRepository.refreshIshSubCharacteristics()
                qualityManagementProductsRepository.refreshManufacturingProjects()
                qualityManagementProductsRepository.refreshCharacteristics()
                qualityManagementProductsRepository.refreshMetrixes()
                qualityManagementProductsRepository.refreshKeys()
                qualityManagementProductsRepository.refreshProductBases()
                qualityManagementProductsRepository.refreshProducts()
                qualityManagementProductsRepository.refreshComponents()
                qualityManagementProductsRepository.refreshComponentInStages()
                qualityManagementProductsRepository.refreshVersionStatuses()
                qualityManagementProductsRepository.refreshProductVersions()
                qualityManagementProductsRepository.refreshComponentVersions()
                qualityManagementProductsRepository.refreshComponentInStageVersions()
                qualityManagementProductsRepository.refreshProductTolerances()
                qualityManagementProductsRepository.refreshComponentTolerances()
                qualityManagementProductsRepository.refreshComponentInStageTolerances()
                qualityManagementProductsRepository.refreshProductsToLines()
                qualityManagementProductsRepository.refreshComponentsToLines()
                qualityManagementProductsRepository.refreshComponentInStagesToLines()

                qualityManagementInvestigationsRepository.refreshInputForOrder()
                qualityManagementInvestigationsRepository.refreshOrdersStatuses()
                qualityManagementInvestigationsRepository.refreshInvestigationReasons()
                qualityManagementInvestigationsRepository.refreshInvestigationTypes()
                qualityManagementInvestigationsRepository.refreshOrders()
                qualityManagementInvestigationsRepository.refreshSubOrders()
                qualityManagementInvestigationsRepository.refreshSubOrderTasks()
                qualityManagementInvestigationsRepository.refreshSamples()
                qualityManagementInvestigationsRepository.refreshResultsDecryptions()
                qualityManagementInvestigationsRepository.refreshResults()
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }
}