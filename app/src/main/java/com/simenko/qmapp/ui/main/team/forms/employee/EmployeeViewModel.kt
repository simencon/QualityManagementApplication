package com.simenko.qmapp.ui.main.team.forms.employee

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.DomainCompany
import com.simenko.qmapp.domain.entities.DomainDepartment
import com.simenko.qmapp.domain.entities.DomainJobRole
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.domain.entities.DomainEmployee
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.ui.main.main.page.TopScreenState
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInError
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInInitialState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInSuccess
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInState
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
class EmployeeViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val topScreenState: TopScreenState,
    private val repository: ManufacturingRepository
) : ViewModel() {
    private fun updateLoadingState(state: Pair<Boolean, String?>) {
        topScreenState.trySendLoadingState(state)
    }

    private val _isNewEmployeeRecord = mutableStateOf(false)
    suspend fun setupTopScreen(addEditMode: AddEditMode, employeeId: Int) {
        topScreenState.trySendTopScreenSetup(
            addEditMode = Pair(addEditMode) {
                _isNewEmployeeRecord.value = addEditMode == AddEditMode.ADD_EMPLOYEE
                validateInput()
            },
            refreshAction = {},
            filterAction = {}
        )
        if (addEditMode == AddEditMode.EDIT_EMPLOYEE)
            withContext(Dispatchers.Default) { loadEmployee(employeeId) }
    }

    private val _employee: MutableStateFlow<DomainEmployee> = MutableStateFlow(DomainEmployee())
    private var _employeeErrors: MutableStateFlow<EmployeeErrors> = MutableStateFlow(EmployeeErrors())
    private fun loadEmployee(id: Int) {
        _employee.value = repository.getEmployeeById(id)
    }

    val employee get() = _employee.asStateFlow()
    val employeeErrors get() = _employeeErrors.asStateFlow()

    fun setFullName(it: String) {
        _employee.value = _employee.value.copy(fullName = it)
        _employeeErrors.value = _employeeErrors.value.copy(fullNameError = false)
        _fillInState.value = FillInInitialState
    }

    private val _employeeCompanies: Flow<List<DomainCompany>> = repository.companies
    val employeeCompanies: StateFlow<List<Triple<Int, String, Boolean>>> = _employeeCompanies.flatMapLatest { company ->
        _employee.flatMapLatest { employee ->
            val cpy = mutableListOf<Triple<Int, String, Boolean>>()
            company.forEach { cpy.add(Triple(it.id, it.companyName ?: NoString.str, it.id == employee.companyId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setEmployeeCompany(id: Int) {
        if (_employee.value.companyId != id) {
            _employee.value = _employee.value.copy(companyId = id, departmentId = NoRecord.num, subDepartmentId = null)
            _employeeErrors.value = _employeeErrors.value.copy(companyError = false)
            _fillInState.value = FillInInitialState
        }
    }

    private val _employeeDepartments: Flow<List<DomainDepartment>> = repository.departments
    val employeeDepartments: StateFlow<List<Triple<Int, String, Boolean>>> = _employeeDepartments.flatMapLatest { departments ->
        _employee.flatMapLatest { employee ->
            val cpy = mutableListOf<Triple<Int, String, Boolean>>()
            departments
                .filter { it.companyId == employee.companyId }
                .forEach { cpy.add(Triple(it.id, it.depAbbr ?: NoString.str, it.id == employee.departmentId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setEmployeeDepartment(id: Int) {
        if (_employee.value.departmentId != id) {
            _employee.value = _employee.value.copy(departmentId = id, subDepartmentId = null)
            _employeeErrors.value = _employeeErrors.value.copy(departmentError = false)
            _fillInState.value = FillInInitialState
        }
    }

    private val _employeeSubDepartments: Flow<List<DomainSubDepartment>> = repository.subDepartments
    val employeeSubDepartments: StateFlow<List<Triple<Int, String, Boolean>>> = _employeeSubDepartments.flatMapLatest { subDepartments ->
        _employee.flatMapLatest { employee ->
            val cpy = mutableListOf<Triple<Int, String, Boolean>>()
            subDepartments
                .filter { it.depId == employee.departmentId }
                .forEach { cpy.add(Triple(it.id, it.subDepAbbr ?: NoString.str, it.id == employee.subDepartmentId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setEmployeeSubDepartment(id: Int) {
        if (_employee.value.subDepartmentId != id) {
            _employee.value = _employee.value.copy(subDepartmentId = if (id == NoRecord.num) null else id)
            _employeeErrors.value = _employeeErrors.value.copy(subDepartmentError = false)
            _fillInState.value = FillInInitialState
        }
    }

    private val _employeeJobRoles: Flow<List<DomainJobRole>> = repository.jobRoles
    val employeeJobRoles: StateFlow<List<Triple<Int, String, Boolean>>> = _employeeJobRoles.flatMapLatest { subDepartments ->
        _employee.flatMapLatest { employee ->
            val cpy = mutableListOf<Triple<Int, String, Boolean>>()
            subDepartments
                .filter { it.companyId == employee.companyId }
                .forEach { cpy.add(Triple(it.id, it.jobRoleDescription, it.id == employee.jobRoleId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setEmployeeJobRole(id: Int) {
        if (_employee.value.subDepartmentId != id) {
            _employee.value = _employee.value.copy(jobRoleId = id)
            _employeeErrors.value = _employeeErrors.value.copy(jobRoleIdError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun setJobRole(it: String) {
        _employee.value = _employee.value.copy(jobRole = it)
        _employeeErrors.value = _employeeErrors.value.copy(jobRoleError = false)
        _fillInState.value = FillInInitialState
    }

    fun setEmail(it: String) {
        _employee.value = _employee.value.copy(email = it)
        _employeeErrors.value = _employeeErrors.value.copy(emailError = false)
        _fillInState.value = FillInInitialState
    }

    fun setPassword(it: String) {
        _employee.value = _employee.value.copy(passWord = it)
        _employeeErrors.value = _employeeErrors.value.copy(passwordError = false)
        _fillInState.value = FillInInitialState
    }

    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()

    fun validateInput(principle: DomainEmployee = _employee.value) {
        val errorMsg = buildString {
            if (principle.fullName.isEmpty()) {
                _employeeErrors.value = _employeeErrors.value.copy(fullNameError = true)
                append("Full name field is mandatory\n")
            }
            if (principle.companyId == NoRecord.num) {
                _employeeErrors.value = _employeeErrors.value.copy(companyError = true)
                append("Company field is mandatory\n")
            }
            if (principle.departmentId == NoRecord.num) {
                _employeeErrors.value = _employeeErrors.value.copy(departmentError = true)
                append("Department field is mandatory\n")
            }
            if (principle.jobRoleId == NoRecord.num) {
                _employeeErrors.value = _employeeErrors.value.copy(jobRoleIdError = true)
                append("Job role field is mandatory\n")
            }
            if (principle.jobRole.isEmpty()) {
                _employeeErrors.value = _employeeErrors.value.copy(jobRoleError = true)
                append("Job role description field is mandatory\n")
            }
            if (!principle.email.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(principle.email ?: EmptyString.str).matches()) {
                _employeeErrors.value = _employeeErrors.value.copy(emailError = true)
                append("Wrong email format\n")
            }
            if (!principle.passWord.isNullOrEmpty() && principle.passWord!!.length < 6) {
                _employeeErrors.value = _employeeErrors.value.copy(passwordError = true)
                append("Password should be at least 6 characters long\n")
            }
        }

        if (errorMsg.isNotEmpty()) _fillInState.value = FillInError(errorMsg)
        else _fillInState.value = FillInSuccess
    }

    /**
     * Data Base/REST API Operations --------------------------------------------------------------------------------------------------------------------------
     * */
    fun makeEmployee(newRecord: Boolean = _isNewEmployeeRecord.value) = viewModelScope.launch {
        updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run { if (newRecord) insertTeamMember(_employee.value) else updateTeamMember(_employee.value) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> updateLoadingState(Pair(true, null))
                        Status.SUCCESS -> navBackToRecord(resource.data?.id)
                        Status.ERROR -> {
                            updateLoadingState(Pair(true, resource.message))
                            _fillInState.value = FillInInitialState
                        }
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(id: Int?) {
        updateLoadingState(Pair(false, null))
        withContext(Dispatchers.Main) {
            id?.let {
                appNavigator.tryNavigateTo(
                    route = Route.Main.Team.Employees.withArgs(it.toString()),
                    popUpToRoute = Route.Main.Team.Employees.link,
                    inclusive = true
                )
            }
        }
    }

}

data class EmployeeErrors(
    var fullNameError: Boolean = false,
    var companyError: Boolean = false,
    var departmentError: Boolean = false,
    var subDepartmentError: Boolean = false,
    var jobRoleIdError: Boolean = false,
    var jobRoleError: Boolean = false,
    var emailError: Boolean = false,
    var passwordError: Boolean = false,
)