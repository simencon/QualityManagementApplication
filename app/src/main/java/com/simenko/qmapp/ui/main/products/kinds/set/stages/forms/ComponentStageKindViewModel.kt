package com.simenko.qmapp.ui.main.products.kinds.set.stages.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainComponentStageKind
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
class ComponentStageKindViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _componentStageKind = MutableStateFlow(DomainComponentStageKind.DomainComponentStageKindComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.ProductSpecification.AddEditComponentStageKind) {
        viewModelScope.launch(Dispatchers.IO) {
            if (route.componentStageKindId == NoRecord.num) {
                prepareComponentStageKind(route.componentKindId)
            } else {
                _componentStageKind.value = repository.componentStageKind(route.componentStageKindId)
            }

            mainPageHandler = MainPageHandler.Builder(if (route.componentStageKindId == NoRecord.num) Page.ADD_COMPONENT_STAGE_KIND else Page.EDIT_COMPONENT_STAGE_KIND, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { validateInput() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    private suspend fun prepareComponentStageKind(componentKindId: ID) {
        val componentKind = repository.componentKind(componentKindId)
        _componentStageKind.value = DomainComponentStageKind.DomainComponentStageKindComplete(
            componentKind = componentKind,
            componentStageKind = DomainComponentStageKind(componentKindId = componentKind.componentKind.id)
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */

    val componentStageKind get() = _componentStageKind.asStateFlow()

    fun onSetComponentStageKindOrder(order: String) {
        val orderInt = order.toIntOrNull() ?: NoRecord.num.toInt()
        if (_componentStageKind.value.componentStageKind.componentStageOrder != orderInt) {
            _componentStageKind.value = _componentStageKind.value.copy(componentStageKind = _componentStageKind.value.componentStageKind.copy(componentStageOrder = orderInt))
            _fillInErrors.value = _fillInErrors.value.copy(componentStageKindOrderError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetComponentKindDescription(description: String) {
        if (_componentStageKind.value.componentStageKind.componentStageDescription != description) {
            _componentStageKind.value = _componentStageKind.value.copy(componentStageKind = _componentStageKind.value.componentStageKind.copy(componentStageDescription = description))
            _fillInErrors.value = _fillInErrors.value.copy(componentStageKindDescriptionError = false)
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
            with(_componentStageKind.value.componentStageKind) {
                if (componentStageOrder == NoRecord.num.toInt()) {
                    _fillInErrors.value = _fillInErrors.value.copy(componentStageKindOrderError = true)
                    append("Component stage order field is mandatory\n")
                }
                if (componentStageDescription.isEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(componentStageKindDescriptionError = true)
                    append("Component stage description field is mandatory\n")
                }
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
        with(repository) {
            if (_componentStageKind.value.componentStageKind.id == NoRecord.num)
                insertComponentStageKind(_componentStageKind.value.componentStageKind)
            else
                updateComponentStageKind(_componentStageKind.value.componentStageKind)
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
            val productKindId = _componentStageKind.value.componentKind.productKind.productKind.id
            val componentKindId = _componentStageKind.value.componentStageKind.componentKindId
            val componentStageKindId = it
            appNavigator.navigateTo(
                route = Route.Main.ProductLines.ProductKinds.ProductSpecification.ProductSpecificationList(productKindId, componentKindId, componentStageKindId),
                popUpToRoute = Route.Main.ProductLines,
                inclusive = true
            )
        }
    }
}

data class FillInErrors(
    var componentStageKindOrderError: Boolean = false,
    var componentStageKindDescriptionError: Boolean = false,
)