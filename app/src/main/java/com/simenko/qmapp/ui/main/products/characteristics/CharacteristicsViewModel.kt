package com.simenko.qmapp.ui.main.products.characteristics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.MetricIdParameter
import com.simenko.qmapp.di.ProductLineCharacteristicIdParameter
import com.simenko.qmapp.di.ProductLineIdParameter
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
class CharacteristicsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
    @ProductLineIdParameter val productLineId: Int,
    @ProductLineCharacteristicIdParameter val characteristicId: Int,
    @MetricIdParameter val metricId: Int
) : ViewModel() {
    private val _characteristicsVisibility = MutableStateFlow(Pair(SelectedNumber(characteristicId), NoRecord))
    private val _metricVisibility = MutableStateFlow(Pair(SelectedNumber(metricId), NoRecord))
    private val _productLine = MutableStateFlow(DomainProductLine())
    private val _characteristics = repository.productLineCharacteristics(productLineId)
    private val _metrics = _characteristicsVisibility.flatMapLatest { repository.metrics(it.first.num) }

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.PRODUCT_LINE_CHARACTERISTICS, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { onAddProductLineCharacteristicClick(Pair(productLineId, NoRecord.num)) }
            .setOnPullRefreshAction { updateProductLineCharacteristicsData() }
            .build()
        viewModelScope.launch(Dispatchers.IO) { _productLine.value = repository.productLine(productLineId) }
    }

    /**
     * UI operations ---------------------------------------------------------------------------------------------------------------------------------
     * */
    fun setProductLineCharacteristicsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _characteristicsVisibility.value = _characteristicsVisibility.value.setVisibility(dId, aId)
    }

    fun setMetricsVisibility(dId: SelectedNumber = NoRecord, aId: SelectedNumber = NoRecord) {
        _metricVisibility.value = _metricVisibility.value.setVisibility(dId, aId)
    }

    /**
     * UI state --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val productLine get() = _productLine.asStateFlow()

    val characteristics = _characteristics.flatMapLatest { characteristics ->
        _characteristicsVisibility.flatMapLatest { visibility ->
            val cpy = characteristics.map { it.copy(detailsVisibility = it.characteristic.id == visibility.first.num, isExpanded = it.characteristic.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    val metrics = _metrics.flatMapLatest { metrics ->
        _metricVisibility.flatMapLatest { visibility ->
            val cpy = metrics.map { it.copy(detailsVisibility = it.id == visibility.first.num, isExpanded = it.id == visibility.second.num) }
            flow { emit(cpy) }
        }
    }

    /**
     * REST operations -------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onDeleteProductLineCharacteristicClick(it: Int) {
        TODO("Not yet implemented")
    }

    private fun updateProductLineCharacteristicsData() {
        TODO("Not yet implemented")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun onAddProductLineCharacteristicClick(it: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    fun onEditProductLineCharacteristicClick(it: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }
}