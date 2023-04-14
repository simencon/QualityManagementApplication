package com.simenko.qmapp.ui.main.team

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.DomainTeamMemberComplete
import com.simenko.qmapp.repository.QualityManagementManufacturingRepository
import com.simenko.qmapp.room.implementation.getDatabase
import kotlinx.coroutines.launch
import javax.inject.Inject

class TeamViewModel @Inject constructor(
    context: Context
) : ViewModel() {
    private val roomDatabase = getDatabase(context)

    private val manufacturingRepository =
        QualityManagementManufacturingRepository(roomDatabase)

    val teamS: SnapshotStateList<DomainTeamMemberComplete> = mutableStateListOf()

    fun addTeamToSnapShot() {
        viewModelScope.launch {
            manufacturingRepository.teamComplete().collect() {
                teamS.apply {
                    clear()
                    addAll(it)
                }
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
}