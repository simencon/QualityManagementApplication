package com.simenko.qmapp.ui.main.products.items.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ComponentIdParameter
import com.simenko.qmapp.di.ComponentKindIdParameter
import com.simenko.qmapp.di.ComponentStageIdParameter
import com.simenko.qmapp.di.ComponentStageKindIdParameter
import com.simenko.qmapp.di.ProductIdParameter
import com.simenko.qmapp.di.ProductKindIdParameter
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainProductKind
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    @ProductKindIdParameter productKindId: ID,
    @ProductIdParameter productId: ID,
    @ComponentKindIdParameter componentKindId: ID,
    @ComponentIdParameter componentId: ID,
    @ComponentStageKindIdParameter componentStageKindId: ID,
    @ComponentStageIdParameter componentStageId: ID
) : ViewModel() {
    private val _productsVisibility = MutableStateFlow(Pair(SelectedNumber(productId), NoRecord))
    private val _componentKindsVisibility = MutableStateFlow(Pair(SelectedNumber(componentKindId), NoRecord))
    private val _componentsVisibility = MutableStateFlow(Pair(SelectedNumber(componentId), NoRecord))
    private val _componentStageKindsVisibility = MutableStateFlow(Pair(SelectedNumber(componentStageKindId), NoRecord))
    private val _componentStagesVisibility = MutableStateFlow(Pair(SelectedNumber(componentStageId), NoRecord))
    private val _versionsVisibility = MutableStateFlow(Triple(NoRecord, NoRecord, NoRecord))

    private val _productKind = MutableStateFlow(DomainProductKind.DomainProductKindComplete())
    private val _products = repository.productKindProducts(productKindId)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_KIND_LIST, mainPageState)
            .setOnFabClickAction {
                if (isSecondColumnVisible.value)
                    if (_versionsVisibility.value.first != NoRecord) onAddProductVersionClick(_productsVisibility.value.first.num)
                    else if (_versionsVisibility.value.second != NoRecord) onAddComponentVersionClick(_componentsVisibility.value.first.num)
                    else onAddComponentStageVersionClick(_componentStagesVisibility.value.first.num)
                else
                    onAddProduct(productKindId)
            }
            .setOnPullRefreshAction { updateProductsData() }
            .build()

        viewModelScope.launch(Dispatchers.IO) { _productKind.value = repository.productKind(productKindId) }
    }


    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _productsVisibility.value = _productsVisibility.value.setVisibility(dId, aId)
    }
    fun onProductVersionsClick(it: ID) {
        TODO("Not yet implemented")
    }

    /**
     * UI state -------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productKind get() = _productKind.asStateFlow()

    private val _viewState = MutableStateFlow(false)
    val setViewState: (Boolean) -> Unit = {
        if (!it) _isComposed.value = BooleanArray(5) { false }
        _viewState.value = it
    }

    private val _isComposed = MutableStateFlow(BooleanArray(5) { false })
    val setIsComposed: (Int, Boolean) -> Unit = { i, value ->
        val cpy = _isComposed.value.copyOf()
        cpy[i] = value
        _isComposed.value = cpy
    }

    val isSecondColumnVisible: StateFlow<Boolean> = _isComposed.flatMapLatest { isComposed ->
        _versionsVisibility.flatMapLatest { versionsVisibility ->
            flow { emit(((versionsVisibility.first != NoRecord) || (versionsVisibility.second != NoRecord) || (versionsVisibility.third != NoRecord)) && isComposed.component5()) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val productsVisibility get() = _productsVisibility.asStateFlow()
    val products = _products.flatMapLatest { products ->
        _productsVisibility.flatMapLatest { visibility ->
            val cpy = products.map { it.copy(detailsVisibility = it.productKindProduct.productId == visibility.first.num, isExpanded = it.productKindProduct.productId == visibility.second.num) }
            flow { emit(cpy) }
        }
    }
    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun updateProductsData() {
        TODO("Not yet implemented")
    }
    fun onDeleteProductClick(it: ID) {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    private fun onAddProduct(productKindId: ID) {
        TODO("Not yet implemented")
    }
    fun onEditProductClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    private fun onAddProductVersionClick(productId: ID) {
        TODO("Not yet implemented")
    }

    private fun onAddComponentVersionClick(componentId: ID) {
        TODO("Not yet implemented")
    }

    private fun onAddComponentStageVersionClick(componentStageId: ID) {
        TODO("Not yet implemented")
    }
}