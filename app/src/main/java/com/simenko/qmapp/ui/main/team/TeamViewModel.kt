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
    private val _currentTeamMemberVisibility = MutableStateFlow(Pair(NoRecord, NoRecord))
    fun setCurrentOrderVisibility(
        dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord
    ) {
        _currentTeamMemberVisibility.value = _currentTeamMemberVisibility.value.setVisibility(dId, aId)
    }

    val employees: StateFlow<List<DomainTeamMemberComplete>> =
        _employees.flatMapLatest { team ->
            _currentTeamMemberVisibility.flatMapLatest { visibility ->
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

    val users: StateFlow<List<DomainUser>> = _users.flatMapLatest {
        flow { emit(it) }
    }.flowOn(Dispatchers.IO)
        .conflate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun updateEmployeesData() = viewModelScope.launch {
        try {
            _mainActivityViewModel.updateLoadingState(Pair(true, null))

            manufacturingRepository.syncCompanies()
            manufacturingRepository.syncDepartments()
            manufacturingRepository.syncTeamMembers()

            _mainActivityViewModel.updateLoadingState(Pair(false, null))
        } catch (e: Exception) {
            _mainActivityViewModel.updateLoadingState(Pair(false, e.message))
        }
    }
}