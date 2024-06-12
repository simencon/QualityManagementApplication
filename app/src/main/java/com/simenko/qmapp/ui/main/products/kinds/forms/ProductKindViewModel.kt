package com.simenko.qmapp.ui.main.products.kinds.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainProductKind
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProductKindViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val repository: ProductsRepository,
) : ViewModel() {
    private val _productKind = MutableStateFlow(DomainProductKind.DomainProductKindComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.ProductKinds.AddEditProductKind) {
        viewModelScope.launch(Dispatchers.IO) {
            if (route.productKindId == NoRecord.num) {
                prepareProductLine(route.productLineId)
            } else {
                _productKind.value = repository.productKind(route.productKindId)
            }

            mainPageHandler = MainPageHandler.Builder(if (route.productKindId == NoRecord.num) Page.ADD_PRODUCT_KIND else Page.EDIT_PRODUCT_KIND, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { validateInput() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    private suspend fun prepareProductLine(productLineId: ID) {
        val productLine = repository.productLineById(productLineId)
        _productKind.value = DomainProductKind.DomainProductKindComplete(
            productLine = productLine,
            productKind = DomainProductKind(projectId = productLine.manufacturingProject.id)
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */

    val productKind get() = _productKind.asStateFlow()

    fun onSetProductKindDesignation(designation: String) {
        if (_productKind.value.productKind.productKindDesignation != designation) {
            _productKind.value = _productKind.value.copy(productKind = _productKind.value.productKind.copy(productKindDesignation = designation))
            _fillInErrors.value = _fillInErrors.value.copy(productKindDesignationError = false)
            _fillInState.value = FillInInitialState
        }
    }

    fun onSetProductKindIndustry(industry: String) {
        if (_productKind.value.productKind.comments != industry) {
            _productKind.value = _productKind.value.copy(productKind = _productKind.value.productKind.copy(comments = industry))
            _fillInErrors.value = _fillInErrors.value.copy(productKindIndustryError = false)
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
            with(_productKind.value.productKind) {
                if (productKindDesignation.isEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(productKindDesignationError = true)
                    append("Product designation field is mandatory\n")
                }
                if (comments.isNullOrEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(productKindIndustryError = true)
                    append("Product industry field is mandatory\n")
                }
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
        with(repository) {
            if (_productKind.value.productKind.id == NoRecord.num) insertProductKind(_productKind.value.productKind) else updateProductKind(_productKind.value.productKind)
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
        withContext(Dispatchers.Main) {
            id?.let {
                val productLineId = _productKind.value.productKind.projectId
                val productKindId = it
                appNavigator.tryNavigateTo(
                    route = Route.Main.ProductLines.ProductKinds.ProductKindsList(productLineId, productKindId),
                    popUpToRoute = Route.Main.ProductLines,
                    inclusive = true
                )
            }
        }
    }
}

data class FillInErrors(
    var productKindDesignationError: Boolean = false,
    var productKindIndustryError: Boolean = false,
)