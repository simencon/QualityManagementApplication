package com.simenko.qmapp.ui.main.team

import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.DomainTeamMember
import com.simenko.qmapp.domain.entities.DomainTeamMemberComplete
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.ui.main.MainActivityViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TeamViewModel @Inject constructor(
    private val repository: ManufacturingRepository,
) : ViewModel() {
    private lateinit var mainActivityViewModel: MainActivityViewModel

    fun initMainActivityViewModel(viewModel: MainActivityViewModel) {
        this.mainActivityViewModel = viewModel
    }

    fun deleteRecord(teamMemberId: Int) = viewModelScope.launch {
        mainActivityViewModel.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run {
                deleteTeamMember(teamMemberId).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainActivityViewModel.updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> mainActivityViewModel.updateLoadingState(Pair(false, null))
                            Status.ERROR -> mainActivityViewModel.updateLoadingState(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }

    fun insertRecord(record: DomainTeamMember) = viewModelScope.launch {
        mainActivityViewModel.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run {
                insertTeamMember(record).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainActivityViewModel.updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> mainActivityViewModel.updateLoadingState(Pair(false, null))
                            Status.ERROR -> mainActivityViewModel.updateLoadingState(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }

    fun updateRecord(record: DomainTeamMember) = viewModelScope.launch {
        mainActivityViewModel.updateLoadingState(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run {
                updateTeamMember(record).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainActivityViewModel.updateLoadingState(Pair(true, null))
                            Status.SUCCESS -> mainActivityViewModel.updateLoadingState(Pair(false, null))
                            Status.ERROR -> mainActivityViewModel.updateLoadingState(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }

    private val _currentMemberDetails = MutableStateFlow(NoRecord)

    private val _teamSF: Flow<List<DomainTeamMemberComplete>> = repository.teamCompleteList()

    fun changeCurrentTeamMember(id: Int) {
        if (_currentMemberDetails.value.num != id) {
            _currentMemberDetails.value = SelectedNumber(id)
        } else {
            _currentMemberDetails.value = NoRecord
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val teamSF: StateFlow<List<DomainTeamMemberComplete>> =
        _teamSF.flatMapLatest { team ->
            _currentMemberDetails.flatMapLatest { visibility ->
                val cpy = mutableListOf<DomainTeamMemberComplete>()
                team.forEach {
                    cpy.add(
                        it.copy(
                            detailsVisibility = it.teamMember.id == visibility.num,
                        )
                    )
                }
                flow { emit(cpy) }
            }
        }
            .flowOn(Dispatchers.IO)
            .conflate()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun updateEmployeesData() = viewModelScope.launch {
        try {
            mainActivityViewModel.updateLoadingState(Pair(true, null))

            repository.refreshCompanies()
            repository.refreshDepartments()
            repository.syncTeamMembers()

            mainActivityViewModel.updateLoadingState(Pair(false, null))
        } catch (e: Exception) {
            mainActivityViewModel.updateLoadingState(Pair(false, e.message))
        }
    }
}