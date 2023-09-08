package com.simenko.qmapp.ui.main.team.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.DomainCompany
import com.simenko.qmapp.domain.entities.DomainDepartment
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.domain.entities.DomainTeamMember
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.main.AddEditMode
import com.simenko.qmapp.ui.main.MainActivityViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EmployeeViewModel @Inject constructor(private val repository: ManufacturingRepository, private val userRepository: UserRepository) :
    ViewModel() {
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

    private val _employeeCompanies: Flow<List<DomainCompany>> = repository.companies
    val employeeCompanies: StateFlow<List<Triple<Int, String, Boolean>>> = _employeeCompanies.flatMapLatest { company ->
        _employee.flatMapLatest { employee ->
            val cpy = mutableListOf<Triple<Int, String, Boolean>>()
            company.forEach { cpy.add(Triple(it.id, it.companyName?: NoString.str, it.id == employee.companyId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setEmployeeCompany(id: Int) {
        if (_employee.value.companyId != id)
            _employee.value = _employee.value.copy(companyId = id, departmentId = NoRecord.num, subDepartmentId = null)
    }

    private val _employeeDepartments: Flow<List<DomainDepartment>> = repository.departments
    val employeeDepartments: StateFlow<List<Triple<Int, String, Boolean>>> = _employeeDepartments.flatMapLatest { departments ->
        _employee.flatMapLatest { employee ->
            val cpy = mutableListOf<Triple<Int, String, Boolean>>()
            departments
                .filter { it.companyId == employee.companyId }
                .forEach { cpy.add(Triple(it.id, it.depAbbr?: NoString.str, it.id == employee.departmentId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setEmployeeDepartment(id: Int) {
        if (_employee.value.departmentId != id)
            _employee.value = _employee.value.copy(departmentId = id, subDepartmentId = null)
    }

    private val _employeeSubDepartments: Flow<List<DomainSubDepartment>> = repository.subDepartments
    val employeeSubDepartments: StateFlow<List<Triple<Int, String, Boolean>>> = _employeeSubDepartments.flatMapLatest { subDepartments ->
        _employee.flatMapLatest { employee ->
            val cpy = mutableListOf<Triple<Int, String, Boolean>>()
            subDepartments
                .filter { it.depId == employee.departmentId }
                .forEach { cpy.add(Triple(it.id, it.subDepAbbr?: NoString.str, it.id == employee.roleLevelId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setEmployeeSubDepartment(id: Int) {
        if (_employee.value.roleLevelId != id)
            _employee.value = _employee.value.copy(subDepartmentId = id)
    }

}

data class EmployeeErrors(
    var fullNameError: Boolean = false,
    var departmentError: Boolean = false,
    var jobRoleError: Boolean = false,
    var emailError: Boolean = false,
    var passwordError: Boolean = false,
)