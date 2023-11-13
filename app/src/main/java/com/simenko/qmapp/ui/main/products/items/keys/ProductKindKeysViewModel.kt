package com.simenko.qmapp.ui.main.products.items.keys

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ProductKindIdParameter
import com.simenko.qmapp.di.ProductKindKeyIdParameter
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainProductKind
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductKindKeysViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
    val storage: Storage,
    @ProductKindIdParameter private val productKindId: ID,
    @ProductKindKeyIdParameter private val productKindKeyId: ID
) : ViewModel() {
    private val _productKindKeysVisibility = MutableStateFlow(Pair(SelectedNumber(productKindKeyId), NoRecord))
    private val _productKind = MutableStateFlow(DomainProductKind.DomainProductKindComplete())
    private val _productKindKeys = repository.productKindKeys(productKindId)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_KIND_KEYS, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { onAddProductKindKeyClick(productKindId) }
            .setOnPullRefreshAction { updateCompanyProductsData() }
            .build()
        viewModelScope.launch(Dispatchers.IO) { _productKind.value = repository.productKind(productKindId) }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductKindKeysVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _productKindKeysVisibility.value = _productKindKeysVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productKind get() = _productKind.asStateFlow()
    val productKindKeys = _productKindKeys.flatMapLatest { productKindKeys ->
        _productKindKeysVisibility.flatMapLatest { visibility ->
            val cpy = productKindKeys.map {
                it.copy(key = it.key.copy(detailsVisibility = it.key.productLineKey.id == visibility.first.num, isExpanded = it.key.productLineKey.id == visibility.second.num))
            }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteProductKindKeyClick(it: ID) {
        TODO("Not yet implemented")
    }

    private fun updateCompanyProductsData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onAddProductKindKeyClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onEditProductKindClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onEditProductKindKeyClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onProductKindKeysClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onProductKindSpecificationClick(it: ID) {
        TODO("Not yet implemented")
    }

    fun onProductKindItemsClick(it: ID) {
        TODO("Not yet implemented")
    }
}