package com.simenko.qmapp.ui.main.products.characteristics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ProductsRepository
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
class CharacteristicsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
) : ViewModel() {
    private val _productLine = MutableStateFlow(NoRecord.num)
    private val _charGroupVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _charSubGroupVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _characteristicVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _metricVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _charGroups = _productLine.flatMapLatest { repository.charGroups(it) }
    private val _charSubGroups = _charGroupVisibility.flatMapLatest { repository.charSubGroups(it.first.num) }
    private val _characteristics = _charSubGroupVisibility.flatMapLatest { repository.characteristicsByParent(it.first.num) }
    private val _metrics = _characteristicVisibility.flatMapLatest { repository.metrics(it.first.num) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null
    fun onEntered(route: Route.Main.ProductLines.Characteristics.CharacteristicsList) {
        viewModelScope.launch {
            if (mainPageHandler == null) {
                _productLine.value = route.productLineId
                _charGroupVisibility.value = Pair(SelectedNumber(route.charGroupId), NoRecord)
                _charSubGroupVisibility.value = Pair(SelectedNumber(route.charSubGroupId), NoRecord)
                _characteristicVisibility.value = Pair(SelectedNumber(route.characteristicId), NoRecord)
                _metricVisibility.value = Pair(SelectedNumber(route.metricId), NoRecord)
            }

            mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_LINE_CHARACTERISTICS, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { onAddCharGroupClick(Pair(route.productLineId, NoRecord.num)) }
                .setOnPullRefreshAction { updateCharacteristicsData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setGroupsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _charGroupVisibility.value = _charGroupVisibility.value.setVisibility(dId, aId)
    }

    fun setCharSubGroupsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _charSubGroupVisibility.value = _charSubGroupVisibility.value.setVisibility(dId, aId)
    }

    fun setCharacteristicsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _characteristicVisibility.value = _characteristicVisibility.value.setVisibility(dId, aId)
    }

    fun setMetricsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _metricVisibility.value = _metricVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productLine = _productLine.flatMapLatest { flow { emit(repository.productLine(it)) } }.flowOn(Dispatchers.IO)

    private val _viewState = MutableStateFlow(false)
    val setViewState: (Boolean) -> Unit = {
        if (!it) _isComposed.value = BooleanArray(4) { false }
        _viewState.value = it
    }

    private val _isComposed = MutableStateFlow(BooleanArray(4) { false })
    val setIsComposed: (Int, Boolean) -> Unit = { i, value ->
        val cpy = _isComposed.value.copyOf()
        cpy[i] = value
        _isComposed.value = cpy
    }

    val isSecondColumnVisible: StateFlow<Boolean> = _isComposed.flatMapLatest { isComposed ->
        _characteristicVisibility.flatMapLatest { visibility ->
            flow { emit((visibility.first != NoRecord) && (isComposed.component3())) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val listsIsInitialized: Flow<Pair<Boolean, Boolean>> = _viewState.flatMapLatest { viewState ->
        _charGroups.flatMapLatest { firstList ->
            _secondListIsInitialized.flatMapLatest { secondListState ->
                if (viewState)
                    flow {
                        if (_charGroupVisibility.value.first.num != NoRecord.num) {
                            storage.setLong(ScrollStates.CHAR_GROUPS.indexKey, firstList.map { it.charGroup.id }.indexOf(_charGroupVisibility.value.first.num).toLong())
                            storage.setLong(ScrollStates.CHAR_GROUPS.offsetKey, ZeroValue.num)
                            emit(Pair(true, secondListState))
                        } else {
                            emit(Pair(true, secondListState))
                        }
                    }
                else
                    flow {
                        emit(Pair(false, false))
                    }
            }
        }
    }

    private val _secondListIsInitialized: Flow<Boolean> = _metrics.flatMapLatest { secondList ->
        flow {
            if (_metricVisibility.value.first.num != NoRecord.num) {
                storage.setLong(ScrollStates.METRICS.indexKey, secondList.map { it.id }.indexOf(_metricVisibility.value.first.num).toLong())
                storage.setLong(ScrollStates.METRICS.offsetKey, ZeroValue.num)
                emit(true)
            } else {
                emit(true)
            }
        }
    }

    val charGroupVisibility get() = _charGroupVisibility.asStateFlow()
    val charGroups = _charGroups.flatMapLatest { charGroups ->
        _charGroupVisibility.flatMapLatest { visibility ->
            val cpy = charGroups.map { it.copy(detailsVisibility = it.charGroup.id == visibility.first.num, isExpanded = it.charGroup.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }
    val charSubGroupVisibility get() = _charSubGroupVisibility.asStateFlow()
    val charSubGroups = _charSubGroups.flatMapLatest { charSubGroups ->
        _charSubGroupVisibility.flatMapLatest { visibility ->
            val cpy = charSubGroups.map { it.copy(detailsVisibility = it.charSubGroup.id == visibility.first.num, isExpanded = it.charSubGroup.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    val characteristics = _characteristics.flatMapLatest { characteristics ->
        _characteristicVisibility.flatMapLatest { visibility ->
            val cpy = characteristics.map { it.copy(detailsVisibility = it.characteristic.id == visibility.first.num, isExpanded = it.characteristic.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    val metrics = _metrics.flatMapLatest { metrics ->
        _metricVisibility.flatMapLatest { visibility ->
            val cpy = metrics.map { it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteCharGroupClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onDeleteCharSubGroupClick(id: ID) = viewModelScope.launch {
        mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run {
                deleteCharSubGroup(id).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                            Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                            Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, resource.message))
                        }
                    }
                }
            }
        }
    }

    fun onDeleteCharacteristicClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onDeleteMetricClick(it: ID) {
        TODO("Not yet implemented")
    }

    private fun updateCharacteristicsData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddCharGroupClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onAddCharSubGroupClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.Characteristics.CharSubGroupAddEdit(charGroupId = it))
    }

    fun onAddCharacteristicClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onAddMetricClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onEditCharGroupClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onEditCharSubGroupClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.Characteristics.CharSubGroupAddEdit(charGroupId = it.first, charSubGroupId = it.second))
    }

    fun onEditCharacteristicClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onEditMetricClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }
}