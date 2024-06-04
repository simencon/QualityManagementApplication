package com.simenko.qmapp.ui.main.structure.forms.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainManufacturingChannel
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.repository.ManufacturingRepository
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
class ChannelViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
) : ViewModel() {
    private val _channel = MutableStateFlow(DomainManufacturingChannel.DomainManufacturingChannelComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null
        private set

    fun onEntered(route: Route.Main.CompanyStructure.ChannelAddEdit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (route.channelId == NoRecord.num) prepareLine(route.subDepartmentId) else _channel.value = repository.channelById(route.channelId)
                mainPageHandler = MainPageHandler.Builder(if (route.channelId == NoRecord.num) Page.ADD_CHANNEL else Page.EDIT_CHANNEL, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { validateInput() }
                    .build()
                    .apply { setupMainPage(0, true) }
            }
        }
    }

    private fun prepareLine(subDepId: ID) {
        _channel.value = DomainManufacturingChannel.DomainManufacturingChannelComplete(
            channel = DomainManufacturingChannel(subDepId = subDepId),
            subDepartmentWithParents = repository.subDepartmentWithParentsById(subDepId)
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val channel get() = _channel.asStateFlow()

    fun setChannelOrder(it: Int) {
        _channel.value = _channel.value.copy(channel = _channel.value.channel.copy(channelOrder = it))
        _fillInErrors.value = _fillInErrors.value.copy(channelOrderError = false)
        _fillInState.value = FillInInitialState
    }

    fun setChannelAbbr(it: String) {
        _channel.value = _channel.value.copy(channel = _channel.value.channel.copy(channelAbbr = it))
        _fillInErrors.value = _fillInErrors.value.copy(channelAbbrError = false)
        _fillInState.value = FillInInitialState
    }

    fun setChannelDesignation(it: String) {
        _channel.value = _channel.value.copy(channel = _channel.value.channel.copy(channelDesignation = it))
        _fillInErrors.value = _fillInErrors.value.copy(channelDesignationError = false)
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
            if (_channel.value.channel.channelOrder == NoRecord.num.toInt()) {
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
        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch {
        mainPageHandler?.updateLoadingState?.invoke(Pair(true, null))
        withContext(Dispatchers.IO) {
            repository.run { if (_channel.value.channel.id == NoRecord.num) insertChannel(_channel.value.channel) else updateChannel(_channel.value.channel) }.consumeEach { event ->
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
                val companyId = _channel.value.subDepartmentWithParents.companyId
                val depId = _channel.value.subDepartmentWithParents.departmentId
                val subDepId = _channel.value.subDepartmentWithParents.id
                appNavigator.tryNavigateTo(
                    route = Route.Main.CompanyStructure.StructureView(companyId, depId, subDepId, it),
                    popUpToRoute = Route.Main.CompanyStructure,
                    inclusive = true
                )
            }
        }
    }
}

data class FillInErrors(
    var channelOrderError: Boolean = false,
    var channelAbbrError: Boolean = false,
    var channelDesignationError: Boolean = false,
)