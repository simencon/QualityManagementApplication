package com.simenko.qmapp.ui.main.structure.forms.operation.subforms.previous_operation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInError
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccess
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.DomainOperationsFlow.DomainOperationsFlowComplete
import com.simenko.qmapp.repository.ManufacturingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PreviousOperationViewModel @Inject constructor(
    repository: ManufacturingRepository
) : ViewModel() {
    private val _operationToAdd = MutableStateFlow(DomainOperationsFlowComplete())
    val operationToAdd get() = _operationToAdd.asStateFlow()

    private val _operationToAddErrors = MutableStateFlow(FillInErrors())
    val operationToAddErrors get() = _operationToAddErrors.asStateFlow()

    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()
    fun clearOperationToAdd() {
        _operationToAdd.value = DomainOperationsFlowComplete()
        _selectedDepId.value = NoRecord.num
        _selectedSubDepId.value = NoRecord.num
        _selectedChannelId.value = NoRecord.num
        _selectedLineId.value = NoRecord.num
        _selectedOperationId.value = NoRecord.num
    }

    fun clearOperationToAddErrors() {
        _operationToAddErrors.value = FillInErrors()
        _fillInState.value = FillInInitialState
    }

    private val _operationWithFlow = MutableStateFlow(Pair(NoRecord.num, listOf<DomainOperationsFlowComplete>()))
    fun setOperationWithFlow(operationWithFlow: Pair<Int, List<DomainOperationsFlowComplete>>) {
        this._operationWithFlow.value = operationWithFlow
    }

    private val _operations = repository.operationsComplete.flatMapLatest { operations ->
        _operationWithFlow.flatMapLatest { addedOperations ->
            flow { emit(operations.filter { operation -> operation.operation.id !in addedOperations.second.map { it.previousOperationId } }.toList()) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    /**
     * (recId, name, isSelected)
     * */
    private val _selectedDepId = MutableStateFlow(NoRecord.num)
    val selectDepId: (Int) -> Unit = {
        if (it != _selectedDepId.value) {
            _selectedDepId.value = it

            _selectedOperationId.value = NoRecord.num
            _selectedLineId.value = NoRecord.num
            _selectedChannelId.value = NoRecord.num
            _selectedSubDepId.value = NoRecord.num

            _operationToAddErrors.value = _operationToAddErrors.value.copy(departmentError = false)
            _fillInState.value = FillInInitialState
        }
    }
    val availableDepartments: StateFlow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _selectedDepId.flatMapLatest { recId ->
            flow {
                emit(
                    operations.map { Triple(it.lineComplete.departmentId, it.lineComplete.depAbbr ?: NoString.str, it.lineComplete.channelOrder) }.toSet().sortedBy { it.third }.map {
                        Triple(it.first, it.second, it.first == recId)
                    }
                )
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _selectedSubDepId = MutableStateFlow(NoRecord.num)
    val selectSubDepId: (Int) -> Unit = {
        if (it != _selectedSubDepId.value) {
            _selectedSubDepId.value = it

            _selectedOperationId.value = NoRecord.num
            _selectedLineId.value = NoRecord.num
            _selectedChannelId.value = NoRecord.num

            _operationToAddErrors.value = _operationToAddErrors.value.copy(subDepartmentError = false)
            _fillInState.value = FillInInitialState
        }
    }
    val availableSubDepartments: StateFlow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _selectedDepId.flatMapLatest { parentId ->
            _selectedSubDepId.flatMapLatest { recId ->
                flow {
                    emit(
                        operations.filter { it.lineComplete.departmentId == parentId }.map { Pair(it.lineComplete.subDepartmentId, it.lineComplete.subDepAbbr ?: NoString.str) }.toSet().map {
                            Triple(it.first, it.second, it.first == recId)
                        }
                    )
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _selectedChannelId = MutableStateFlow(NoRecord.num)
    val selectedChannelId: (Int) -> Unit = {
        if (it != _selectedChannelId.value) {
            _selectedChannelId.value = it

            _selectedOperationId.value = NoRecord.num
            _selectedLineId.value = NoRecord.num

            _operationToAddErrors.value = _operationToAddErrors.value.copy(channelError = false)
            _fillInState.value = FillInInitialState
        }
    }
    val availableChannels: StateFlow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _selectedSubDepId.flatMapLatest { parentId ->
            _selectedChannelId.flatMapLatest { recId ->
                flow {
                    emit(
                        operations.filter { it.lineComplete.subDepartmentId == parentId }.map { Pair(it.lineComplete.channelId, it.lineComplete.channelAbbr ?: NoString.str) }.toSet().map {
                            Triple(it.first, it.second, it.first == recId)
                        }
                    )
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _selectedLineId = MutableStateFlow(NoRecord.num)
    val selectedLineId: (Int) -> Unit = {
        if (it != _selectedLineId.value) {
            _selectedLineId.value = it

            _selectedOperationId.value = NoRecord.num

            _operationToAddErrors.value = _operationToAddErrors.value.copy(lineError = false)
            _fillInState.value = FillInInitialState
        }
    }
    val availableLines: StateFlow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _selectedChannelId.flatMapLatest { parentId ->
            _selectedLineId.flatMapLatest { recId ->
                flow {
                    emit(
                        operations.filter { it.lineComplete.channelId == parentId }.map { Pair(it.lineComplete.id, it.lineComplete.lineAbbr ?: NoString.str) }.toSet().map {
                            Triple(it.first, it.second, it.first == recId)
                        }
                    )
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _selectedOperationId = MutableStateFlow(NoRecord.num)
    val selectedOperationId: (Int) -> Unit = {
        if (it != _selectedOperationId.value) {
            _selectedOperationId.value = it

            _operationToAddErrors.value = _operationToAddErrors.value.copy(operationError = false)
            _fillInState.value = FillInInitialState
        }
    }
    val availableOperations: StateFlow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _selectedLineId.flatMapLatest { parentId ->
            _selectedOperationId.flatMapLatest { recId ->
                _operationWithFlow.flatMapLatest { operationWithFlow ->
                    flow {
                        emit(
                            operations.filter { it.lineComplete.id == parentId }.toSet().map {
                                if (it.operation.id == recId) _operationToAdd.value = DomainOperationsFlowComplete().copy(
                                    currentOperationId = operationWithFlow.first,
                                    previousOperationId = recId,
                                    depAbbr = it.lineComplete.depAbbr,
                                    subDepAbbr = it.lineComplete.subDepAbbr,
                                    channelAbbr = it.lineComplete.channelAbbr,
                                    lineAbbr = it.lineComplete.lineAbbr,
                                    operationAbbr = it.operation.operationAbbr,
                                    operationDesignation = it.operation.operationDesignation,
                                    equipment = it.operation.equipment
                                )
                                Triple(it.operation.id, concatTwoStrings1(it.operation.equipment, it.operation.operationAbbr), it.operation.id == recId)
                            }
                        )
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun validateInput() {
        val errorMsg = buildString {
            if (_selectedDepId.value == NoRecord.num) {
                _operationToAddErrors.value = _operationToAddErrors.value.copy(departmentError  = true)
                append("Department is mandatory\n")
            }
            if (_selectedSubDepId.value == NoRecord.num) {
                _operationToAddErrors.value = _operationToAddErrors.value.copy(subDepartmentError  = true)
                append("Sub department is mandatory\n")
            }
            if (_selectedChannelId.value == NoRecord.num) {
                _operationToAddErrors.value = _operationToAddErrors.value.copy(channelError  = true)
                append("Channel is mandatory\n")
            }
            if (_selectedLineId.value == NoRecord.num) {
                _operationToAddErrors.value = _operationToAddErrors.value.copy(lineError  = true)
                append("Line is mandatory\n")
            }
            if (_selectedOperationId.value == NoRecord.num) {
                _operationToAddErrors.value = _operationToAddErrors.value.copy(operationError  = true)
                append("Operation is mandatory\n")
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInError(errorMsg)
        else _fillInState.value = FillInSuccess
    }
}

data class FillInErrors(
    var departmentError: Boolean = false,
    var subDepartmentError: Boolean = false,
    var channelError: Boolean = false,
    var lineError: Boolean = false,
    var operationError: Boolean = false
)