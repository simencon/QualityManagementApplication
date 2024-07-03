package com.simenko.qmapp.ui.main.products.kinds.list.forms.product.existing_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainProductKindProduct
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductKindProductViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _productKindProduct = MutableStateFlow(DomainProductKindProduct.DomainProductKindProductComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.AddProductKindProduct) {
        viewModelScope.launch(Dispatchers.IO) {
            prepareProductKindProduct(route.productKindId)
        }
    }

    private suspend fun prepareProductKindProduct(productKindId: ID) {
        val productKind = repository.productKind(productKindId).productKind
        _productKindProduct.value = DomainProductKindProduct.DomainProductKindProductComplete(
            productKindProduct = DomainProductKindProduct(productKindId = productKind.id),
            productKind = productKind,
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    private val _availableKeys = _productKindProduct.flatMapLatest { productKindProduct ->
        repository.productKindKeys(productKindProduct.productKind.id).map { list ->
            list.map { item -> item.key.productLineKey }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val availableKeys = _availableKeys.flatMapLatest { list ->
        _productKindProduct.flatMapLatest { productKindProduct ->
            flow { emit(list.map { Triple(it.id, it.componentKey, it.id == productKindProduct.product.product.keyId) }) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun onSelectProductKey(id: ID) {
        if (_productKindProduct.value.product.product.keyId != id) {
            val product = _productKindProduct.value.product
            _availableKeys.value.find { it.id == id }?.let { key ->
                _productKindProduct.value = _productKindProduct.value.copy(
                    product = product.copy(product = product.product.copy(keyId = key.id), key = key)
                )
            } ?: run {
                val key = _productKindProduct.value.product.key.copy(id = id)
                _productKindProduct.value = _productKindProduct.value.copy(
                    product = product.copy(product = product.product.copy(keyId = key.id), key = key)
                )
            }
            _fillInErrors.value = _fillInErrors.value.copy(productKeyError = false)
            _fillInState.value = FillInInitialState
        }
    }


    val productKindProduct get() = _productKindProduct.asStateFlow()

    private val _availableProductBases = _productKindProduct.flatMapLatest { productKindProduct ->
        repository.productBases(productKindProduct.productKind.projectId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val availableProductBases = _availableProductBases.flatMapLatest { list ->
        _productKindProduct.flatMapLatest { productKindProduct ->
            flow { emit(list.map { item -> Triple(item.id, item.componentBaseDesignation ?: EmptyString.str, item.id == productKindProduct.product.product.productBaseId) }) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


    fun onSelectProductBase(id: ID) {
        if (_productKindProduct.value.product.product.productBaseId != id) {
            val product = _productKindProduct.value.product
            _availableProductBases.value.find { it.id == id }?.let { productBase ->
                _productKindProduct.value = _productKindProduct.value.copy(
                    product = product.copy(product = product.product.copy(productBaseId = productBase.id), productBase = productBase)
                )
            } ?: run {
                val productBase = _productKindProduct.value.product.productBase.copy(id = id)
                _productKindProduct.value = _productKindProduct.value.copy(
                    product = product.copy(product = product.product.copy(productBaseId = productBase.id), productBase = productBase)
                )
            }
            _fillInErrors.value = _fillInErrors.value.copy(productBaseError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetProductDescription(value: String) {
        if (_productKindProduct.value.product.product.productDesignation != value) {
            val product = _productKindProduct.value.product
            _productKindProduct.value = _productKindProduct.value.copy(product = product.copy(product = product.product.copy(productDesignation = value)))
            _fillInErrors.value = _fillInErrors.value.copy(productDescriptionError = false)
            _fillInState.value = FillInInitialState
        }
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    private val _fillInErrors = MutableStateFlow(FillInErrors())
    val fillInErrors get() = _fillInErrors.asStateFlow()
    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()

    private fun validateInput() {
        val errorMsg = buildString {
            with(_productKindProduct.value.product.product) {
                if (keyId == NoRecord.num) {
                    _fillInErrors.value = _fillInErrors.value.copy(productKeyError = true)
                    append("Product designation field is mandatory\n")
                }
                if (productBaseId == NoRecord.num) {
                    _fillInErrors.value = _fillInErrors.value.copy(productBaseError = true)
                    append("Product base field is mandatory\n")
                }
                if (productDesignation.isEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(productDescriptionError = true)
                    append("Product description field is mandatory\n")
                }
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
        val product = _productKindProduct.value.product.product
        val isNewRecord = product.id == NoRecord.num
        with(repository) {
            if (isNewRecord) insertProduct(product) else updateProduct(product)
        }.consumeEach { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> mainPageState.sendLoadingState(Pair(true, null))
                    Status.SUCCESS -> resource.data?.id?.let { if (isNewRecord) makeProductKindProduct(it) else navBackToRecord(it) }
                    Status.ERROR -> {
                        mainPageState.sendLoadingState(Pair(true, resource.message))
                        _fillInState.value = FillInInitialState
                    }
                }
            }
        }
    }

    private suspend fun makeProductKindProduct(productId: ID) = withContext(Dispatchers.IO) {
        val productKindProduct = _productKindProduct.value.productKindProduct.copy(productId = productId)
        with(repository) { insertProductKindProduct(productKindProduct) }.consumeEach { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> mainPageState.sendLoadingState(Pair(true, null))
                    Status.SUCCESS -> resource.data?.id?.let { navBackToRecord(productId) }
                    Status.ERROR -> {
                        mainPageState.sendLoadingState(Pair(true, resource.message))
                        _fillInState.value = FillInInitialState
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(id: ID) {
        val productKindId = _productKindProduct.value.productKind.id
        appNavigator.navigateTo(
            route = Route.Main.ProductLines.ProductKinds.Products.ProductsList(productKindId = productKindId, productId = id),
            popUpToRoute = Route.Main.ProductLines.ProductKinds.Products,
            inclusive = true
        )
    }

    fun navBack() {
        appNavigator.tryNavigateBack(inclusive = false)
    }
}

data class FillInErrors(
    val productKeyError: Boolean = false,
    val productBaseError: Boolean = false,
    val productDescriptionError: Boolean = false
)