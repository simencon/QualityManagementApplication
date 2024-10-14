package com.simenko.qmapp.presentation.ui.main.structure.forms.department

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainDepartment
import com.simenko.qmapp.domain.entities.DomainEmployeeComplete
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ManufacturingRepository
import com.simenko.qmapp.presentation.ui.main.main.MainPageHandler
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.utils.EmployeesFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DepartmentViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
) : ViewModel() {
    private val _department = MutableStateFlow(DomainDepartment.DomainDepartmentComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.CompanyStructure.DepartmentAddEdit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (route.departmentId == NoRecord.num) prepareDepartment(route.companyId) else _department.value = repository.departmentById(route.departmentId)
                mainPageHandler = MainPageHandler.Builder(if (route.departmentId == NoRecord.num) Page.ADD_DEPARTMENT else Page.EDIT_DEPARTMENT, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { validateInput() }
                    .build()
                    .apply { setupMainPage(0, true) }
            }
        }
    }

    private suspend fun prepareDepartment(companyId: ID) {
        _department.value = DomainDepartment.DomainDepartmentComplete(
            department = DomainDepartment(companyId = companyId),
            company = repository.companyById(companyId)
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val department get() = _department.asStateFlow()

    fun setDepartmentOrder(it: Int) {
        _department.value = _department.value.copy(department = _department.value.department.copy(depOrder = it))
        _fillInErrors.value = _fillInErrors.value.copy(departmentOrderError = false)
        _fillInState.value = FillInInitialState
    }

    fun setDepartmentAbbr(it: String) {
        _department.value = _department.value.copy(department = _department.value.department.copy(depAbbr = it))
        _fillInErrors.value = _fillInErrors.value.copy(departmentAbbrError = false)
        _fillInState.value = FillInInitialState
    }

    fun setDepartmentDesignation(it: String) {
        _department.value = _department.value.copy(department = _department.value.department.copy(depName = it))
        _fillInErrors.value = _fillInErrors.value.copy(departmentDesignationError = false)
        _fillInState.value = FillInInitialState
    }

    private val _companyEmployees: Flow<List<DomainEmployeeComplete>> = _department.flatMapLatest { department ->
        repository.employeesComplete(EmployeesFilter(parentId = department.company.id))
    }

    val companyEmployees: StateFlow<List<Triple<ID, String, Boolean>>> = _companyEmployees.flatMapLatest { employees ->
        _department.flatMapLatest { department ->
            val cpy = mutableListOf<Triple<ID, String, Boolean>>()
            employees.forEach { cpy.add(Triple(it.teamMember.id, it.teamMember.fullName, it.teamMember.id == department.depManager.id)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setDepartmentManager(it: ID) {
        if (_department.value.department.depManager != it) {
            _department.value = _department.value.copy(department = _department.value.department.copy(depManager = it))
            _fillInErrors.value = _fillInErrors.value.copy(departmentManagerError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun setDepartmentFunction(it: String) {
        _department.value = _department.value.copy(department = _department.value.department.copy(depOrganization = it))
        _fillInErrors.value = _fillInErrors.value.copy(departmentOrganizationError = false)
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
            if (_department.value.department.depOrder == NoRecord.num.toInt()) {
                _fillInErrors.value = _fillInErrors.value.copy(departmentOrderError = true)
                append("Department order field is mandatory\n")
            }
            if (_department.value.department.depAbbr.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(departmentAbbrError = true)
                append("Department ID field is mandatory\n")
            }
            if (_department.value.department.depName.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(departmentDesignationError = true)
                append("Department complete name field is mandatory\n")
            }
            if (_department.value.department.depManager == NoRecord.num) {
                _fillInErrors.value = _fillInErrors.value.copy(departmentManagerError = true)
                append("Department manager field is mandatory\n")
            }
            if (_department.value.department.depOrganization.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(departmentOrganizationError = true)
                append("Department organisation field is mandatory\n")
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch {
        mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
        withContext(Dispatchers.IO) {
            repository.run { if (_department.value.department.id == NoRecord.num) insertDepartment(_department.value.department) else updateDepartment(_department.value.department) }
                .consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                            Status.SUCCESS -> navBackToRecord(resource.data?.id)
                            Status.ERROR -> {
                                mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, resource.message))
                                _fillInState.value = FillInInitialState
                            }
                        }
                    }
                }
        }
    }

    private suspend fun navBackToRecord(id: ID?) {
        mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
        withContext(Dispatchers.Main) {
            id?.let {
                val companyId = _department.value.department.companyId ?: NoRecord.num
                appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.StructureView(companyId, it), popUpToRoute = Route.Main.CompanyStructure, inclusive = true)
            }
        }
    }
}

data class FillInErrors(
    var departmentOrderError: Boolean = false,
    var departmentAbbrError: Boolean = false,
    var departmentDesignationError: Boolean = false,
    var departmentManagerError: Boolean = false,
    var departmentOrganizationError: Boolean = false,
)