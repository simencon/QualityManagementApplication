package com.simenko.qmapp.ui.main.team

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.ManufacturingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

private const val TAG = "TeamViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TeamViewModel @Inject constructor(
    private val repository: ManufacturingRepository
) : ViewModel() {

    val isLoadingInProgress = MutableLiveData(false)
    val isNetworkError = MutableLiveData(false)

    fun onNetworkErrorShown() {
        isLoadingInProgress.value = false
        isNetworkError.value = false
    }

    fun insertRecord(record: DomainTeamMember) {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    val channel = repository.insertRecord(this, record)
                    channel.consumeEach {
                    }
                }
                updateTeamFromRoom()
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun deleteRecord(record: DomainTeamMember) = viewModelScope.launch {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    val channel = repository.deleteRecord(this, record)
                    channel.consumeEach {
                    }
                }
                updateTeamFromRoom()
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun updateRecord(record: DomainTeamMember) = viewModelScope.launch {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                withContext(Dispatchers.IO) {
                    val channel = repository.updateRecord(this, record)
                    channel.consumeEach {
                    }
                }
                updateTeamFromRoom()
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    private val _needToUpdateTeamFromRoom = MutableStateFlow(false)
    private val _currentMemberDetails = MutableStateFlow<SelectedNumber>(NoSelectedRecord)

    private val _teamSF = MutableStateFlow<SnapshotStateList<DomainTeamMemberComplete>>(
        mutableStateListOf()
    )

    private fun updateTeamFromRoom() {
        _needToUpdateTeamFromRoom.value = true
    }

    fun changeCurrentTeamMember(id: Int) {
        if (_currentMemberDetails.value.num != id) {
            _currentMemberDetails.value = SelectedNumber(id)
        } else {
            _currentMemberDetails.value = NoSelectedRecord
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val teamSF: StateFlow<SnapshotStateList<DomainTeamMemberComplete>> =
        _needToUpdateTeamFromRoom.flatMapLatest { needToUpdate ->
            _currentMemberDetails.flatMapLatest { visibility ->
                if (needToUpdate) {
                    _teamSF.value = repository.teamCompleteList().toMutableStateList()
                    _needToUpdateTeamFromRoom.value = false
                }
                val cpy = mutableStateListOf<DomainTeamMemberComplete>()
                _teamSF.value.forEach {
                    cpy.add(it.copy())
                }
                flow { emit(cpy.changeOrderVisibility(visibility.num).toMutableStateList()) }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), mutableStateListOf())

    fun syncTeam() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true

                repository.refreshCompanies()
                repository.refreshDepartments()
                repository.refreshTeamMembers()

                updateTeamFromRoom()
                isLoadingInProgress.value = false
                isNetworkError.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    init {
        viewModelScope.launch {
            _teamSF.value = repository.teamCompleteList().toMutableStateList()
        }
    }
}