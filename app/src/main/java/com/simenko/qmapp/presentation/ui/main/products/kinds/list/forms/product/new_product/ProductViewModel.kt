package com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.product.new_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainProductBase
import com.simenko.qmapp.domain.entities.products.DomainProductKindProduct
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ProductsRepository
import com.simenko.qmapp.presentation.ui.main.main.MainPageHandler
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
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
class ProductViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _productKindProduct = MutableStateFlow(DomainProductKindProduct.DomainProductKindProductComplete())
    private val _productBaseToAdd = MutableStateFlow(Pair(EmptyString.str, DomainProductBase()))
    private val _isAddProductBaseDialogVisible = MutableStateFlow(false)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.AddEditProduct) {
        viewModelScope.launch(Dispatchers.IO) {
            if (route.productId == NoRecord.num) {
                prepareProductKindProduct(route.productKindId)
            } else {
                _productKindProduct.value = repository.productKindProductById(route.productKindId, route.productId)
            }

            repository.productKind(route.productKindId).let {
                _productBaseToAdd.value = _productBaseToAdd.value.copy(second = _productBaseToAdd.value.second.copy(projectId = it.productLine.manufacturingProject.id))
            }

            mainPageHandler = MainPageHandler.Builder(if (route.productKindId == NoRecord.num) Page.ADD_PRODUCT_KIND_PRODUCT else Page.EDIT_PRODUCT_KIND_PRODUCT, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { validateInput() }
                .build()
                .apply { setupMainPage(0, true) }
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

    val isAddProductBaseDialogVisible = _isAddProductBaseDialogVisible.asStateFlow()
    fun onChangeAddProductLineVisibility(isVisible: Boolean) {
        _isAddProductBaseDialogVisible.value = isVisible
        if (!isVisible) {
            _productBaseToAdd.value = Pair(EmptyString.str, DomainProductBase(projectId = _productBaseToAdd.value.second.projectId))
        }
    }

    val productBaseToAdd = _productBaseToAdd.asStateFlow()
    fun onChangeProductBaseName(name: String) {
        _productBaseToAdd.value = _productBaseToAdd.value.copy(
            first = EmptyString.str,
            second = _productBaseToAdd.value.second.copy(componentBaseDesignation = name)
        )
    }


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
    fun onAddProductBase() = viewModelScope.launch(Dispatchers.IO) {
        val itemToAdd = _productBaseToAdd.value
        if (_availableProductBases.value.any {
                it.projectId == itemToAdd.second.projectId &&
                        (it.componentBaseDesignation ?: EmptyString.str).lowercase().trim() == (itemToAdd.second.componentBaseDesignation ?: EmptyString.str).lowercase().trim()
            }
        ) {
            _productBaseToAdd.value = _productBaseToAdd.value.copy(first = "Such product base already exists")
        } else {
            _isAddProductBaseDialogVisible.value = false
            with(repository) {
                insertProductBase(itemToAdd.second).consumeEach { event ->
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource.status) {
                            Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))

                            Status.SUCCESS -> {
                                mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                                _productBaseToAdd.value = Pair(EmptyString.str, DomainProductBase(projectId = itemToAdd.second.projectId))
                            }

                            Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, resource.message))
                        }
                    }
                }
            }
        }
    }

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
                    Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                    Status.SUCCESS -> resource.data?.id?.let { if (isNewRecord) makeProductKindProduct(it) else navBackToRecord(it) }
                    Status.ERROR -> {
                        mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, resource.message))
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
                    Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                    Status.SUCCESS -> resource.data?.id?.let { navBackToRecord(productId) }
                    Status.ERROR -> {
                        mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, resource.message))
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
}

data class FillInErrors(
    val productKeyError: Boolean = false,
    val productBaseError: Boolean = false,
    val productDescriptionError: Boolean = false
)