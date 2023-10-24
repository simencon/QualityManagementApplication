package com.simenko.qmapp.ui.main.structure.forms.line

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInError
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInSuccess
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.entities.DomainManufacturingLine
import com.simenko.qmapp.other.Constants.DEFAULT_SPACE
import com.simenko.qmapp.other.Constants.FAB_HEIGHT
import com.simenko.qmapp.ui.common.InfoLine
import com.simenko.qmapp.ui.common.RecordFieldItem
import com.simenko.qmapp.utils.StringUtils.concatTwoStrings

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LineForm(
    modifier: Modifier = Modifier,
    viewModel: LineViewModel
) {
    val lnComplete by viewModel.line.collectAsStateWithLifecycle(DomainManufacturingLine.DomainManufacturingLineComplete())
    LaunchedEffect(lnComplete) {
        viewModel.mainPageHandler?.setupMainPage?.invoke(0, true)
    }

    val fillInErrors by viewModel.fillInErrors.collectAsStateWithLifecycle()

    val fillInState by viewModel.fillInState.collectAsStateWithLifecycle()
    var error by rememberSaveable { mutableStateOf(EmptyString.str) }

    LaunchedEffect(fillInState) {
        fillInState.let { state ->
            when (state) {
                is FillInSuccess -> viewModel.makeRecord()
                is FillInError -> error = state.errorMsg
                is FillInInitialState -> error = EmptyString.str
            }
        }
    }

    val (orderFR) = FocusRequester.createRefs()
    val (abbreviationFR) = FocusRequester.createRefs()
    val (designationFR) = FocusRequester.createRefs()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(all = 0.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Department", body = concatTwoStrings(lnComplete.channelComplete.depAbbr, lnComplete.channelComplete.depName))
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Sub department", body = concatTwoStrings(lnComplete.channelComplete.subDepAbbr, lnComplete.channelComplete.subDepDesignation))
            InfoLine(modifier = modifier.padding(start = 0.dp), title = "Channel", body = concatTwoStrings(lnComplete.channelComplete.channelAbbr, lnComplete.channelComplete.channelDesignation))
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                valueParam = Triple(lnComplete.line.lineOrder.let { if (it == NoRecord.num) EmptyString.str else it }.toString(), fillInErrors.lineOrderError) {
                    viewModel.setLineOrder(if(it == EmptyString.str) NoRecord.num else it.toInt())
                },
                keyboardNavigation = Pair(orderFR) { abbreviationFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Number, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.FormatListNumbered, "Line order", "Enter line order"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                valueParam = Triple(lnComplete.line.lineAbbr, fillInErrors.lineAbbrError) { viewModel.setLineAbbr(it) },
                keyboardNavigation = Pair(abbreviationFR) { designationFR.requestFocus() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Next),
                contentDescription = Triple(Icons.Outlined.Info, "Line id", "Enter line id"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecordFieldItem(
                valueParam = Triple(lnComplete.line.lineDesignation, fillInErrors.lineDesignationError) { viewModel.setLineDesignation(it) },
                keyboardNavigation = Pair(designationFR) { keyboardController?.hide() },
                keyBoardTypeAction = Pair(KeyboardType.Ascii, ImeAction.Done),
                contentDescription = Triple(Icons.Outlined.Info, "Line complete name", "Enter line complete name"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (error != EmptyString.str)
                Text(
                    modifier = Modifier
                        .padding(all = 5.dp)
                        .width(320.dp),
                    text = error,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                    textAlign = TextAlign.Center
                )
            Spacer(modifier = Modifier.height((FAB_HEIGHT + DEFAULT_SPACE).dp))
        }
    }
}