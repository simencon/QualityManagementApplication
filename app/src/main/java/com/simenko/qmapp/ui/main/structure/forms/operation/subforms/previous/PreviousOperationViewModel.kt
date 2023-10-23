package com.simenko.qmapp.ui.main.structure.forms.operation.subforms.previous

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.domain.FillInError
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccess
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation.DomainManufacturingOperationComplete
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn

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

    fun clearOperationToAddErrors() {
        _operationToAddErrors.value = FillInErrors()
        _fillInState.value = FillInInitialState
    }

    private val _previousOperations = MutableStateFlow(listOf<DomainOperationsFlowComplete>())
    fun setOperationWithFlow(operation: DomainManufacturingOperationComplete) {
        this._previousOperations.value = operation.previousOperations
        this._operationToAdd.value = DomainOperationsFlowComplete().copy(
            currentOperationId = operation.operation.id,
            depId = operation.lineComplete.departmentId,
            subDepId = operation.lineComplete.subDepartmentId,
            channelId = operation.lineComplete.channelId,
            lineId = operation.lineComplete.id
        )
    }

    private val _operations = repository.operationsComplete.flatMapLatest { operations ->
        _previousOperations.flatMapLatest { addedOperations ->
            _operationToAdd.flatMapLatest { toAdd ->
                flow {
                    emit(operations.filter { operation -> operation.operation.id !in addedOperations.map { it.previousOperationId } && operation.operation.id != toAdd.currentOperationId }
                        .toList())
                }
            }
        }
    }.flowOn(Dispatchers.Default)

    /**
     * (recId, name, isSelected)
     * */
    val selectDepId: (Int) -> Unit = {
        if (it != _operationToAdd.value.depId) {
            _operationToAdd.value = _operationToAdd.value.copy(previousOperationId = NoRecord.num, lineId = NoRecord.num, channelId = NoRecord.num, subDepId = NoRecord.num, depId = it)

            _operationToAddErrors.value = _operationToAddErrors.value.copy(departmentError = false)
            _fillInState.value = FillInInitialState
        }
    }
    val availableDepartments: Flow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _operationToAdd.flatMapLatest { record ->
            flow {
                emit(
                    operations.asSequence()
                        .map { Triple(it.lineComplete.departmentId, it.lineComplete.depAbbr ?: NoString.str, it.lineComplete.depOrder) }.toSet().sortedBy { it.third }.map {
                            Triple(it.first, it.second, it.first == record.depId)
                        }.toList()
                )
            }
        }
    }.flowOn(Dispatchers.Default)

    val selectSubDepId: (Int) -> Unit = {
        if (it != _operationToAdd.value.subDepId) {
            _operationToAdd.value = _operationToAdd.value.copy(previousOperationId = NoRecord.num, lineId = NoRecord.num, channelId = NoRecord.num, subDepId = it)

            _operationToAddErrors.value = _operationToAddErrors.value.copy(subDepartmentError = false)
            _fillInState.value = FillInInitialState
        }
    }
    val availableSubDepartments: Flow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _operationToAdd.flatMapLatest { record ->
            flow {
                emit(
                    operations.asSequence().filter { it.lineComplete.departmentId == record.depId }
                        .map { Triple(it.lineComplete.subDepartmentId, it.lineComplete.subDepAbbr ?: NoString.str, it.lineComplete.subDepOrder) }.toSet().sortedBy { it.third }.map {
                            Triple(it.first, it.second, it.first == record.subDepId)
                        }.toList()
                )
            }
        }
    }.flowOn(Dispatchers.Default)

    val selectedChannelId: (Int) -> Unit = {
        if (it != _operationToAdd.value.channelId) {
            _operationToAdd.value = _operationToAdd.value.copy(previousOperationId = NoRecord.num, lineId = NoRecord.num, channelId = it)

            _operationToAddErrors.value = _operationToAddErrors.value.copy(channelError = false)
            _fillInState.value = FillInInitialState
        }
    }
    val availableChannels: Flow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _operationToAdd.flatMapLatest { record ->
            flow {
                emit(
                    operations.asSequence().filter { it.lineComplete.subDepartmentId == record.subDepId }
                        .map { Triple(it.lineComplete.channelId, it.lineComplete.channelAbbr ?: NoString.str, it.lineComplete.channelOrder) }.toSet().sortedBy { it.third }.map {
                            Triple(it.first, it.second, it.first == record.channelId)
                        }.toList()
                )
            }
        }
    }.flowOn(Dispatchers.Default)

    val selectedLineId: (Int) -> Unit = {
        if (it != _operationToAdd.value.lineId) {
            _operationToAdd.value = _operationToAdd.value.copy(previousOperationId = NoRecord.num, lineId = it)

            _operationToAddErrors.value = _operationToAddErrors.value.copy(lineError = false)
            _fillInState.value = FillInInitialState
        }
    }
    val availableLines: Flow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _operationToAdd.flatMapLatest { record ->
            flow {
                emit(
                    operations.asSequence().filter { it.lineComplete.channelId == record.channelId }
                        .map { Triple(it.lineComplete.id, it.lineComplete.lineAbbr ?: NoString.str, it.lineComplete.lineOrder) }.toSet().sortedBy { it.third }.map {
                            Triple(it.first, it.second, it.first == record.lineId)
                        }.toList()
                )
            }
        }
    }.flowOn(Dispatchers.Default)

    val selectedOperationId: (Int) -> Unit = {
        if (it != _operationToAdd.value.previousOperationId) {
            _operationToAdd.value = _operationToAdd.value.copy(previousOperationId = it)

            _operationToAddErrors.value = _operationToAddErrors.value.copy(operationError = false)
            _fillInState.value = FillInInitialState
        }
    }
    val availableOperations: Flow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _operationToAdd.flatMapLatest { record ->
            flow {
                emit(
                    operations.filter { it.lineComplete.id == record.lineId }.toSet().sortedBy { it.operation.operationOrder }.map {
                        if (it.operation.id == record.previousOperationId) _operationToAdd.value = record.copy(
                            depAbbr = it.lineComplete.depAbbr,
                            subDepAbbr = it.lineComplete.subDepAbbr,
                            channelAbbr = it.lineComplete.channelAbbr,
                            lineAbbr = it.lineComplete.lineAbbr,
                            operationAbbr = it.operation.operationAbbr,
                            operationDesignation = it.operation.operationDesignation,
                            equipment = it.operation.equipment
                        )
                        Triple(it.operation.id, concatTwoStrings1(it.operation.equipment, it.operation.operationAbbr), it.operation.id == record.previousOperationId)
                    }
                )
            }
        }
    }.flowOn(Dispatchers.Default)

    fun validateInput() {
        val errorMsg = buildString {
            if (_operationToAdd.value.depId == NoRecord.num) {
                _operationToAddErrors.value = _operationToAddErrors.value.copy(departmentError = true)
                append("Department is mandatory\n")
            }
            if (_operationToAdd.value.subDepId == NoRecord.num) {
                _operationToAddErrors.value = _operationToAddErrors.value.copy(subDepartmentError = true)
                append("Sub department is mandatory\n")
            }
            if (_operationToAdd.value.channelId == NoRecord.num) {
                _operationToAddErrors.value = _operationToAddErrors.value.copy(channelError = true)
                append("Channel is mandatory\n")
            }
            if (_operationToAdd.value.lineId == NoRecord.num) {
                _operationToAddErrors.value = _operationToAddErrors.value.copy(lineError = true)
                append("Line is mandatory\n")
            }
            if (_operationToAdd.value.previousOperationId == NoRecord.num) {
                _operationToAddErrors.value = _operationToAddErrors.value.copy(operationError = true)
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