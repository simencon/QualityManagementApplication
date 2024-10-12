package com.simenko.qmapp.ui.main.structure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.storage.ScrollStates
import com.simenko.qmapp.storage.Storage
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    val storage: Storage,
) : ViewModel() {
    private val _companyId = MutableStateFlow(NoRecord.num)
    private val _departmentsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _subDepartmentsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _channelsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _linesVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _operationsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _departments = _companyId.flatMapLatest { id -> repository.departmentsComplete(id) }.flowOn(Dispatchers.IO)
    private val _subDepartments = _departmentsVisibility.flatMapLatest { repository.subDepartments(it.first.num) }
    private val _channels = _subDepartmentsVisibility.flatMapLatest { repository.channels(it.first.num) }
    private val _lines = _channelsVisibility.flatMapLatest { repository.lines(it.first.num) }
    private val _operations = _linesVisibility.flatMapLatest { repository.operations(it.first.num) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.CompanyStructure.StructureView) {
        viewModelScope.launch {
            if (mainPageHandler == null) {
                _companyId.value = route.companyId
                _departmentsVisibility.value = Pair(SelectedNumber(route.departmentId), NoRecord)
                _subDepartmentsVisibility.value = Pair(SelectedNumber(route.subDepartmentId), NoRecord)
                _channelsVisibility.value = Pair(SelectedNumber(route.channelId), NoRecord)
                _linesVisibility.value = Pair(SelectedNumber(route.lineId), NoRecord)
                _operationsVisibility.value = Pair(SelectedNumber(route.operationId), NoRecord)
            }

            mainPageHandler = MainPageHandler.Builder(Page.COMPANY_STRUCTURE, mainPageState)
                .setOnFabClickAction { if (isSecondColumnVisible.value) onAddLineClick(_channelsVisibility.value.first.num) else onAddDepartmentClick(route.companyId) }
                .setOnPullRefreshAction { updateCompanyStructureData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
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

    private val _channelDetailsId = MutableStateFlow(NoRecord.num)
    val channelDetailsId get() = _channelDetailsId.asStateFlow()

    fun setChannelDetailsId(id: ID) {
        if (id == _channelDetailsId.value) {
            _channelDetailsId.value = NoRecord.num
        } else {
            _channelDetailsId.value = id
        }
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
    private val _viewState = MutableStateFlow(false)
    val setViewState: (Boolean) -> Unit = {
        if (!it) _isComposed.value = BooleanArray(5) { false }
        _viewState.value = it
    }

    private val _isComposed = MutableStateFlow(BooleanArray(5) { false })
    val setIsComposed: (Int, Boolean) -> Unit = { i, value ->
        val cpy = _isComposed.value.copyOf()
        cpy[i] = value
        _isComposed.value = cpy
    }

    val isSecondColumnVisible: StateFlow<Boolean> = _isComposed.flatMapLatest { isComposed ->
        _channelsVisibility.flatMapLatest { channelsVisibility ->
            flow { emit((channelsVisibility.first != NoRecord) && (isComposed.component3())) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val listsIsInitialized: Flow<Pair<Boolean, Boolean>> = _departmentsVisibility.flatMapLatest { depVisibility ->
        _viewState.flatMapLatest { viewState ->
            _departments.flatMapLatest { departments ->
                _lineListIsInitialized.flatMapLatest { sl ->
                    if (viewState)
                        flow {
                            if (depVisibility.first.num != NoRecord.num) {
                                storage.setLong(ScrollStates.DEPARTMENTS.indexKey, departments.map { it.department.id }.indexOf(depVisibility.first.num).toLong())
                                storage.setLong(ScrollStates.DEPARTMENTS.offsetKey, ZeroValue.num)
                                emit(Pair(true, sl))

                            } else {
                                emit(Pair(true, sl))
                            }
                        }
                    else
                        flow {
                            emit(Pair(false, false))
                        }
                }
            }
        }
    }

    private val _lineListIsInitialized: Flow<Boolean> = _linesVisibility.flatMapLatest { linesVisibility ->
        _lines.flatMapLatest { lines ->
            flow {
                if (linesVisibility.first.num != NoRecord.num) {
                    storage.setLong(ScrollStates.LINES.indexKey, lines.map { it.id }.indexOf(linesVisibility.first.num).toLong())
                    storage.setLong(ScrollStates.LINES.offsetKey, ZeroValue.num)
                    emit(true)
                } else {
                    emit(true)
                }
            }
        }
    }


    val departmentsVisibility = _departmentsVisibility.asStateFlow()
    val departments = _departments.flatMapLatest { departments ->
        _departmentsVisibility.flatMapLatest { visibility ->
            _subDepartmentsVisibility.value.first.let { if (it != NoRecord && _isComposed.value.component2()) setSubDepartmentsVisibility(dId = it) }
            _channelsVisibility.value.first.let { if (it != NoRecord && _isComposed.value.component3()) setChannelsVisibility(dId = it) }
            _linesVisibility.value.first.let { if (it != NoRecord && _isComposed.value.component4()) setLinesVisibility(dId = it) }
            _operationsVisibility.value.first.let { if (it != NoRecord && _isComposed.value.component5()) setOperationsVisibility(dId = it) }
            val cpy = departments.map { it.copy(detailsVisibility = it.department.id == visibility.first.num, isExpanded = it.department.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    val subDepartmentsVisibility = _subDepartmentsVisibility.asStateFlow()
    val subDepartments = _subDepartments.flatMapLatest { subDepartment ->
        _subDepartmentsVisibility.flatMapLatest { visibility ->
            _channelsVisibility.value.first.let { if (it != NoRecord && _isComposed.value.component3()) setChannelsVisibility(dId = it) }
            _linesVisibility.value.first.let { if (it != NoRecord && _isComposed.value.component4()) setLinesVisibility(dId = it) }
            _operationsVisibility.value.first.let { if (it != NoRecord && _isComposed.value.component5()) setOperationsVisibility(dId = it) }
            val cpy = subDepartment.map { it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    val channels = _channels.flatMapLatest { channel ->
        _channelsVisibility.flatMapLatest { visibility ->
            _linesVisibility.value.first.let { if (it != NoRecord && _isComposed.value.component4()) setLinesVisibility(dId = it) }
            _operationsVisibility.value.first.let { if (it != NoRecord && _isComposed.value.component5()) setOperationsVisibility(dId = it) }
            val cpy = channel.map { it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    val linesVisibility = _linesVisibility.asStateFlow()
    val lines = _lines.flatMapLatest { line ->
        _linesVisibility.flatMapLatest { visibility ->
            _operationsVisibility.value.first.let { if (it != NoRecord && _isComposed.value.component5()) setOperationsVisibility(dId = it) }
            val cpy = line.map { it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    val operations = _operations.flatMapLatest { operation ->
        _operationsVisibility.flatMapLatest { visibility ->
            val cpy = operation.map { it.copy(detailsVisibility = it.operation.id == visibility.first.num, isExpanded = it.operation.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteDepartmentClick(it: ID) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteDepartment(it).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onDeleteSubDepartmentClick(it: ID) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteSubDepartment(it).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onDeleteChannelClick(it: ID) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteChannel(it).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onDeleteLineClick(it: ID) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteLine(it).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onDeleteOperationClick(it: ID) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.run {
                    deleteOperation(it).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                                Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                                Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, resource.message))
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
                mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))

                repository.syncTeamMembers()
                repository.syncCompanies()
                repository.syncDepartments()
                repository.syncSubDepartments()
                repository.syncChannels()
                repository.syncLines()
                repository.syncOperations()
                repository.syncOperationsFlows()

                mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
            } catch (e: Exception) {
                mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, e.message))
            }
        }
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddDepartmentClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.DepartmentAddEdit(companyId = it))
    }

    fun onEditDepartmentClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.DepartmentAddEdit(companyId = it.first, departmentId = it.second))
    }

    fun onDepartmentProductsClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.DepartmentProductLines(companyId = it.first, departmentId = it.second))
    }

    fun onAddSubDepartmentClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.SubDepartmentAddEdit(departmentId = it))
    }

    fun onEditSubDepartmentClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.SubDepartmentAddEdit(departmentId = it.first, subDepartmentId = it.second))
    }

    fun onSubDepartmentProductsClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.SubDepartmentItemKinds(departmentId = it.first, subDepartmentId = it.second))
    }

    fun onAddChannelClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.ChannelAddEdit(subDepartmentId = it))
    }

    fun onEditChannelClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.ChannelAddEdit(subDepartmentId = it.first, channelId = it.second))
    }

    fun onChannelProductsClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.ChannelItemKeys(subDepartmentId = it.first, channelId = it.second))
    }

    private fun onAddLineClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.LineAddEdit(channelId = it))
    }

    fun onEditLineClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.LineAddEdit(channelId = it.first, lineId = it.second))
    }

    fun onLineProductsClick(it: Pair<ID, ID>) {
        if (_subDepartmentsVisibility.value.first.num != NoRecord.num) {
            appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.LineItems(subDepartmentId = _subDepartmentsVisibility.value.first.num, channelId = it.first, lineId = it.second))
        }
    }

    fun onAddOperationClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.OperationAddEdit(lineId = it))
    }

    fun onEditOperationClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.CompanyStructure.OperationAddEdit(lineId = it.first, operationId = it.second))
    }

    fun onOperationProductsClick(it: ID) {
        TODO("Not yet implemented")
    }
}