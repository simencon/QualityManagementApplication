package com.simenko.qmapp.ui.main.products.kinds.list.forms.component.existing_component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainProductComponent
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
    val repository: ProductsRepository,
) : ViewModel() {
    private val _filterKeyId = MutableStateFlow(NoRecord.num)
    private val _filterProductId = MutableStateFlow(NoRecord.num)
    private val _productComponent = MutableStateFlow(DomainProductComponent())
    private val _searchValue = MutableStateFlow(EmptyString.str)
    private val _availableKeys = MutableStateFlow<List<DomainProductComponent.DomainProductComponentComplete>>(emptyList())
    private val _availableProductComponents = MutableStateFlow<List<DomainProductComponent.DomainProductComponentComplete>>(emptyList())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.AddProductComponent) {
        viewModelScope.launch(Dispatchers.IO) {

            _productComponent.value = _productComponent.value.copy(productId = route.productId,)

            repository.allProductComponents().collect { list ->
                _availableKeys.value = list.filter { it.component.componentKindComponent.id == route.componentKindId }.distinctBy { it.component.component.key.id }
            }

            _availableKeys.combine(repository.allProductComponents()) { keys, list ->
                _availableProductComponents.value = list.filter { productComponent ->
                    keys.any { it.component.component.key.id == productComponent.component.componentKind.id }
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
                emit(list.map { Triple(it.component.component.key.id, it.product.key.componentKey, it.component.component.key.id == keyId) })
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
                _productComponent.flatMapLatest { productComponent ->
                    _searchValue.flatMapLatest { searchValue ->
                        flow {
                            emit(list
                                .filter {
                                    it.component.component.key.id == keyId &&
                                            (it.product.product.id == productId || productId == NoRecord.num) &&
                                            (searchValue.isNotEmpty() || it.component.component.component.componentDesignation.lowercase().contains(searchValue.lowercase()))
                                }
                                .distinctBy { it.component.component.component.id }
                                .map { it.product.copy(isSelected = it.component.component.component.id == productComponent.componentId) })
                        }
                    }
                }
            }
        }
    }

    fun onSelectComponent(id: ID) {
        if (_productComponent.value.componentId != id) {
            _availableProductComponents.value.firstOrNull { it.product.product.id == id }?.let {
                _productComponent.value = _productComponent.value.copy(
                    componentId = id
                )
            }
        }
    }

    val isReadyToAdd = _productComponent.flatMapLatest {
        flow { emit(it.productId != NoRecord.num && it.componentId != NoRecord.num) }
    }


    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
//        with(repository) { insertProductKindProduct(_productComponent.value.productKindProduct) }.consumeEach { event ->
//            event.getContentIfNotHandled()?.let { resource ->
//                when (resource.status) {
//                    Status.LOADING -> mainPageState.sendLoadingState(Pair(true, null))
//                    Status.SUCCESS -> resource.data?.let { navBackToRecord(Pair(it.productKindId, it.productId)) }
//                    Status.ERROR -> mainPageState.sendLoadingState(Pair(true, resource.message))
//                }
//            }
//        }
    }

    private suspend fun navBackToRecord(record: Pair<ID, ID>) {
//        appNavigator.navigateTo(
//            route = Route.Main.ProductLines.ProductKinds.Products.ProductsList(productKindId = record.first, productId = record.second),
//            popUpToRoute = Route.Main.ProductLines.ProductKinds.Products,
//            inclusive = true
//        )
    }

    fun navBack() {
        appNavigator.tryNavigateBack(inclusive = false)
    }
}