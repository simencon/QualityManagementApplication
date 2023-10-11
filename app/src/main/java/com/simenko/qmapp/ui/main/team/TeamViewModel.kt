package com.simenko.qmapp.ui.main.team

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.DomainEmployeeComplete
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.Page
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.utils.BaseFilter
import com.simenko.qmapp.utils.EmployeesFilter
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import com.simenko.qmapp.utils.TeamUtils.filterEmployees
import com.simenko.qmapp.utils.TeamUtils.filterUsers
import com.simenko.qmapp.utils.UsersFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TeamViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val userRepository: UserRepository,
    private val systemRepository: SystemRepository,
    private val manufacturingRepository: ManufacturingRepository,
) : ViewModel() {
    private val _employees: Flow<List<DomainEmployeeComplete>> = manufacturingRepository.employeesComplete
    private val _users: Flow<List<DomainUser>> = systemRepository.users

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.TEAM, mainPageState)
            .setOnSearchClickAction {
                setEmployeesFilter(it)
                setUsersFilter(it)
            }
            .setOnTabSelectAction { navigateByTopTabs(it) }
            .setOnFabClickAction { onEmployeeAddEdictClick(NoRecord.num) }
            .setOnPullRefreshAction { updateEmployeesData() }
            .setTabBadgesFlow(
                combine(_employees, _users) { employees, users ->
                    listOf(
                        Triple(employees.size, Color.Green, Color.Black),
                        Triple(users.filter { !it.restApiUrl.isNullOrEmpty() }.size, Color.Green, Color.Black),
                        Triple(users.filter { it.restApiUrl.isNullOrEmpty() }.size, Color.Red, Color.White)
                    )
                }
            )
            .build()
    }

    /**
     * Common for employees and users ----------------------------------------------------------------------------------------------------------------
     * */
    val isOwnAccount: (String) -> Boolean = { it == userRepository.user.email }

    /**
     * Employee logic and operations -----------------------------------------------------------------------------------------------------------------
     * */
    private val _selectedEmployeeRecord = MutableStateFlow(Event(NoRecord.num))
    val selectedEmployeeRecord = _selectedEmployeeRecord.asStateFlow()
    fun setSelectedEmployeeRecord(id: Int) {
        if (selectedEmployeeRecord.value.peekContent() != id) this._selectedEmployeeRecord.value = Event(id)
    }

    private val _currentEmployeeVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))

    fun setCurrentEmployeeVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _currentEmployeeVisibility.value = _currentEmployeeVisibility.value.setVisibility(dId, aId)
    }

    private val _currentEmployeesFilter = MutableStateFlow(EmployeesFilter())
    private fun setEmployeesFilter(filter: BaseFilter) {
        val cpy = _currentEmployeesFilter.value
        _currentEmployeesFilter.value = _currentEmployeesFilter.value.copy(
            stringToSearch = filter.stringToSearch ?: cpy.stringToSearch
        )
    }

    val employees: SharedFlow<List<DomainEmployeeComplete>> = _employees.flatMapLatest { employees ->
        _currentEmployeeVisibility.flatMapLatest { visibility ->
            _currentEmployeesFilter.flatMapLatest { filter ->
                val cpy = mutableListOf<DomainEmployeeComplete>()
                employees
                    .filterEmployees(filter)
                    .forEach {
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
        .flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun deleteEmployee(teamMemberId: Int) = viewModelScope.launch {
        mainPageHandler.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            manufacturingRepository.run {
                deleteTeamMember(teamMemberId).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler.updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> mainPageHandler.updateLoadingState(Pair(false, null))
                            Status.ERROR -> mainPageHandler.updateLoadingState(Pair(true, resource.message))
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

    private val _currentUserVisibility = MutableStateFlow(Pair(NoRecordStr, NoRecordStr))
    fun setCurrentUserVisibility(dId: SelectedString = NoRecordStr, aId: SelectedString = NoRecordStr) {
        _currentUserVisibility.value = _currentUserVisibility.value.setVisibility(dId, aId)
    }

    private val _currentUsersFilter = MutableStateFlow(UsersFilter())
    fun setUsersFilter(filter: BaseFilter) {
        val cpy = _currentUsersFilter.value
        _currentUsersFilter.value = _currentUsersFilter.value.copy(
            stringToSearch = filter.stringToSearch ?: cpy.stringToSearch,
            newUsers = filter.newUsers ?: cpy.newUsers
        )
    }

    val users: SharedFlow<List<DomainUser>> = _users.flatMapLatest { users ->
        _currentUserVisibility.flatMapLatest { visibility ->
            _currentUsersFilter.flatMapLatest { filter ->
                val cpy = mutableListOf<DomainUser>()
                users
                    .filterUsers(filter)
                    .forEach {
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
        .flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _isRemoveUserDialogVisible = MutableStateFlow(false)
    val isRemoveUserDialogVisible get() = _isRemoveUserDialogVisible.asStateFlow()

    fun setRemoveUserDialogVisibility(value: Boolean) {
        _isRemoveUserDialogVisible.value = value
    }

    fun removeUser(userId: String) = viewModelScope.launch {
        mainPageHandler.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            systemRepository.run {
                removeUser(userId).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler.updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> {
                                mainPageHandler.updateLoadingState(Pair(false, null))
                                _selectedUserRecord.value = Event(NoRecordStr.str)
                                setRemoveUserDialogVisibility(false)
                                navToRemovedRecord(resource.data?.email)
                            }

                            Status.ERROR -> mainPageHandler.updateLoadingState(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }

    private fun navigateByTopTabs(tag: SelectedNumber) {
        when (tag) {
            FirstTabId -> {
                appNavigator.tryNavigateBack()
            }

            SecondTabId -> {
                appNavigator.tryNavigateTo(route = Route.Main.Team.Users.withArgs(NoRecordStr.str), popUpToRoute = Route.Main.Team.Employees.link)
            }

            ThirdTabId -> {
                appNavigator.tryNavigateTo(route = Route.Main.Team.Requests.withArgs(NoRecordStr.str), popUpToRoute = Route.Main.Team.Employees.link)
            }
        }
    }

    fun onEmployeeAddEdictClick(employeeId: Int) {
        println("onEmployeeAddEdictClick - $employeeId")
        setSelectedEmployeeRecord(NoRecord.num)
        appNavigator.tryNavigateTo(Route.Main.Team.EmployeeAddEdit.withArgs(employeeId.toString()))
    }

    fun onUserEditClick(userId: String) {
        setSelectedUserRecord(NoRecordStr.str)
        appNavigator.tryNavigateTo(Route.Main.Team.EditUser.withArgs(userId))
    }

    fun onUserAuthorizeClick(userId: String) {
        setSelectedUserRecord(NoRecordStr.str)
        appNavigator.tryNavigateTo(Route.Main.Team.AuthorizeUser.withArgs(userId))
    }

    private suspend fun navToRemovedRecord(id: String?) {
        mainPageHandler.updateLoadingState(Pair(false, null))
        withContext(Dispatchers.Main) {
            id?.let { appNavigator.tryNavigateTo(Route.Main.Team.Requests.withArgs(it), Route.Main.Team.Employees.link) }
        }
    }

    private fun updateEmployeesData() = viewModelScope.launch {
        try {
            mainPageHandler.updateLoadingState(Pair(true, null))

            systemRepository.syncUserRoles()
            systemRepository.syncUsers()

            manufacturingRepository.syncCompanies()
            manufacturingRepository.syncJobRoles()
            manufacturingRepository.syncDepartments()
            manufacturingRepository.syncTeamMembers()

            mainPageHandler.updateLoadingState(Pair(false, null))
        } catch (e: Exception) {
            mainPageHandler.updateLoadingState(Pair(false, e.message))
        }
    }
}