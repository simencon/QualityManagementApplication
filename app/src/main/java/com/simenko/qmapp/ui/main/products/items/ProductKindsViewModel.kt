package com.simenko.qmapp.ui.main.products.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ProductKindIdParameter
import com.simenko.qmapp.di.ProductLineIdParameter
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainProductLine
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.storage.Storage
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductKindsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
    @ProductLineIdParameter private val productLineId: ID,
    @ProductKindIdParameter private val productKindId: ID
) : ViewModel() {
    private val _productKindsVisibility = MutableStateFlow(Pair(SelectedNumber(productKindId), NoRecord))
    private val _productLine = MutableStateFlow(DomainProductLine())
    private val _productKinds = repository.productKinds(productLineId)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_KINDS, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { onAddProductKindClick(Pair(productLineId, NoRecord.num)) }
            .setOnPullRefreshAction { updateCompanyProductsData() }
            .build()
        viewModelScope.launch(Dispatchers.IO) { _productLine.value = repository.productLine(productLineId) }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductKindsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _productKindsVisibility.value = _productKindsVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */

    val productKinds = _productKinds.flatMapLatest { productKinds ->
        _productKindsVisibility.flatMapLatest { visibility ->
            val cpy = productKinds.map { it.copy(detailsVisibility = it.productKind.id == visibility.first.num, isExpanded = it.productKind.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteProductKindClick(it: ID) {
        TODO("Not yet implemented")
    }

    private fun updateCompanyProductsData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddProductKindClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onEditProductKindClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onProductKindSpecificationClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onProductKindItemsClick(it: ID) {
        TODO("Not yet implemented")
    }
}