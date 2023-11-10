package com.simenko.qmapp.ui.main.products.characteristics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.CharGroupIdParameter
import com.simenko.qmapp.di.CharSubGroupIdParameter
import com.simenko.qmapp.di.MetricIdParameter
import com.simenko.qmapp.di.CharacteristicIdParameter
import com.simenko.qmapp.di.ProductLineIdParameter
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainProductLine
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.storage.ScrollStates
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharacteristicsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
    @ProductLineIdParameter private val productLineId: ID,
    @CharGroupIdParameter private val charGroupId: ID,
    @CharSubGroupIdParameter private val charSubGroupId: ID,
    @CharacteristicIdParameter private val characteristicId: ID,
    @MetricIdParameter private val metricId: ID
) : ViewModel() {
    private val _charGroupVisibility = MutableStateFlow(Pair(SelectedNumber(charGroupId), NoRecord))
    private val _charSubGroupVisibility = MutableStateFlow(Pair(SelectedNumber(charSubGroupId), NoRecord))
    private val _characteristicsVisibility = MutableStateFlow(Pair(SelectedNumber(characteristicId), NoRecord))
    private val _metricVisibility = MutableStateFlow(Pair(SelectedNumber(metricId), NoRecord))
    private val _productLine = MutableStateFlow(DomainProductLine())
    private val _charGroups = repository.charGroups(productLineId)
    private val _charSubGroups = _charGroupVisibility.flatMapLatest { repository.charSubGroups(it.first.num) }
    private val _characteristics = _charSubGroupVisibility.flatMapLatest { repository.characteristicsByParent(it.first.num) }
    private val _metrics = _characteristicsVisibility.flatMapLatest { repository.metrics(it.first.num) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_LINE_CHARACTERISTICS, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { onAddCharGroupClick(Pair(productLineId, NoRecord.num)) }
            .setOnPullRefreshAction { updateCharacteristicsData() }
            .build()
        viewModelScope.launch(Dispatchers.IO) { _productLine.value = repository.productLine(productLineId) }
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
        _characteristicsVisibility.value = _characteristicsVisibility.value.setVisibility(dId, aId)
    }

    fun setMetricsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _metricVisibility.value = _metricVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productLine get() = _productLine.asStateFlow()

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
        _metricVisibility.flatMapLatest { visibility ->
            flow { emit((visibility.first != NoRecord) && (isComposed.component3())) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val listsIsInitialized: Flow<Pair<Boolean, Boolean>> = _viewState.flatMapLatest { viewState ->
        _charGroups.flatMapLatest { firstList ->
            _secondListIsInitialized.flatMapLatest { secondListState ->
                if (viewState)
                    flow {
                        if (charGroupId != NoRecord.num) {
                            storage.setLong(ScrollStates.CHAR_GROUPS.indexKey, firstList.map { it.charGroup.id }.indexOf(charGroupId).toLong())
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
            if (metricId != NoRecord.num) {
                storage.setLong(ScrollStates.METRICS.indexKey, secondList.map { it.id }.indexOf(metricId).toLong())
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
    val charSubGroups = _charSubGroups.flatMapLatest { charSubGroups ->
        _charSubGroupVisibility.flatMapLatest { visibility ->
            val cpy = charSubGroups.map { it.copy(detailsVisibility = it.charSubGroup.id == visibility.first.num, isExpanded = it.charSubGroup.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    val characteristics = _characteristics.flatMapLatest { characteristics ->
        _characteristicsVisibility.flatMapLatest { visibility ->
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
    fun onDeleteCharSubGroupClick(it: ID) {
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
        TODO("Not yet implemented")
    }

    fun onEditCharGroupClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }
    fun onEditCharSubGroupClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }
}