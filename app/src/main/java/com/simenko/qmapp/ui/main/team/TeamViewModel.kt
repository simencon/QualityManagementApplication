package com.simenko.qmapp.ui.main.team

import androidx.compose.material3.FabPosition
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.*
import androidx.navigation.NavHostController
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.DomainEmployeeComplete
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.ui.main.AddEditMode
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import com.simenko.qmapp.utils.UsersFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TeamViewModel @Inject constructor(
    private val systemRepository: SystemRepository,
    private val manufacturingRepository: ManufacturingRepository,
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

    /**
     * Common for employees and users ----------------------------------------------------------------------------------------------------------------
     * */
    private fun updateBudges(employees: List<DomainEmployeeComplete>, users: List<DomainUser>) {
        _mainViewModel.setTopBadgesCount(0, employees.size, Color.Green, Color.Black)
        _mainViewModel.setTopBadgesCount(1, users.filter { !it.restApiUrl.isNullOrEmpty() }.size, Color.Green, Color.Black)
        _mainViewModel.setTopBadgesCount(2, users.filter { it.restApiUrl.isNullOrEmpty() }.size, Color.Red, Color.White)
    }

    /**
     * Employee logic and operations -----------------------------------------------------------------------------------------------------------------
     * */
    private val _selectedEmployeeRecord = MutableStateFlow(Event(NoRecord.num))
    val selectedEmployeeRecord = _selectedEmployeeRecord.asStateFlow()
    fun setSelectedEmployeeRecord(id: Int) {
        if (selectedEmployeeRecord.value.peekContent() != id) this._selectedEmployeeRecord.value = Event(id)
    }

    fun onListEnd(position: FabPosition) {
        _mainViewModel.onListEnd(position)
    }

    private val _employees: Flow<List<DomainEmployeeComplete>> = manufacturingRepository.employeesComplete
    private val _currentEmployeeVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))

    fun setCurrentEmployeeVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _currentEmployeeVisibility.value = _currentEmployeeVisibility.value.setVisibility(dId, aId)
    }

    val employees: StateFlow<List<DomainEmployeeComplete>> = _employees.flatMapLatest { employees ->
        _currentEmployeeVisibility.flatMapLatest { visibility ->
            _users.flatMapLatest { users ->
                updateBudges(employees, users)
                val cpy = mutableListOf<DomainEmployeeComplete>()
                employees.forEach {
                    cpy.add(
                        it.copy(
                            detailsVisibility = it.teamMember.id == visibility.first.num,
                            isExpanded = it.teamMember.id == visibility.second.num
                        )
                    )
                }
                flow { emit(cpy) }
            }
        }
    }
        .flowOn(Dispatchers.IO)
        .conflate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun deleteEmployee(teamMemberId: Int) = viewModelScope.launch {
        _mainViewModel.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            manufacturingRepository.run {
                deleteTeamMember(teamMemberId).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> _mainViewModel.updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> _mainViewModel.updateLoadingState(Pair(false, null))
                            Status.ERROR -> _mainViewModel.updateLoadingState(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }

    /**
     * User logic and operations ---------------------------------------------------------------------------------------------------------------------
     * */
    private val _selectedUserRecord = MutableStateFlow(Event(NoRecordStr.str))
    val selectedUserRecord = _selectedUserRecord.asStateFlow()
    fun setSelectedUserRecord(id: String) {
        if (selectedUserRecord.value.peekContent() != id) this._selectedUserRecord.value = Event(id)
    }

    fun getSelectedUser(id: String): DomainUser = systemRepository.getUserById(id)

    private val _users: Flow<List<DomainUser>> = systemRepository.users
    private val _currentUserVisibility = MutableStateFlow(Pair(NoRecordStr, NoRecordStr))
    fun setCurrentUserVisibility(dId: SelectedString = NoRecordStr, aId: SelectedString = NoRecordStr) {
        _currentUserVisibility.value = _currentUserVisibility.value.setVisibility(dId, aId)
    }

    private val _currentUsersFilter = MutableStateFlow(UsersFilter())
    fun setUsersFilter(newUsers: Boolean = false) {
        _currentUsersFilter.value = UsersFilter(newUsers = newUsers)
    }

    val users: StateFlow<List<DomainUser>> = _users.flatMapLatest { users ->
        _currentUserVisibility.flatMapLatest { visibility ->
            _currentUsersFilter.flatMapLatest { filter ->
                _employees.flatMapLatest { employees ->
                    updateBudges(employees, users)
                    val cpy = mutableListOf<DomainUser>()
                    users.forEach {
                        if (it.restApiUrl.isNullOrEmpty() == filter.newUsers)
                            cpy.add(
                                it.copy(
                                    detailsVisibility = it.email == visibility.first.str,
                                    isExpanded = it.email == visibility.second.str
                                )
                            )
                    }
                    flow { emit(cpy) }
                }
            }
        }
    }
        .flowOn(Dispatchers.IO)
        .conflate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _isRemoveUserDialogVisible = MutableStateFlow(false)
    val isRemoveUserDialogVisible get() = _isRemoveUserDialogVisible.asStateFlow()

    fun setRemoveUserDialogVisibility(value: Boolean) {
        _isRemoveUserDialogVisible.value = value
    }

    fun removeUser(userId: String) = viewModelScope.launch {
        _mainViewModel.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            systemRepository.run {
                removeUser(userId).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> _mainViewModel.updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> {
                                _mainViewModel.updateLoadingState(Pair(false, null))
                                _selectedUserRecord.value = Event(NoRecordStr.str)
                                setRemoveUserDialogVisibility(false)

                                resource.data?.let {
                                    setUsersFilter(it.restApiUrl.isNullOrEmpty())
                                    navToRemovedRecord(it.email)
                                }
                            }

                            Status.ERROR -> _mainViewModel.updateLoadingState(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }

    private suspend fun navToRemovedRecord(id: String?) {
        _mainViewModel.updateLoadingState(Pair(false, null))
        setAddEditMode(AddEditMode.NO_MODE)
        withContext(Dispatchers.Main) {
            id?.let {
                navController.navigate(Screen.Main.Team.Users.withArgs(it)) {
                    popUpTo(Screen.Main.Team.Users.routeWithArgKeys()) { inclusive = true }
                }
            }
        }
    }

    fun updateEmployeesData() = viewModelScope.launch {
        try {
            _mainViewModel.updateLoadingState(Pair(true, null))

            systemRepository.syncUserRoles()
            systemRepository.syncUsers()

            manufacturingRepository.syncCompanies()
            manufacturingRepository.syncJobRoles()
            manufacturingRepository.syncDepartments()
            manufacturingRepository.syncTeamMembers()

            _mainViewModel.updateLoadingState(Pair(false, null))
        } catch (e: Exception) {
            _mainViewModel.updateLoadingState(Pair(false, e.message))
        }
    }
}