package com.simenko.qmapp.ui.main.team.forms.user.subforms.role

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.repository.SystemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RoleViewModel @Inject constructor(
    private val repository: SystemRepository
) : ViewModel() {
    private val _userRoleToAdd = MutableStateFlow(Triple(NoRecordStr.str, NoRecordStr.str, NoRecordStr.str))
    val userRoleToAdd = _userRoleToAdd.asStateFlow()
    private val _userRoleToAddErrors = MutableStateFlow(Triple(false, false, false))
    val userRoleToAddErrors get() = _userRoleToAddErrors.asStateFlow()
    fun clearUserRoleToAdd() {
        _userRoleToAdd.value = Triple(NoRecordStr.str, NoRecordStr.str, NoRecordStr.str)
    }

    fun clearUserRoleToAddErrors() {
        _userRoleToAddErrors.value = Triple(false, false, false)
        _roleFillInState.value = FillInInitialState
    }

    private val _user = MutableStateFlow(DomainUser())
    fun setUser(user: DomainUser) {
        _user.value = user
    }

    private val _availableUserRoles = repository.userRoles.flatMapLatest { roles ->
        _user.flatMapLatest { user ->
            val cpy = roles.toMutableList()
            cpy.removeAll(user.rolesAsUserRoles().toSet())
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val roleFunctions = _availableUserRoles.flatMapLatest { roles ->
        _userRoleToAdd.flatMapLatest { roleToAdd ->
            val cpy = mutableListOf<Pair<String, Boolean>>()
            roles.map { it.function }.toSet().sorted().forEach {
                cpy.add(Pair(it, it == roleToAdd.first))
            }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setRoleFunction(value: String) {
        _userRoleToAdd.value = _userRoleToAdd.value.copy(first = value, second = NoRecordStr.str, third = NoRecordStr.str)
        _userRoleToAddErrors.value = _userRoleToAddErrors.value.copy(first = false)
        _roleFillInState.value = FillInInitialState
    }

    val roleLevels = _availableUserRoles.flatMapLatest { roles ->
        _userRoleToAdd.flatMapLatest { roleToAdd ->
            val cpy = mutableListOf<Pair<String, Boolean>>()
            roles.filter { it.function == roleToAdd.first }.map { it.roleLevel }.toSet().sorted().forEach {
                cpy.add(Pair(it, it == roleToAdd.second))
            }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setRoleLevel(value: String) {
        _userRoleToAdd.value = _userRoleToAdd.value.copy(second = value, third = NoRecordStr.str)
        _userRoleToAddErrors.value = _userRoleToAddErrors.value.copy(second = false)
        _roleFillInState.value = FillInInitialState
    }

    val roleAccesses = _availableUserRoles.flatMapLatest { roles ->
        _userRoleToAdd.flatMapLatest { roleToAdd ->
            val cpy = mutableListOf<Pair<String, Boolean>>()
            roles.filter { it.function == roleToAdd.first && it.roleLevel == roleToAdd.second }.map { it.accessLevel }.toSet().sorted().forEach {
                cpy.add(Pair(it, it == roleToAdd.third))
            }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setRoleAccess(value: String) {
        _userRoleToAdd.value = _userRoleToAdd.value.copy(third = value)
        _userRoleToAddErrors.value = _userRoleToAddErrors.value.copy(third = false)
        _roleFillInState.value = FillInInitialState
    }

    private val _roleFillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val roleFillInState get() = _roleFillInState.asStateFlow()

    fun validateInput(userRole: Triple<String, String, String> = _userRoleToAdd.value) {
        val errorMsg = buildString {
            if (userRole.first == NoRecordStr.str) {
                _userRoleToAddErrors.value = _userRoleToAddErrors.value.copy(first = true)
                append("Function is mandatory\n")
            }
            if (userRole.second == NoRecordStr.str) {
                _userRoleToAddErrors.value = _userRoleToAddErrors.value.copy(second = true)
                append("Role level is mandatory\n")
            }
            if (userRole.third == NoRecordStr.str) {
                _userRoleToAddErrors.value = _userRoleToAddErrors.value.copy(third = true)
                append("Access level is mandatory\n")
            }
        }
        if (errorMsg.isNotEmpty()) _roleFillInState.value = FillInErrorState(errorMsg)
        else _roleFillInState.value = FillInSuccessState
    }
}