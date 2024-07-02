package com.simenko.qmapp.ui.main.products.kinds.set

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductKindSpecificationViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
) : ViewModel() {
    private val _productKindId = MutableStateFlow(NoRecord.num)
    private val _componentKindsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _componentStageKindsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _componentKinds = _productKindId.flatMapLatest { repository.componentKinds(it) }
    private val _componentStageKinds = _componentKindsVisibility.flatMapLatest { repository.componentStageKinds(it.first.num) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.ProductSpecification.ProductSpecificationList) {
        viewModelScope.launch {
            if (mainPageHandler == null) {
                _productKindId.value = route.productKindId
                _componentKindsVisibility.value = Pair(SelectedNumber(route.componentKindId), NoRecord)
                _componentStageKindsVisibility.value = Pair(SelectedNumber(route.componentStageKindId), NoRecord)
            }

            mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_KIND_SPECIFICATION, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { onAddComponentKindClick(route.productKindId) }
                .setOnPullRefreshAction { updateCompanyProductsData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setComponentKindsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentKindsVisibility.value = _componentKindsVisibility.value.setVisibility(dId, aId)
    }

    fun setComponentStageKindsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _componentStageKindsVisibility.value = _componentStageKindsVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productKind = _productKindId.flatMapLatest { flow { emit(repository.productKind(it)) } }.flowOn(Dispatchers.IO)

    val componentKindsVisibility get() = _componentKindsVisibility.asStateFlow()
    val componentKinds = _componentKinds.flatMapLatest { componentKinds ->
        _componentKindsVisibility.flatMapLatest { visibility ->
            val cpy = componentKinds.map { it.copy(detailsVisibility = it.componentKind.id == visibility.first.num, isExpanded = it.componentKind.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }
    val componentStageKinds = _componentStageKinds.flatMapLatest { componentStageKinds ->
        _componentStageKindsVisibility.flatMapLatest { visibility ->
            val cpy = componentStageKinds.map { it.copy(detailsVisibility = it.componentStageKind.id == visibility.first.num, isExpanded = it.componentStageKind.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteComponentKindClick(it: ID) = viewModelScope.launch(Dispatchers.IO) {
        with(repository) {
            deleteComponentKind(it).consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                        Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
                        Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Pair(false, resource.message))
                    }
                }
            }
        }
    }


    fun onDeleteComponentStageKindClick(it: ID) {
        TODO("Not yet implemented")
    }

    private fun updateCompanyProductsData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddComponentKindClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.ProductSpecification.AddEditComponentKind(productKindId = it))
    }

    fun onAddComponentStageKindClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onEditComponentKindClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.ProductSpecification.AddEditComponentKind(productKindId = it.first, componentKindId = it.second))
    }

    fun onEditComponentStageKindClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onComponentKindKeysClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentKindKeys.ComponentKindKeysList(componentKindId = it))
    }

    fun onComponentStageKindKeysClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindKeys.ComponentStageKindKeysList(componentStageKindId = it))
    }

    fun onComponentKindCharacteristicsClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentKindCharacteristics.ComponentKindCharacteristicsList(componentKindId = it))
    }

    fun onComponentStageKindCharacteristicsClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.ProductSpecification.ComponentStageKindCharacteristics.ComponentStageKindCharacteristicsList(componentStageKindId = it))
    }
}