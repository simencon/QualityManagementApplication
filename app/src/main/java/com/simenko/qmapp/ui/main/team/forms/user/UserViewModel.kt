package com.simenko.qmapp.ui.main.team.forms.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.domain.entities.DomainEmployee
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.domain.entities.DomainUserRole
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.ui.main.AddEditMode
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInError
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInInitialState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInSuccess
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: SystemRepository,
    private val manufacturingRepository: ManufacturingRepository
) : ViewModel() {
    private lateinit var navController: NavHostController
    fun initNavController(controller: NavHostController) {
        this.navController = controller
    }

    private lateinit var _mainViewModel: MainActivityViewModel
    fun initMainActivityViewModel(viewModel: MainActivityViewModel) {
        this._mainViewModel = viewModel
    }

    fun setAddEditMode(mode: AddEditMode) {
        _mainViewModel.setAddEditMode(mode)
    }

    private val _user = MutableStateFlow(DomainUser())
    private val _userErrors = MutableStateFlow(UserErrors())
    fun loadUser(id: String) {
        _user.value = repository.getUserById(id)
    }

    val user get() = _user.asStateFlow()
    private val _currentUserRoleVisibility = MutableStateFlow(Pair(NoRecordStr, NoRecordStr))
    val userErrors get() = _userErrors.asStateFlow()

    val userRoles: StateFlow<List<DomainUserRole>> = user.flatMapLatest { user ->
        _currentUserRoleVisibility.flatMapLatest { visibility ->
            val cpy = mutableListOf<DomainUserRole>()
            user.rolesAsUserRoles().forEach {
                cpy.add(it.copy(detailsVisibility = it.getRecordId() == visibility.first.str, isExpanded = it.getRecordId() == visibility.second.str))
            }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setCurrentUserRoleVisibility(dId: SelectedString = NoRecordStr, aId: SelectedString = NoRecordStr) {
        _currentUserRoleVisibility.value = _currentUserRoleVisibility.value.setVisibility(dId, aId)
    }

    fun deleteUserRole(id: String) {
        val roles = _user.value.roles?.toHashSet()
        roles?.remove(id)
        _user.value = _user.value.copy(roles = roles)
    }

    private val _userEmployees: Flow<List<DomainEmployee>> = manufacturingRepository.employees

    val userEmployees: StateFlow<List<Triple<Int, String, Boolean>>> = _userEmployees.flatMapLatest { employees ->
        _user.flatMapLatest { user ->
            val cpy = mutableListOf<Triple<Int, String, Boolean>>()
            employees.forEach { cpy.add(Triple(it.id, it.fullName, it.id.toLong() == user.teamMemberId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setUserEmployee(id: Int) {
        if (_user.value.teamMemberId != id.toLong()) {
            _user.value = _user.value.copy(teamMemberId = id.toLong())
            _userErrors.value = _userErrors.value.copy(teamMemberError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun setUserIsEnabled(value: Boolean) {
        if (_user.value.enabled != value) {
            _user.value = _user.value.copy(enabled = value)
            _userErrors.value = _userErrors.value.copy(enabledError = false)
            _fillInState.value = FillInInitialState
        }
    }

    private val _availableUserRoles = repository.userRoles.flatMapLatest { roles ->
        _user.flatMapLatest { user ->
            val cpy = roles.toMutableList()
            cpy.removeAll(user.rolesAsUserRoles().toSet())
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _userRoleToAdd = MutableStateFlow(Triple(NoRecordStr.str, NoRecordStr.str, NoRecordStr.str))
    private val _userRoleToAddErrors = MutableStateFlow(Triple(false, false, false))
    fun clearUserRoleToAdd() {
        _userRoleToAdd.value = Triple(NoRecordStr.str, NoRecordStr.str, NoRecordStr.str)
    }

    val roleFunctions = _availableUserRoles.flatMapLatest { roles ->
        _userRoleToAdd.flatMapLatest { roleToAdd ->
            val cpy = mutableListOf<Pair<String, Boolean>>()
            roles.map { it.function }.toSet().forEach {
                cpy.add(Pair(it, it == roleToAdd.first))
            }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setRoleFunction(value: String) {
        _userRoleToAdd.value = _userRoleToAdd.value.copy(first = value, second = NoRecordStr.str, third = NoRecordStr.str)
    }

    val roleLevels = _availableUserRoles.flatMapLatest { roles ->
        _userRoleToAdd.flatMapLatest { roleToAdd ->
            val cpy = mutableListOf<Pair<String, Boolean>>()
            roles.filter { it.function == roleToAdd.first }.map { it.roleLevel }.toSet().forEach {
                cpy.add(Pair(it, it == roleToAdd.second))
            }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setRoleLevel(value: String) {
        _userRoleToAdd.value = _userRoleToAdd.value.copy(second = value, third = NoRecordStr.str)
    }

    val roleAccesses = _availableUserRoles.flatMapLatest { roles ->
        _userRoleToAdd.flatMapLatest { roleToAdd ->
            val cpy = mutableListOf<Pair<String, Boolean>>()
            roles.filter { it.function == roleToAdd.first && it.roleLevel == roleToAdd.second }.map { it.accessLevel }.toSet().forEach {
                cpy.add(Pair(it, it == roleToAdd.third))
            }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setRoleAccess(value: String) {
        _userRoleToAdd.value = _userRoleToAdd.value.copy(third = value)
    }

    fun addUserRole() {
        val roleToAdd = "${_userRoleToAdd.value.first}:${_userRoleToAdd.value.second}:${_userRoleToAdd.value.third}"
        val roles = _user.value.roles.let { it?.toHashSet() ?: mutableSetOf() }
        roles.add(roleToAdd)
        _user.value = _user.value.copy(roles = roles)
    }

    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()

    fun validateInput(user: DomainUser = _user.value) {
        val errorMsg = buildString {
            if (user.teamMemberId == NoRecord.num.toLong()) {
                _userErrors.value = _userErrors.value.copy(teamMemberError = true)
                append("Employee field is mandatory\n")
            }
            if (user.roles.isNullOrEmpty()) {
                _userErrors.value = _userErrors.value.copy(rolesError = true)
                append("User must have at least one role\n")
            }
            if (!user.enabled) {
                _userErrors.value = _userErrors.value.copy(enabledError = true)
                append("To authorize you need to enable user\n")
            }
        }

        if (errorMsg.isNotEmpty()) _fillInState.value = FillInError(errorMsg)
        else _fillInState.value = FillInSuccess
    }

    /**
     * Data Base/REST API Operations --------------------------------------------------------------------------------------------------------------------------
     * */
    fun makeUser(record: DomainUser) = viewModelScope.launch {
        /*_mainViewModel.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run {
                if (_mainViewModel.addEditMode.value == AddEditMode.ADD_EMPLOYEE.ordinal)
                    insertTeamMember(record)
                else
                    updateTeamMember(record)
            }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> _mainViewModel.updateLoadingState(Pair(true, null))
                        Status.SUCCESS -> navBackToRecord(resource.data?.id)
                        Status.ERROR -> _mainViewModel.updateLoadingState(Pair(true, resource.message))
                    }
                }
            }
        }*/
    }

    private suspend fun navBackToRecord(id: Int?) {
        /*_mainViewModel.updateLoadingState(Pair(false, null))
        setAddEditMode(AddEditMode.NO_MODE)
        withContext(Dispatchers.Main) {
            id?.let {
                navController.navigate(Screen.Main.Team.Employees.withArgs(it.toString())) {
                    popUpTo(Screen.Main.Team.Employees.routeWithArgKeys()) { inclusive = true }
                }
            }
        }*/
    }
}

data class UserErrors(
    var teamMemberError: Boolean = false,
    var rolesError: Boolean = false,
    var enabledError: Boolean = false
)