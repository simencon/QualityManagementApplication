package com.simenko.qmapp.ui.main.team.forms

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.domain.entities.DomainTeamMember
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.ui.main.AddEditMode
import com.simenko.qmapp.ui.main.MainActivityViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(private val repository: ManufacturingRepository) : ViewModel() {
    private lateinit var _mainViewModel: MainActivityViewModel
    fun initMainActivityViewModel(viewModel: MainActivityViewModel) {
        this._mainViewModel = viewModel
    }

    fun setAddEditMode(mode: AddEditMode) {
        _mainViewModel.setAddEditMode(mode)
    }

    private val _employee: MutableStateFlow<DomainTeamMember> = MutableStateFlow(DomainTeamMember())
    private var _employeeErrors: MutableStateFlow<EmployeeErrors> = MutableStateFlow(EmployeeErrors())
    fun loadEmployee(id: Int) {
        _employee.value = repository.getEmployeeById(id)
    }

    val employee: StateFlow<DomainTeamMember> get() = _employee
    val employeeErrors: StateFlow<EmployeeErrors> get() = _employeeErrors

    fun setFullName(it: String) {
        _employee.value = _employee.value.copy(fullName = it)
        _employeeErrors.value = _employeeErrors.value.copy(fullNameError = false)
    }
}

data class EmployeeErrors(
    var fullNameError: Boolean = false,
    var departmentError: Boolean = false,
    var jobRoleError: Boolean = false,
    var emailError: Boolean = false,
    var passwordError: Boolean = false,
)