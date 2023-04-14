package com.simenko.qmapp.ui.main.investigations

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.DomainOrderComplete
import com.simenko.qmapp.repository.QualityManagementInvestigationsRepository
import com.simenko.qmapp.room.implementation.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InvestigationsViewModel @Inject constructor(
    context: Context
) : ViewModel() {
    private val roomDatabase = getDatabase(context)

    private val investigationsRepository =
        QualityManagementInvestigationsRepository(roomDatabase)

    val ordersS: SnapshotStateList<DomainOrderComplete> = mutableStateListOf()

    fun addOrdersToSnapShot(
        currentStatus: Int = 0,
        lookUpNumber: String = ""
    ) {
        viewModelScope.launch {
            investigationsRepository.completeOrders().collect() { it ->
                ordersS.apply {
                    this.clear()
                    it.forEach {itF ->
                        if (itF.order.statusId == currentStatus || currentStatus == 0)
                            if (itF.order.orderNumber.toString()
                                    .contains(lookUpNumber) || lookUpNumber == "0"
                            )
                                this.add(itF)
                    }
                }
            }
        }
    }
}