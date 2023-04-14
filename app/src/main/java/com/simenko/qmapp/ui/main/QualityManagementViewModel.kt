package com.simenko.qmapp.ui.main

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import com.simenko.qmapp.di.main.MainScope
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.repository.QualityManagementProductsRepository
import com.simenko.qmapp.room.implementation.getDatabase
import com.simenko.qmapp.ui.common.DialogFor
import com.simenko.qmapp.ui.common.DialogInput
import com.simenko.qmapp.ui.main.investigations.InvestigationsFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import java.io.IOException
import javax.inject.Inject

private const val TAG = "QualityManagementViewMo"

@MainScope
class QualityManagementViewModel @Inject constructor(
    context: Context
) : ViewModel() {
    /**
     * Gets data from Repositories - which is live data with list
     */

    private val roomDatabase = getDatabase(context)

    private val manufacturingRepository =
        QualityManagementManufacturingRepository(roomDatabase)
    private val productsRepository =
        QualityManagementProductsRepository(roomDatabase)
    private val investigationsRepository =
        QualityManagementInvestigationsRepository(roomDatabase)
    val isLoadingInProgress = MutableLiveData(false)
    val isNetworkError = MutableLiveData(false)

    val pairedOrderTrigger: MutableLiveData<Boolean> = MutableLiveData(true)
    val pairedSubOrderTrigger: MutableLiveData<Boolean> = MutableLiveData(true)
    val pairedTaskTrigger: MutableLiveData<Boolean> = MutableLiveData(true)

    val team = manufacturingRepository.teamComplete

    val departments = manufacturingRepository.departments
    val departmentsDetailed = manufacturingRepository.departmentsDetailed
    val subDepartments = manufacturingRepository.subDepartments
    val channels = manufacturingRepository.channels
    val lines = manufacturingRepository.lines
    val operations = manufacturingRepository.operations

    val inputForOrder = investigationsRepository.inputForOrder
    val investigationReasons = investigationsRepository.investigationReasons

    /**
     * Filters
     * */
    var currentTitle = MutableLiveData("")

    /**
     * Filters
     * */

    val currentOrder = MutableLiveData(0)

    val orders = investigationsRepository.completeOrders

    val currentSubOrder = MutableLiveData(0)

    val completeSubOrders = investigationsRepository.completeSubOrders

    val completeTasks = investigationsRepository.completeSubOrderTasks

    val completeSamples = investigationsRepository.completeSamples

    /**
     *
     */
    var isStatusDialogVisible = MutableLiveData(false)

    val dialogInput = MutableLiveData(DialogInput(0, DialogFor.ORDER, null))

    val investigationStatuses = investigationsRepository.investigationStatuses
    val investigationStatusesMediator: MediatorLiveData<Pair<List<DomainOrdersStatus>?, Boolean?>> =
        MediatorLiveData<Pair<List<DomainOrdersStatus>?, Boolean?>>().apply {
            addSource(investigationStatuses) { value = Pair(it, pairedOrderTrigger.value) }
            addSource(pairedOrderTrigger) { value = Pair(investigationStatuses.value, it) }
        }

    val productTolerances = productsRepository.productTolerances
    val componentTolerances = productsRepository.componentTolerances
    val componentInStageTolerances = productsRepository.componentInStageTolerances

    val itemsTolerances = productsRepository.itemsTolerances

    val metrixes = productsRepository.metrixes

    fun statusDialog(recordId: Int, dialogFor: DialogFor, performerId: Int?) {
        dialogInput.value = DialogInput(recordId, dialogFor, performerId)
        isStatusDialogVisible.value = true
    }
    fun editSubOrder(subOrder: DomainSubOrder) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {

                    completeTasks.value!!
                        .filter { it.subOrderTask.subOrderId == subOrder.id }
                        .map { it.subOrderTask }
                        .forEach {
                            it.statusId = subOrder.statusId
                            it.completedById = subOrder.completedById
                            editTask(it, this)
                        }

                    syncSubOrder(subOrder)

                    val order = orders.value!!.find {
                        it.order.id == subOrder.orderId
                    }!!.order
                    syncOrder(order)
                }
                pairedOrderTrigger.value = !pairedOrderTrigger.value!!
                pairedSubOrderTrigger.value = !pairedSubOrderTrigger.value!!
                isStatusDialogVisible.value = false
                isNetworkError.value = false
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun editSubOrderTask(subOrderTask: DomainSubOrderTask) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {

                    editTask(subOrderTask, this)

                    val subOrder = completeSubOrders.value!!
                        .find { it.subOrder.id == subOrderTask.subOrderId }!!.subOrder
                    syncSubOrder(subOrder)

                    val order = orders.value!!
                        .find { it.order.id == subOrder.orderId }!!.order
                    syncOrder(order)
                }
                pairedOrderTrigger.value = !pairedOrderTrigger.value!!
                pairedSubOrderTrigger.value = !pairedSubOrderTrigger.value!!
                pairedTaskTrigger.value = !pairedTaskTrigger.value!!
                isStatusDialogVisible.value = false
                isNetworkError.value = false
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    private suspend fun editTask(subOrderTask: DomainSubOrderTask, coroutineScope: CoroutineScope) {
        val listOfResults: MutableList<DomainResult> = mutableListOf()

        /**
         * 1.Get latest status task
         * 2.Compare with new status
         * 3.If change is "To Do"/"Rejected" -> "Done" = Collect/Post new results and change status
         * 4.If change is "Done" -> "To Do" = Delete all results
         * 5.If change is "Done" -> "Rejected" = Do nothing, just change the status
         * 6.If change is "To Do" <-> "Rejected" = Do nothing, just change the status
         * */
        val channel1 = investigationsRepository.getRecord(
            coroutineScope,
            subOrderTask
        )
        channel1.consumeEach {
            if (it.statusId == 1 || it.statusId == 4) {
                if (subOrderTask.statusId == 3)
                /**
                 * Collect/Post new results and change status
                 * */
                {
                    val subOrder =
                        completeSubOrders.value?.find { sIt -> sIt.subOrder.id == subOrderTask.subOrderId }?.subOrder!!
                    val metrixesToRecord: List<DomainMetrix?>? =
                        when (subOrder.itemPreffix.substring(0, 1)) {
                            "p" -> {
                                productTolerances.value?.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    ?.map { pfIt -> metrixes.value?.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                            "c" -> {
                                componentTolerances.value?.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    ?.map { pfIt -> metrixes.value?.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                            "s" -> {
                                componentInStageTolerances.value?.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    ?.map { pfIt -> metrixes.value?.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                            else -> {
                                componentTolerances.value?.filter { pIt -> pIt.versionId == subOrder.itemVersionId && pIt.isActual }
                                    ?.map { pfIt -> metrixes.value?.findLast { mIt -> mIt.id == pfIt.metrixId && mIt.charId == subOrderTask.charId } }
                            }
                        }

                    completeSamples.value?.filter { sIt -> sIt.sample.subOrderId == subOrder.id }
                        ?.distinctBy { sfIt -> sfIt.sample.id }?.forEach { sdIt ->
                            metrixesToRecord?.forEach { mIt ->
                                if (mIt != null) {
                                    listOfResults.add(
                                        DomainResult(
                                            id = 0,
                                            sampleId = sdIt.sample.id,
                                            metrixId = mIt.id,
                                            result = null,
                                            isOk = true,
                                            resultDecryptionId = 1,
                                            taskId = subOrderTask.id
                                        )
                                    )
                                }
                            }
                        }

                    listOfResults.forEach { dResult ->
                        val channel3 =
                            investigationsRepository.getCreatedRecord(
                                coroutineScope,
                                dResult
                            )
                        channel3.consumeEach { }
                    }

                    /*val channel3 =
                        investigationsRepository.getCreatedRecords(
                            coroutineScope,
                            listOfResults
                            )
                    channel3.consumeEach {
                        nResultsIt.forEach { }
                    }*/
                }
            } else if (it.statusId == 3) {
                if (subOrderTask.statusId == 1) {
                    /**
                     * Delete all results and change status
                     * */
                    deleteResultsBasedOnTask(subOrderTask)
                }
            }
        }

        val channel2 = investigationsRepository.updateRecord(
            coroutineScope,
            subOrderTask
        )
        channel2.consumeEach {
        }
    }

    private fun syncOrder(order: DomainOrder) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val channel = investigationsRepository.getRecord(
                        this,
                        order
                    )
                    channel.consumeEach { }
                }
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    private fun syncSubOrder(subOrder: DomainSubOrder) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val channel = investigationsRepository.getRecord(
                        this,
                        subOrder
                    )
                    channel.consumeEach { }
                }
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun onNetworkErrorShown() {
        isLoadingInProgress.value = false
        isNetworkError.value = false
    }

    /**
     *
     */

    fun syncResults() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                investigationsRepository.refreshResults()

                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun deleteResultsBasedOnTask(task: DomainSubOrderTask) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                investigationsRepository.deleteResults(charId = task.id)
                syncResults()

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

                manufacturingRepository.refreshPositionLevels()
                manufacturingRepository.refreshTeamMembers()
                manufacturingRepository.refreshCompanies()
                manufacturingRepository.refreshDepartments()
                manufacturingRepository.refreshSubDepartments()
                manufacturingRepository.refreshManufacturingChannels()
                manufacturingRepository.refreshManufacturingLines()
                manufacturingRepository.refreshManufacturingOperations()
                manufacturingRepository.refreshOperationsFlows()

                productsRepository.refreshElementIshModels()
                productsRepository.refreshIshSubCharacteristics()
                productsRepository.refreshManufacturingProjects()
                productsRepository.refreshCharacteristics()
                productsRepository.refreshMetrixes()
                productsRepository.refreshKeys()
                productsRepository.refreshProductBases()
                productsRepository.refreshProducts()
                productsRepository.refreshComponents()
                productsRepository.refreshComponentInStages()
                productsRepository.refreshVersionStatuses()
                productsRepository.refreshProductVersions()
                productsRepository.refreshComponentVersions()
                productsRepository.refreshComponentInStageVersions()
                productsRepository.refreshProductTolerances()
                productsRepository.refreshComponentTolerances()
                productsRepository.refreshComponentInStageTolerances()
                productsRepository.refreshProductsToLines()
                productsRepository.refreshComponentsToLines()
                productsRepository.refreshComponentInStagesToLines()

                investigationsRepository.refreshInputForOrder()
                investigationsRepository.refreshOrdersStatuses()
                investigationsRepository.refreshInvestigationReasons()
                investigationsRepository.refreshInvestigationTypes()
                investigationsRepository.refreshOrders()
                investigationsRepository.refreshSubOrders()
                investigationsRepository.refreshSubOrderTasks()
                investigationsRepository.refreshSamples()
                investigationsRepository.refreshResultsDecryptions()
                investigationsRepository.refreshResults()
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }
}