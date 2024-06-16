package com.simenko.qmapp.ui.main.products.characteristics.forms.characteristic

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
import com.simenko.qmapp.domain.entities.products.DomainCharGroup
import com.simenko.qmapp.domain.entities.products.DomainCharSubGroup
import com.simenko.qmapp.domain.entities.products.DomainCharacteristic
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.Rounder
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharacteristicViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ProductsRepository,
) : ViewModel() {
    private val _characteristic = MutableStateFlow(DomainCharacteristic.DomainCharacteristicComplete())
    private val _sampleRelatedTime = MutableStateFlow(NoString.str)
    private val _measurementRelatedTime = MutableStateFlow(NoString.str)

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(route: Route.Main.ProductLines.Characteristics.AddEditChar) = viewModelScope.launch(Dispatchers.IO) {
        if (route.characteristicId == NoRecord.num) {
            prepareChar(route.charSubGroupId)
        } else {
            _characteristic.value = repository.characteristicById(route.characteristicId)
            with(_characteristic.value.characteristic) {
                _sampleRelatedTime.value = sampleRelatedTime?.let { Rounder.withToleranceStrCustom(it, 2) } ?: _sampleRelatedTime.value
                _measurementRelatedTime.value = measurementRelatedTime?.let { Rounder.withToleranceStrCustom(it, 2) } ?: _measurementRelatedTime.value
            }
        }
        mainPageHandler = MainPageHandler.Builder(if (route.characteristicId == NoRecord.num) Page.ADD_PRODUCT_LINE_CHAR else Page.EDIT_PRODUCT_LINE_CHAR, mainPageState)
            .setOnNavMenuClickAction { appNavigator.navigateBack() }
            .setOnFabClickAction { validateInput() }
            .build()
            .apply { setupMainPage(0, true) }
    }

    private fun prepareChar(charSubGroupId: ID) = viewModelScope.launch(Dispatchers.IO) {
        val charSubGroup = repository.charSubGroupById(charSubGroupId)
        _characteristic.value = DomainCharacteristic.DomainCharacteristicComplete(
            characteristicSubGroup = charSubGroup,
            characteristic = DomainCharacteristic(ishSubCharId = charSubGroup.charSubGroup.id)
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val characteristic get() = _characteristic.asStateFlow()
    val sampleRelatedTime get() = _sampleRelatedTime.asStateFlow()
    val measurementRelated get() = _measurementRelatedTime.asStateFlow()

    private val _charGroups = _characteristic.flatMapLatest { char ->
        repository.charGroups(char.characteristicSubGroup.charGroup.productLine.manufacturingProject.id)
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    val charGroups = _charGroups.flatMapLatest { charGroups ->
        _characteristic.flatMapLatest { char ->
            flow { emit(charGroups.map { it.charGroup.run { Triple(first = id, second = ishElement ?: EmptyString.str, third = id == char.characteristicSubGroup.charGroup.charGroup.id) } }) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun onSetCharGroup(id: ID) {
        if (_characteristic.value.characteristicSubGroup.charSubGroup.charGroupId != id) {
            _charGroups.value.find { it.charGroup.id == id }?.let {
                _characteristic.value = _characteristic.value.copy(
                    characteristicSubGroup = DomainCharSubGroup.DomainCharSubGroupComplete(
                        charGroup = it,
                        charSubGroup = DomainCharSubGroup()
                    ),
                    characteristic = _characteristic.value.characteristic.copy(ishSubCharId = NoRecord.num)
                )
                _fillInErrors.value = _fillInErrors.value.copy(charGroupError = false)
                _fillInState.value = FillInInitialState
            } ?: run {
                _characteristic.value = _characteristic.value.copy(
                    characteristicSubGroup = DomainCharSubGroup.DomainCharSubGroupComplete(
                        charGroup = DomainCharGroup.DomainCharGroupComplete(
                            productLine = _characteristic.value.characteristicSubGroup.charGroup.productLine
                        ),
                        charSubGroup = DomainCharSubGroup()
                    ),
                    characteristic = _characteristic.value.characteristic.copy(ishSubCharId = NoRecord.num)
                )
                _fillInErrors.value = _fillInErrors.value.copy(charGroupError = false)
                _fillInState.value = FillInInitialState
            }
        }
    }

    private val _charSubGroups = _characteristic.flatMapLatest { char ->
        repository.charSubGroups(char.characteristicSubGroup.charGroup.charGroup.id)
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    val charSubGroups = _charSubGroups.flatMapLatest { charSubGroups ->
        _characteristic.flatMapLatest { char ->
            flow { emit(charSubGroups.map { it.charSubGroup.run { Triple(first = id, second = ishElement ?: EmptyString.str, third = id == char.characteristicSubGroup.charSubGroup.id) } }) }
        }
    }.flowOn(Dispatchers.IO).conflate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun onSetCharSubGroup(id: ID) {
        if (_characteristic.value.characteristic.ishSubCharId != id) {
            _charSubGroups.value.find { it.charSubGroup.id == id }?.let {
                _characteristic.value = _characteristic.value.copy(
                    characteristicSubGroup = it,
                    characteristic = _characteristic.value.characteristic.copy(ishSubCharId = it.charSubGroup.id)
                )
                _fillInErrors.value = _fillInErrors.value.copy(charSubGroupError = false)
                _fillInState.value = FillInInitialState
            } ?: run {
                _characteristic.value = _characteristic.value.copy(
                    characteristicSubGroup = _characteristic.value.characteristicSubGroup.copy(charSubGroup = DomainCharSubGroup()),
                    characteristic = _characteristic.value.characteristic.copy(ishSubCharId = NoRecord.num)
                )
                _fillInErrors.value = _fillInErrors.value.copy(charSubGroupError = false)
                _fillInState.value = FillInInitialState
            }
        }
    }

    fun onSetOrder(order: Int) {
        _characteristic.value = _characteristic.value.copy(characteristic = _characteristic.value.characteristic.copy(charOrder = order))
        _fillInErrors.value = _fillInErrors.value.copy(charOrderError = false)
        _fillInState.value = FillInInitialState
    }

    fun onSetCharDesignation(it: String) {
        _characteristic.value = _characteristic.value.copy(characteristic = _characteristic.value.characteristic.copy(charDesignation = it))
        _fillInErrors.value = _fillInErrors.value.copy(charDesignationError = false)
        _fillInState.value = FillInInitialState
    }

    fun onSetCharDescription(it: String) {
        _characteristic.value = _characteristic.value.copy(characteristic = _characteristic.value.characteristic.copy(charDescription = it))
        _fillInErrors.value = _fillInErrors.value.copy(charDescriptionError = false)
        _fillInState.value = FillInInitialState
    }

    fun onSetSampleRelatedTime(value: String) {
        if (_sampleRelatedTime.value != value) {
            _sampleRelatedTime.value = value
            value.toDoubleOrNull()?.let { time ->
                _characteristic.value = _characteristic.value.copy(characteristic = _characteristic.value.characteristic.copy(sampleRelatedTime = time))
                _fillInErrors.value = _fillInErrors.value.copy(sampleRelatedTimeError = false)
                _fillInState.value = FillInInitialState
            } ?: run {
                _characteristic.value = _characteristic.value.copy(characteristic = _characteristic.value.characteristic.copy(sampleRelatedTime = null))
                if (value.isNotEmpty() && value != NoString.str) {
                    _fillInErrors.value = _fillInErrors.value.copy(sampleRelatedTimeError = true)
                } else {
                    _fillInErrors.value = _fillInErrors.value.copy(sampleRelatedTimeError = false)
                    _fillInState.value = FillInInitialState
                }
            }
        }
    }

    fun onSetMeasurementRelatedTime(value: String) {
        if (_measurementRelatedTime.value != value) {
            _measurementRelatedTime.value = value
            value.toDoubleOrNull()?.let { time ->
                _characteristic.value = _characteristic.value.copy(characteristic = _characteristic.value.characteristic.copy(measurementRelatedTime = time))
                _fillInErrors.value = _fillInErrors.value.copy(measurementRelatedTimeError = false)
                _fillInState.value = FillInInitialState
            } ?: run {
                _characteristic.value = _characteristic.value.copy(characteristic = _characteristic.value.characteristic.copy(measurementRelatedTime = null))
                if (value.isNotEmpty() && value != NoString.str) {
                    _fillInErrors.value = _fillInErrors.value.copy(measurementRelatedTimeError = true)
                } else {
                    _fillInErrors.value = _fillInErrors.value.copy(measurementRelatedTimeError = false)
                    _fillInState.value = FillInInitialState
                }
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
            with(_characteristic.value.characteristic) {
                if (_characteristic.value.characteristicSubGroup.charGroup.charGroup.id == NoRecord.num) {
                    _fillInErrors.value = _fillInErrors.value.copy(charGroupError = true)
                    append("Characteristic group must be selected\n")
                }
                if (ishSubCharId == NoRecord.num) {
                    _fillInErrors.value = _fillInErrors.value.copy(charSubGroupError = true)
                    append("Characteristic sub group must be selected\n")
                }
                if (charOrder == NoRecord.num.toInt()) {
                    _fillInErrors.value = _fillInErrors.value.copy(charOrderError = true)
                    append("Characteristic order field is mandatory\n")
                }
                if (charDesignation.isNullOrEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(charDesignationError = true)
                    append("Char. designation field is mandatory\n")
                }
                if (charDescription.isNullOrEmpty()) {
                    _fillInErrors.value = _fillInErrors.value.copy(charDescriptionError = true)
                    append("Char. description field is mandatory\n")
                }
                if (_fillInErrors.value.sampleRelatedTimeError) {
                    append("Wrong format of sample related time!\n")
                }
                if (_fillInErrors.value.measurementRelatedTimeError) {
                    append("Wrong format of measurement related time!\n")
                }
            }
        }
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch(Dispatchers.IO) {
        mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
        repository.run {
            if (_characteristic.value.characteristic.id == NoRecord.num) insertCharacteristic(_characteristic.value.characteristic) else updateCharacteristic(_characteristic.value.characteristic)
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
            val productLine = _characteristic.value.characteristicSubGroup.charGroup.productLine.manufacturingProject.id
            val charGroupId = _characteristic.value.characteristicSubGroup.charGroup.charGroup.id
            val charSubGroupId = _characteristic.value.characteristic.ishSubCharId
            val charId = it
            appNavigator.navigateTo(
                route = Route.Main.ProductLines.Characteristics.CharacteristicGroupList(
                    productLineId = productLine,
                    charGroupId = charGroupId,
                    charSubGroupId = charSubGroupId,
                    characteristicId = charId
                ),
                popUpToRoute = Route.Main.ProductLines.Characteristics,
                inclusive = true
            )
        }
    }
}

data class FillInErrors(
    val charGroupError: Boolean = false,
    val charSubGroupError: Boolean = false,
    val charOrderError: Boolean = false,
    val charDesignationError: Boolean = false,
    val charDescriptionError: Boolean = false,
    val sampleRelatedTimeError: Boolean = false,
    val measurementRelatedTimeError: Boolean = false,
)