package com.simenko.qmapp.ui.main.team

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import com.simenko.qmapp.domain.DomainTeamMemberComplete
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.room.implementation.getDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flatMapLatest
import java.io.IOException
import javax.inject.Inject


@JvmInline
value class SwitchState(val number: Int)

val FirstValue = SwitchState(-1)
val SecondValue = SwitchState(4)

private const val TAG = "TeamViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class TeamViewModel @Inject constructor(
    context: Context
) : ViewModel() {
    private val roomDatabase = getDatabase(context)

    private val manufacturingRepository =
        QualityManagementManufacturingRepository(roomDatabase)

    val isLoadingInProgress = MutableLiveData(false)
    val isNetworkError = MutableLiveData(false)

    fun flipTheSwitch() {
        if (latestSwitchState.value == FirstValue)
            latestSwitchState.value = SecondValue
        else
            latestSwitchState.value = FirstValue
    }

    private val latestSwitchState = MutableStateFlow<SwitchState>(FirstValue);

    private val _teamFow1: MutableLiveData<List<DomainTeamMemberComplete>> =
        MutableLiveData(mutableListOf())
    private val _teamFow2: MutableLiveData<List<DomainTeamMemberComplete>> =
        MutableLiveData(mutableListOf())

    private val teamFlow: Flow<List<DomainTeamMemberComplete>> =
        latestSwitchState.flatMapLatest { switchState ->
            Log.d(TAG, "switchMap is applied")
            when (switchState) {
                FirstValue -> manufacturingRepository.teamComplete()
                else -> manufacturingRepository.teamCompleteByDepartment(switchState.number)
            }
        }

    val teamS: SnapshotStateList<DomainTeamMemberComplete> = mutableStateListOf()
    lateinit var teamJob: Job

    fun addTeamToSnapShot() {
        teamJob = viewModelScope.launch {
            teamFlow.cancellable().collect() {
                teamS.apply {
                    clear()
                    addAll(it)
                }
                this.coroutineContext.job.cancel()
            }
        }
    }

    fun changeTeamMembersDetailsVisibility(itemId: Int) {
        val iterator = teamS.listIterator()

        while (iterator.hasNext()) {
            val current = iterator.next()
            if (current.teamMember.id == itemId) {
                iterator.set(current.copy(detailsVisibility = !current.detailsVisibility))
            } else {
                iterator.set(current.copy(detailsVisibility = false))
            }
        }
    }

    fun syncTeam() {
        viewModelScope.launch {
            try {
                isLoadingInProgress.value = true
                flipTheSwitch()

                repeat(10) {
                    delay(1000L)
                    manufacturingRepository.refreshCompanies()
                    manufacturingRepository.refreshDepartments()
                    manufacturingRepository.refreshTeamMembers()
                }

                isLoadingInProgress.value = false
                isNetworkError.value = false
                teamJob.start()

            } catch (networkError: IOException) {
                delay(500)
                isNetworkError.value = true
            }
        }
    }

    fun onNetworkErrorShown() {
        isLoadingInProgress.value = false
        isNetworkError.value = false
    }
}