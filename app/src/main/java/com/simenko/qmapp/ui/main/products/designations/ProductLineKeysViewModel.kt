package com.simenko.qmapp.ui.main.products.designations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ProductLineKeyIdParameter
import com.simenko.qmapp.di.ProductLineIdParameter
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.entities.products.DomainProductLine
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductLineKeysViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
    @ProductLineIdParameter val productLineId: ID,
    @ProductLineKeyIdParameter val productKeyId: ID
) : ViewModel() {
    private val _productKeysVisibility = MutableStateFlow(Pair(SelectedNumber(productKeyId), NoRecord))
    private val _productLine = MutableStateFlow(DomainProductLine())
    private val _productKeys = repository.productLineKeys(productLineId)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_LINE_KEYS, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { onAddProductLineKeyClick(Pair(productLineId, NoRecord.num)) }
            .setOnPullRefreshAction { updateProductLineKeysData() }
            .build()
        viewModelScope.launch(Dispatchers.IO) { _productLine.value = repository.productLine(productLineId) }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductLineKeysVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _productKeysVisibility.value = _productKeysVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productLine get() = _productLine.asStateFlow()

    val productKeys = _productKeys.flatMapLatest { key ->
        _productKeysVisibility.flatMapLatest { visibility ->
            val cpy = key.map { it.copy(detailsVisibility = it.productLineKey.id == visibility.first.num, isExpanded = it.productLineKey.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteProductLineKeyClick(it: ID) {
        TODO("Not yet implemented")
    }

    private fun updateProductLineKeysData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddProductLineKeyClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }

    fun onEditProductLineKeyClick(it: Pair<ID, ID>) {
        TODO("Not yet implemented")
    }
}