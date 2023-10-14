package com.simenko.qmapp.ui.main.structure

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.di.ChannelIdParameter
import com.simenko.qmapp.di.DepartmentIdParameter
import com.simenko.qmapp.di.LineIdParameter
import com.simenko.qmapp.di.OperationIdParameter
import com.simenko.qmapp.di.SubDepartmentIdParameter
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class StructureViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    @DepartmentIdParameter private val depId: Int,
    @SubDepartmentIdParameter private val subDepId: Int,
    @ChannelIdParameter private val channelId: Int,
    @LineIdParameter private val lineId: Int,
    @OperationIdParameter private val operationId: Int,
) : ViewModel() {
    data class StructureIds(val departmentId: Event<Int>, val subDepartmentId: Event<Int>, val channelId: Event<Int>, val lineId: Event<Int>, val operationId: Event<Int>)

    private val _isLoadingInProgress: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _createdRecord: MutableStateFlow<StructureIds> = MutableStateFlow(StructureIds(Event(depId), Event(subDepId), Event(channelId), Event(lineId), Event(operationId)))
    private val _departmentsVisibility = MutableStateFlow(Pair(SelectedNumber(depId), NoRecord))
    private val _subDepartmentsVisibility = MutableStateFlow(Pair(SelectedNumber(subDepId), NoRecord))
    private val _channelsVisibility: MutableStateFlow<Pair<SelectedNumber, SelectedNumber>> = MutableStateFlow(Pair(SelectedNumber(channelId), NoRecord))
    private val _linesVisibility = MutableStateFlow(Pair(SelectedNumber(lineId), NoRecord))
    private val _operationsVisibility = MutableStateFlow(Pair(SelectedNumber(operationId), NoRecord))

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.COMPANY_STRUCTURE, mainPageState)
            .setOnFabClickAction { onAddChannelClick() }
            .setOnPullRefreshAction { syncCompanyStructureData() }
            .build()
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddChannelClick() {
        TODO("Not yet implemented")
    }

    /**
     * UI operations
     * */
    fun setDepartmentsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _departmentsVisibility.value = _departmentsVisibility.value.setVisibility(dId, aId)
    }

    fun setSubDepartmentsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _subDepartmentsVisibility.value = _subDepartmentsVisibility.value.setVisibility(dId, aId)
    }

    fun setChannelsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _channelsVisibility.value = _channelsVisibility.value.setVisibility(dId, aId)
    }

    fun setLinesVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _linesVisibility.value = _linesVisibility.value.setVisibility(dId, aId)
    }

    fun setOperationsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _operationsVisibility.value = _operationsVisibility.value.setVisibility(dId, aId)
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun syncCompanyStructureData() {
        TODO("Not yet implemented")
    }

}