package com.simenko.qmapp.ui.main.structure.forms.operation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.LineIdParameter
import com.simenko.qmapp.di.OperationIdParameter
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation.DomainManufacturingOperationComplete
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OperationViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    @LineIdParameter private val lineId: Int,
    @OperationIdParameter private val operationId: Int
) : ViewModel() {
    private val _operation = MutableStateFlow(DomainManufacturingOperationComplete())
    val operation get() = _operation.asStateFlow()
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
            operation = DomainManufacturingOperation(),
            lineComplete = repository.lineById(lineId)
        )
    }
    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */

    private val _isAddPreviousOperationDialogVisible = MutableStateFlow(false)
    val isAddPreviousOperationDialogVisible = _isAddPreviousOperationDialogVisible.asStateFlow()
    fun setAddRoleDialogVisibility(value: Boolean) {
        _isAddPreviousOperationDialogVisible.value = value
    }
    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private val _fillInErrors = MutableStateFlow(FillInErrors())
    val fillInErrors get() = _fillInErrors.asStateFlow()
    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()
    private fun validateInput() {
        TODO("Not yet implemented")
    }

    fun makeRecord() {
        TODO("Not yet implemented")
    }

}

data class FillInErrors(
    var teamMemberError: Boolean = false,
    var rolesError: Boolean = false,
    var enabledError: Boolean = false
)