package com.simenko.qmapp.presentation.ui.main.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ProductsRepository
import com.simenko.qmapp.presentation.ui.main.main.MainPageHandler
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.utils.InvestigationsUtils.setVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductLinesViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _companyId = MutableStateFlow(NoRecord.num)
    private val _productLinesVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _productLines = _companyId.flatMapLatest { companyId -> repository.productLines(companyId) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductLinesList) {
        viewModelScope.launch {
            if (mainPageHandler == null) {
                _companyId.value = route.companyId
                _productLinesVisibility.value = Pair(SelectedNumber(route.productLineId), NoRecord)
            }
            mainPageHandler = MainPageHandler.Builder(Page.PRODUCTS, mainPageState)
                .setOnFabClickAction { onAddProductLineClick(route.companyId) }
                .setOnPullRefreshAction { updateCompanyProductsData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
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
    fun onDeleteProductLineClick(it: ID) = viewModelScope.launch(Dispatchers.IO) {
        with(repository) {
            deleteProductLine(it).consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                        Status.SUCCESS -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                        Status.ERROR -> mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, resource.message))
                    }
                }
            }
        }
    }

    private fun updateCompanyProductsData() = viewModelScope.launch {
        try {
            mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))

            repository.syncProductLines()

            mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
        } catch (e: Exception) {
            mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, e.message))
        }
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddProductLineClick(companyId: ID) {
        appNavigator.tryNavigateTo(Route.Main.ProductLines.AddEditProductLine(companyId = companyId))
    }

    fun onEditProductLineClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(Route.Main.ProductLines.AddEditProductLine(companyId = it.first, productLineId = it.second))
    }

    fun onProductLineKeysClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductLineKeys.ProductLineKeysList(productLineId = it))
    }

    fun onProductLineCharacteristicsClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.Characteristics.CharacteristicGroupList(productLineId = it))
    }

    fun onProductLineItemsClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.ProductKindsList(productLineId = it))
    }

}