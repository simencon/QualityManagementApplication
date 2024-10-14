package com.simenko.qmapp.presentation.ui.main.products.designations

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
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductLineKeysViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _productLineId = MutableStateFlow(NoRecord.num)
    private val _productKeysVisibility = MutableStateFlow(Pair(SelectedNumber(NoRecord.num), NoRecord))
    private val _productKeys = _productLineId.flatMapLatest { repository.productLineKeys(it) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductLineKeys.ProductLineKeysList) {
        viewModelScope.launch {
            if (mainPageHandler == null) {
                _productLineId.value = route.productLineId
                _productKeysVisibility.value = Pair(SelectedNumber(route.productLineKeyId), NoRecord)
            }
            mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_LINE_KEYS, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { onAddProductLineKeyClick(Pair(route.productLineId, NoRecord.num)) }
                .setOnPullRefreshAction { updateProductLineKeysData() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    val productLine = _productLineId.flatMapLatest { flow { emit(repository.productLine(it)) } }.flowOn(Dispatchers.IO)

    fun setProductLineKeysVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _productKeysVisibility.value = _productKeysVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */

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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                with(repository) {
                    deleteProductLineKey(it).consumeEach { event ->
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
        }
    }

    private fun updateProductLineKeysData() {
        viewModelScope.launch {
            try {
                mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))

                repository.syncProductLineKeys()

                mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
            } catch (e: Exception) {
                mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, e.message))
            }
        }
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddProductLineKeyClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductLineKeys.AddEditProductLineKey(productLineId = it.first, productLineKeyId = it.second))
    }

    fun onEditProductLineKeyClick(it: Pair<ID, ID>) {
        appNavigator.tryNavigateTo(route = Route.Main.ProductLines.ProductLineKeys.AddEditProductLineKey(productLineId = it.first, productLineKeyId = it.second))
    }
}