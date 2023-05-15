package com.simenko.qmapp.ui.main.team

import androidx.lifecycle.*
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.repository.ManufacturingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

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
                isLoadingInProgress.value = false
            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    private val _currentMemberDetails = MutableStateFlow<SelectedNumber>(NoSelectedRecord)

    private val _teamF = repository.teamComplete()

    fun changeCurrentTeamMember(id: Int) {
        if (_currentMemberDetails.value.num != id) {
            _currentMemberDetails.value = SelectedNumber(id)
        } else {
            _currentMemberDetails.value = NoSelectedRecord
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val teamSF: Flow<List<DomainTeamMemberComplete>> = _currentMemberDetails.flatMapLatest { currentMember ->
        _teamF.map {
            val result = it.changeOrderVisibility(currentMember.num)
            result
        }
    }

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
}