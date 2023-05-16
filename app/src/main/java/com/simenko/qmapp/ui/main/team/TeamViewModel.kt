package com.simenko.qmapp.ui.main.team

import android.util.Log
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
    private val _teamSF = MutableStateFlow<List<DomainTeamMemberComplete>>(listOf())

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
    val teamSF: Flow<List<DomainTeamMemberComplete>> =
        _needToUpdateTeamFromRoom.flatMapLatest { needToUpdate ->
            _currentMemberDetails.flatMapLatest { visibility ->
                if (needToUpdate) {
                    _teamSF.value = repository.teamCompleteList()
                    _needToUpdateTeamFromRoom.value = false
                }
                _teamSF.map {
                    Log.d(TAG, "debugListSF comparison: ${debugListSF == it}")
                    Log.d(TAG, "debugListSF comparison, old value hashcode: ${debugListSF.hashCode()}\n")
                    debugListSF = it
                    Log.d(TAG, "debugListSF comparison, new value hashcode: ${debugListSF.hashCode()}\n")
                    it.changeOrderVisibility(visibility.num)
                }
                flow { _teamSF.value.changeOrderVisibility(visibility.num) }
            }
        }

    private val _teamF: Flow<List<DomainTeamMemberComplete>> = repository.teamComplete()

    @OptIn(ExperimentalCoroutinesApi::class)
    val teamF: Flow<List<DomainTeamMemberComplete>> =
        _currentMemberDetails.flatMapLatest { currentMember ->
            _teamF.map {
                Log.d(TAG, "debugListF comparison: ${debugListF == it}\n")
                Log.d(TAG, "debugListF comparison, old value hashcode: ${debugListF.hashCode()}\n")
                debugListF = it
                Log.d(TAG, "debugListF comparison, new value hashcode: ${debugListF.hashCode()}\n")
                it.changeOrderVisibility(currentMember.num)
            }
        }

    var debugListF: List<DomainTeamMemberComplete> = listOf()
    var debugListSF: List<DomainTeamMemberComplete> = listOf()

    fun syncTeam() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                repeat(10) {
                    delay(1000L)
                    repository.refreshCompanies()
                    repository.refreshDepartments()
                    repository.refreshTeamMembers()
                }

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
            _teamSF.value = repository.teamCompleteList()
            debugListSF = _teamSF.value

            _teamF.collect() {
                debugListF = it
            }
        }
    }
}