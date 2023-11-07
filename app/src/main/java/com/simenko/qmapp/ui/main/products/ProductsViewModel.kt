package com.simenko.qmapp.ui.main.products

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.di.CompanyIdParameter
import com.simenko.qmapp.di.ProductLineIdParameter
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
    @CompanyIdParameter val companyId: Int,
    @ProductLineIdParameter val productLineId: Int
) : ViewModel() {
    private val _productLinesVisibility = MutableStateFlow(Pair(SelectedNumber(productLineId), NoRecord))
    private val _productLines = repository.productLines(companyId.toLong())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.PRODUCTS, mainPageState)
            .setOnFabClickAction { onAddProductLineClick(companyId) }
            .setOnPullRefreshAction { updateCompanyProductsData() }
            .build()
    }
    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductLinesVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _productLinesVisibility.value = _productLinesVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */

    val productLines = _productLines.flatMapLatest { productLines ->
        _productLinesVisibility.flatMapLatest { visibility ->
            val cpy = productLines.map { it.copy(detailsVisibility = it.manufacturingProject.id == visibility.first.num, isExpanded = it.manufacturingProject.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun updateCompanyProductsData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddProductLineClick(companyId: Int) {
        TODO("Not yet implemented")
    }

    fun onDeleteProductLineClick(it: Long) {
        TODO("Not yet implemented")
    }

    fun onEditProductLineClick(it: Pair<Long, Long>) {
        TODO("Not yet implemented")
    }

    fun onProductLineKeysClick(it: Long) {
        TODO("Not yet implemented")
    }

    fun onProductLineCharacteristicsClick(it: Long) {
        TODO("Not yet implemented")
    }

    fun onProductLineItemsClick(it: Long) {
        TODO("Not yet implemented")
    }

}