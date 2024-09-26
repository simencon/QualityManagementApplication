package com.simenko.qmapp.ui.main.products.kinds.set.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainComponentKind
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComponentKindViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _componentKind = MutableStateFlow(DomainComponentKind.DomainComponentKindComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.ProductSpecification.AddEditComponentKind) {
        viewModelScope.launch(Dispatchers.IO) {
            if (route.componentKindId == NoRecord.num) {
                prepareComponentKind(route.productKindId)
            } else {
                _componentKind.value = repository.componentKind(route.componentKindId)
            }

            mainPageHandler = MainPageHandler.Builder(if (route.componentKindId == NoRecord.num) Page.ADD_COMPONENT_KIND else Page.EDIT_COMPONENT_KIND, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { validateInput() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    private suspend fun prepareComponentKind(productLineId: ID) {
        val productKind = repository.productKind(productLineId)
        _componentKind.value = DomainComponentKind.DomainComponentKindComplete(
            productKind = productKind,
            componentKind = DomainComponentKind(productKindId = productKind.productKind.id)
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */

    val productKind get() = _componentKind.asStateFlow()

    fun onSetComponentKindOrder(order: String) {
        val orderInt = order.toIntOrNull()?: NoRecord.num.toInt()
        if (_componentKind.value.componentKind.componentKindOrder != orderInt) {
            _componentKind.value = _componentKind.value.copy(componentKind = _componentKind.value.componentKind.copy(componentKindOrder = orderInt))
            _fillInErrors.value = _fillInErrors.value.copy(componentKindOrderError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetComponentKindDescription(description: String) {
        if (_componentKind.value.componentKind.componentKindDescription != description) {
            _componentKind.value = _componentKind.value.copy(componentKind = _componentKind.value.componentKind.copy(componentKindDescription = description))
            _fillInErrors.value = _fillInErrors.value.copy(componentKindDescriptionError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetQuantityUnits(units: String) {
        if (_componentKind.value.componentKind.quantityUnits != units) {
            _componentKind.value = _componentKind.value.copy(componentKind = _componentKind.value.componentKind.copy(quantityUnits = units))
            _fillInErrors.value = _fillInErrors.value.copy(quantityUnitsError = false)
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
            with(_componentKind.value.componentKind) {
                if (componentKindOrder == NoRecord.num.toInt()) {
                    _fillInErrors.value = _fillInErrors.value.copy(componentKindOrderError = true)
                    append("Component order field is mandatory\n")
                }
                if (componentKindDescription.isEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(componentKindDescriptionError = true)
                    append("Component description field is mandatory\n")
                }
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
        with(repository) {
            if (_componentKind.value.componentKind.id == NoRecord.num) insertComponentKind(_componentKind.value.componentKind) else updateComponentKind(_componentKind.value.componentKind)
        }.consumeEach { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                    Status.SUCCESS -> navBackToRecord(resource.data?.id)
                    Status.ERROR -> {
                        mainPageHandler?.updateLoadingState?.invoke(Pair(true, resource.message))
                        _fillInState.value = FillInInitialState
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(id: ID?) {
        id?.let {
            mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
            val productKindId = _componentKind.value.componentKind.productKindId
            val componentKindId = it
            appNavigator.navigateTo(
                route = Route.Main.ProductLines.ProductKinds.ProductSpecification.ProductSpecificationList(productKindId, componentKindId),
                popUpToRoute = Route.Main.ProductLines,
                inclusive = true
            )
        }
    }
}

data class FillInErrors(
    var componentKindOrderError: Boolean = false,
    var componentKindDescriptionError: Boolean = false,
    var quantityUnitsError: Boolean = false,
)