package com.simenko.qmapp.presentation.ui.main.products.kinds.list.forms.component_stage.new_component_stage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.domain.entities.products.DomainComponentComponentStage
import com.simenko.qmapp.domain.entities.products.DomainComponentKind
import com.simenko.qmapp.domain.entities.products.DomainComponentStageKindComponentStage
import com.simenko.qmapp.domain.usecase.MakeComponentStageUseCase
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
class ComponentStageViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val makeComponentStageUseCase: MakeComponentStageUseCase,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _route = MutableStateFlow(Route.Main.ProductLines.ProductKinds.Products.AddEditComponentStage(NoRecord.num, NoRecord.num, NoRecord.num, NoRecord.num, NoRecord.num, NoRecord.num))
    private val _componentKind = MutableStateFlow(DomainComponentKind.DomainComponentKindComplete())
    private val _componentComponentStage = MutableStateFlow(DomainComponentComponentStage.DomainComponentComponentStageComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.Products.AddEditComponentStage) {
        viewModelScope.launch(Dispatchers.IO) {
            _route.value = route
            _componentKind.value = repository.componentKind(route.componentKindId)
            if (route.componentStageId == NoRecord.num) {
                prepareProductComponent(route.componentId, route.componentStageKindId)
            } else {
                _componentComponentStage.value = repository.componentComponentStageById(route.componentId, route.componentStageKindId, route.componentStageId)
            }

            mainPageHandler = MainPageHandler.Builder(if (route.componentId == NoRecord.num) Page.ADD_PRODUCT_COMPONENT else Page.EDIT_PRODUCT_COMPONENT, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { validateInput() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    private suspend fun prepareProductComponent(componentId: ID, componentStageKindId: ID) {
        val component = repository.componentById(componentId)
        val componentStageKind = repository.componentStageKind(componentStageKindId).componentStageKind

        _componentComponentStage.value = DomainComponentComponentStage.DomainComponentComponentStageComplete(
            componentComponentStage = DomainComponentComponentStage(componentId = component.component.id),
            component = component,
            componentStage = DomainComponentStageKindComponentStage.DomainComponentStageKindComponentStageComplete(
                componentStageKindComponentStage = DomainComponentStageKindComponentStage(componentStageKindId = componentStageKind.id),
                componentStageKind = componentStageKind
            )
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val componentKind = _componentKind.asStateFlow()

    private val _availableKeys = _componentComponentStage.flatMapLatest { productComponent ->
        repository.componentStageKindKeys(productComponent.componentStage.componentStageKind.id).map { list ->
            list.map { item -> item.key.productLineKey }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val availableKeys = _availableKeys.flatMapLatest { list ->
        _componentComponentStage.flatMapLatest { productComponent ->
            flow { emit(list.map { Triple(it.id, it.componentKey, it.id == productComponent.componentStage.componentStage.componentStage.keyId) }) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun onSelectComponentStageKey(id: ID) {
        if (_componentComponentStage.value.componentStage.componentStage.componentStage.keyId != id) {
            val componentStage = _componentComponentStage.value.componentStage
            _availableKeys.value.find { it.id == id }?.let { key ->
                _componentComponentStage.value = _componentComponentStage.value.copy(
                    componentStage = componentStage.copy(
                        componentStage = componentStage.componentStage.copy(
                            key = key,
                            componentStage = componentStage.componentStage.componentStage.copy(keyId = key.id)
                        )
                    )
                )
            } ?: run {
                val key = _componentComponentStage.value.componentStage.componentStage.key.copy(id = id)
                _componentComponentStage.value = _componentComponentStage.value.copy(
                    componentStage = componentStage.copy(
                        componentStage = componentStage.componentStage.copy(
                            key = key,
                            componentStage = componentStage.componentStage.componentStage.copy(keyId = key.id)
                        )
                    )
                )
            }
            _fillInErrors.value = _fillInErrors.value.copy(componentStageKeyError = false)
            _fillInState.value = FillInInitialState
        }
    }


    val componentComponentStage get() = _componentComponentStage.asStateFlow()

    fun onSetComponentStageDescription(value: String) {
        if (_componentComponentStage.value.componentStage.componentStage.componentStage.componentInStageDescription != value) {
            val componentStage = _componentComponentStage.value.componentStage
            _componentComponentStage.value = _componentComponentStage.value.copy(
                componentStage = componentStage.copy(
                    componentStage = componentStage.componentStage.copy(
                        componentStage = componentStage.componentStage.componentStage.copy(componentInStageDescription = value)
                    )
                )
            )
            _fillInErrors.value = _fillInErrors.value.copy(componentStageDescriptionError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetProductComponentQuantity(value: String) {
        val componentStage = _componentComponentStage.value.componentComponentStage
        value.toFloatOrNull()?.let { finalValue ->
            if (_componentComponentStage.value.componentComponentStage.quantity != finalValue) {
                _componentComponentStage.value = _componentComponentStage.value.copy(
                    componentComponentStage = componentStage.copy(quantity = finalValue)
                )
                _fillInErrors.value = _fillInErrors.value.copy(componentStageQntError = false)
                _fillInState.value = FillInInitialState
            }
        } ?: run {
            if (value.isEmpty()) {
                _componentComponentStage.value = _componentComponentStage.value.copy(
                    componentComponentStage = componentStage.copy(quantity = ZeroValue.num.toFloat())
                )
                _fillInErrors.value = _fillInErrors.value.copy(componentStageQntError = false)
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
            with(_componentComponentStage.value) {
                if (componentStage.componentStage.componentStage.keyId == NoRecord.num) {
                    _fillInErrors.value = _fillInErrors.value.copy(componentStageKeyError = true)
                    append("Comp. stage designation field is mandatory\n")
                }
                if (componentComponentStage.quantity == ZeroValue.num.toFloat()) {
                    _fillInErrors.value = _fillInErrors.value.copy(componentStageQntError = true)
                    append("Quantity field cannot be zero\n")
                }
                if (componentStage.componentStage.componentStage.componentInStageDescription.isEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(componentStageDescriptionError = true)
                    append("Component description field is mandatory\n")
                }
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() {
        viewModelScope.launch(Dispatchers.IO) {
            makeComponentStageUseCase.execute(
                scope = this,
                stage = _componentComponentStage.value.componentStage.componentStage.componentStage,
                stageKindId = _componentComponentStage.value.componentStage.componentStageKindComponentStage.componentStageKindId,
                componentId = _componentComponentStage.value.componentComponentStage.componentId,
                quantity = _componentComponentStage.value.componentComponentStage.quantity
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
        val route = _route.value
        appNavigator.navigateTo(
            route = Route.Main.ProductLines.ProductKinds.Products.ProductsList(
                productKindId = route.productKindId,
                productId = route.productId,
                componentKindId = route.componentKindId,
                componentId = route.componentId,
                componentStageKindId = route.componentStageKindId,
                componentStageId = id
            ),
            popUpToRoute = Route.Main.ProductLines.ProductKinds.Products,
            inclusive = true
        )
    }
}

data class FillInErrors(
    val componentStageKeyError: Boolean = false,
    val componentStageDescriptionError: Boolean = false,
    val componentStageQntError: Boolean = false
)