package com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component.new_component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainComponentKindComponent
import com.simenko.qmapp.domain.entities.products.DomainProductComponent
import com.simenko.qmapp.domain.entities.products.DomainProductKind
import com.simenko.qmapp.domain.usecase.MakeProductComponentUseCase
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ComponentViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val makeProductComponentUseCase: MakeProductComponentUseCase,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _productKind = MutableStateFlow(DomainProductKind.DomainProductKindComplete())
    private val _productComponent = MutableStateFlow(DomainProductComponent.DomainProductComponentComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.AddEditComponent) {
        viewModelScope.launch(Dispatchers.IO) {
            _productKind.value = repository.productKind(route.productKindId)
            if (route.componentId == NoRecord.num) {
                prepareProductComponent(route.productId, route.componentKindId)
            } else {
                _productComponent.value = repository.productComponentById(route.productId, route.componentKindId, route.componentId)
            }

            mainPageHandler = MainPageHandler.Builder(if (route.componentId == NoRecord.num) Page.ADD_PRODUCT_COMPONENT else Page.EDIT_PRODUCT_COMPONENT, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { validateInput() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    private suspend fun prepareProductComponent(productId: ID, componentKindId: ID) {
        val product = repository.productById(productId)
        val componentKind = repository.componentKind(componentKindId).componentKind
        _productComponent.value = DomainProductComponent.DomainProductComponentComplete(
            productComponent = DomainProductComponent(productId = product.product.id),
            product = product,
            component = DomainComponentKindComponent.DomainComponentKindComponentComplete(
                componentKindComponent = DomainComponentKindComponent(componentKindId = componentKind.id),
                componentKind = componentKind
            )
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productKind = _productKind.asStateFlow()

    private val _availableKeys = _productComponent.flatMapLatest { productComponent ->
        repository.componentKindKeys(productComponent.component.componentKind.id).map { list ->
            list.map { item -> item.key.productLineKey }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val availableKeys = _availableKeys.flatMapLatest { list ->
        _productComponent.flatMapLatest { productComponent ->
            flow { emit(list.map { Triple(it.id, it.componentKey, it.id == productComponent.component.component.component.keyId) }) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun onSelectProductKey(id: ID) {
        if (_productComponent.value.component.component.component.keyId != id) {
            val component = _productComponent.value.component
            _availableKeys.value.find { it.id == id }?.let { key ->
                _productComponent.value = _productComponent.value.copy(
                    component = component.copy(
                        component = component.component.copy(
                            key = key,
                            component = component.component.component.copy(keyId = key.id)
                        )
                    )
                )
            } ?: run {
                val key = _productComponent.value.component.component.key.copy(id = id)
                _productComponent.value = _productComponent.value.copy(
                    component = component.copy(
                        component = component.component.copy(
                            key = key,
                            component = component.component.component.copy(keyId = key.id)
                        )
                    )
                )
            }
            _fillInErrors.value = _fillInErrors.value.copy(componentKeyError = false)
            _fillInState.value = FillInInitialState
        }
    }


    val productComponent get() = _productComponent.asStateFlow()

    fun onSetComponentDescription(value: String) {
        if (_productComponent.value.component.component.component.componentDesignation != value) {
            val component = _productComponent.value.component
            _productComponent.value = _productComponent.value.copy(
                component = component.copy(
                    component = component.component.copy(
                        component = component.component.component.copy(componentDesignation = value)
                    )
                ),
            )
            _fillInErrors.value = _fillInErrors.value.copy(componentDescriptionError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetProductComponentQuantity(value: String) {
        val productComponent = _productComponent.value.productComponent
        value.toFloatOrNull()?.let { finalValue ->
            if (_productComponent.value.productComponent.quantity != finalValue) {
                _productComponent.value = _productComponent.value.copy(
                    productComponent = productComponent.copy(quantity = finalValue)
                )
                _fillInErrors.value = _fillInErrors.value.copy(productComponentQntError = false)
                _fillInState.value = FillInInitialState
            }
        } ?: run {
            if (value.isEmpty()) {
                _productComponent.value = _productComponent.value.copy(
                    productComponent = productComponent.copy(quantity = ZeroValue.num.toFloat())
                )
                _fillInErrors.value = _fillInErrors.value.copy(productComponentQntError = false)
                _fillInState.value = FillInInitialState
            }
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
            with(_productComponent.value) {
                if (component.component.component.keyId == NoRecord.num) {
                    _fillInErrors.value = _fillInErrors.value.copy(componentKeyError = true)
                    append("Component designation field is mandatory\n")
                }
                if (productComponent.quantity == ZeroValue.num.toFloat()) {
                    _fillInErrors.value = _fillInErrors.value.copy(productComponentQntError = true)
                    append("Quantity field cannot be zero\n")
                }
                if (component.component.component.componentDesignation.isEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(componentDescriptionError = true)
                    append("Component description field is mandatory\n")
                }
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() {
        viewModelScope.launch(Dispatchers.IO) {
            makeProductComponentUseCase.execute(
                scope = this,
                component = _productComponent.value.component.component.component,
                componentKindId = _productComponent.value.component.componentKindComponent.componentKindId,
                productId = _productComponent.value.productComponent.productId,
                quantity = _productComponent.value.productComponent.quantity
            ).consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                        Status.SUCCESS -> resource.data?.let { navBackToRecord(it) }
                        Status.ERROR -> {
                            mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, resource.message))
                            _fillInState.value = FillInInitialState
                        }
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(id: ID) {
        val productKindId = _productKind.value.productKind.id
        val productId = _productComponent.value.productComponent.productId
        val componentKindId = _productComponent.value.component.componentKind.id
        appNavigator.navigateTo(
            route = Route.Main.ProductLines.ProductKinds.Products.ProductsList(productKindId = productKindId, productId = productId, componentKindId = componentKindId, componentId = id),
            popUpToRoute = Route.Main.ProductLines.ProductKinds.Products,
            inclusive = true
        )
    }
}

data class FillInErrors(
    val componentKeyError: Boolean = false,
    val productComponentQntError: Boolean = false,
    val componentDescriptionError: Boolean = false
)