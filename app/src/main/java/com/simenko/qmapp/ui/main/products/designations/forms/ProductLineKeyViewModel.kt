package com.simenko.qmapp.ui.main.products.designations.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainKey
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ProductLineKeyViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _productLineKey = MutableStateFlow(DomainKey.DomainKeyComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null
    fun onEntered(route: Route.Main.ProductLines.ProductLineKeys.AddEditProductLineKey) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (route.productLineKeyId == NoRecord.num) prepareProductLineKey(route.productLineId) else _productLineKey.value = repository.productLineKeyById(route.productLineKeyId)
                mainPageHandler = MainPageHandler.Builder(if (route.productLineKeyId == NoRecord.num) Page.ADD_PRODUCT_LINE_KEY else Page.EDIT_PRODUCT_LINE_KEY, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { validateInput() }
                    .build()
                    .apply { setupMainPage(0, true) }
            }
        }
    }

    private suspend fun prepareProductLineKey(productLineId: ID) {
        val productLine = repository.productLine(productLineId)
        _productLineKey.value = DomainKey.DomainKeyComplete(
            productLine = productLine,
            productLineKey = DomainKey(projectId = productLine.id)
        )
    }

    val productLineKey get() = _productLineKey.asStateFlow()

    fun setProductLineKey(it: String) {
        _productLineKey.value = _productLineKey.value.copy(productLineKey = _productLineKey.value.productLineKey.copy(componentKey = it))
        _fillInErrors.value = fillInErrors.value.copy(keyError = false)
        _fillInState.value = FillInInitialState
    }

    fun setProductLineKeyDescription(it: String) {
        _productLineKey.value = _productLineKey.value.copy(productLineKey = _productLineKey.value.productLineKey.copy(componentKeyDescription = it))
        _fillInErrors.value = fillInErrors.value.copy(keyDescriptionError = false)
        _fillInState.value = FillInInitialState
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
            if (_productLineKey.value.productLineKey.componentKey.isEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(keyError = true)
                append("Designation field is mandatory")
            }
            if (_productLineKey.value.productLineKey.componentKeyDescription.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(keyError = true)
                append("Designation description field is mandatory")
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            with(repository) {
                if (_productLineKey.value.productLineKey.id == NoRecord.num) insertProductLineKey(_productLineKey.value.productLineKey) else updateProductLineKey(_productLineKey.value.productLineKey)
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
    }

    private suspend fun navBackToRecord(id: ID?) {
        mainPageHandler?.updateLoadingState?.invoke(Pair(false, null))
        withContext(Dispatchers.Main) {
            id?.let {
                val productLineId = _productLineKey.value.productLineKey.projectId
                val keyId = it
                appNavigator.tryNavigateTo(
                    route = Route.Main.ProductLines.ProductLineKeys.ProductLineKeysList(productLineId, keyId),
                    popUpToRoute = Route.Main.ProductLines.ProductLineKeys,
                    inclusive = true
                )
            }
        }
    }
}

data class FillInErrors(
    var keyError: Boolean = false,
    var keyDescriptionError: Boolean = false,
)