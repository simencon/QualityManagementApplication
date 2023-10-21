package com.simenko.qmapp.ui.main.structure.forms.operation.subforms.previous_operation

import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.DomainOperationsFlow.DomainOperationsFlowComplete
import com.simenko.qmapp.repository.ManufacturingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings1

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PreviousOperationViewModel @Inject constructor(
    repository: ManufacturingRepository
) {
    private val _operationWithFlow = MutableStateFlow(Pair(NoRecord.num, listOf<DomainOperationsFlowComplete>()))
    fun setOperationWithFlow(operationWithFlow: Pair<Int, List<DomainOperationsFlowComplete>>) {
        this._operationWithFlow.value = operationWithFlow
    }

    private val _operations = repository.operationsComplete.flatMapLatest { operations ->
        _operationWithFlow.flatMapLatest { addedOperations ->
            flow { emit(operations.filter { operation -> operation.operation.id !in addedOperations.second.map { it.previousOperationId } }.toList()) }
        }
    }

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
        }
    }
    val availableDepartments: Flow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _selectedDepId.flatMapLatest { recId ->
            flow {
                emit(
                    operations.map { Pair(it.lineComplete.departmentId, it.lineComplete.depAbbr ?: NoString.str) }.toSet().map {
                        Triple(it.first, it.second, it.first == recId)
                    }
                )
            }
        }
    }

    private val _selectedSubDepId = MutableStateFlow(NoRecord.num)
    val selectSubDepId: (Int) -> Unit = {
        if (it != _selectedSubDepId.value) {
            _selectedSubDepId.value = it

            _selectedOperationId.value = NoRecord.num
            _selectedLineId.value = NoRecord.num
            _selectedChannelId.value = NoRecord.num
        }
    }
    val availableSubDepartments: Flow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
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
    }

    private val _selectedChannelId = MutableStateFlow(NoRecord.num)
    val selectedChannelId: (Int) -> Unit = {
        if (it != _selectedChannelId.value) {
            _selectedChannelId.value = it

            _selectedOperationId.value = NoRecord.num
            _selectedLineId.value = NoRecord.num
        }
    }
    val availableChannels: Flow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
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
    }

    private val _selectedLineId = MutableStateFlow(NoRecord.num)
    val selectedLineId: (Int) -> Unit = {
        if (it != _selectedLineId.value) {
            _selectedLineId.value = it

            _selectedOperationId.value = NoRecord.num
        }
    }
    val availableLines: Flow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
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
    }

    private val _operationToAdd = MutableStateFlow(DomainOperationsFlowComplete())

    private val _selectedOperationId = MutableStateFlow(NoRecord.num)
    val selectedOperationId: (Int) -> Unit = { _selectedDepId.value = it }
    val availableOperations: Flow<List<Triple<Int, String, Boolean>>> = _operations.flatMapLatest { operations ->
        _selectedLineId.flatMapLatest { parentId ->
            _selectedChannelId.flatMapLatest { recId ->
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
    }
}