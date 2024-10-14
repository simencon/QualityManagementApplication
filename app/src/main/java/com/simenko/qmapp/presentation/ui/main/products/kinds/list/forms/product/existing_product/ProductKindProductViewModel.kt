package com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.product.existing_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainProductKindProduct
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ProductsRepository
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductKindProductViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _productKindProduct = MutableStateFlow(DomainProductKindProduct.DomainProductKindProductComplete())
    private val _anotherProductKindId = MutableStateFlow(NoRecord.num)
    private val _searchValue = MutableStateFlow(EmptyString.str)
    private val _availableProductKindProducts = MutableStateFlow<List<DomainProductKindProduct.DomainProductKindProductComplete>>(emptyList())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.AddProductKindProduct) {
        viewModelScope.launch(Dispatchers.IO) {
            val productKind = repository.productKind(route.productKindId).productKind
            repository.allProductKindProducts().collect { list ->
                _availableProductKindProducts.value = list.filter { it.productKind.projectId == productKind.projectId && it.productKind.id != route.productKindId }
                _productKindProduct.value = _productKindProduct.value.copy(
                    productKind = productKind,
                    productKindProduct = _productKindProduct.value.productKindProduct.copy(productKindId = productKind.id)
                )
            }
        }
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val availableProductKinds = _availableProductKindProducts.flatMapLatest { list ->
        _anotherProductKindId.flatMapLatest { anotherProductKindId ->
            flow { emit(list.distinctBy { it.productKind.id }.map { Triple(it.productKind.id, it.productKind.productKindDesignation, it.productKind.id == anotherProductKindId) }) }
        }
    }

    fun onSelectProductKind(id: ID) {
        if (_anotherProductKindId.value != id) _anotherProductKindId.value = id
    }

    val availableDesignations = _availableProductKindProducts.flatMapLatest { list ->
        _anotherProductKindId.flatMapLatest { anotherProductKindId ->
            _productKindProduct.flatMapLatest { productKindProduct ->
                flow {
                    emit(
                        list.filter { it.productKind.id == anotherProductKindId }.distinctBy { it.product.key.id }
                            .map { Triple(it.product.key.id, it.product.key.componentKey, it.product.key.id == productKindProduct.product.key.id) }
                    )
                }
            }
        }
    }

    fun onSelectDesignation(id: ID) {
        if (_productKindProduct.value.product.key.id != id) {
            _availableProductKindProducts.value.firstOrNull { it.product.key.id == id }?.let {
                _productKindProduct.value = _productKindProduct.value.copy(
                    product = _productKindProduct.value.product.copy(
                        product = _productKindProduct.value.product.product.copy(keyId = it.product.key.id),
                        key = it.product.key
                    )
                )
            }
        }
    }

    val searchValue = _searchValue.asStateFlow()
    fun onChangeSearchValue(value: String) {
        if (_searchValue.value != value) _searchValue.value = value
    }

    val availableProducts = _availableProductKindProducts.flatMapLatest { list ->
        _productKindProduct.flatMapLatest { productKindProduct ->
            _anotherProductKindId.flatMapLatest { anotherProductKindId ->
                _searchValue.flatMapLatest { searchValue ->
                    flow {
                        emit(list
                            .filter {
                                it.productKind.id == anotherProductKindId &&
                                        it.product.key.id == productKindProduct.product.key.id &&
                                        (searchValue.isNotEmpty() ||
                                                (it.product.product.productDesignation.lowercase().contains(searchValue.lowercase()) ||
                                                        (it.product.productBase.componentBaseDesignation ?: EmptyString.str).lowercase().contains(searchValue.lowercase())))
                            }
                            .distinctBy { it.product.product.id }
                            .map { it.product.copy(isSelected = it.product.product.id == productKindProduct.product.product.id) })
                    }
                }
            }
        }
    }

    fun onSelectProduct(id: ID) {
        if (_productKindProduct.value.product.product.id != id) {
            _availableProductKindProducts.value.firstOrNull { it.product.product.id == id }?.let {
                _productKindProduct.value = _productKindProduct.value.copy(
                    productKindProduct = _productKindProduct.value.productKindProduct.copy(productId = it.product.product.id),
                    product = it.product,
                )
            }
        }
    }

    val isReadyToAdd = _productKindProduct.flatMapLatest {
        flow { emit(it.productKindProduct.productKindId != NoRecord.num && it.productKindProduct.productId != NoRecord.num) }
    }


    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
        with(repository) { insertProductKindProduct(_productKindProduct.value.productKindProduct) }.consumeEach { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> mainPageState.sendLoadingState(Triple(true, false, null))
                    Status.SUCCESS -> resource.data?.let { navBackToRecord(Pair(it.productKindId, it.productId)) }
                    Status.ERROR -> mainPageState.sendLoadingState(Triple(true, false, resource.message))
                }
            }
        }
    }

    private suspend fun navBackToRecord(record: Pair<ID, ID>) {
        appNavigator.navigateTo(
            route = Route.Main.ProductLines.ProductKinds.Products.ProductsList(productKindId = record.first, productId = record.second),
            popUpToRoute = Route.Main.ProductLines.ProductKinds.Products,
            inclusive = true
        )
    }

    fun navBack() {
        appNavigator.tryNavigateBack(inclusive = false)
    }
}