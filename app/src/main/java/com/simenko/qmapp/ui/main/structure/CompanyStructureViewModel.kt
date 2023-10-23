package com.simenko.qmapp.ui.main.structure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ChannelIdParameter
import com.simenko.qmapp.di.DepartmentIdParameter
import com.simenko.qmapp.di.LineIdParameter
import com.simenko.qmapp.di.OperationIdParameter
import com.simenko.qmapp.di.SubDepartmentIdParameter
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.DomainDepartmentComplete
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel
import com.simenko.qmapp.domain.entities.DomainManufacturingLine
import com.simenko.qmapp.domain.entities.DomainManufacturingOperation
import com.simenko.qmapp.domain.entities.DomainSubDepartment
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CompanyStructureViewModel @Inject constructor(
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
    private val _departments = repository.departmentsComplete
    private val _subDepartments = _departmentsVisibility.flatMapLatest { repository.subDepartmentsByDepartment(it.first.num) }
    private val _channels = _subDepartmentsVisibility.flatMapLatest { repository.channelsBySubDepartment(it.first.num) }
    private val _lines = _channelsVisibility.flatMapLatest { repository.linesByChannel(it.first.num) }
    private val _operations = _linesVisibility.flatMapLatest { repository.operationsCompleteByLine(it.first.num) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.COMPANY_STRUCTURE, mainPageState)
            .setOnFabClickAction { onAddDepartmentClick() }
            .setOnPullRefreshAction { updateCompanyStructureData() }
            .build()
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
     * UI state -------------------------------------------------------------------------------------------------------------------------------------
     * */
    val departmentsVisibility = _departmentsVisibility.asStateFlow()
    val departments = _departments.flatMapLatest { departments ->
            _departmentsVisibility.flatMapLatest { visibility ->
                val cyp = mutableListOf<DomainDepartmentComplete>()
                departments.forEach { cyp.add(it.copy(detailsVisibility = it.department.id == visibility.first.num, isExpanded = it.department.id == visibility.second.num)) }
                flow { emit(cyp) }
            }
        }.flowOn(Dispatchers.Default).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val subDepartmentsVisibility = _subDepartmentsVisibility.asStateFlow()
    val subDepartments = _subDepartments.flatMapLatest { subDepartment ->
        _subDepartmentsVisibility.flatMapLatest { visibility ->
            val cyp = mutableListOf<DomainSubDepartment>()
            subDepartment.forEach { cyp.add(it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num)) }
            flow { emit(cyp) }
        }
    }.flowOn(Dispatchers.Default).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val channelsVisibility = _channelsVisibility.asStateFlow()
    val channels = _channels.flatMapLatest { channel ->
        _channelsVisibility.flatMapLatest { visibility ->
            val cyp = mutableListOf<DomainManufacturingChannel>()
            channel.forEach { cyp.add(it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num)) }
            flow { emit(cyp) }
        }
    }.flowOn(Dispatchers.Default).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val linesVisibility = _linesVisibility.asStateFlow()
    val lines = _lines.flatMapLatest { line ->
        _linesVisibility.flatMapLatest { visibility ->
            val cyp = mutableListOf<DomainManufacturingLine>()
            line.forEach { cyp.add(it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num)) }
            flow { emit(cyp) }
        }
    }.flowOn(Dispatchers.Default).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val operations = _operations.flatMapLatest { operation ->
        _operationsVisibility.flatMapLatest { visibility ->
            val cyp = mutableListOf<DomainManufacturingOperation.DomainManufacturingOperationComplete>()
            operation.forEach { cyp.add(it.copy(detailsVisibility = it.operation.id == visibility.first.num, isExpanded = it.operation.id == visibility.second.num)) }
            flow { emit(cyp) }
        }
    }.flowOn(Dispatchers.Default).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun updateCompanyStructureData() {
        viewModelScope.launch {
            try {
                mainPageHandler.updateLoadingState(Pair(true, null))

                repository.syncTeamMembers()
                repository.syncCompanies()
                repository.syncDepartments()
                repository.syncSubDepartments()
                repository.syncChannels()
                repository.syncLines()
                repository.syncOperations()
                repository.syncOperationsFlows()

                mainPageHandler.updateLoadingState(Pair(false, null))
            } catch (e: Exception) {
                mainPageHandler.updateLoadingState(Pair(false, e.message))
            }
        }
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteDepartmentClick(it: Int) {
        TODO("Not yet implemented")
    }
    private fun onAddDepartmentClick() {
        TODO("Not yet implemented")
    }

    fun onEditDepartmentClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onDepartmentProductsClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onDeleteSubDepartmentClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onAddSubDepartmentClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onEditSubDepartmentClick(it: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    fun onSubDepartmentProductsClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onDeleteChannelClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onAddChannelClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onEditChannelClick(it: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    fun onChannelProductsClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onDeleteLineClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onAddLineClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onEditLineClick(it: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    fun onLineProductsClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onDeleteOperationClick(it: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteOperation(it).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler.updateLoadingState(Pair(true, null))
                                Status.SUCCESS -> mainPageHandler.updateLoadingState(Pair(false, null))
                                Status.ERROR -> mainPageHandler.updateLoadingState(Pair(false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onAddOperationClick(it: Int) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.OperationAddEdit.withArgs(it.toString(), NoRecordStr.str))
    }

    fun onEditOperationClick(it: Pair<Int, Int>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.OperationAddEdit.withArgs(it.first.toString(), it.second.toString()))
    }

    fun onOperationProductsClick(it: Int) {
        TODO("Not yet implemented")
    }
}