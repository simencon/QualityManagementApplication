package com.simenko.qmapp.ui.main.structure.forms.line

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.di.ChannelIdParameter
import com.simenko.qmapp.di.LineIdParameter
import com.simenko.qmapp.domain.FillInError
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccess
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainManufacturingLine
import com.simenko.qmapp.domain.entities.DomainManufacturingLine.DomainManufacturingLineComplete
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
class LineViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val repository: ManufacturingRepository,
    @ChannelIdParameter private val channelId: Int,
    @LineIdParameter private val lineId: Int
) : ViewModel() {
    private val _line = MutableStateFlow(DomainManufacturingLineComplete())

    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    var mainPageHandler: MainPageHandler? = null
        private set

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (lineId == NoRecord.num) prepareLine(channelId) else _line.value = repository.lineCompleteById(lineId)
                mainPageHandler = MainPageHandler.Builder(if (lineId == NoRecord.num) Page.ADD_LINE else Page.EDIT_LINE, mainPageState)
                    .setOnNavMenuClickAction { appNavigator.navigateBack() }
                    .setOnFabClickAction { validateInput() }
                    .build()
            }
        }
    }

    private fun prepareLine(channelId: Int) {
        _line.value = DomainManufacturingLineComplete(
            line = DomainManufacturingLine(chId = channelId),
            channelComplete = repository.channelById(channelId)
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
            if (_line.value.line.lineOrder == NoRecord.num) {
                _fillInErrors.value = _fillInErrors.value.copy(lineOrderError = true)
                append("Operation order field is mandatory\n")
            }
            if (_line.value.line.lineAbbr.isEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(lineAbbrError = true)
                append("Operation ID field is mandatory\n")
            }
            if (_line.value.line.lineDesignation.isEmpty()) {
                _fillInErrors.value = _fillInErrors.value.copy(lineDesignationError = true)
                append("Operation complete name field is mandatory\n")
            }
        }

        if (errorMsg.isNotEmpty()) _fillInState.value = FillInError(errorMsg) else _fillInState.value = FillInSuccess
    }
}

data class FillInErrors(
    var lineOrderError: Boolean = false,
    var lineAbbrError: Boolean = false,
    var lineDesignationError: Boolean = false,
)