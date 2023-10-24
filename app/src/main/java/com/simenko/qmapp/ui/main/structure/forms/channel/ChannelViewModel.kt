package com.simenko.qmapp.ui.main.structure.forms.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ChannelIdParameter
import com.simenko.qmapp.di.SubDepartmentIdParameter
import com.simenko.qmapp.domain.FillInError
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccess
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    @SubDepartmentIdParameter private val subDepId: Int,
    @ChannelIdParameter private val channelId: Int,
) : ViewModel() {
    private val _channel = MutableStateFlow(DomainManufacturingChannel.DomainManufacturingChannelComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null
        private set

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (channelId == NoRecord.num) prepareLine(subDepId) else _channel.value = repository.channelById(channelId)
                mainPageHandler = MainPageHandler.Builder(if (channelId == NoRecord.num) Page.ADD_LINE else Page.EDIT_LINE, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { validateInput() }
                    .build()
            }
        }
    }

    private fun prepareLine(subDepId: Int) {
        _channel.value = DomainManufacturingChannel.DomainManufacturingChannelComplete(
            channel = DomainManufacturingChannel(subDepId = subDepId),
            subDepartmentWithParents = repository.subDepartmentWithParentsById(subDepId)
        )
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
            if (_channel.value.channel.channelOrder == NoRecord.num) {
                _fillInErrors.value = _fillInErrors.value.copy(channelOrderError = true)
                append("Channel order field is mandatory\n")
            }
            if (_channel.value.channel.channelAbbr.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(channelAbbrError = true)
                append("Channel ID field is mandatory\n")
            }
            if (_channel.value.channel.channelDesignation.isNullOrEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(channelDesignationError = true)
                append("Channel complete name field is mandatory\n")
            }
        }

        if (errorMsg.isNotEmpty()) _fillInState.value = FillInError(errorMsg) else _fillInState.value = FillInSuccess
    }
}

data class FillInErrors(
    var channelOrderError: Boolean = false,
    var channelAbbrError: Boolean = false,
    var channelDesignationError: Boolean = false,
)