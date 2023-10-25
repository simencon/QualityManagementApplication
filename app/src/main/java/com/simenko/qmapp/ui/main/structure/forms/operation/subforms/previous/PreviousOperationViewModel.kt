package com.simenko.qmapp.ui.main.structure.forms.operation.subforms.previous

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
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
            depId = operation.lineWithParents.departmentId,
            subDepId = operation.lineWithParents.subDepartmentId,
            channelId = operation.lineWithParents.channelId,
            lineId = operation.lineWithParents.id
        )
    }

    private val _operations = repository.operations(NoRecord.num).flatMapLatest { operations ->
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
                        .map { Triple(it.lineWithParents.departmentId, it.lineWithParents.depAbbr ?: NoString.str, it.lineWithParents.depOrder) }.toSet().sortedBy { it.third }.map {
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
                    operations.asSequence().filter { it.lineWithParents.departmentId == record.depId }
                        .map { Triple(it.lineWithParents.subDepartmentId, it.lineWithParents.subDepAbbr ?: NoString.str, it.lineWithParents.subDepOrder) }.toSet().sortedBy { it.third }.map {
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
                    operations.asSequence().filter { it.lineWithParents.subDepartmentId == record.subDepId }
                        .map { Triple(it.lineWithParents.channelId, it.lineWithParents.channelAbbr ?: NoString.str, it.lineWithParents.channelOrder) }.toSet().sortedBy { it.third }.map {
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
                    operations.asSequence().filter { it.lineWithParents.channelId == record.channelId }
                        .map { Triple(it.lineWithParents.id, it.lineWithParents.lineAbbr ?: NoString.str, it.lineWithParents.lineOrder) }.toSet().sortedBy { it.third }.map {
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
                    operations.filter { it.lineWithParents.id == record.lineId }.toSet().sortedBy { it.operation.operationOrder }.map {
                        if (it.operation.id == record.previousOperationId) _operationToAdd.value = record.copy(
                            depAbbr = it.lineWithParents.depAbbr,
                            subDepAbbr = it.lineWithParents.subDepAbbr,
                            channelAbbr = it.lineWithParents.channelAbbr,
                            lineAbbr = it.lineWithParents.lineAbbr,
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
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg)
        else _fillInState.value = FillInSuccessState
    }
}

data class FillInErrors(
    var departmentError: Boolean = false,
    var subDepartmentError: Boolean = false,
    var channelError: Boolean = false,
    var lineError: Boolean = false,
    var operationError: Boolean = false
)