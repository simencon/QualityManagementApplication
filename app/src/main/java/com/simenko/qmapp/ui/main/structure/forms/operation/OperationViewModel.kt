package com.simenko.qmapp.ui.main.structure.forms.operation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.LineIdParameter
import com.simenko.qmapp.di.OperationIdParameter
import com.simenko.qmapp.domain.FillInError
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccess
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation.DomainManufacturingOperationComplete
import com.simenko.qmapp.domain.entities.DomainOperationsFlow
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OperationViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    @LineIdParameter private val lineId: Int,
    @OperationIdParameter private val operationId: Int
) : ViewModel() {
    private val _operation = MutableStateFlow(DomainManufacturingOperationComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null
        private set

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (operationId == NoRecord.num) prepareOperation(lineId) else _operation.value = repository.operationById(operationId)
                mainPageHandler = MainPageHandler.Builder(if (operationId == NoRecord.num) Page.ADD_OPERATION else Page.EDIT_OPERATION, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { validateInput() }
                    .build()
            }
        }
    }

    private fun prepareOperation(lineId: Int) {
        _operation.value = DomainManufacturingOperationComplete(
            operation = DomainManufacturingOperation(lineId = lineId),
            lineComplete = repository.lineById(lineId)
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    private val _previousOperationsVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    fun setPreviousOperationVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _previousOperationsVisibility.value = _previousOperationsVisibility.value.setVisibility(dId, aId)
    }

    val operationComplete = _operation.flatMapLatest { operation ->
        _previousOperationsVisibility.flatMapLatest { visibility ->
            val previousOperations = operation.previousOperations.filter { !it.toBeDeleted }.map {
                it.copy(detailsVisibility = it.hashCode() == visibility.first.num, isExpanded = it.hashCode() == visibility.second.num)
            }
            flow { emit(operation.copy(previousOperations = previousOperations)) }
        }
    }

    fun setOperationOrder(it: Int) {
        _operation.value = _operation.value.copy(operation = _operation.value.operation.copy(operationOrder = it))
        _fillInErrors.value = _fillInErrors.value.copy(operationOrderError = false)
        _fillInState.value = FillInInitialState
    }

    fun setOperationAbbr(it: String) {
        _operation.value = _operation.value.copy(operation = _operation.value.operation.copy(operationAbbr = it))
        _fillInErrors.value = _fillInErrors.value.copy(operationAbbrError = false)
        _fillInState.value = FillInInitialState
    }

    fun setOperationDesignation(it: String) {
        _operation.value = _operation.value.copy(operation = _operation.value.operation.copy(operationDesignation = it))
        _fillInErrors.value = _fillInErrors.value.copy(operationDesignationError = false)
        _fillInState.value = FillInInitialState
    }

    fun setOperationEquipment(it: String) {
        _operation.value = _operation.value.copy(operation = _operation.value.operation.copy(equipment = it))
        _fillInErrors.value = _fillInErrors.value.copy(operationEquipmentError = false)
        _fillInState.value = FillInInitialState
    }

    private val _isAddPreviousOperationDialogVisible = MutableStateFlow(false)
    val isAddPreviousOperationDialogVisible = _isAddPreviousOperationDialogVisible.asStateFlow()
    fun setPreviousOperationDialogVisibility(value: Boolean) {
        _isAddPreviousOperationDialogVisible.value = value
    }

    fun addPreviousOperation(operationToAdd: DomainOperationsFlow.DomainOperationsFlowComplete) {
        val previousOperations = this._operation.value.previousOperations.toMutableList().also { it.add(operationToAdd) }
        this._operation.value = this._operation.value.copy(previousOperations = previousOperations)
    }

    fun deletePreviousOperation(id: Int) {
        val previousOperations = this._operation.value.previousOperations.toMutableList().also { list ->
            list.find { it.hashCode() == id }?.let { toBeDeleted ->
                list.remove(toBeDeleted)
                list.add(toBeDeleted.copy(toBeDeleted = true))
            }
        }
        this._operation.value = this._operation.value.copy(previousOperations = previousOperations)
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private val _fillInErrors = MutableStateFlow(FillInErrors())
    val fillInErrors get() = _fillInErrors.asStateFlow()
    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()
    private fun validateInput() {
        val errorMsg = buildString {
            if (_operation.value.operation.operationOrder == NoRecord.num) {
                _fillInErrors.value = _fillInErrors.value.copy(operationOrderError = true)
                append("Operation order field is mandatory\n")
            }
            if (_operation.value.operation.operationAbbr.isEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(operationAbbrError = true)
                append("Operation ID field is mandatory\n")
            }
            if (_operation.value.operation.operationDesignation.isEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(operationDesignationError = true)
                append("Operation complete name field is mandatory\n")
            }
            if (_operation.value.operation.equipment.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(operationEquipmentError = true)
                append("Operation equipment field is mandatory\n")
            }
            if (_operation.value.previousOperations.none { !it.toBeDeleted }) {
                _fillInErrors.value = _fillInErrors.value.copy(previousOperationsError = true)
                append("Operation must have at leas one previous operation\n")
            }
        }

        if (errorMsg.isNotEmpty()) _fillInState.value = FillInError(errorMsg) else _fillInState.value = FillInSuccess
    }

    fun makeRecord() = viewModelScope.launch {
        mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run { if (operationId == NoRecord.num) insertOperation(_operation.value.operation) else updateOperation(_operation.value.operation) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                        Status.SUCCESS -> insertOperationsFlows(resource.data?.id)
                        Status.ERROR -> {
                            mainPageHandler?.updateLoadingState?.invoke(Pair(true, resource.message))
                            _fillInState.value = FillInInitialState
                        }
                    }
                }
            }
        }
    }

    private fun insertOperationsFlows(id: Int?) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            id?.let { id ->
                val listToInsert = _operation.value.previousOperations.filter { it.id == NoRecord.num && !it.toBeDeleted }.map { it.toSimplestModel().copy(currentOperationId = id) }
                if (listToInsert.isNotEmpty())
                    repository.run {
                        insertOpFlows(listToInsert).consumeEach { event ->
                            event.getContentIfNotHandled()?.let { resource ->
                                when (resource.status) {
                                    Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                                    Status.SUCCESS -> deleteOperationsFlows(id)
                                    Status.ERROR -> {
                                        mainPageHandler?.updateLoadingState?.invoke(Pair(true, resource.message))
                                        _fillInState.value = FillInInitialState
                                    }
                                }
                            }
                        }
                    }
                else
                    deleteOperationsFlows(id)
            }
        }
    }

    private fun deleteOperationsFlows(id: Int?) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _operation.value.previousOperations.filter { it.toBeDeleted }.map { it.toSimplestModel() }.let { listToDelete ->
                if (listToDelete.isNotEmpty())
                    repository.run {
                        deleteOpFlows(listToDelete).consumeEach { event ->
                            event.getContentIfNotHandled()?.let { resource ->
                                when (resource.status) {
                                    Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                                    Status.SUCCESS -> navBackToRecord(id)
                                    Status.ERROR -> {
                                        mainPageHandler?.updateLoadingState?.invoke(Pair(true, resource.message))
                                        _fillInState.value = FillInInitialState
                                    }
                                }
                            }
                        }
                    }
                else
                    navBackToRecord(id)
            }
        }
    }

    private suspend fun navBackToRecord(id: Int?) {
        mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
        withContext(Dispatchers.Main) {
            id?.let {
                val depId = _operation.value.lineComplete.departmentId.toString()
                val subDepId = _operation.value.lineComplete.subDepartmentId.toString()
                val chId = _operation.value.lineComplete.channelId.toString()
                val lineId = _operation.value.lineComplete.id.toString()
                val opId = it.toString()
                appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.withOpts(depId, subDepId, chId, lineId, opId), popUpToRoute = Route.Main.CompanyStructure.route, inclusive = true)
            }
        }
    }
}

data class FillInErrors(
    var operationOrderError: Boolean = false,
    var operationAbbrError: Boolean = false,
    var operationDesignationError: Boolean = false,
    var operationEquipmentError: Boolean = false,
    var previousOperationsError: Boolean = false
)