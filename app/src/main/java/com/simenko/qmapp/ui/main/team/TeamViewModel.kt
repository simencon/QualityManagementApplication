package com.simenko.qmapp.ui.main.team

import androidx.compose.material3.FabPosition
import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.DomainTeamMember
import com.simenko.qmapp.domain.entities.DomainTeamMemberComplete
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.SystemRepository
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
    private lateinit var _mainActivityViewModel: MainActivityViewModel

    fun initMainActivityViewModel(viewModel: MainActivityViewModel) {
        this._mainActivityViewModel = viewModel
    }

    fun onListEnd(position: FabPosition) {
        _mainActivityViewModel.onListEnd(position)
    }

    fun deleteRecord(teamMemberId: Int) = viewModelScope.launch {
        _mainActivityViewModel.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            manufacturingRepository.run {
                deleteTeamMember(teamMemberId).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> _mainActivityViewModel.updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> _mainActivityViewModel.updateLoadingState(Pair(false, null))
                            Status.ERROR -> _mainActivityViewModel.updateLoadingState(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }

    fun insertRecord(record: DomainTeamMember) = viewModelScope.launch {
        _mainActivityViewModel.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            manufacturingRepository.run {
                insertTeamMember(record).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> _mainActivityViewModel.updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> _mainActivityViewModel.updateLoadingState(Pair(false, null))
                            Status.ERROR -> _mainActivityViewModel.updateLoadingState(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }

    fun updateRecord(record: DomainTeamMember) = viewModelScope.launch {
        _mainActivityViewModel.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            manufacturingRepository.run {
                updateTeamMember(record).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> _mainActivityViewModel.updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> _mainActivityViewModel.updateLoadingState(Pair(false, null))
                            Status.ERROR -> _mainActivityViewModel.updateLoadingState(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }

    private val _employees: Flow<List<DomainTeamMemberComplete>> = manufacturingRepository.teamCompleteList()

    /**
     * Visibility operations
     * */
    private val _currentEmployeeVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    fun setCurrentEmployeeVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _currentEmployeeVisibility.value = _currentEmployeeVisibility.value.setVisibility(dId, aId)
    }

    val employees: StateFlow<List<DomainTeamMemberComplete>> = _employees.flatMapLatest { team ->
        _currentEmployeeVisibility.flatMapLatest { visibility ->
            val cpy = mutableListOf<DomainTeamMemberComplete>()
            team.forEach {
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
        .flowOn(Dispatchers.IO)
        .conflate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


    private val _users: Flow<List<DomainUser>> = systemRepository.usersList()

    /**
     * Visibility operations
     * */
    private val _currentUserVisibility = MutableStateFlow(Pair(NoRecordStr, NoRecordStr))
    fun setCurrentUserVisibility(dId: SelectedString = NoRecordStr, aId: SelectedString = NoRecordStr) {
        _currentUserVisibility.value = _currentUserVisibility.value.setVisibility(dId, aId)
    }

    /**
     * Filtering operations
     * */
    private val _currentUsersFilter = MutableStateFlow(UsersFilter())
    fun setUsersFilter(newUsers: Boolean = false) {
        _currentUsersFilter.value = UsersFilter(newUsers = newUsers)
    }

    /**
     * The result flow
     * */
    val users: StateFlow<List<DomainUser>> = _users.flatMapLatest { users ->
        _currentUserVisibility.flatMapLatest { visibility ->
            _currentUsersFilter.flatMapLatest { filter ->
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
    }.flowOn(Dispatchers.IO)
        .conflate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun updateEmployeesData() = viewModelScope.launch {
        try {
            _mainActivityViewModel.updateLoadingState(Pair(true, null))

            systemRepository.syncUserRoles()
            systemRepository.syncUsers()

            manufacturingRepository.syncCompanies()
            manufacturingRepository.syncDepartments()
            manufacturingRepository.syncTeamMembers()

            _mainActivityViewModel.updateLoadingState(Pair(false, null))
        } catch (e: Exception) {
            _mainActivityViewModel.updateLoadingState(Pair(false, e.message))
        }
    }
}