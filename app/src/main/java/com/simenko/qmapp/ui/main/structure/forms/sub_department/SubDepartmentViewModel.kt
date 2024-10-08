package com.simenko.qmapp.ui.main.structure.forms.sub_department

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubDepartmentViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
) : ViewModel() {
    private val _subDepartment = MutableStateFlow(DomainSubDepartment.DomainSubDepartmentComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.CompanyStructure.SubDepartmentAddEdit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (route.subDepartmentId == NoRecord.num) prepareSubDepartment(route.departmentId) else _subDepartment.value = repository.subDepartmentById(route.subDepartmentId)
                mainPageHandler = MainPageHandler.Builder(if (route.subDepartmentId == NoRecord.num) Page.ADD_SUB_DEPARTMENT else Page.EDIT_SUB_DEPARTMENT, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { validateInput() }
                    .build()
                    .apply { setupMainPage(0, true) }
            }
        }
    }

    private fun prepareSubDepartment(depId: ID) {
        _subDepartment.value = DomainSubDepartment.DomainSubDepartmentComplete(
            subDepartment = DomainSubDepartment(depId = depId),
            department = repository.departmentById(depId).department
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val subDepartment get() = _subDepartment.asStateFlow()

    fun setSubDepartmentOrder(it: Int) {
        _subDepartment.value = _subDepartment.value.copy(subDepartment = _subDepartment.value.subDepartment.copy(subDepOrder = it))
        _fillInErrors.value = _fillInErrors.value.copy(subDepartmentOrderError = false)
        _fillInState.value = FillInInitialState
    }

    fun setSubDepartmentAbbr(it: String) {
        _subDepartment.value = _subDepartment.value.copy(subDepartment = _subDepartment.value.subDepartment.copy(subDepAbbr = it))
        _fillInErrors.value = _fillInErrors.value.copy(subDepartmentAbbrError = false)
        _fillInState.value = FillInInitialState
    }

    fun setSubDepartmentDesignation(it: String) {
        _subDepartment.value = _subDepartment.value.copy(subDepartment = _subDepartment.value.subDepartment.copy(subDepDesignation = it))
        _fillInErrors.value = _fillInErrors.value.copy(subDepartmentDesignationError = false)
        _fillInState.value = FillInInitialState
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
            if (_subDepartment.value.subDepartment.subDepOrder == NoRecord.num.toInt()) {
                _fillInErrors.value = _fillInErrors.value.copy(subDepartmentOrderError = true)
                append("Sub department order field is mandatory\n")
            }
            if (_subDepartment.value.subDepartment.subDepAbbr.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(subDepartmentAbbrError = true)
                append("Sub department ID field is mandatory\n")
            }
            if (_subDepartment.value.subDepartment.subDepDesignation.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(subDepartmentDesignationError = true)
                append("Sub department complete name field is mandatory\n")
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch {
        mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run { if (_subDepartment.value.subDepartment.id == NoRecord.num) insertSubDepartment(_subDepartment.value.subDepartment) else updateSubDepartment(_subDepartment.value.subDepartment) }
                .consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                            Status.SUCCESS -> navBackToRecord(resource.data?.id)
                            Status.ERROR -> {
                                mainPageHandler?.updateLoadingState?.invoke(Pair(true, resource.message))
                                _fillInState.value = FillInInitialState
                            }
                        }
                    }
                }
        }
    }

    private suspend fun navBackToRecord(id: ID?) {
        mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
        withContext(Dispatchers.Main) {
            id?.let {
                val companyId = _subDepartment.value.department.companyId ?: NoRecord.num
                val depId = _subDepartment.value.department.id
                appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.StructureView(companyId, depId, it), popUpToRoute = Route.Main.CompanyStructure, inclusive = true)
            }
        }
    }
}

data class FillInErrors(
    var subDepartmentOrderError: Boolean = false,
    var subDepartmentAbbrError: Boolean = false,
    var subDepartmentDesignationError: Boolean = false
)