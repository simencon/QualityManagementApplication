package com.simenko.qmapp.ui.main.products.characteristics.forms.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.products.DomainCharGroup
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
class CharGroupViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
) : ViewModel() {
    private val _charGroup = MutableStateFlow(DomainCharGroup.DomainCharGroupComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.Characteristics.AddEditCharGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            if (route.charGroupId == NoRecord.num) prepareCharGroup(route.productLineId) else _charGroup.value = repository.charGroupById(route.charGroupId)
            mainPageHandler = MainPageHandler.Builder(if (route.charGroupId == NoRecord.num) Page.ADD_PRODUCT_LINE_CHAR_GROUP else Page.EDIT_PRODUCT_LINE_CHAR_GROUP, mainPageState)
                .setOnNavMenuClickAction { appNavigator.navigateBack() }
                .setOnFabClickAction { validateInput() }
                .build()
                .apply { setupMainPage(0, true) }
        }
    }

    private fun prepareCharGroup(productLineId: ID) = viewModelScope.launch(Dispatchers.IO) {
        val productLine = repository.productLineById(productLineId)
        _charGroup.value = DomainCharGroup.DomainCharGroupComplete(
            productLine = productLine,
            charGroup = DomainCharGroup(productLineId = productLine.manufacturingProject.id)
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val charGroup get() = _charGroup.asStateFlow()

    fun onSetCharGroupDescription(it: String) {
        _charGroup.value = _charGroup.value.copy(charGroup = _charGroup.value.charGroup.copy(ishElement = it))
        _fillInErrors.value = _fillInErrors.value.copy(charGroupDescriptionError = false)
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
            if (_charGroup.value.charGroup.ishElement.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(charGroupDescriptionError = true)
                append("Char. group description field is mandatory\n")
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
        mainPageHandler?.updateLoadingState?.invoke(Triple(true,  false,null))
        repository.run { if (_charGroup.value.charGroup.id == NoRecord.num) insertCharGroup(_charGroup.value.charGroup) else updateCharGroup(_charGroup.value.charGroup) }
            .consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true,  false,null))
                        Status.SUCCESS -> navBackToRecord(resource.data?.id)
                        Status.ERROR -> {
                            mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, resource.message))
                            _fillInState.value = FillInInitialState
                        }
                    }
                }
            }
    }

    private suspend fun navBackToRecord(id: ID?) {
        id?.let {
            mainPageHandler?.updateLoadingState?.invoke(Triple(false,  false,null))
            val productLine = _charGroup.value.productLine.manufacturingProject.id
            val charGroupId = it
            appNavigator.navigateTo(
                route = Route.Main.ProductLines.Characteristics.CharacteristicGroupList(productLineId = productLine, charGroupId = charGroupId),
                popUpToRoute = Route.Main.ProductLines.Characteristics,
                inclusive = true
            )
        }
    }
}

data class FillInErrors(
    var charGroupDescriptionError: Boolean = false
)