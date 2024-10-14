package com.simenko.qmapp.presentation.ui.main.team

import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.*
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.DomainEmployeeComplete
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ManufacturingRepository
import com.simenko.qmapp.data.repository.SystemRepository
import com.simenko.qmapp.data.repository.UserRepository
import com.simenko.qmapp.presentation.ui.main.main.MainPageHandler
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.utils.BaseFilter
import com.simenko.qmapp.utils.EmployeesFilter
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import com.simenko.qmapp.utils.UsersFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TeamViewModel @Inject constructor(
    context: BaseApplication,
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val userRepository: UserRepository,
    private val sRepository: SystemRepository,
    private val mRepository: ManufacturingRepository,
) : ViewModel() {

    private val _currentEmployeesFilter = MutableStateFlow(EmployeesFilter())
    private val _employees: Flow<List<DomainEmployeeComplete>> = _currentEmployeesFilter.flatMapLatest { filter -> mRepository.employeesComplete(filter) }.flowOn(Dispatchers.IO)
    private val _currentUsersFilter = MutableStateFlow(UsersFilter())
    private val _users: Flow<List<DomainUser>> = _currentUsersFilter.flatMapLatest { filter -> sRepository.users(filter) }.flowOn(Dispatchers.IO)
    private val _createdRecord: MutableStateFlow<Pair<Event<ID>, Event<String>>> = MutableStateFlow(Pair(Event(NoRecord.num), Event(NoRecordStr.str)))
    private val _employeesVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _usersVisibility = MutableStateFlow(Pair(SelectedString(NoRecordStr.str), NoRecordStr))
    val currentUserVisibility = _usersVisibility.asStateFlow()

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null

    fun onEntered(employeeId: ID, userId: String, selectedTabIndex: Int, isFabVisible: Boolean) {
        viewModelScope.launch {
            mainPageHandler = MainPageHandler.Builder(Page.TEAM, mainPageState)
                .setOnSearchClickAction {
                    setEmployeesFilter(it)
                    setUsersFilter(it)
                }
                .setOnTabSelectAction { navigateByTopTabs(it) }
                .setOnFabClickAction { onEmployeeAddEdictClick(NoRecord.num) }
                .setOnPullRefreshAction { updateEmployeesData() }
                .setTabBadgesFlow(
                    combine(
                        mRepository.employeesComplete(EmployeesFilter()),
                        sRepository.users(UsersFilter(newUsers = false)),
                        sRepository.users(UsersFilter(newUsers = true))
                    ) { employees, users, requests ->
                        listOf(
                            Triple(employees.size, Color.Green, Color.Black),
                            Triple(users.size, Color.Green, Color.Black),
                            Triple(requests.size, Color.Red, Color.White)
                        )
                    }.flowOn(Dispatchers.IO)
                )
                .build()
                .apply { setupMainPage(selectedTabIndex, isFabVisible) }

            _createdRecord.value = Pair(Event(employeeId), Event(userId))
            _employeesVisibility.value = Pair(SelectedNumber(employeeId), NoRecord)
            _usersVisibility.value = Pair(SelectedString(userId), NoRecordStr)
        }
    }

    fun onEndOfList(isEndOfList: Boolean) {
        viewModelScope.launch {
            mainPageHandler?.onListEnd?.invoke(isEndOfList)
        }
    }

    /**
     * Common for employees and users ----------------------------------------------------------------------------------------------------------------
     * */
    private val _isComposed = MutableStateFlow(false)
    val setIsComposed: (Boolean) -> Unit = { _isComposed.value = it }

    val scrollToRecord: Flow<Pair<Event<ID>, Event<String>>?> = _createdRecord.flatMapLatest { record ->
        _isComposed.flatMapLatest { isComposed ->
            if (isComposed) flow { emit(record) } else flow { emit(null) }
        }
    }

    val channel = Channel<Job>(capacity = Channel.UNLIMITED).apply { viewModelScope.launch { consumeEach { it.join() } } }

    val isOwnAccount: (String) -> Boolean = { userId ->
        (userId == userRepository.user.email).also { if (it) Toast.makeText(context, "You cannot edit your own account!", Toast.LENGTH_LONG).show() }
    }

    /**
     * Employee logic and operations -----------------------------------------------------------------------------------------------------------------
     * */
    fun setEmployeesVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _employeesVisibility.value = _employeesVisibility.value.setVisibility(dId, aId)
    }

    private fun setEmployeesFilter(filter: BaseFilter) {
        val cpy = _currentEmployeesFilter.value
        _currentEmployeesFilter.value = _currentEmployeesFilter.value.copy(
            stringToSearch = filter.stringToSearch ?: cpy.stringToSearch
        )
    }

    val employees = _employees.flatMapLatest { employees ->
        _employeesVisibility.flatMapLatest { visibility ->
            val cpy = employees.map { it.copy(detailsVisibility = it.teamMember.id == visibility.first.num, isExpanded = it.teamMember.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    fun deleteEmployee(teamMemberId: ID) = viewModelScope.launch {
        mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
        withContext(Dispatchers.IO) {
            mRepository.run {
                deleteTeamMember(teamMemberId).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                            Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                            Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, resource.message))
                        }
                    }
                }
            }
        }
    }

    /**
     * User logic and operations ---------------------------------------------------------------------------------------------------------------------
     * */
    fun setUsersVisibility(dId: SelectedString = NoRecordStr, aId: SelectedString = NoRecordStr) {
        _usersVisibility.value = _usersVisibility.value.setVisibility(dId, aId)
    }

    fun setUsersFilter(filter: BaseFilter) {
        val cpy = _currentUsersFilter.value
        _currentUsersFilter.value = _currentUsersFilter.value.copy(
            stringToSearch = filter.stringToSearch ?: cpy.stringToSearch,
            newUsers = filter.newUsers ?: cpy.newUsers
        )
    }

    val users = _users.flatMapLatest { users ->
        _usersVisibility.flatMapLatest { visibility ->
            val cpy = users.map { it.copy(detailsVisibility = it.email == visibility.first.str, isExpanded = it.email == visibility.second.str) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    private val _isRemoveUserDialogVisible = MutableStateFlow(false)
    val isRemoveUserDialogVisible get() = _isRemoveUserDialogVisible.asStateFlow()

    fun setRemoveUserDialogVisibility(isDialogVisible: Boolean, userId: String = _createdRecord.value.second.peekContent()) {
        if (isOwnAccount(userId)) return
        _isRemoveUserDialogVisible.value = isDialogVisible
    }

    fun removeUser(userId: String) = viewModelScope.launch {
        mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
        withContext(Dispatchers.IO) {
            sRepository.run {
                removeUser(userId).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                            Status.SUCCESS -> {
                                mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                                setRemoveUserDialogVisibility(false)
                                navToRemovedRecord(resource.data?.email)
                            }

                            Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, resource.message))
                        }
                    }
                }
            }
        }
    }

    private fun navigateByTopTabs(tag: SelectedNumber) {
        when (tag) {
            FirstTabId -> appNavigator.tryNavigateTo(route = Route.Main.Team.Employees(NoRecord.num), popUpToRoute = Route.Main.Team, inclusive = true)
            SecondTabId -> appNavigator.tryNavigateTo(route = Route.Main.Team.Users(NoRecordStr.str), popUpToRoute = Route.Main.Team, inclusive = true)
            ThirdTabId -> appNavigator.tryNavigateTo(route = Route.Main.Team.Requests(NoRecordStr.str), popUpToRoute = Route.Main.Team, inclusive = true)
        }
    }

    fun onEmployeeAddEdictClick(employeeId: ID) {
        appNavigator.tryNavigateTo(Route.Main.Team.EmployeeAddEdit(employeeId))
    }

    fun onUserEditClick(userId: String) {
        if (isOwnAccount(userId)) return
        appNavigator.tryNavigateTo(Route.Main.Team.EditUser(userId))
    }

    fun onUserAuthorizeClick(userId: String) {
        if (isOwnAccount(userId)) return
        appNavigator.tryNavigateTo(Route.Main.Team.AuthorizeUser(userId))
    }

    private suspend fun navToRemovedRecord(id: String?) {
        mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
        withContext(Dispatchers.Main) {
            id?.let { appNavigator.tryNavigateTo(Route.Main.Team.Requests(it), Route.Main.Team, inclusive = true) }
        }
    }

    private fun updateEmployeesData() = viewModelScope.launch {
        try {
            mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))

            sRepository.syncUserRoles()
            sRepository.syncUsers()

            mRepository.syncCompanies()
            mRepository.syncJobRoles()
            mRepository.syncDepartments()
            mRepository.syncTeamMembers()

            mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
        } catch (e: Exception) {
            mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, e.message))
        }
    }
}