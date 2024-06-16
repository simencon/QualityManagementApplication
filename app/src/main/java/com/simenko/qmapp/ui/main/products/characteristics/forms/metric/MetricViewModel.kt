package com.simenko.qmapp.ui.main.products.characteristics.forms.metric

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.ZeroDouble
import com.simenko.qmapp.domain.entities.products.DomainMetrix
import com.simenko.qmapp.domain.entities.products.DomainProductLine
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetricViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
) : ViewModel() {
    private val _productLine = MutableStateFlow(DomainProductLine())
    private val _metric = MutableStateFlow(DomainMetrix.DomainMetricWithParents())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.Characteristics.AddEditMetric) = viewModelScope.launch(Dispatchers.IO) {
        if (route.metricId == NoRecord.num) {
            prepareMetric(route.characteristicId)
        } else {
            _productLine.value = repository.characteristicById(route.characteristicId).characteristicSubGroup.charGroup.productLine.manufacturingProject
            _metric.value = repository.metricById(route.metricId)
        }
        mainPageHandler = MainPageHandler.Builder(if (route.characteristicId == NoRecord.num) Page.ADD_PRODUCT_LINE_METRIC else Page.EDIT_PRODUCT_LINE_METRIC, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { validateInput() }
            .build()
            .apply { setupMainPage(0, true) }
    }

    private suspend fun prepareMetric(charId: ID) {
        val characteristic = repository.characteristicById(charId)
        _productLine.value = characteristic.characteristicSubGroup.charGroup.productLine.manufacturingProject
        _metric.value = DomainMetrix.DomainMetricWithParents(
            groupId = characteristic.characteristicSubGroup.charGroup.charGroup.id,
            groupDescription = characteristic.characteristicSubGroup.charGroup.charGroup.ishElement ?: EmptyString.str,
            subGroupId = characteristic.characteristicSubGroup.charSubGroup.id,
            subGroupDescription = characteristic.characteristicSubGroup.charSubGroup.ishElement ?: EmptyString.str,
            subGroupRelatedTime = characteristic.characteristicSubGroup.charSubGroup.measurementGroupRelatedTime ?: ZeroDouble.double,
            charId = characteristic.characteristic.id,
            charOrder = characteristic.characteristic.charOrder ?: NoRecord.num.toInt(),
            charDesignation = characteristic.characteristic.charDesignation,
            charDescription = characteristic.characteristic.charDescription ?: EmptyString.str,
            sampleRelatedTime = characteristic.characteristic.sampleRelatedTime ?: ZeroDouble.double,
            measurementRelatedTime = characteristic.characteristic.measurementRelatedTime ?: ZeroDouble.double,
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */

    val productLine get() = _productLine.asStateFlow()
    val metric get() = _metric.asStateFlow()

    fun onSetOrder(it: Int) {
        if (_metric.value.metricOrder != it) {
            _metric.value = _metric.value.copy(metricOrder = it)
            _fillInErrors.value = _fillInErrors.value.copy(metricOrderError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetDesignation(it: String) {
        if (_metric.value.metricDesignation != it) {
            _metric.value = _metric.value.copy(metricDesignation = it)
            _fillInErrors.value = _fillInErrors.value.copy(metricDesignationError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetDescription(it: String) {
        if (_metric.value.metricDescription != it) {
            _metric.value = _metric.value.copy(metricDescription = it)
            _fillInErrors.value = _fillInErrors.value.copy(metricDescriptionError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetUnits(it: String) {
        if (_metric.value.metricUnits != it) {
            _metric.value = _metric.value.copy(metricUnits = it)
            _fillInErrors.value = _fillInErrors.value.copy(metricUnitsError = false)
            _fillInState.value = FillInInitialState
        }
    }


    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */

    private val _fillInErrors = MutableStateFlow(FillInErrors())
    val fillInErrors get() = _fillInErrors.asStateFlow()
    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    val fillInState get() = _fillInState.asStateFlow()
    private fun validateInput() {
        val errorMsg = buildString {
            with(_metric.value) {
                if (metricOrder == NoRecord.num.toInt()) {
                    _fillInErrors.value = _fillInErrors.value.copy(metricOrderError = true)
                    append("Metric order field is mandatory\n")
                }
                if (metricDesignation.isNullOrEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(metricDesignationError = true)
                    append("Metric designation field is mandatory\n")
                }
                if (metricDescription.isNullOrEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(metricDescriptionError = true)
                    append("Metric description field is mandatory\n")
                }
                if (metricUnits.isEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(metricUnitsError = true)
                    append("Metric units field is mandatory\n")
                }
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
        mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))

        val metric = with(_metric.value) {
            DomainMetrix(
                id = metricId,
                charId = charId,
                metrixOrder = metricOrder,
                metrixDesignation = metricDesignation,
                metrixDescription = metricDescription,
                units = metricUnits,
            )
        }

        repository.run {
            if (metric.id == NoRecord.num) insertMetric(metric) else updateMetric(metric)
        }.consumeEach { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
                    Status.SUCCESS -> navBackToRecord(resource.data?.id)
                    Status.ERROR -> {
                        mainPageHandler?.updateLoadingState?.invoke(Pair(true, resource.message))
                        _fillInState.value = FillInInitialState
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(id: ID?) {
        mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
        id?.let {
            val productLine = _productLine.value.id
            val charGroupId = _metric.value.groupId
            val charSubGroupId = _metric.value.subGroupId
            val charId = _metric.value.charId
            val metricId = it
            appNavigator.navigateTo(
                route = Route.Main.ProductLines.Characteristics.CharacteristicGroupList(
                    productLineId = productLine,
                    charGroupId = charGroupId,
                    charSubGroupId = charSubGroupId,
                    characteristicId = charId,
                    metricId = metricId
                ),
                popUpToRoute = Route.Main.ProductLines.Characteristics,
                inclusive = true
            )
        }
    }

}

data class FillInErrors(
    val metricOrderError: Boolean = false,
    val metricDesignationError: Boolean = false,
    val metricDescriptionError: Boolean = false,
    val metricUnitsError: Boolean = false,
)