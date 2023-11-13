package com.simenko.qmapp.ui.main.products.items.specification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ComponentKindIdParameter
import com.simenko.qmapp.di.ComponentStageKindIdParameter
import com.simenko.qmapp.di.ProductKindIdParameter
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainProductKind
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductKindSpecificationViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    @ProductKindIdParameter private val productKindId: ID,
    @ComponentKindIdParameter private val componentKindId: ID,
    @ComponentStageKindIdParameter private val componentStageKindId: ID
) : ViewModel() {
    private val _componentKindsVisibility = MutableStateFlow(Pair(SelectedNumber(componentKindId), NoRecord))
    private val _componentStageKindsVisibility = MutableStateFlow(Pair(SelectedNumber(componentStageKindId), NoRecord))
    private val _productKind = MutableStateFlow(DomainProductKind.DomainProductKindComplete())
    private val _componentKinds = repository.componentKinds(productKindId)
    private val _componentStageKinds = _componentKindsVisibility.flatMapLatest { repository.componentStageKinds(it.first.num) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_KIND_SPECIFICATION, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { onAddComponentKindClick(Pair(productKindId, NoRecord.num)) }
            .setOnPullRefreshAction { updateCompanyProductsData() }
            .build()
        viewModelScope.launch(Dispatchers.IO) { _productKind.value = repository.productKind(productKindId) }
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
    val productKind get() = _productKind.asStateFlow()

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
    fun onDeleteComponentKindClick(it: ID) {
        TODO("Not yet implemented")
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
    private fun onAddComponentKindClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }
    fun onAddComponentStageKindClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onEditComponentKindClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }
    fun onEditComponentStageKindClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onComponentKindKeysClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.Products.ProductLines.ProductKinds.ProductKindKeys.withOpts(it.toString(), NoRecordStr.str))
    }
    fun onComponentStageKindKeysClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.Products.ProductLines.ProductKinds.ProductKindKeys.withOpts(it.toString(), NoRecordStr.str))
    }
}