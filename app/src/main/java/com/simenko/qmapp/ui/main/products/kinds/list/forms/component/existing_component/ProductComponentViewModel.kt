package com.simenko.qmapp.ui.main.products.kinds.list.forms.component.existing_component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainProductComponent
import com.simenko.qmapp.domain.usecase.products.MakeProductComponentUseCase
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
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
class ProductComponentViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val makeProductComponentUseCase: MakeProductComponentUseCase,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _productKindId = MutableStateFlow(NoRecord.num)
    private val _route = MutableStateFlow(Route.Main.ProductLines.ProductKinds.Products.AddProductComponent(NoRecord.num, NoRecord.num))
    private val _selectedComponentIdAndQuantity = MutableStateFlow(Pair(NoRecord.num, ZeroValue.num.toFloat()))

    private val _filterKeyId = MutableStateFlow(NoRecord.num)
    private val _filterProductId = MutableStateFlow(NoRecord.num)
    private val _searchValue = MutableStateFlow(EmptyString.str)
    private val _availableKeys = MutableStateFlow<List<DomainProductComponent.DomainProductComponentComplete>>(emptyList())
    private val _productExistingComponents = MutableStateFlow<List<Long>>(emptyList())
    private val _availableProductComponents = MutableStateFlow<List<DomainProductComponent.DomainProductComponentComplete>>(emptyList())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.AddProductComponent) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.componentKind(route.componentKindId).let { _productKindId.value = it.productKind.productKind.id }
            _route.value = route

            val componentDesignations = repository.componentKindKeysByParent(route.componentKindId).map { it.componentKindKey.keyId }

            launch {
                repository.allProductComponents().collect { list ->
                    _productExistingComponents.value = list
                        .filter { it.productComponent.productId == route.productId && it.component.componentKind.id == route.componentKindId }
                        .distinctBy { it.component.component.component.id }
                        .map { it.component.component.component.id }
                }
            }

            launch {
                combine(repository.allProductComponents(), _productExistingComponents) { list, productExistingComponentIds ->
                    list
                        .filter { productComponent ->
                            componentDesignations.contains(productComponent.component.component.key.id) && !productExistingComponentIds.any { it == productComponent.component.component.component.id }
                        }
                        .distinctBy { it.component.component.key.id }
                }.collect {
                    _availableKeys.value = it
                }
            }

            launch {
                combine(_filterKeyId, repository.allProductComponents(), _productExistingComponents) { keyId, list, productExistingComponentIds ->
                    list.filter { productComponent ->
                        keyId == productComponent.component.component.key.id && !productExistingComponentIds.any { it == productComponent.component.component.component.id }
                    }
                }.collect {
                    _availableProductComponents.value = it
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
                emit(list.map { Triple(it.component.component.key.id, it.component.component.key.componentKey, it.component.component.key.id == keyId) })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun onSelectDesignation(id: ID) {
        if (_filterKeyId.value != id) _filterKeyId.value = id
    }

    val availableProducts = _availableProductComponents.flatMapLatest { list ->
        _filterProductId.flatMapLatest { productId ->
            flow {
                emit(list
                    .distinctBy { it.product.product.id }
                    .map { Triple(it.product.product.id, StringUtils.concatTwoStrings3(it.product.key.componentKey, it.product.product.productDesignation), it.product.product.id == productId) })
            }
        }
    }

    fun onSelectProductKind(id: ID) {
        if (_filterProductId.value != id) _filterProductId.value = id
    }

    val searchValue = _searchValue.asStateFlow()
    fun onChangeSearchValue(value: String) {
        if (_searchValue.value != value) _searchValue.value = value
    }

    val availableComponents = _availableProductComponents.flatMapLatest { list ->
        _filterKeyId.flatMapLatest { keyId ->
            _filterProductId.flatMapLatest { productId ->
                _selectedComponentIdAndQuantity.flatMapLatest { componentIdAndQuantity ->
                    _searchValue.flatMapLatest { searchValue ->
                        flow {
                            emit(list
                                .filter {
                                    it.component.component.key.id == keyId &&
                                            (it.product.product.id == productId || productId == NoRecord.num) &&
                                            (searchValue.isEmpty() || it.component.component.component.componentDesignation.lowercase().contains(searchValue.lowercase()))
                                }
                                .distinctBy { it.component.component.component.id }
                                .map { it.component.component.copy(isSelected = it.component.component.component.id == componentIdAndQuantity.first) })
                        }
                    }
                }
            }
        }
    }

    fun onSelectComponent(id: ID) {
        if (_selectedComponentIdAndQuantity.value.first != id) {
            _selectedComponentIdAndQuantity.value = _selectedComponentIdAndQuantity.value.copy(first = id)
        }
    }

    private val _addWasClicked = MutableStateFlow(false)

    val quantityInProduct: StateFlow<Pair<String, Boolean>> = _selectedComponentIdAndQuantity.flatMapLatest {
        _addWasClicked.flatMapLatest { addWasClicked ->
            flow {
                emit(
                    Pair(
                        first = if (it.second == ZeroValue.num.toFloat()) EmptyString.str else it.second.toString(),
                        second = if (addWasClicked) it.second <= ZeroValue.num.toInt() else false
                    )
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Pair(EmptyString.str, false))

    fun onSetProductComponentQuantity(value: String) {
        value.toFloatOrNull()?.let { finalValue ->
            if (_selectedComponentIdAndQuantity.value.second != finalValue) {
                _selectedComponentIdAndQuantity.value = _selectedComponentIdAndQuantity.value.copy(second = finalValue)
            }
            _addWasClicked.value = false
        } ?: run {
            if (value.isEmpty()) {
                _selectedComponentIdAndQuantity.value = _selectedComponentIdAndQuantity.value.copy(second = ZeroValue.num.toFloat())
            }
        }
    }

    val isReadyToAdd = _selectedComponentIdAndQuantity.flatMapLatest { componentIdAndQuantity ->
        flow { emit(componentIdAndQuantity.first != NoRecord.num) }
    }


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun makeComponentKindComponent() {
        viewModelScope.launch(Dispatchers.IO) {
            _availableProductComponents.value.find { it.component.component.component.id == _selectedComponentIdAndQuantity.value.first }?.let { component ->
                _addWasClicked.value = true
                if (_selectedComponentIdAndQuantity.value.second > ZeroValue.num.toInt()) {
                    makeProductComponentUseCase.execute(
                        scope = this,
                        component = component.component.component.component,
                        componentKindId = _route.value.componentKindId,
                        productId = _route.value.productId,
                        quantity = _selectedComponentIdAndQuantity.value.second
                    ).consumeEach { event ->
                        event.getContentIfNotHandled()?.let { resource ->
                            when (resource.status) {
                                Status.LOADING -> {
                                    mainPageState.sendLoadingState(Triple(true, false, null)); _isLoading.value = true
                                }

                                Status.SUCCESS -> resource.data?.let { navBackToRecord(it) }

                                Status.ERROR -> {
                                    mainPageState.sendLoadingState(Triple(false, false, resource.message)); _isLoading.value = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(componentId: ID) {
        val productKindId = _productKindId.value
        val productId = _route.value.productId
        val componentKindId = _route.value.componentKindId
        appNavigator.navigateTo(
            route = Route.Main.ProductLines.ProductKinds.Products.ProductsList(productKindId = productKindId, productId = productId, componentKindId = componentKindId, componentId = componentId),
            popUpToRoute = Route.Main.ProductLines.ProductKinds.Products,
            inclusive = true
        )
    }

    fun navBack() {
        appNavigator.tryNavigateBack(inclusive = false)
    }
}