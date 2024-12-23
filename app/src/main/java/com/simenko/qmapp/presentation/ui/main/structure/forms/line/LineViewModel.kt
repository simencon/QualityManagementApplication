package com.simenko.qmapp.presentation.ui.main.structure.forms.line

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainManufacturingLine
import com.simenko.qmapp.domain.entities.DomainManufacturingLine.DomainManufacturingLineComplete
import com.simenko.qmapp.other.Status
import com.simenko.qmapp.data.repository.ManufacturingRepository
import com.simenko.qmapp.presentation.ui.main.main.MainPageHandler
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LineViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
) : ViewModel() {
    private val _line = MutableStateFlow(DomainManufacturingLineComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null
        private set

    fun onEntered(route: Route.Main.CompanyStructure.LineAddEdit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (route.lineId == NoRecord.num) prepareLine(route.channelId) else _line.value = repository.lineById(route.lineId)
                mainPageHandler = MainPageHandler.Builder(if (route.lineId == NoRecord.num) Page.ADD_LINE else Page.EDIT_LINE, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { validateInput() }
                    .build()
                    .apply { setupMainPage(0, true) }
            }
        }
    }

    private fun prepareLine(channelId: ID) {
        _line.value = DomainManufacturingLineComplete(
            line = DomainManufacturingLine(chId = channelId),
            channelWithParents = repository.channelWithParentsById(channelId)
        )
    }

    /**
     * UI State --------------------------------------------------------------------------------------------------------------------------------------
     * */
    val line get() = _line.asStateFlow()

    fun setLineOrder(it: Int) {
        _line.value = _line.value.copy(line = _line.value.line.copy(lineOrder = it))
        _fillInErrors.value = _fillInErrors.value.copy(lineOrderError = false)
        _fillInState.value = FillInInitialState
    }

    fun setLineAbbr(it: String) {
        _line.value = _line.value.copy(line = _line.value.line.copy(lineAbbr = it))
        _fillInErrors.value = _fillInErrors.value.copy(lineAbbrError = false)
        _fillInState.value = FillInInitialState
    }

    fun setLineDesignation(it: String) {
        _line.value = _line.value.copy(line = _line.value.line.copy(lineDesignation = it))
        _fillInErrors.value = _fillInErrors.value.copy(lineDesignationError = false)
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
            if (_line.value.line.lineOrder == NoRecord.num.toInt()) {
                _fillInErrors.value = _fillInErrors.value.copy(lineOrderError = true)
                append("Line order field is mandatory\n")
            }
            if (_line.value.line.lineAbbr.isEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(lineAbbrError = true)
                append("Line ID field is mandatory\n")
            }
            if (_line.value.line.lineDesignation.isEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(lineDesignationError = true)
                append("Line complete name field is mandatory\n")
            }
        }

        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg) else _fillInState.value = FillInSuccessState
    }

    fun makeRecord() = viewModelScope.launch {
        mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
        withContext(Dispatchers.IO) {
            repository.run { if (_line.value.line.id == NoRecord.num) insertLine(_line.value.line) else updateLine(_line.value.line) }.consumeEach { event ->
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, null))
                        Status.SUCCESS -> navBackToRecord(resource.data?.id)
                        Status.ERROR -> {
                            mainPageHandler?.updateLoadingState?.invoke(Triple(true, false, resource.message))
                            _fillInState.value = FillInInitialState
                        }
                    }
                }
            }
        }
    }

    private suspend fun navBackToRecord(id: ID?) {
        mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
        withContext(Dispatchers.Main) {
            id?.let {
                val companyId = _line.value.channelWithParents.companyId
                val depId = _line.value.channelWithParents.departmentId
                val subDepId = _line.value.channelWithParents.subDepartmentId
                val chId = _line.value.channelWithParents.id
                appNavigator.tryNavigateTo(
                    route = Route.Main.CompanyStructure.StructureView(companyId, depId, subDepId, chId, it),
                    popUpToRoute = Route.Main.CompanyStructure,
                    inclusive = true
                )
            }
        }
    }
}

data class FillInErrors(
    var lineOrderError: Boolean = false,
    var lineAbbrError: Boolean = false,
    var lineDesignationError: Boolean = false,
)