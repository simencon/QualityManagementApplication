package com.simenko.qmapp.ui.main.structure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ChannelIdParameter
import com.simenko.qmapp.di.CompanyIdParameter
import com.simenko.qmapp.di.DepartmentIdParameter
import com.simenko.qmapp.di.LineIdParameter
import com.simenko.qmapp.di.OperationIdParameter
import com.simenko.qmapp.di.SubDepartmentIdParameter
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.SelectedNumber
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CompanyStructureViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    @CompanyIdParameter private val companyId: Int,
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
    private val _departments = repository.departmentsComplete(companyId)
    private val _subDepartments = _departmentsVisibility.flatMapLatest { repository.subDepartments(it.first.num) }
    private val _channels = _subDepartmentsVisibility.flatMapLatest { repository.channels(it.first.num) }
    private val _lines = _channelsVisibility.flatMapLatest { repository.lines(it.first.num) }
    private val _operations = _linesVisibility.flatMapLatest { repository.operations(it.first.num) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.COMPANY_STRUCTURE, mainPageState)
            .setOnFabClickAction { onAddDepartmentClick(companyId) }
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
    private val _isComposed = MutableStateFlow(false)
    val setIsComposed: (Boolean) -> Unit = { _isComposed.value = it }

    val scrollToRecord: SharedFlow<StructureIds?> = _createdRecord.flatMapLatest { record ->
        _isComposed.flatMapLatest { isComposed ->
            if (isComposed) flow { emit(record) } else flow { emit(null) }
        }
    }.flowOn(Dispatchers.IO).conflate().shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val channel = Channel<Job>(capacity = Channel.UNLIMITED).apply { viewModelScope.launch { consumeEach { it.join() } } }


    val departmentsVisibility = _departmentsVisibility.asStateFlow()
    val departments = _departments.flatMapLatest { departments ->
        _departmentsVisibility.flatMapLatest { visibility ->
            if (_isComposed.value) println("Departments - lifecycleState: departments triggered")
            _subDepartmentsVisibility.value.first.let { if (it != NoRecord && _isComposed.value) setSubDepartmentsVisibility(dId = it) }
            _channelsVisibility.value.first.let { if (it != NoRecord && _isComposed.value) setChannelsVisibility(dId = it) }
            _linesVisibility.value.first.let { if (it != NoRecord && _isComposed.value) setLinesVisibility(dId = it) }
            _operationsVisibility.value.first.let { if (it != NoRecord && _isComposed.value) setOperationsVisibility(dId = it) }
            val cpy = departments.map { it.copy(detailsVisibility = it.department.id == visibility.first.num, isExpanded = it.department.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    val subDepartmentsVisibility = _subDepartmentsVisibility.asStateFlow()
    val subDepartments = _subDepartments.flatMapLatest { subDepartment ->
        _subDepartmentsVisibility.flatMapLatest { visibility ->
            if (_isComposed.value) println("Departments - lifecycleState: subDepartments triggered")
            _channelsVisibility.value.first.let { if (it != NoRecord && _isComposed.value) setChannelsVisibility(dId = it) }
            _linesVisibility.value.first.let { if (it != NoRecord && _isComposed.value) setLinesVisibility(dId = it) }
            _operationsVisibility.value.first.let { if (it != NoRecord && _isComposed.value) setOperationsVisibility(dId = it) }
            val cpy = subDepartment.map { it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    val channelsVisibility = _channelsVisibility.asStateFlow()
    val channels = _channels.flatMapLatest { channel ->
        _channelsVisibility.flatMapLatest { visibility ->
            if (_isComposed.value) println("Departments - lifecycleState: channels triggered")
            _linesVisibility.value.first.let { if (it != NoRecord && _isComposed.value) setLinesVisibility(dId = it) }
            _operationsVisibility.value.first.let { if (it != NoRecord && _isComposed.value) setOperationsVisibility(dId = it) }
            val cpy = channel.map { it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    val linesVisibility = _linesVisibility.asStateFlow()
    val lines = _lines.flatMapLatest { line ->
        _linesVisibility.flatMapLatest { visibility ->
            if (_isComposed.value) println("Departments - lifecycleState: lines triggered")
            _operationsVisibility.value.first.let { if (it != NoRecord && _isComposed.value) setOperationsVisibility(dId = it) }
            val cpy = line.map { it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    val operations = _operations.flatMapLatest { operation ->
        _operationsVisibility.flatMapLatest { visibility ->
            if (_isComposed.value) println("Departments - lifecycleState: operations triggered")
            val cpy = operation.map { it.copy(detailsVisibility = it.operation.id == visibility.first.num, isExpanded = it.operation.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteSubDepartmentClick(it: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteSubDepartment(it).consumeEach { event ->
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

    fun onDeleteChannelClick(it: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteChannel(it).consumeEach { event ->
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

    fun onDeleteLineClick(it: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteLine(it).consumeEach { event ->
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

    private fun onAddDepartmentClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onEditDepartmentClick(it: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    fun onDepartmentProductsClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onAddSubDepartmentClick(it: Int) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.SubDepartmentAddEdit.withArgs(it.toString(), NoRecordStr.str))
    }

    fun onEditSubDepartmentClick(it: Pair<Int, Int>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.SubDepartmentAddEdit.withArgs(it.first.toString(), it.second.toString()))
    }

    fun onSubDepartmentProductsClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onAddChannelClick(it: Int) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.ChannelAddEdit.withArgs(it.toString(), NoRecordStr.str))
    }

    fun onEditChannelClick(it: Pair<Int, Int>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.ChannelAddEdit.withArgs(it.first.toString(), it.second.toString()))
    }

    fun onChannelProductsClick(it: Int) {
        TODO("Not yet implemented")
    }

    fun onAddLineClick(it: Int) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.LineAddEdit.withArgs(it.toString(), NoRecordStr.str))
    }

    fun onEditLineClick(it: Pair<Int, Int>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.LineAddEdit.withArgs(it.first.toString(), it.second.toString()))
    }

    fun onLineProductsClick(it: Int) {
        TODO("Not yet implemented")
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