package com.simenko.qmapp.ui.main.products.characteristics.forms.sub_group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.entities.products.DomainCharSubGroup
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharSubGroupViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
) : ViewModel() {
    private val _charSubGroup = MutableStateFlow(DomainCharSubGroup.DomainCharSubGroupComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.Characteristics.AddEditCharSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        if (route.charSubGroupId == NoRecord.num) prepareCharSubGroup(route.charGroupId) else _charSubGroup.value = repository.charSubGroupById(route.charSubGroupId)
        mainPageHandler = MainPageHandler.Builder(if (route.charSubGroupId == NoRecord.num) Page.ADD_PRODUCT_LINE_CHAR_SUB_GROUP else Page.EDIT_PRODUCT_LINE_CHAR_SUB_GROUP, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { validateInput() }
            .build()
            .apply { setupMainPage(0, true) }
    }

    private suspend fun prepareCharSubGroup(groupId: ID) {
        _charSubGroup.value = DomainCharSubGroup.DomainCharSubGroupComplete(
            charSubGroup = DomainCharSubGroup(charGroupId = groupId),
            charGroup = repository.charGroupById(groupId)
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val charSubGroup get() = _charSubGroup.asStateFlow()

    private val _charGroups = _charSubGroup.flatMapLatest { charSubGroup ->
        repository.charGroups(charSubGroup.charGroup.charGroup.productLineId)
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val charGroups = _charGroups.flatMapLatest { charGroups ->
        _charSubGroup.flatMapLatest { charSubGroup ->
            flow { emit(charGroups.map { it.charGroup.run { Triple(first = id, second = ishElement ?: EmptyString.str, third = id == charSubGroup.charGroup.charGroup.id) } }) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun setCharGroup(id: ID) {
        if (_charSubGroup.value.charSubGroup.charGroupId != id) {
            _charGroups.value.find { it.charGroup.id == id }?.let {
                _charSubGroup.value = _charSubGroup.value.copy(charGroup = it, charSubGroup = _charSubGroup.value.charSubGroup.copy(charGroupId = id))
                _fillInErrors.value = _fillInErrors.value.copy(charGroupError = false)
                _fillInState.value = FillInInitialState
            }
        }
    }

    fun setCharSubGroupDescription(it: String) {
        _charSubGroup.value = _charSubGroup.value.copy(charSubGroup = _charSubGroup.value.charSubGroup.copy(ishElement = it))
        _fillInErrors.value = _fillInErrors.value.copy(charSubGroupDescriptionError = false)
        _fillInState.value = FillInInitialState
    }

    fun setCharSubGroupMeasurementTime(value: String) {
        value.toDoubleOrNull()?.let { time ->
            _charSubGroup.value = _charSubGroup.value.copy(charSubGroup = _charSubGroup.value.charSubGroup.copy(measurementGroupRelatedTime = time))
            _fillInErrors.value = _fillInErrors.value.copy(charSubGroupRelatedTimeError = false)
            _fillInState.value = FillInInitialState
        } ?: run {
            if (value.isNotEmpty() && value != NoString.str) {
                _fillInErrors.value = _fillInErrors.value.copy(charSubGroupRelatedTimeError = true)
            } else {
                _charSubGroup.value = _charSubGroup.value.copy(charSubGroup = _charSubGroup.value.charSubGroup.copy(measurementGroupRelatedTime = null))
                _fillInErrors.value = _fillInErrors.value.copy(charSubGroupRelatedTimeError = false)
                _fillInState.value = FillInInitialState
            }
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
            if (_charSubGroup.value.charSubGroup.charGroupId == NoRecord.num) {
                _fillInErrors.value = _fillInErrors.value.copy(charGroupError = true)
                append("Characteristic group must be selected\n")
            }
            if (_charSubGroup.value.charSubGroup.ishElement.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(charSubGroupDescriptionError = true)
                append("Characteristic sub group description must be provided\n")
            }
            if (_fillInErrors.value.charSubGroupRelatedTimeError) {
                append("Characteristic sub group measurement related time with wrong format\n")
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
        mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
        repository.run {
            if (_charSubGroup.value.charSubGroup.id == NoRecord.num) insertCharSubGroup(_charSubGroup.value.charSubGroup) else updateCharSubGroup(_charSubGroup.value.charSubGroup)
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
            val productLine = _charSubGroup.value.charGroup.productLine.manufacturingProject.id
            val charGroupId = _charSubGroup.value.charGroup.charGroup.id
            appNavigator.navigateTo(
                route = Route.Main.ProductLines.Characteristics.CharacteristicGroupList(productLineId = productLine, charGroupId = charGroupId, charSubGroupId = it),
                popUpToRoute = Route.Main.ProductLines.Characteristics,
                inclusive = true
            )
        }
    }
}

data class FillInErrors(
    var charGroupError: Boolean = false,
    var charSubGroupDescriptionError: Boolean = false,
    var charSubGroupRelatedTimeError: Boolean = false
)