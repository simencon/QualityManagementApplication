package com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component_stage.existing_component_stage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainComponentComponentStage
import com.simenko.qmapp.domain.usecase.products.MakeComponentStageUseCase
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ProductsRepository
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.utils.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ComponentComponentStageViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val makeComponentStageUseCase: MakeComponentStageUseCase,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _route = MutableStateFlow(Route.Main.ProductLines.ProductKinds.Products.AddComponentComponentStage(NoRecord.num, NoRecord.num, NoRecord.num, NoRecord.num, NoRecord.num))
    private val _selectedStageIdAndQuantityInProduct = MutableStateFlow(Pair(NoRecord.num, ZeroValue.num.toFloat()))
    private val _filterKeyId = MutableStateFlow(NoRecord.num)
    private val _filterComponentId = MutableStateFlow(NoRecord.num)
    private val _searchValue = MutableStateFlow(EmptyString.str)
    private val _availableKeys = MutableStateFlow<List<DomainComponentComponentStage.DomainComponentComponentStageComplete>>(emptyList())
    private val _componentExistingComponentStages = MutableStateFlow<List<Long>>(emptyList())
    private val _availableComponentComponentStages = MutableStateFlow<List<DomainComponentComponentStage.DomainComponentComponentStageComplete>>(emptyList())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.AddComponentComponentStage) {
        viewModelScope.launch(Dispatchers.IO) {
            _route.value = route

            val stageDesignations = repository.componentStageKindKeysByParent(route.componentStageKindId).map { it.key.productLineKey.id }

            launch {
                repository.allComponentComponentStages().collect { list: List<DomainComponentComponentStage.DomainComponentComponentStageComplete> ->
                    _componentExistingComponentStages.value = list
                        .filter { it.componentComponentStage.componentId == route.componentId && it.componentStage.componentStageKind.id == route.componentStageKindId }
                        .distinctBy { it.componentStage.componentStage.componentStage.id }
                        .map { it.componentStage.componentStage.componentStage.id }
                }
            }

            launch {
                combine(repository.allComponentComponentStages(), _componentExistingComponentStages) { list, componentExistingComponentStageIds ->
                    list
                        .filter { productComponent ->
                            stageDesignations.contains(productComponent.componentStage.componentStage.key.id) && !componentExistingComponentStageIds.any { it == productComponent.componentStage.componentStage.componentStage.id }
                        }
                        .distinctBy { it.componentStage.componentStage.key.id }
                }.collect {
                    _availableKeys.value = it
                }
            }

            launch {
                combine(_filterKeyId, repository.allComponentComponentStages(), _componentExistingComponentStages) { keyId, list, componentExistingComponentStageIds ->
                    list.filter { productComponent ->
                        keyId == productComponent.componentStage.componentStage.key.id && !componentExistingComponentStageIds.any { it == productComponent.componentStage.componentStage.componentStage.id }
                    }
                }.collect {
                    _availableComponentComponentStages.value = it
                }
            }
        }
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val availableDesignations = _availableKeys.flatMapLatest { list ->
        _filterKeyId.flatMapLatest { keyId ->
            flow {
                emit(list.map { Triple(it.componentStage.componentStage.key.id, it.componentStage.componentStage.key.componentKey, it.componentStage.componentStage.key.id == keyId) })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun onSelectDesignation(id: ID) {
        if (_filterKeyId.value != id) _filterKeyId.value = id
    }

    val availableComponents = _availableComponentComponentStages.flatMapLatest { list ->
        _filterComponentId.flatMapLatest { componentId ->
            flow {
                emit(list
                    .distinctBy { it.component.component.id }
                    .map {
                        Triple(
                            it.component.component.id,
                            StringUtils.concatTwoStrings3(it.component.key.componentKey, it.component.component.componentDesignation),
                            it.component.component.id == componentId
                        )
                    })
            }
        }
    }

    fun onSelectComponent(id: ID) {
        if (_filterComponentId.value != id) _filterComponentId.value = id
    }

    val searchValue = _searchValue.asStateFlow()
    fun onChangeSearchValue(value: String) {
        if (_searchValue.value != value) _searchValue.value = value
    }

    val availableComponentStages = _availableComponentComponentStages.flatMapLatest { list ->
        _filterKeyId.flatMapLatest { keyId ->
            _filterComponentId.flatMapLatest { componentId ->
                _selectedStageIdAndQuantityInProduct.flatMapLatest { stageIdAndQuantity ->
                    _searchValue.flatMapLatest { searchValue ->
                        flow {
                            emit(list
                                .filter {
                                    it.componentStage.componentStage.key.id == keyId &&
                                            (it.component.component.id == componentId || componentId == NoRecord.num) &&
                                            (searchValue.isEmpty() || it.componentStage.componentStage.componentStage.componentInStageDescription.lowercase().contains(searchValue.lowercase()))
                                }
                                .distinctBy { it.componentStage.componentStage.componentStage.id }
                                .map { it.componentStage.componentStage.copy(isSelected = it.componentStage.componentStage.componentStage.id == stageIdAndQuantity.first) })
                        }
                    }
                }
            }
        }
    }

    fun onSelectComponentStage(id: ID) {
        if (_selectedStageIdAndQuantityInProduct.value.first != id) {
            _selectedStageIdAndQuantityInProduct.value = _selectedStageIdAndQuantityInProduct.value.copy(first = id)
        }
    }

    private val _addWasClicked = MutableStateFlow(false)

    val quantityInProduct: StateFlow<Pair<String, Boolean>> = _selectedStageIdAndQuantityInProduct.flatMapLatest { stageIdAndQuantity ->
        _addWasClicked.flatMapLatest { addWasClicked ->
            flow {
                emit(
                    Pair(
                        first = if (stageIdAndQuantity.second == ZeroValue.num.toFloat()) EmptyString.str else stageIdAndQuantity.second.toString(),
                        second = if (addWasClicked) stageIdAndQuantity.second <= ZeroValue.num.toInt() else false
                    )
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Pair(EmptyString.str, false))

    fun onSetProductComponentQuantity(value: String) {
        value.toFloatOrNull()?.let { finalValue ->
            if (_selectedStageIdAndQuantityInProduct.value.second != finalValue) {
                _selectedStageIdAndQuantityInProduct.value = _selectedStageIdAndQuantityInProduct.value.copy(second = finalValue)
            }
            _addWasClicked.value = false
        } ?: run {
            if (value.isEmpty()) {
                _selectedStageIdAndQuantityInProduct.value = _selectedStageIdAndQuantityInProduct.value.copy(second = ZeroValue.num.toFloat())
            }
        }
    }

    val isReadyToAdd = _selectedStageIdAndQuantityInProduct.flatMapLatest {
        flow { emit(it.first != NoRecord.num) }
    }


    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    fun makeRecord() {
        viewModelScope.launch(Dispatchers.IO) {
            _availableComponentComponentStages.value.find { it.componentStage.componentStage.componentStage.id == _selectedStageIdAndQuantityInProduct.value.first }?.let { stageToAdd ->
                _addWasClicked.value = true
                if (_selectedStageIdAndQuantityInProduct.value.second > ZeroValue.num.toInt()) {
                    makeComponentStageUseCase.execute(
                        scope = this,
                        stage = stageToAdd.componentStage.componentStage.componentStage,
                        stageKindId = _route.value.componentStageKindId,
                        componentId = _route.value.componentId,
                        quantity = _selectedStageIdAndQuantityInProduct.value.second
                    ).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> mainPageState.sendLoadingState(Triple(false, true, null))
                                Status.SUCCESS -> resource.data?.let { navBackToRecord(it) }
                                Status.ERROR -> mainPageState.sendLoadingState(Triple(false, false, resource.message))
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(componentStageId: ID) {
        appNavigator.navigateTo(
            route = Route.Main.ProductLines.ProductKinds.Products.ProductsList(
                productKindId = _route.value.productKindId, productId = _route.value.productId,
                componentKindId = _route.value.componentKindId, componentId = _route.value.componentId,
                componentStageKindId = _route.value.componentStageKindId, componentStageId = componentStageId,
            ),
            popUpToRoute = Route.Main.ProductLines.ProductKinds.Products,
            inclusive = true
        )
    }

    fun navBack() {
        appNavigator.tryNavigateBack(inclusive = false)
    }
}