package com.simenko.qmapp.ui.main.team.forms.user

import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.domain.entities.DomainEmployee
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.domain.entities.DomainUserRole
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.ui.common.TopScreenState
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInError
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInInitialState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInSuccess
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
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
import java.util.Objects
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserViewModel @Inject constructor(
    @Named("MainActivity") private val appNavigator: AppNavigator,
    private val topScreenState: TopScreenState,
    private val repository: SystemRepository,
    private val manufacturingRepository: ManufacturingRepository,
    private val notificationManager: NotificationManagerCompat
) : ViewModel() {
    private fun updateLoadingState(state: Pair<Boolean, String?>) {
        topScreenState.trySendLoadingState(state)
    }

    private val _isUserToAuthorize = mutableStateOf(false)
    fun setupTopScreen(addEditMode: AddEditMode) {
        topScreenState.trySendTopScreenSetup(
            addEditMode = Pair(addEditMode) {
                _isUserToAuthorize.value = addEditMode == AddEditMode.AUTHORIZE_USER
                validateInput()
            },
            refreshAction = {},
            filterAction = {}
        )
    }

    fun clearNotificationIfExists(email: String) {
        notificationManager.activeNotifications.find { it.id == Objects.hash(email) }?.let { notificationManager.cancel(it.id) }
    }

    private val _user = MutableStateFlow(DomainUser())
    val user get() = _user.asStateFlow()
    fun loadUser(id: String) {
        _user.value = repository.getUserById(id)
    }

    private val _userErrors = MutableStateFlow(UserErrors())
    val userErrors get() = _userErrors.asStateFlow()

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

    private val _currentUserRoleVisibility = MutableStateFlow(Pair(NoRecordStr, NoRecordStr))
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

    private val _isAddRoleDialogVisible = MutableStateFlow(false)
    val isAddRoleDialogVisible = _isAddRoleDialogVisible.asStateFlow()
    fun setAddRoleDialogVisibility(value: Boolean) {
        _isAddRoleDialogVisible.value = value
    }

    fun deleteUserRole(id: String) {
        val roles = _user.value.roles?.toHashSet()
        roles?.remove(id)
        _user.value = _user.value.copy(roles = roles)
    }

    fun addUserRole(role: Triple<String, String, String>) {
        val roleToAdd = "${role.first}:${role.second}:${role.third}"
        val roles = _user.value.roles.let { it?.toHashSet() ?: mutableSetOf() }
        roles.add(roleToAdd)
        _user.value = _user.value.copy(roles = roles)
        _userErrors.value = _userErrors.value.copy(rolesError = false)
        _fillInState.value = FillInInitialState
    }

    fun setUserIsEnabled(value: Boolean) {
        if (_user.value.enabled != value) {
            _user.value = _user.value.copy(enabled = value)
            _userErrors.value = _userErrors.value.copy(enabledError = false)
            _fillInState.value = FillInInitialState
        }
    }

    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()

    fun validateInput(user: DomainUser = _user.value) {
        val errorMsg = buildString {
            println("validateInput - ${user.teamMemberId}")
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
    fun makeUser(userToAuthorize: Boolean = _isUserToAuthorize.value) = viewModelScope.launch {
        updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run { if (userToAuthorize) authorizeUser(_user.value) else updateUserCompanyData(_user.value) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> updateLoadingState(Pair(true, null))
                        Status.SUCCESS -> navBackToRecord(resource.data?.email)
                        Status.ERROR -> {
                            updateLoadingState(Pair(true, resource.message))
                            _fillInState.value = FillInInitialState
                        }
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(id: String?) {
        updateLoadingState(Pair(false, null))
        withContext(Dispatchers.Main) {
            id?.let {
                appNavigator.tryNavigateTo(
                    route = Route.Main.Team.Users.withArgs(it),
                    popUpToRoute = Route.Main.Team.Employees.link,
                    inclusive = true
                )
            }
        }
    }
}

data class UserErrors(
    var teamMemberError: Boolean = false,
    var rolesError: Boolean = false,
    var enabledError: Boolean = false
)