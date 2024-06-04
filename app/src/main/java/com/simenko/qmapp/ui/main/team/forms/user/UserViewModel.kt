package com.simenko.qmapp.ui.main.team.forms.user

import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.domain.entities.DomainEmployee
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.domain.entities.DomainUserRole
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.RouteCompose
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: SystemRepository,
    private val manufacturingRepository: ManufacturingRepository,
    private val notificationManager: NotificationManagerCompat,
) : ViewModel() {

    private val _user = MutableStateFlow(DomainUser())
    val user get() = _user.asStateFlow()
    private var _isUserToAuthorize = false

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null
        private set

    fun onEntered(userId: String) {
        notificationManager.activeNotifications.find { it.id == Objects.hash(userId) }?.let { notificationManager.cancel(it.id) }
        if (userId != NoRecordStr.str)
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.getUserById(userId).let {
                        _user.value = it
                        _isUserToAuthorize = it.restApiUrl.isNullOrEmpty()
                        mainPageHandler = MainPageHandler.Builder(if (_isUserToAuthorize) Page.AUTHORIZE_USER else Page.EDIT_USER, mainPageState)
                            .setOnNavMenuClickAction { appNavigator.navigateBack() }
                            .setOnFabClickAction { validateInput() }
                            .build()
                    }
                }
            }
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------------------------------
     * */
    private val _userErrors = MutableStateFlow(UserErrors())
    val userErrors get() = _userErrors.asStateFlow()

    private val _userEmployees: Flow<List<DomainEmployee>> = manufacturingRepository.employees

    val userEmployees: StateFlow<List<Triple<ID, String, Boolean>>> = _userEmployees.flatMapLatest { employees ->
        _user.flatMapLatest { user ->
            val cpy = mutableListOf<Triple<ID, String, Boolean>>()
            employees.forEach { cpy.add(Triple(it.id, it.fullName, it.id == user.teamMemberId)) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setUserEmployee(id: ID) {
        if (_user.value.teamMemberId != id) {
            _user.value = _user.value.copy(teamMemberId = id)
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

    private fun validateInput(user: DomainUser = _user.value) {
        val errorMsg = buildString {
            println("validateInput - ${user.teamMemberId}")
            if (user.teamMemberId == NoRecord.num) {
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

        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg)
        else _fillInState.value = FillInSuccessState
    }

    /**
     * Data Base/REST API Operations --------------------------------------------------------------------------------------------------------------------------
     * */
    fun makeUser() = viewModelScope.launch {
        mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run { if (_isUserToAuthorize) authorizeUser(_user.value) else updateUserCompanyData(_user.value) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                        Status.SUCCESS -> navBackToRecord(resource.data?.email)
                        Status.ERROR -> {
                            mainPageHandler?.updateLoadingState?.invoke(Pair(true, resource.message))
                            _fillInState.value = FillInInitialState
                        }
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(id: String?) {
        mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
        withContext(Dispatchers.Main) {
            id?.let { appNavigator.tryNavigateTo(route = RouteCompose.Main.Team.Users(it), popUpToRoute = RouteCompose.Main.Team, inclusive = true) }
        }
    }
}

data class UserErrors(
    var teamMemberError: Boolean = false,
    var rolesError: Boolean = false,
    var enabledError: Boolean = false
)