package com.simenko.qmapp.ui.main.team

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import androidx.lifecycle.map
import com.simenko.qmapp.domain.DomainTeamMember
import com.simenko.qmapp.domain.DomainTeamMemberComplete
import com.simenko.qmapp.domain.changeDetails
import com.simenko.qmapp.repository.ManufacturingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject


@JvmInline
value class CurrentMember(val id: Int)

val NoCurrentMember = CurrentMember(-1)

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

    private val _currentMember = MutableStateFlow<CurrentMember>(NoCurrentMember)

    private val _teamF = repository.teamComplete()

    fun changeCurrentTeamMember(id: Int) {
        _currentMember.value = CurrentMember(id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val teamSF: Flow<List<DomainTeamMemberComplete>> = _currentMember.flatMapLatest { currentMember ->
        _teamF.map {
            it.changeDetails(currentMember.id)
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