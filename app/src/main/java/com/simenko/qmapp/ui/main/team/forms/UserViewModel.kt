package com.simenko.qmapp.ui.main.team.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainEmployee
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.SystemRepository
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInError
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInInitialState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInState
import com.simenko.qmapp.ui.user.registration.enterdetails.FillInSuccess
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModel @Inject constructor(
    private val repository: SystemRepository,
    private val manufacturingRepository: ManufacturingRepository
) : ViewModel() {
    private val _user = MutableStateFlow(DomainUser())
    private val _userErrors = MutableStateFlow(UserErrors())
    fun loadUser(id: String) {
        _user.value = repository.getUserById(id)
    }
    val user get() = _user.asStateFlow()
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
}

data class UserErrors(
    var teamMemberError: Boolean = false,
    var rolesError: Boolean = false,
    var enabledError: Boolean = false
)