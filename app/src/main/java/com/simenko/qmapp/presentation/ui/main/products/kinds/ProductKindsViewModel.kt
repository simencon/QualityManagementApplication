package com.simenko.qmapp.presentation.ui.main.products.kinds

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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductKindsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
) : ViewModel() {
    private val _productLineId = MutableStateFlow(NoRecord.num)
    private val _productKindsVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _productKinds = _productLineId.flatMapLatest { repository.productKinds(it) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.ProductKindsList) {
        viewModelScope.launch {
            if (mainPageHandler == null) {
                _productLineId.value = route.productLineId
                _productKindsVisibility.value = Pair(SelectedNumber(route.productKindId), NoRecord)
            }

            mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_KINDS, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { onAddProductKindClick(Pair(route.productLineId, NoRecord.num)) }
                .setOnPullRefreshAction { updateCompanyProductsData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
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
    val productLine = _productLineId.flatMapLatest { flow { emit(repository.productLine(it)) } }.flowOn(Dispatchers.IO)

    val productKinds = _productKinds.flatMapLatest { productKinds ->
        _productKindsVisibility.flatMapLatest { visibility ->
            val cpy = productKinds.map { it.copy(detailsVisibility = it.productKind.id == visibility.first.num, isExpanded = it.productKind.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteProductKindClick(it: ID) = viewModelScope.launch(Dispatchers.IO) {
        with(repository) {
            deleteProductKind(it).consumeEach { event ->
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

            repository.syncProductKinds()

            mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
        } catch (e: Exception) {
            mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, e.message))
        }
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddProductKindClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(Route.Main.ProductLines.ProductKinds.AddEditProductKind(productLineId = it.first, productKindId = it.second))
    }

    fun onEditProductKindClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(Route.Main.ProductLines.ProductKinds.AddEditProductKind(productLineId = it.first, productKindId = it.second))
    }

    fun onProductKindKeysClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.ProductKindKeys.ProductKindKeysList(productKindId = it))
    }

    fun onProductKindCharacteristicsClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.ProductKindCharacteristics.ProductKindCharacteristicsList(productKindId = it))
    }

    fun onProductKindSpecificationClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.ProductSpecification.ProductSpecificationList(productKindId = it))
    }

    fun onProductKindItemsClick(it: ID) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductKinds.Products.ProductsList(productKindId = it))
    }
}